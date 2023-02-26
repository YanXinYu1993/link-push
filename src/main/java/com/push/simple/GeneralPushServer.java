package com.push.simple;

import com.alibaba.fastjson2.JSON;
import com.push.entity.MgrCompany;
import com.push.entity.PushData;
import com.push.entity.PushDataError;
import com.push.mapper.PushDataErrorMapper;
import com.push.mapper.PushDataMapper;
import com.push.utils.CompanyCacheUtil;
import com.push.utils.OkhttpUtils;
import com.push.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @description: 原有推送服务
 * @author: Yan XinYu
 */
@Slf4j
@Component
public class GeneralPushServer {

    @Autowired
    private CompanyCacheUtil companyCacheUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PushDataMapper pushDataMapper;

    @Autowired
    private PushDataErrorMapper pushDataErrorMapper;

    private final String COMPANY_INDEX_KEY = "push.company.index.%s";

    private final Map<Long,CompanyPushThread> mapThread = new ConcurrentHashMap<>(256);

    public void start() throws InterruptedException {
        // 查所有公司
        List<MgrCompany> all = companyCacheUtil.getAll();

        // 为每个公司生成一个推送线程并执行
        for (MgrCompany company : all) {
            CompanyPushThread companyPushThread = new CompanyPushThread(company, this::run);
            companyPushThread.start();
            mapThread.put(company.getId(), companyPushThread);
        }

        synchronized (this){
            wait();
        }
    }

    public void close(){
        Iterator<Map.Entry<Long, CompanyPushThread>> iterator = mapThread.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Long, CompanyPushThread> next = iterator.next();
            next.getValue().shutdown();
        }
        synchronized (this){
            notify();
        }
    }

    public void run(MgrCompany company){

        // 获取当前index
        Long currentIndex = getCurrentIndex(company.getId());

        // 查这个公司需要推送的数据,id升序排序
        List<PushData> pushData = pushDataMapper.queryRecordByLimit(currentIndex, 1000);
        if(pushData == null || pushData.isEmpty()){
            ThreadUtils.sleepTime(200L);
            return;
        }
        // 结束
        Long endIndex = pushData.get(pushData.size() - 1 ).getId();
        String uuid = UUID.randomUUID().toString().replace("-","").substring(10);

        // 同步推送数据
        String responseBody = null;
        try {
            log.info("push req: companyId:{},startIndex:{},endIndex:{},uuid:{}",company.getId(),currentIndex,endIndex,uuid);
            responseBody = OkhttpUtils.post(company.getUrl(), JSON.toJSONString(pushData));
            log.info("push resp: companyId:{},startIndex:{},endIndex:{},uuid:{}",company.getId(),currentIndex,endIndex,uuid);
        } catch (IOException e) {
            // 记录推送失败数据
            log.error("companyId:{},companyName:{},推送失败:url:{},resp:{}",company.getId(),company.getName(),company.getUrl(),responseBody,e);
            onFailure(company,pushData,e.getMessage());
        }
        // 记录推送点
        setIndexOfValue(company.getId(), endIndex);

        if(endIndex==2442698){
            close();
        }
    }

    protected void onFailure(MgrCompany company,List<PushData> pushData,String cause){
        LocalDateTime now = LocalDateTime.now();
        List<PushDataError> errorData = new ArrayList<>(pushData.size());
        for (PushData record : pushData) {
            PushDataError pushDataError = new PushDataError();
            pushDataError.setUrl(company.getUrl());
            pushDataError.setCompanyId(company.getId().intValue());
            pushDataError.setPushData(JSON.toJSONString(record));
            pushDataError.setError(cause);
            pushDataError.setCreateTime(now);
        }
        pushDataErrorMapper.saveBatch(errorData);
    }

    private Long getCurrentIndex(Long companyId){
        String key = String.format(COMPANY_INDEX_KEY,companyId);
        String s = stringRedisTemplate.opsForValue().get(key);
        if(s == null || s.isEmpty()){
            return pushDataMapper.minByCompanyId(companyId);
        }
        return Long.valueOf(s);
    }

    private void setIndexOfValue(Long companyId,Long value){
        String key = String.format(COMPANY_INDEX_KEY,companyId);
        stringRedisTemplate.opsForValue().set(key,String.valueOf(value));
    }

    public static class CompanyPushThread extends Thread {

        private volatile boolean running = true;
        private final MgrCompany company;
        private final Consumer<MgrCompany> consumer;

        public CompanyPushThread(MgrCompany company, Consumer<MgrCompany> consumer) {
            this.company = company;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            try{
                while (this.running){
                    consumer.accept(company);
                }
            } catch (Exception e){
                log.error("error:companyId:{}",company.getId(),e);
            }
        }

        public void shutdown(){
            synchronized (this){
                this.running = false;
            }
        }

    }
}

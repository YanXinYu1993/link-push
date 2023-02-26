package com.push.core;

import com.alibaba.fastjson2.JSON;
import com.push.core.mainDto.TransferDto;
import com.push.entity.CommitLog;
import com.push.entity.MgrCompany;
import com.push.entity.PushData;
import com.push.entity.PushDataError;
import com.push.mapper.PushDataErrorMapper;
import com.push.mapper.PushDataMapper;
import com.push.utils.CompanyCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Yan XinYu
 */
@Slf4j
@Component
public class PushEventHandler implements Ihandler {

    @Autowired
    private PushDataErrorMapper pushDataErrorMapper;
    @Autowired
    private PushDataMapper pushDataMapper;
    @Autowired
    private CompanyCacheUtil companyCacheUtil;

    @Override
    public List<PushData> getDataList(CommitLog commitLog) {
        return pushDataMapper.queryRecord(commitLog.getStartIndex(), commitLog.getEndIndex());
    }
    @Override
    public List<TransferDto> jobHandler(List<PushData> records) {

        List<TransferDto> list = new ArrayList<>(records.size());

        // 通过公司id数据分组
        Map<Long, List<PushData>> collect = records.stream().collect(Collectors.groupingBy(PushData::getCompanyId));

        // 通过公司id从缓存中获取推送地址
        collect.forEach((k,y)->{
            MgrCompany mgrCompany = companyCacheUtil.get(k);
            TransferDto transferDto = new TransferDto();
            transferDto.setCompanyId(k.intValue());
            transferDto.setUrl(mgrCompany.getUrl());
            transferDto.setRecords(y);
            list.add(transferDto);
        });

        return list;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onFailure(TransferDto dto ,Throwable e){
        // 失败数据记录到数据库中，下次数据推送从error表中直接推送,
        LocalDateTime now = LocalDateTime.now();
        List<PushDataError> errorData = new ArrayList<>(dto.getRecords().size());
        for (PushData record : dto.getRecords()) {
            PushDataError pushDataError = new PushDataError();
            pushDataError.setUrl(dto.getUrl());
            pushDataError.setCompanyId(dto.getCompanyId());
            pushDataError.setPushData(JSON.toJSONString(record));
            pushDataError.setError(e.getMessage());
            pushDataError.setCreateTime(now);
        }
        pushDataErrorMapper.saveBatch(errorData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onSuccess(TransferDto dto){
        // do nothing
    }
}

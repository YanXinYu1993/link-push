package com.push.utils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.push.entity.MgrCompany;
import com.push.mapper.MgrCompanyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @description: 对公司信息进行缓存
 * @author: Yan XinYu
 */
@Slf4j
@Component
public class CompanyCacheUtil implements InitializingBean {

    @Autowired
    private MgrCompanyMapper mapper;

    private static final Map<Long, MgrCompany> cache = new ConcurrentHashMap<>(256);

    public List<MgrCompany> getAll(){
        return new ArrayList<>(cache.values());
    }

    public MgrCompany get(Long id){
        return cache.get(id);
    }

    public void setCache(Long id,MgrCompany company){
        cache.put(id,company);
    }

    public void remove(Long id){
        cache.remove(id);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("loading company cache ...");
        List<MgrCompany> mgrCompanies = mapper.selectList(Wrappers.emptyWrapper());
        for (MgrCompany mgrCompany : mgrCompanies) {
            cache.put(mgrCompany.getId(),mgrCompany);
        }
    }
}

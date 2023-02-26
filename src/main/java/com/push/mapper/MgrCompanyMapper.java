package com.push.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.push.entity.CommitLog;
import com.push.entity.MgrCompany;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MgrCompanyMapper extends BaseMapper<MgrCompany> {

}
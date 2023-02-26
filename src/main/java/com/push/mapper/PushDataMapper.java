package com.push.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.push.entity.PushData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PushDataMapper extends BaseMapper<PushData> {

    @Select("select * from push_data where id >= #{startIndex} and id < #{endIndex}")
    List<PushData> queryRecord(Long startIndex,Long endIndex);

    @Select("select max(id) from push_data")
    Long maxId();

    @Select("select min(id) from push_data")
    Long minId();

    /**
     * 下面是普通推送用到的 sql api。
     * @param companyId 公司编号
     * @return
     */
    @Select("select min(id) from push_data where company_id = #{companyId}")
    Long minByCompanyId(Long companyId);

    @Select("select * from push_data where id > #{currentId} order by id asc limit #{size}")
    List<PushData> queryRecordByLimit(Long currentId,Integer size);

}
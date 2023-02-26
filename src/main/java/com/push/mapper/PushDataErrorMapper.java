package com.push.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.push.entity.CommitLog;
import com.push.entity.PushDataError;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import java.util.List;

@Mapper
public interface PushDataErrorMapper extends BaseMapper<PushDataError> {

    @Insert("<script> " +
            "INSERT into push_data_error " +
            "(error, company_name, company_id, url,push_data,return_data,create_time)" +
            "values " +
            "<foreach collection='subList' index='index' item='item' separator=','> " +
            "(#{item.error}, #{item.companyName}, #{item.companyId}, #{item.url}" +
            ", #{item.pushData}, #{item.returnData}, #{item.createTime}) "+
            "</foreach> " +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void saveBatch(List<PushDataError> subList);

}
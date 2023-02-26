package com.push.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.push.entity.CommitLog;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CommitLogMapper extends BaseMapper<CommitLog> {

    @Select("select * from commit_log order by id asc")
    List<CommitLog> queryALl();

    @Insert("<script> " +
            "INSERT into commit_log " +
            "(start_index, end_index, status, create_time)" +
            "values " +
            "<foreach collection='subList' index='index' item='item' separator=','> " +
            "(#{item.startIndex}, #{item.endIndex}, #{item.status}, #{item.createTime}) "+
            "</foreach> " +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void saveBatch(List<CommitLog> subList);

    @Update("update commit_log set `status`=#{status} where id=#{id}")
    void updateStatus(Long id,Boolean status);

}
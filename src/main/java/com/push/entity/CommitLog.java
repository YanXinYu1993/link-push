package com.push.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
    * 数据推送时提交的日志
    */
@Data
@TableName(value = "commit_log")
public class CommitLog {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 开始标记
     */
    @TableField(value = "start_index")
    private Long startIndex;

    /**
     * 结束标记
     */
    @TableField(value = "end_index")
    private Long endIndex;

    /**
     * 状态标记：0-未完成，1-已完成
     */
    @TableField(value = "`status`")
    private Boolean status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    public static final String COL_ID = "id";

    public static final String COL_START_INDEX = "start_index";

    public static final String COL_END_INDEX = "end_index";

    public static final String COL_STATUS = "status";

    public static final String COL_CREATE_TIME = "create_time";
}
package com.push.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

@Data
@TableName(value = "push_data_error")
public class PushDataError {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 失败原因
     */
    @TableField(value = "error")
    private String error;

    /**
     * 公司名称
     */
    @TableField(value = "company_name")
    private String companyName;

    /**
     * 公司ID
     */
    @TableField(value = "company_id")
    private Integer companyId;

    /**
     * url
     */
    @TableField(value = "url")
    private String url;

    /**
     * 发送数据
     */
    @TableField(value = "push_data")
    private String pushData;

    /**
     * 返回数据
     */
    @TableField(value = "return_data")
    private String returnData;

    /**
     * 失败时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    public static final String COL_ID = "id";

    public static final String COL_ERROR = "error";

    public static final String COL_COMPANY_NAME = "company_name";

    public static final String COL_COMPANY_ID = "company_id";

    public static final String COL_URL = "url";

    public static final String COL_PUSH_DATA = "push_data";

    public static final String COL_RETURN_DATA = "return_data";

    public static final String COL_CREATE_TIME = "create_time";
}
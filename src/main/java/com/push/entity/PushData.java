package com.push.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

@Data
@TableName(value = "push_data")
public class PushData {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "application_id")
    private Long applicationId;

    @TableField(value = "app_eui")
    private String appEui;

    @TableField(value = "`data`")
    private String data;

    @TableField(value = "data_type")
    private Integer dataType;

    @TableField(value = "device_type")
    private Integer deviceType;

    @TableField(value = "is_send")
    private Integer isSend;

    @TableField(value = "last_update_time")
    private Date lastUpdateTime;

    @TableField(value = "mac")
    private String mac;

    @TableField(value = "reserver")
    private String reserver;

    @TableField(value = "send_date")
    private Date sendDate;

    @TableField(value = "v_id")
    private String vId;

    @TableField(value = "company_id")
    private Long companyId;

    @TableField(value = "gateways")
    private String gateways;

    @TableField(value = "create_times")
    private String createTimes;

    @TableField(value = "send_dates")
    private String sendDates;

    public static final String COL_ID = "id";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_APPLICATION_ID = "application_id";

    public static final String COL_APP_EUI = "app_eui";

    public static final String COL_DATA = "data";

    public static final String COL_DATA_TYPE = "data_type";

    public static final String COL_DEVICE_TYPE = "device_type";

    public static final String COL_IS_SEND = "is_send";

    public static final String COL_LAST_UPDATE_TIME = "last_update_time";

    public static final String COL_MAC = "mac";

    public static final String COL_RESERVER = "reserver";

    public static final String COL_SEND_DATE = "send_date";

    public static final String COL_V_ID = "v_id";

    public static final String COL_COMPANY_ID = "company_id";

    public static final String COL_GATEWAYS = "gateways";

    public static final String COL_CREATE_TIMES = "create_times";

    public static final String COL_SEND_DATES = "send_dates";
}
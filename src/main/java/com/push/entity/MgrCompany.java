package com.push.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

@Data
@TableName(value = "mgr_company")
public class MgrCompany {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 公司名称
     */
    @TableField(value = "`name`")
    private String name;

    /**
     * 公司地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 公司电话
     */
    @TableField(value = "telephone")
    private String telephone;

    /**
     * 公司邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 公司类型
     */
    @TableField(value = "`type`")
    private String type;

    @TableField(value = "url")
    private String url;

    @TableField(value = "port")
    private Integer port;

    @TableField(value = "`state`")
    private Integer state;

    @TableField(value = "max_size")
    private Integer maxSize;

    @TableField(value = "token")
    private String token;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 是否推送isGateways
     */
    @TableField(value = "is_gateways")
    private Integer isGateways;

    /**
     * 重连状态 0:重连1:不重连
     */
    @TableField(value = "recon_state")
    private Integer reconState;

    /**
     * 心跳状态 0:发送1:不发送
     */
    @TableField(value = "heartbeat_state")
    private Integer heartbeatState;

    /**
     * 告警状态 0:发送1:不发送
     */
    @TableField(value = "alarm_state")
    private Integer alarmState;

    @TableField(value = "timeout_state")
    private Boolean timeoutState;

    @TableField(value = "signature")
    private Boolean signature;

    /**
     * 父id,-1:无父公司
     */
    @TableField(value = "parent_company_id")
    private Long parentCompanyId;

    /**
     * 可添加子公司的数量
     */
    @TableField(value = "sub_company_num")
    private Integer subCompanyNum;

    @TableField(value = "suf_topic")
    private String sufTopic;

    @TableField(value = "pre_topic")
    private String preTopic;

    /**
     * 是否推送设备状态0否1是
     */
    @TableField(value = "push_device_status")
    private String pushDeviceStatus;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_ADDRESS = "address";

    public static final String COL_TELEPHONE = "telephone";

    public static final String COL_EMAIL = "email";

    public static final String COL_TYPE = "type";

    public static final String COL_URL = "url";

    public static final String COL_PORT = "port";

    public static final String COL_STATE = "state";

    public static final String COL_MAX_SIZE = "max_size";

    public static final String COL_TOKEN = "token";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_IS_GATEWAYS = "is_gateways";

    public static final String COL_RECON_STATE = "recon_state";

    public static final String COL_HEARTBEAT_STATE = "heartbeat_state";

    public static final String COL_ALARM_STATE = "alarm_state";

    public static final String COL_TIMEOUT_STATE = "timeout_state";

    public static final String COL_SIGNATURE = "signature";

    public static final String COL_PARENT_COMPANY_ID = "parent_company_id";

    public static final String COL_SUB_COMPANY_NUM = "sub_company_num";

    public static final String COL_SUF_TOPIC = "suf_topic";

    public static final String COL_PRE_TOPIC = "pre_topic";

    public static final String COL_PUSH_DEVICE_STATUS = "push_device_status";
}
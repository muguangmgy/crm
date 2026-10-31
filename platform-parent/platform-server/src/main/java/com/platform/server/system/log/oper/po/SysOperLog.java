package com.platform.server.system.log.oper.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 模块名称
     */
    private String module;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 操作对象类型
     */
    private String targetType;

    /**
     * 操作对象ID
     */
    private Long targetId;

    /**
     * 请求参数（JSON）
     */
    private String requestData;

    /**
     * 响应状态：1成功 0失败
     */
    private Integer responseStatus;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 操作时间
     */
    private LocalDateTime operTime;

    /**
     * 执行耗时(ms)
     */
    private Integer duration;
}
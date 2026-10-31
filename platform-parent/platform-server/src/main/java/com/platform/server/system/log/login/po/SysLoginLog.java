package com.platform.server.system.log.login.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志实体
 */
@Data
@TableName("sys_login_log")
public class SysLoginLog {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String userCode;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 登录类型：1登录 2注销 3刷新
     */
    private Integer loginType;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 状态：1成功 0失败
     */
    private Integer status;

    /**
     * 提示消息
     */
    private String message;
}
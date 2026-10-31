package com.platform.server.system.log.login.service;

import com.platform.server.common.vo.PageVO;
import com.platform.server.system.log.login.dto.LoginLogQueryDTO;
import com.platform.server.system.log.login.vo.LoginLogVO;
import jakarta.servlet.http.HttpServletRequest;

public interface SysLoginLogService {

    PageVO<LoginLogVO> getLoginLogPage(LoginLogQueryDTO query);

    void cleanLogs(Integer keepDays);

    /**
     * 异步保存登录日志
     *
     * @param userCode  用户名
     * @param tenantId  租户ID（登录失败时可为null）
     * @param loginType 登录类型：1登录 2注销 3刷新
     * @param status    状态：1成功 0失败
     * @param message   提示消息
     *
     */
    void saveLoginLog(String userCode, Long tenantId, Integer loginType, Integer status,
                      String message, HttpServletRequest request);
}
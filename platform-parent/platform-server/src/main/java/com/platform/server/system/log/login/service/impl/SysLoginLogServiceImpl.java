package com.platform.server.system.log.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.utils.IpUtil;
import com.platform.common.utils.UserAgentUtils;
import com.platform.server.common.constants.SecurityConstants;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.log.login.dto.LoginLogQueryDTO;
import com.platform.server.system.log.login.mapper.SysLoginLogMapper;
import com.platform.server.system.log.login.po.SysLoginLog;
import com.platform.server.system.log.login.service.SysLoginLogService;
import com.platform.server.system.log.login.vo.LoginLogVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysLoginLogServiceImpl implements SysLoginLogService {

    private final SysLoginLogMapper sysLoginLogMapper;

    @Override
    public PageVO<LoginLogVO> getLoginLogPage(LoginLogQueryDTO query) {
        Page<LoginLogVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<LoginLogVO> result = sysLoginLogMapper.selectLogPage(page, query);
        return PageVO.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public void cleanLogs(Integer keepDays) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(keepDays);
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.le(SysLoginLog::getLoginTime, threshold));
        sysLoginLogMapper.delete(wrapper);
    }

    @Override
    @Async
    public void saveLoginLog(String userCode, Long tenantId, Integer loginType, Integer status,
                             String message, HttpServletRequest request) {
        String deviceInfo = IpUtil.getDeviceInfo(request);
        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setUserCode(userCode);
        loginLog.setTenantId(tenantId != null ? tenantId : SecurityConstants.DEFAULT_TENANT_ID);
        loginLog.setLoginType(loginType);
        loginLog.setIpAddress(IpUtil.getClientIp(request));
        loginLog.setDeviceInfo(deviceInfo);
        loginLog.setBrowser(UserAgentUtils.parseBrowser(deviceInfo));
        loginLog.setOs(UserAgentUtils.parseOs(deviceInfo));
        loginLog.setLoginTime(LocalDateTime.now());
        loginLog.setStatus(status);
        loginLog.setMessage(message);
        sysLoginLogMapper.insert(loginLog);
    }
}
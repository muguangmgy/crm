package com.platform.server.system.log.oper.service;

import com.platform.server.common.vo.PageVO;
import com.platform.server.system.log.oper.dto.OperLogQueryDTO;
import com.platform.server.system.log.oper.po.SysOperLog;
import com.platform.server.system.log.oper.vo.OperLogVO;

public interface SysOperLogService {

    PageVO<OperLogVO> getOperLogPage(OperLogQueryDTO query);

    void cleanLogs(Integer keepDays);

    /**
     * 异步保存操作日志
     *
     * @param operLog 操作日志实体
     */
    void saveOperLog(SysOperLog operLog);
}
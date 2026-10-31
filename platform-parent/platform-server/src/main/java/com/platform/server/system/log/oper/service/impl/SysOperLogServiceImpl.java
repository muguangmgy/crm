package com.platform.server.system.log.oper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.log.oper.dto.OperLogQueryDTO;
import com.platform.server.system.log.oper.mapper.SysOperLogMapper;
import com.platform.server.system.log.oper.po.SysOperLog;
import com.platform.server.system.log.oper.service.SysOperLogService;
import com.platform.server.system.log.oper.vo.OperLogVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl implements SysOperLogService {

    private final SysOperLogMapper sysOperLogMapper;

    @Override
    public PageVO<OperLogVO> getOperLogPage(OperLogQueryDTO query) {
        Page<OperLogVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<OperLogVO> result = sysOperLogMapper.selectLogPage(page, query);
        return PageVO.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public void cleanLogs(Integer keepDays) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(keepDays);
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SysOperLog::getOperTime, threshold);
        sysOperLogMapper.delete(wrapper);
    }

    @Override
    @Async
    public void saveOperLog(SysOperLog operLog) {
        try {
            sysOperLogMapper.insert(operLog);
            log.debug("操作日志保存成功: module={}, operation={}", operLog.getModule(), operLog.getOperation());
        } catch (Exception e) {
            log.error("操作日志保存失败: module={}, operation={}", operLog.getModule(), operLog.getOperation(), e);
        }
    }
}
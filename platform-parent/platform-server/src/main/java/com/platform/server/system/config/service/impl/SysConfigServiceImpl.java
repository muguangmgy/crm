package com.platform.server.system.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.ResultCode;
import com.platform.common.exception.BusinessException;
import com.platform.server.common.constants.SecurityConstants;
import com.platform.server.common.converter.SystemConverter;
import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.config.dto.ConfigQueryDTO;
import com.platform.server.system.config.dto.ConfigSaveDTO;
import com.platform.server.system.config.mapper.SysConfigMapper;
import com.platform.server.system.config.po.SysConfig;
import com.platform.server.system.config.service.SysConfigService;
import com.platform.server.system.config.vo.ConfigVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;
    private final SystemConverter systemConverter;

    @Override
    public PageVO<ConfigVO> getConfigPage(ConfigQueryDTO query) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        if (query.getTenantId() != null) {
            wrapper.eq(SysConfig::getTenantId, query.getTenantId());
        }
        if (StringUtils.isNoneBlank(query.getGroupCode())) {
            wrapper.eq(SysConfig::getGroupCode, query.getGroupCode());
        }
        if (StringUtils.isNoneBlank(query.getConfigKey())) {
            wrapper.like(SysConfig::getConfigKey, query.getConfigKey());
        }
        if (StringUtils.isNoneBlank(query.getConfigName())) {
            wrapper.like(SysConfig::getConfigName, query.getConfigName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysConfig::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SysConfig::getCreateTime);

        Page<SysConfig> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SysConfig> result = sysConfigMapper.selectPage(page, wrapper);

        List<ConfigVO> list = systemConverter.toConfigVOList(result.getRecords());
        return PageVO.of(list, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public ConfigVO getConfigById(Long id) {
        SysConfig config = sysConfigMapper.selectById(id);
        if (config == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return systemConverter.toConfigVO(config);
    }

    @Override
    public Long saveConfig(ConfigSaveDTO dto) {
        boolean isPlatformRole = UserContextHolder.isPlatformRole();

        // 租户ID：平台用户可选择租户，非平台用户使用当前用户的租户ID
        Long tenantId;
        if (isPlatformRole && dto.getTenantId() != null) {
            tenantId = dto.getTenantId();
        } else {
            tenantId = UserContextHolder.getTenantId();
            if (tenantId == null) {
                tenantId = SecurityConstants.DEFAULT_TENANT_ID;
            }
        }

        // 检查参数键是否已存在
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getTenantId, tenantId)
                .eq(SysConfig::getConfigKey, dto.getConfigKey());
        if (sysConfigMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "参数键已存在");
        }

        SysConfig config = new SysConfig();
        config.setTenantId(tenantId);
        config.setGroupCode(dto.getGroupCode());
        config.setConfigKey(dto.getConfigKey());
        config.setConfigName(dto.getConfigName());
        config.setConfigValue(dto.getConfigValue());
        config.setValueType(dto.getValueType());
        config.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        config.setRemark(dto.getRemark());
        sysConfigMapper.insert(config);
        return config.getId();
    }

    @Override
    public void updateConfig(ConfigSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "参数ID不能为空");
        }

        SysConfig existing = sysConfigMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 检查参数键是否已存在（排除自身）
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getTenantId, existing.getTenantId())
                .eq(SysConfig::getConfigKey, dto.getConfigKey())
                .ne(SysConfig::getId, dto.getId());
        if (sysConfigMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "参数键已存在");
        }

        SysConfig config = new SysConfig();
        config.setId(dto.getId());
        config.setGroupCode(dto.getGroupCode());
        config.setConfigKey(dto.getConfigKey());
        config.setConfigName(dto.getConfigName());
        config.setConfigValue(dto.getConfigValue());
        config.setValueType(dto.getValueType());
        config.setStatus(dto.getStatus());
        config.setRemark(dto.getRemark());
        sysConfigMapper.updateById(config);
    }

    @Override
    public void deleteConfig(Long id) {
        sysConfigMapper.deleteById(id);
    }

    @Override
    public void updateStatus(UpdateStatusDto dto) {
        SysConfig config = new SysConfig();
        config.setId(dto.getId());
        config.setStatus(dto.getStatus());
        sysConfigMapper.updateById(config);
    }

    @Override
    public ConfigVO getConfigByKey(String configKey) {
        Long tenantId = UserContextHolder.getTenantId();
        if (tenantId == null) {
            tenantId = SecurityConstants.DEFAULT_TENANT_ID;
        }

        SysConfig config = sysConfigMapper.selectByKeyWithTenant(configKey, tenantId);
        if (config == null) {
            return null;
        }
        return systemConverter.toConfigVO(config);
    }

    @Override
    public List<ConfigVO> getConfigByGroup(String groupCode) {
        Long tenantId = UserContextHolder.getTenantId();
        if (tenantId == null) {
            tenantId = SecurityConstants.DEFAULT_TENANT_ID;
        }

        // 查询所有符合条件的参数（租户+全局）
        List<SysConfig> allItems = sysConfigMapper.selectByGroupWithTenant(groupCode, tenantId);

        // 租户优先去重：同一configKey只保留租户的
        List<ConfigVO> result = systemConverter.toConfigVOList(allItems).stream()
                .collect(Collectors.groupingBy(ConfigVO::getConfigKey))
                .values().stream()
                .map(list -> list.stream()
                        .max(Comparator.comparingLong(ConfigVO::getTenantId))
                        .orElse(null))
                .filter(item -> item != null)
                .collect(Collectors.toList());

        return result;
    }
}
package com.platform.server.system.config.service;

import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.config.dto.ConfigQueryDTO;
import com.platform.server.system.config.dto.ConfigSaveDTO;
import com.platform.server.system.config.vo.ConfigVO;

import java.util.List;

public interface SysConfigService {

    PageVO<ConfigVO> getConfigPage(ConfigQueryDTO query);

    ConfigVO getConfigById(Long id);

    Long saveConfig(ConfigSaveDTO dto);

    void updateConfig(ConfigSaveDTO dto);

    void deleteConfig(Long id);

    void updateStatus(UpdateStatusDto dto);

    /**
     * 根据参数键获取参数值（租户优先）
     */
    ConfigVO getConfigByKey(String configKey);

    /**
     * 根据分组获取参数列表（租户优先）
     */
    List<ConfigVO> getConfigByGroup(String groupCode);
}
package com.platform.server.system.tenant.service;

import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.tenant.dto.TenantQueryDTO;
import com.platform.server.system.tenant.dto.TenantSaveDTO;
import com.platform.server.system.tenant.vo.TenantVO;

import java.util.List;

public interface SysTenantService {

    PageVO<TenantVO> getTenantPage(TenantQueryDTO query);

    TenantVO getTenantById(Long id);

    Long saveTenant(TenantSaveDTO dto);

    void updateTenant(TenantSaveDTO dto);

    void deleteTenant(Long id);

    void updateStatus(UpdateStatusDto dto);

    List<TenantVO> getTenantOptions();

    void extendTenant(Long id, Integer days);
}
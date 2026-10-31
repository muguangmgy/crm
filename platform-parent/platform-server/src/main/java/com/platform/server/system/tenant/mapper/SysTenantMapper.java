package com.platform.server.system.tenant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.server.system.tenant.po.SysTenant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysTenantMapper extends BaseMapper<SysTenant> {

    SysTenant selectByTenantCode(@Param("tenantCode") String tenantCode);
}
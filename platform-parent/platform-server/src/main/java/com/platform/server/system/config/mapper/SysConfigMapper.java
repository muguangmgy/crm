package com.platform.server.system.config.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.server.system.config.po.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 根据参数键查询参数（租户优先）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM sys_config WHERE config_key = #{configKey} AND status = 1 " +
            "AND (tenant_id = #{tenantId} OR tenant_id = 0) ORDER BY tenant_id DESC LIMIT 1")
    SysConfig selectByKeyWithTenant(@Param("configKey") String configKey, @Param("tenantId") Long tenantId);

    /**
     * 根据分组查询参数列表（租户优先）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM sys_config WHERE group_code = #{groupCode} AND status = 1 " +
            "AND (tenant_id = #{tenantId} OR tenant_id = 0) ORDER BY tenant_id DESC, create_time ASC")
    List<SysConfig> selectByGroupWithTenant(@Param("groupCode") String groupCode, @Param("tenantId") Long tenantId);
}
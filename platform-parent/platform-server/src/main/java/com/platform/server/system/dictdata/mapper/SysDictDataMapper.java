package com.platform.server.system.dictdata.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.server.system.dictdata.po.SysDictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 根据字典编码查询字典项（租户优先）
     * 忽略租户拦截器，手动处理租户逻辑
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM sys_dict_data WHERE dict_code = #{dictCode} AND status = 1 " +
            "AND (tenant_id = #{tenantId} OR tenant_id = 0) ORDER BY tenant_id DESC, sort ASC")
    List<SysDictData> selectByDictCodeWithTenant(@Param("dictCode") String dictCode, @Param("tenantId") Long tenantId);

    /**
     * 查询全局 CRM 字典项模板（tenant_id=0）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM sys_dict_data WHERE tenant_id = 0 AND dict_code LIKE 'crm_%' " +
            "ORDER BY dict_code ASC, sort ASC, id ASC")
    List<SysDictData> selectGlobalCrmDataTemplates();
}
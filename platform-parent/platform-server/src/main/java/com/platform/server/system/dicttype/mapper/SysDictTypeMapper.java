package com.platform.server.system.dicttype.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.server.system.dicttype.po.SysDictType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    /**
     * 查询所有字典类型（用于构建树，忽略租户拦截器）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT d.*, t.tenant_name FROM sys_dict_type d " +
            "LEFT JOIN sys_tenant t ON d.tenant_id = t.id " +
            "ORDER BY d.tenant_id ASC, d.sort ASC, d.dict_code ASC")
    List<SysDictType> selectAllForTree();

    /**
     * 统计指定父节点下的子字典类型数量
     */
    @Select("SELECT COUNT(*) FROM sys_dict_type WHERE parent_id = #{parentId}")
    int countByParentId(@Param("parentId") Long parentId);

    /**
     * 查询全局 CRM 字典类型模板（tenant_id=0）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM sys_dict_type WHERE tenant_id = 0 " +
            "AND (dict_code = 'crm' OR dict_code LIKE 'crm_%') " +
            "ORDER BY parent_id ASC, sort ASC, id ASC")
    List<SysDictType> selectGlobalCrmTemplates();
}
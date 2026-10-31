package com.platform.server.system.tenant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.server.common.converter.SystemConverter;
import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.dept.mapper.SysDeptMapper;
import com.platform.server.system.dept.po.SysDept;
import com.platform.server.system.dictdata.mapper.SysDictDataMapper;
import com.platform.server.system.dictdata.po.SysDictData;
import com.platform.server.system.dicttype.mapper.SysDictTypeMapper;
import com.platform.server.system.dicttype.po.SysDictType;
import com.platform.server.system.tenant.dto.TenantQueryDTO;
import com.platform.server.system.tenant.dto.TenantSaveDTO;
import com.platform.server.system.tenant.mapper.SysTenantMapper;
import com.platform.server.system.tenant.po.SysTenant;
import com.platform.server.system.tenant.service.SysTenantService;
import com.platform.server.system.tenant.vo.TenantVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SysTenantServiceImpl implements SysTenantService {

    private final SysTenantMapper sysTenantMapper;
    private final SysDeptMapper sysDeptMapper;
    private final SysDictTypeMapper sysDictTypeMapper;
    private final SysDictDataMapper sysDictDataMapper;
    private final SystemConverter systemConverter;

    @Override
    public PageVO<TenantVO> getTenantPage(TenantQueryDTO query) {
        LambdaQueryWrapper<SysTenant> wrapper = new LambdaQueryWrapper<>();
        if (query.getTenantName() != null) {
            wrapper.like(SysTenant::getTenantName, query.getTenantName());
        }
        if (query.getTenantCode() != null) {
            wrapper.like(SysTenant::getTenantCode, query.getTenantCode());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysTenant::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(SysTenant::getId);

        Page<SysTenant> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SysTenant> result = sysTenantMapper.selectPage(page, wrapper);

        List<TenantVO> list = systemConverter.toTenantVOList(result.getRecords());
        return PageVO.of(list, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public TenantVO getTenantById(Long id) {
        SysTenant tenant = sysTenantMapper.selectById(id);
        return systemConverter.toTenantVO(tenant);
    }

    @Override
    @Transactional
    public Long saveTenant(TenantSaveDTO dto) {
        // 1. 创建租户
        SysTenant tenant = new SysTenant();
        tenant.setTenantName(dto.getTenantName());
        tenant.setTenantCode(dto.getTenantCode());
        tenant.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        tenant.setExpireTime(dto.getExpireTime());
        tenant.setContactName(dto.getContactName());
        tenant.setContactPhone(dto.getContactPhone());
        sysTenantMapper.insert(tenant);

        // 2. 创建默认部门（使用租户名称作为部门名称）
        SysDept defaultDept = new SysDept();
        defaultDept.setTenantId(tenant.getId());
        defaultDept.setParentId(0L);
        defaultDept.setDeptName(dto.getTenantName());
        defaultDept.setDeptPath("/0");
        defaultDept.setSort(0);
        defaultDept.setStatus(1);
        sysDeptMapper.insert(defaultDept);

        // 3. 从全局模板复制 CRM 字典到新租户
        copyCrmDictTemplates(tenant.getId());

        return tenant.getId();
    }

    /**
     * 将全局 CRM 字典类型/字典项复制到指定租户，供租户独立维护
     */
    private void copyCrmDictTemplates(Long tenantId) {
        List<SysDictType> typeTemplates = sysDictTypeMapper.selectGlobalCrmTemplates();
        Map<Long, Long> typeIdMap = new HashMap<>();
        for (SysDictType template : typeTemplates) {
            SysDictType copy = new SysDictType();
            copy.setTenantId(tenantId);
            Long oldParentId = template.getParentId();
            copy.setParentId(oldParentId == null || oldParentId == 0L
                    ? 0L
                    : typeIdMap.getOrDefault(oldParentId, 0L));
            copy.setDictCode(template.getDictCode());
            copy.setDictName(template.getDictName());
            copy.setStatus(template.getStatus());
            copy.setRemark(template.getRemark());
            copy.setSort(template.getSort());
            sysDictTypeMapper.insert(copy);
            typeIdMap.put(template.getId(), copy.getId());
        }

        List<SysDictData> dataTemplates = sysDictDataMapper.selectGlobalCrmDataTemplates();
        for (SysDictData template : dataTemplates) {
            SysDictData copy = new SysDictData();
            copy.setTenantId(tenantId);
            copy.setDictCode(template.getDictCode());
            copy.setItemValue(template.getItemValue());
            copy.setItemLabel(template.getItemLabel());
            copy.setSort(template.getSort());
            copy.setStatus(template.getStatus());
            copy.setRemark(template.getRemark());
            sysDictDataMapper.insert(copy);
        }
    }

    @Override
    public void updateTenant(TenantSaveDTO dto) {
        SysTenant tenant = new SysTenant();
        tenant.setId(dto.getId());
        tenant.setTenantName(dto.getTenantName());
        tenant.setTenantCode(dto.getTenantCode());
        tenant.setStatus(dto.getStatus());
        tenant.setExpireTime(dto.getExpireTime());
        tenant.setContactName(dto.getContactName());
        tenant.setContactPhone(dto.getContactPhone());
        sysTenantMapper.updateById(tenant);
    }

    @Override
    public void deleteTenant(Long id) {
        sysTenantMapper.deleteById(id);
    }

    @Override
    public void updateStatus(UpdateStatusDto dto) {
        SysTenant tenant = new SysTenant();
        tenant.setId(dto.getId());
        tenant.setStatus(dto.getStatus());
        sysTenantMapper.updateById(tenant);
    }

    @Override
    public List<TenantVO> getTenantOptions() {
        LambdaQueryWrapper<SysTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysTenant::getStatus, 1);
        wrapper.orderByAsc(SysTenant::getId);
        return systemConverter.toTenantVOList(sysTenantMapper.selectList(wrapper));
    }

    @Override
    public void extendTenant(Long id, Integer days) {
        SysTenant tenant = sysTenantMapper.selectById(id);
        if (tenant == null) {
            throw new RuntimeException("租户不存在");
        }
        if (tenant.getExpireTime() == null) {
            tenant.setExpireTime(java.time.LocalDateTime.now().plusDays(days));
        } else {
            tenant.setExpireTime(tenant.getExpireTime().plusDays(days));
        }
        sysTenantMapper.updateById(tenant);
    }
}
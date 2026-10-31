package com.platform.server.system.dicttype.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.ResultCode;
import com.platform.common.enums.StatusEnum;
import com.platform.common.exception.BusinessException;
import com.platform.server.common.constants.SecurityConstants;
import com.platform.server.common.converter.SystemConverter;
import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.dictdata.service.SysDictDataService;
import com.platform.server.system.dicttype.dto.DictTypeQueryDTO;
import com.platform.server.system.dicttype.dto.DictTypeSaveDTO;
import com.platform.server.system.dicttype.mapper.SysDictTypeMapper;
import com.platform.server.system.dicttype.po.SysDictType;
import com.platform.server.system.dicttype.service.SysDictTypeService;
import com.platform.server.system.dicttype.vo.DictTypeTreeVO;
import com.platform.server.system.dicttype.vo.DictTypeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl implements SysDictTypeService {

    private final SysDictTypeMapper sysDictTypeMapper;

    @Autowired
    private SysDictDataService sysDictDataService;
    private final SystemConverter systemConverter;

    @Override
    public PageVO<DictTypeVO> getDictTypePage(DictTypeQueryDTO query) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        if (query.getTenantId() != null) {
            wrapper.eq(SysDictType::getTenantId, query.getTenantId());
        }
        if (query.getDictCode() != null) {
            wrapper.like(SysDictType::getDictCode, query.getDictCode());
        }
        if (query.getDictName() != null) {
            wrapper.like(SysDictType::getDictName, query.getDictName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysDictType::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SysDictType::getCreateTime);

        Page<SysDictType> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SysDictType> result = sysDictTypeMapper.selectPage(page, wrapper);

        List<DictTypeVO> list = systemConverter.toDictTypeVOList(result.getRecords());
        return PageVO.of(list, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public DictTypeVO getDictTypeById(Long id) {
        SysDictType dictType = sysDictTypeMapper.selectById(id);
        if (dictType == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return systemConverter.toDictTypeVO(dictType);
    }

    @Override
    public Long saveDictType(DictTypeSaveDTO dto) {
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

        // 检查字典编码是否已存在
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getTenantId, tenantId)
                .eq(SysDictType::getDictCode, dto.getDictCode());
        if (sysDictTypeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "字典编码已存在");
        }

        SysDictType dictType = new SysDictType();
        dictType.setTenantId(tenantId);
        dictType.setDictCode(dto.getDictCode());
        dictType.setDictName(dto.getDictName());
        dictType.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        dictType.setSort(dto.getSort() != null ? dto.getSort() : 0);
        dictType.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        dictType.setRemark(dto.getRemark());
        sysDictTypeMapper.insert(dictType);
        return dictType.getId();
    }

    @Override
    public void updateDictType(DictTypeSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "字典类型ID不能为空");
        }

        SysDictType existing = sysDictTypeMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 检查字典编码是否已存在（排除自身）
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getTenantId, existing.getTenantId())
                .eq(SysDictType::getDictCode, dto.getDictCode())
                .ne(SysDictType::getId, dto.getId());
        if (sysDictTypeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "字典编码已存在");
        }

        SysDictType dictType = new SysDictType();
        dictType.setId(dto.getId());
        dictType.setDictCode(dto.getDictCode());
        dictType.setDictName(dto.getDictName());
        dictType.setStatus(dto.getStatus());
        dictType.setRemark(dto.getRemark());
        sysDictTypeMapper.updateById(dictType);
    }

    @Override
    public void deleteDictType(Long id) {
        sysDictTypeMapper.deleteById(id);
    }

    @Override
    public void updateStatus(UpdateStatusDto dto) {
        SysDictType dictType = new SysDictType();
        dictType.setId(dto.getId());
        dictType.setStatus(dto.getStatus());
        sysDictTypeMapper.updateById(dictType);
    }

    @Override
    public List<DictTypeVO> getDictTypeOptions() {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getStatus, StatusEnum.ENABLED.getCode());
        wrapper.orderByAsc(SysDictType::getDictCode);
        return systemConverter.toDictTypeVOList(sysDictTypeMapper.selectList(wrapper));
    }

    @Override
    public List<DictTypeTreeVO> getDictTypeTree() {
        boolean isPlatformRole = UserContextHolder.isPlatformRole();
        Long currentTenantId = UserContextHolder.getTenantId();

        // 查询所有字典类型
        List<SysDictType> allTypes = sysDictTypeMapper.selectAllForTree();

        // 过滤：平台用户看全部，普通用户看全局+当前租户
        List<SysDictType> filteredTypes = allTypes.stream()
                .filter(t -> isPlatformRole || t.getTenantId() == 0 ||
                        (currentTenantId != null && t.getTenantId().equals(currentTenantId)))
                .collect(Collectors.toList());

        // 非平台用户：本租户已有同编码字典时，隐藏全局项，避免树里出现两份
        if (!isPlatformRole && currentTenantId != null) {
            Set<String> tenantDictCodes = filteredTypes.stream()
                    .filter(t -> currentTenantId.equals(t.getTenantId()))
                    .map(SysDictType::getDictCode)
                    .collect(Collectors.toSet());
            filteredTypes = filteredTypes.stream()
                    .filter(t -> t.getTenantId() != 0L || !tenantDictCodes.contains(t.getDictCode()))
                    .collect(Collectors.toList());
        }

        // 构建树
        return buildDictTypeTree(filteredTypes, 0L);
    }

    private List<DictTypeTreeVO> buildDictTypeTree(List<SysDictType> types, Long parentId) {
        List<DictTypeTreeVO> result = new ArrayList<>();
        for (SysDictType t : types) {
            if (t.getParentId() != null && t.getParentId().equals(parentId)) {
                DictTypeTreeVO vo = new DictTypeTreeVO();
                vo.setId(t.getId());
                vo.setTenantId(t.getTenantId());
                vo.setTenantName(t.getTenantId() == 0 ? "全局" : t.getTenantName());
                vo.setParentId(t.getParentId());
                vo.setDictCode(t.getDictCode());
                vo.setDictName(t.getDictName());
                vo.setSort(t.getSort() != null ? t.getSort() : 0);
                vo.setStatus(t.getStatus());
                vo.setRemark(t.getRemark());
                vo.setCreateTime(t.getCreateTime());
                vo.setChildren(buildDictTypeTree(types, t.getId()));
                result.add(vo);
            }
        }
        // 按sort排序
        result.sort(Comparator.comparing(DictTypeTreeVO::getSort));
        return result;
    }

    @Override
    public void deleteDictTypeWithCheck(Long id) {
        // 校验子字典类型
        int childCount = sysDictTypeMapper.countByParentId(id);
        if (childCount > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "请先删除子字典类型");
        }

        // 校验字典项
        SysDictType dictType = sysDictTypeMapper.selectById(id);
        if (dictType != null) {
            int itemCount = sysDictDataService.countByDictCode(dictType.getDictCode());
            if (itemCount > 0) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "请先删除字典项数据");
            }
        }

        sysDictTypeMapper.deleteById(id);
    }
}
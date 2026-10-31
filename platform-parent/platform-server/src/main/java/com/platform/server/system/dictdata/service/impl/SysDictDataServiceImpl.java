package com.platform.server.system.dictdata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.ResultCode;
import com.platform.common.exception.BusinessException;
import com.platform.server.common.converter.SystemConverter;
import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.dictdata.dto.DictDataBatchSaveDTO;
import com.platform.server.system.dictdata.dto.DictDataQueryDTO;
import com.platform.server.system.dictdata.dto.DictDataSaveDTO;
import com.platform.server.system.dictdata.mapper.SysDictDataMapper;
import com.platform.server.system.dictdata.po.SysDictData;
import com.platform.server.system.dictdata.service.SysDictDataService;
import com.platform.server.system.dictdata.vo.DictDataVO;
import com.platform.server.system.dicttype.mapper.SysDictTypeMapper;
import com.platform.server.system.dicttype.po.SysDictType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl implements SysDictDataService {

    private final SysDictDataMapper sysDictDataMapper;
    private final SysDictTypeMapper sysDictTypeMapper;
    private final SystemConverter systemConverter;

    @Override
    public PageVO<DictDataVO> getDictDataPage(DictDataQueryDTO query) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        if (query.getTenantId() != null) {
            wrapper.eq(SysDictData::getTenantId, query.getTenantId());
        }
        if (query.getDictCode() != null) {
            wrapper.eq(SysDictData::getDictCode, query.getDictCode());
        }
        if (query.getItemLabel() != null) {
            wrapper.like(SysDictData::getItemLabel, query.getItemLabel());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysDictData::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(SysDictData::getSort);

        Page<SysDictData> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SysDictData> result = sysDictDataMapper.selectPage(page, wrapper);

        List<DictDataVO> list = systemConverter.toDictDataVOList(result.getRecords());
        return PageVO.of(list, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public DictDataVO getDictDataById(Long id) {
        SysDictData dictData = sysDictDataMapper.selectById(id);
        if (dictData == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return systemConverter.toDictDataVO(dictData);
    }

    @Override
    public Long saveDictData(DictDataSaveDTO dto) {
        boolean isPlatformRole = UserContextHolder.isPlatformRole();

        // 查询字典类型，确定租户ID
        LambdaQueryWrapper<SysDictType> typeWrapper = new LambdaQueryWrapper<>();
        typeWrapper.eq(SysDictType::getDictCode, dto.getDictCode());
        SysDictType dictType = sysDictTypeMapper.selectOne(typeWrapper);
        if (dictType == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "字典类型不存在");
        }

        // 租户ID：跟随字典类型
        Long tenantId = dictType.getTenantId();

        // 检查字典值是否已存在
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getTenantId, tenantId)
                .eq(SysDictData::getDictCode, dto.getDictCode())
                .eq(SysDictData::getItemValue, dto.getItemValue());
        if (sysDictDataMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "字典值已存在");
        }

        SysDictData dictData = new SysDictData();
        dictData.setTenantId(tenantId);
        dictData.setDictCode(dto.getDictCode());
        dictData.setItemValue(dto.getItemValue());
        dictData.setItemLabel(dto.getItemLabel());
        dictData.setSort(dto.getSort() != null ? dto.getSort() : 0);
        dictData.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        dictData.setRemark(dto.getRemark());
        sysDictDataMapper.insert(dictData);
        return dictData.getId();
    }

    @Override
    public void updateDictData(DictDataSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "字典项ID不能为空");
        }

        SysDictData existing = sysDictDataMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 检查字典值是否已存在（排除自身）
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getTenantId, existing.getTenantId())
                .eq(SysDictData::getDictCode, existing.getDictCode())
                .eq(SysDictData::getItemValue, dto.getItemValue())
                .ne(SysDictData::getId, dto.getId());
        if (sysDictDataMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "字典值已存在");
        }

        SysDictData dictData = new SysDictData();
        dictData.setId(dto.getId());
        dictData.setItemValue(dto.getItemValue());
        dictData.setItemLabel(dto.getItemLabel());
        dictData.setSort(dto.getSort());
        dictData.setStatus(dto.getStatus());
        dictData.setRemark(dto.getRemark());
        sysDictDataMapper.updateById(dictData);
    }

    @Override
    public void deleteDictData(Long id) {
        sysDictDataMapper.deleteById(id);
    }

    @Override
    public void updateStatus(UpdateStatusDto dto) {
        SysDictData dictData = new SysDictData();
        dictData.setId(dto.getId());
        dictData.setStatus(dto.getStatus());
        sysDictDataMapper.updateById(dictData);
    }

    @Override
    public List<DictDataVO> getDictDataByCode(String dictCode) {
        Long tenantId = UserContextHolder.getTenantId();
        if (tenantId == null) {
            tenantId = 0L;
        }

        // 查询所有符合条件的字典项（租户+全局）
        List<SysDictData> allItems = sysDictDataMapper.selectByDictCodeWithTenant(dictCode, tenantId);

        // 租户优先去重：同一itemValue只保留租户的
        List<DictDataVO> result = systemConverter.toDictDataVOList(allItems).stream()
                .collect(Collectors.groupingBy(DictDataVO::getItemValue))
                .values().stream()
                .map(list -> list.stream()
                        .max(Comparator.comparingLong(DictDataVO::getTenantId))
                        .orElse(null))
                .filter(item -> item != null)
                .sorted(Comparator.comparingInt(DictDataVO::getSort))
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public List<DictDataVO> getDictDataByDictCode(String dictCode, Long tenantId) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictCode, dictCode);
        // 按租户ID查询，如果传入tenantId则精确匹配
        if (tenantId != null) {
            wrapper.eq(SysDictData::getTenantId, tenantId);
        }
        wrapper.orderByAsc(SysDictData::getSort);
        return systemConverter.toDictDataVOList(sysDictDataMapper.selectList(wrapper));
    }

    @Override
    public void batchSaveDictData(DictDataBatchSaveDTO dto) {
        // 获取字典类型的租户ID（增加tenantId条件区分租户）
        LambdaQueryWrapper<SysDictType> typeWrapper = new LambdaQueryWrapper<>();
        typeWrapper.eq(SysDictType::getDictCode, dto.getDictCode());
        if (dto.getTenantId() != null) {
            typeWrapper.eq(SysDictType::getTenantId, dto.getTenantId());
        }
        SysDictType dictType = sysDictTypeMapper.selectOne(typeWrapper);
        if (dictType == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "字典类型不存在");
        }
        Long tenantId = dictType.getTenantId();

        // 删除
        if (dto.getDeleteIds() != null && !dto.getDeleteIds().isEmpty()) {
            sysDictDataMapper.deleteBatchIds(dto.getDeleteIds());
        }

        // 新增和修改
        if (dto.getItems() != null) {
            int sort = 0;
            for (DictDataBatchSaveDTO.DictDataItemDTO item : dto.getItems()) {
                SysDictData data = new SysDictData();
                data.setTenantId(tenantId);
                data.setDictCode(dto.getDictCode());
                data.setItemLabel(item.getItemLabel());
                data.setItemValue(item.getItemValue());
                data.setStatus(item.getStatus() != null ? item.getStatus() : 1);
                data.setRemark(item.getRemark());
                data.setSort(sort++);

                if (item.getId() != null) {
                    data.setId(item.getId());
                    sysDictDataMapper.updateById(data);
                } else {
                    sysDictDataMapper.insert(data);
                }
            }
        }
    }

    @Override
    public int countByDictCode(String dictCode) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictCode, dictCode);
        return sysDictDataMapper.selectCount(wrapper).intValue();
    }
}
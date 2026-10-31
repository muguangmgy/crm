package com.platform.server.system.dept.service.impl;

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
import com.platform.server.system.dept.dto.DeptQueryDTO;
import com.platform.server.system.dept.dto.DeptSaveDTO;
import com.platform.server.system.dept.mapper.SysDeptMapper;
import com.platform.server.system.dept.po.SysDept;
import com.platform.server.system.dept.service.SysDeptService;
import com.platform.server.system.dept.vo.DeptVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl implements SysDeptService {

    private final SysDeptMapper sysDeptMapper;
    private final SystemConverter systemConverter;

    @Override
    public PageVO<DeptVO> getDeptPage(DeptQueryDTO query) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        if (query.getTenantId() != null) {
            wrapper.eq(SysDept::getTenantId, query.getTenantId());
        }
        if (query.getDeptName() != null) {
            wrapper.like(SysDept::getDeptName, query.getDeptName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysDept::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(SysDept::getSort);

        Page<SysDept> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SysDept> result = sysDeptMapper.selectPage(page, wrapper);

        List<DeptVO> list = systemConverter.toDeptVOList(result.getRecords());
        return PageVO.of(list, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public List<DeptVO> getDeptTree() {
        List<DeptVO> allDepts = sysDeptMapper.selectDeptTree();
        Map<Long, List<DeptVO>> deptMap = allDepts.stream()
                .collect(Collectors.groupingBy(DeptVO::getParentId));
        // 从顶级部门（parentId=0）开始构建树
        return buildTreeByMap(deptMap, 0L);
    }

    private List<DeptVO> buildTreeByMap(Map<Long, List<DeptVO>> deptMap, Long parentId) {
        List<DeptVO> children = deptMap.getOrDefault(parentId, new ArrayList<>());
        for (DeptVO child : children) {
            child.setChildren(buildTreeByMap(deptMap, child.getId()));
        }
        return children;
    }

    @Override
    public DeptVO getDeptById(Long id) {
        return sysDeptMapper.selectDeptById(id);
    }

    @Override
    public Long saveDept(DeptSaveDTO dto) {
        boolean isPlatformRole = UserContextHolder.isPlatformRole();

        // 非平台用户不能创建顶级部门
        if (!isPlatformRole && (dto.getParentId() == null || dto.getParentId() == 0L)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                    "非平台角色用户不能创建顶级部门");
        }

        SysDept dept = new SysDept();
        Long tenantId;
        String parentPath;

        if (dto.getParentId() != null && dto.getParentId() != 0L) {
            // 非顶级部门，继承上级部门的租户ID
            SysDept parentDept = sysDeptMapper.selectById(dto.getParentId());
            if (parentDept == null) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "上级部门不存在");
            }
            tenantId = parentDept.getTenantId();
            parentPath = parentDept.getDeptPath();
        } else {
            // 顶级部门
            parentPath = "/0";
            if (isPlatformRole && dto.getTenantId() != null) {
                // 平台用户可选择租户
                tenantId = dto.getTenantId();
            } else {
                // 非平台用户使用当前用户的租户ID
                tenantId = UserContextHolder.getTenantId();
                if (tenantId == null) {
                    tenantId = SecurityConstants.DEFAULT_TENANT_ID;
                }
            }
        }

        dept.setTenantId(tenantId);
        dept.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        dept.setDeptName(dto.getDeptName());
        dept.setLeaderId(dto.getLeaderId());
        dept.setSort(dto.getSort() != null ? dto.getSort() : 0);
        dept.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        // 先插入获取ID
        sysDeptMapper.insert(dept);
        // 用自身ID构建deptPath
        dept.setDeptPath(parentPath + "/" + dept.getId());
        sysDeptMapper.updateById(dept);
        return dept.getId();
    }

    @Override
    public void updateDept(DeptSaveDTO dto) {
        // 构建deptPath：父路径 + 当前部门ID
        String parentPath;
        if (dto.getParentId() != null && dto.getParentId() != 0L) {
            SysDept parentDept = sysDeptMapper.selectById(dto.getParentId());
            parentPath = parentDept != null ? parentDept.getDeptPath() : "/0";
        } else {
            parentPath = "/0";
        }

        SysDept dept = new SysDept();
        dept.setId(dto.getId());
        dept.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        dept.setDeptName(dto.getDeptName());
        dept.setLeaderId(dto.getLeaderId());
        dept.setSort(dto.getSort());
        dept.setStatus(dto.getStatus());
        dept.setDeptPath(parentPath + "/" + dto.getId());
        sysDeptMapper.updateById(dept);
    }

    @Override
    public void deleteDept(Long id) {
        sysDeptMapper.deleteById(id);
    }

    @Override
    public void updateStatus(UpdateStatusDto dto) {
        SysDept dept = new SysDept();
        dept.setId(dto.getId());
        dept.setStatus(dto.getStatus());
        sysDeptMapper.updateById(dept);
    }
}
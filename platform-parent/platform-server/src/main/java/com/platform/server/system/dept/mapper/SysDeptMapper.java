package com.platform.server.system.dept.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.server.system.dept.po.SysDept;
import com.platform.server.system.dept.vo.DeptVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 多表关联查询：部门树（关联用户表获取负责人姓名）
     */
    List<DeptVO> selectDeptTree();

    /**
     * 多表关联查询：部门详情（关联用户表获取负责人姓名）
     */
    DeptVO selectDeptById(@Param("id") Long id);
}
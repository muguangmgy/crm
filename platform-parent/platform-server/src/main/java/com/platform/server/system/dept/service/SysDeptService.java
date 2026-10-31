package com.platform.server.system.dept.service;

import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.dept.dto.DeptQueryDTO;
import com.platform.server.system.dept.dto.DeptSaveDTO;
import com.platform.server.system.dept.vo.DeptVO;

import java.util.List;

public interface SysDeptService {

    PageVO<DeptVO> getDeptPage(DeptQueryDTO query);

    List<DeptVO> getDeptTree();

    DeptVO getDeptById(Long id);

    Long saveDept(DeptSaveDTO dto);

    void updateDept(DeptSaveDTO dto);

    void deleteDept(Long id);

    void updateStatus(UpdateStatusDto dto);
}
package com.platform.server.system.dicttype.service;

import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.dicttype.dto.DictTypeQueryDTO;
import com.platform.server.system.dicttype.dto.DictTypeSaveDTO;
import com.platform.server.system.dicttype.vo.DictTypeTreeVO;
import com.platform.server.system.dicttype.vo.DictTypeVO;

import java.util.List;

public interface SysDictTypeService {

    PageVO<DictTypeVO> getDictTypePage(DictTypeQueryDTO query);

    DictTypeVO getDictTypeById(Long id);

    Long saveDictType(DictTypeSaveDTO dto);

    void updateDictType(DictTypeSaveDTO dto);

    void deleteDictType(Long id);

    void updateStatus(UpdateStatusDto dto);

    List<DictTypeVO> getDictTypeOptions();

    /**
     * 获取字典类型树结构
     * 平台用户：全局 + 所有租户字典
     * 普通用户：全局 + 当前租户字典
     */
    List<DictTypeTreeVO> getDictTypeTree();

    /**
     * 删除字典类型（校验子节点和字典项）
     */
    void deleteDictTypeWithCheck(Long id);
}
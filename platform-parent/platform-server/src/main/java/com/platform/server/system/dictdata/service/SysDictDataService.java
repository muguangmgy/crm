package com.platform.server.system.dictdata.service;

import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.dictdata.dto.DictDataBatchSaveDTO;
import com.platform.server.system.dictdata.dto.DictDataQueryDTO;
import com.platform.server.system.dictdata.dto.DictDataSaveDTO;
import com.platform.server.system.dictdata.vo.DictDataVO;

import java.util.List;

public interface SysDictDataService {

    PageVO<DictDataVO> getDictDataPage(DictDataQueryDTO query);

    DictDataVO getDictDataById(Long id);

    Long saveDictData(DictDataSaveDTO dto);

    void updateDictData(DictDataSaveDTO dto);

    void deleteDictData(Long id);

    void updateStatus(UpdateStatusDto dto);

    /**
     * 根据字典编码获取字典项列表（租户优先）
     */
    List<DictDataVO> getDictDataByCode(String dictCode);

    /**
     * 根据字典编码获取字典项列表（用于右侧表格）
     */
    List<DictDataVO> getDictDataByDictCode(String dictCode, Long tenantId);

    /**
     * 批量保存字典项（新增、修改、删除）
     */
    void batchSaveDictData(DictDataBatchSaveDTO dto);

    /**
     * 统计字典类型下的字典项数量
     */
    int countByDictCode(String dictCode);
}
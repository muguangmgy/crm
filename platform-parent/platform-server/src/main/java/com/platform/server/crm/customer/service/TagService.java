package com.platform.server.crm.customer.service;

import com.platform.server.crm.customer.dto.TagSaveDTO;
import com.platform.server.crm.customer.vo.TagVO;

import java.util.List;

public interface TagService {

    /** 获取所有标签 */
    List<TagVO> getAllTags();

    /** 保存标签 */
    Long saveTag(TagSaveDTO dto);

    /** 删除标签 */
    void deleteTag(Long id);
}
package com.platform.server.crm.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.ResultCode;
import com.platform.common.exception.BusinessException;
import com.platform.server.crm.common.converter.CRMConverter;
import com.platform.server.crm.customer.dto.TagSaveDTO;
import com.platform.server.crm.customer.mapper.TagMapper;
import com.platform.server.crm.customer.mapper.TagRelMapper;
import com.platform.server.crm.customer.po.CrmCustomerTag;
import com.platform.server.crm.customer.service.TagService;
import com.platform.server.crm.customer.vo.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final TagRelMapper tagRelMapper;
    private final CRMConverter crmConverter;

    @Override
    public List<TagVO> getAllTags() {
        LambdaQueryWrapper<CrmCustomerTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmCustomerTag::getTenantId, UserContextHolder.getTenantId());
        wrapper.eq(CrmCustomerTag::getStatus, 1);
        wrapper.orderByAsc(CrmCustomerTag::getSort);
        List<CrmCustomerTag> tags = tagMapper.selectList(wrapper);
        return crmConverter.toTagVOList(tags);
    }

    @Override
    public Long saveTag(TagSaveDTO dto) {
        CrmCustomerTag tag;
        boolean isNew = dto.getId() == null;

        if (isNew) {
            tag = new CrmCustomerTag();
            tag.setTenantId(UserContextHolder.getTenantId());
        } else {
            tag = tagMapper.selectById(dto.getId());
            if (tag == null) {
                throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "标签不存在");
            }
        }

        tag.setTagName(dto.getTagName());
        tag.setTagColor(dto.getTagColor());
        tag.setSort(dto.getSort() != null ? dto.getSort() : 0);
        tag.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        if (isNew) {
            tagMapper.insert(tag);
        } else {
            tagMapper.updateById(tag);
        }

        return tag.getId();
    }

    @Override
    public void deleteTag(Long id) {
        // 删除标签
        tagMapper.deleteById(id);
        // 删除所有关联
        LambdaQueryWrapper<CrmCustomerTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmCustomerTag::getId, id);
        tagMapper.delete(wrapper);
    }
}
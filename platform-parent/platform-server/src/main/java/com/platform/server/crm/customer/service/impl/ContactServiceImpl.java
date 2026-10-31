package com.platform.server.crm.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.ResultCode;
import com.platform.common.exception.BusinessException;
import com.platform.common.enums.StatusEnum;
import com.platform.server.crm.common.converter.CRMConverter;
import com.platform.server.crm.customer.dto.ContactSaveDTO;
import com.platform.server.crm.customer.mapper.ContactMapper;
import com.platform.server.crm.customer.po.CrmCustomerContact;
import com.platform.server.crm.customer.service.ContactService;
import com.platform.server.crm.customer.vo.ContactVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactMapper contactMapper;
    private final CRMConverter crmConverter;

    @Override
    public List<ContactVO> getContactsByCustomerId(Long customerId) {
        List<CrmCustomerContact> contacts = contactMapper.selectByCustomerId(customerId);
        return crmConverter.toContactVOList(contacts);
    }

    @Override
    @Transactional
    public Long saveContact(ContactSaveDTO dto) {
        CrmCustomerContact contact;
        boolean isNew = dto.getId() == null;

        if (isNew) {
            contact = new CrmCustomerContact();
            contact.setTenantId(UserContextHolder.getTenantId());
            contact.setCustomerId(dto.getCustomerId());
        } else {
            contact = contactMapper.selectById(dto.getId());
            if (contact == null) {
                throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "联系人不存在");
            }
        }

        contact.setContactName(dto.getContactName());
        contact.setGender(dto.getGender());
        contact.setPosition(dto.getPosition());
        contact.setDepartment(dto.getDepartment());
        contact.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : 0);
        contact.setPhone(dto.getPhone());
        contact.setMobile(dto.getMobile());
        contact.setEmail(dto.getEmail());
        contact.setWechat(dto.getWechat());
        contact.setRemark(dto.getRemark());

        if (isNew) {
            contactMapper.insert(contact);
        } else {
            contactMapper.updateById(contact);
        }

        // 如果设置为主要联系人，清除其他主要联系人
        if (contact.getIsPrimary() == 1) {
            setPrimaryContact(dto.getCustomerId(), contact.getId());
        }

        return contact.getId();
    }

    @Override
    public void deleteContact(Long id) {
        contactMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void setPrimaryContact(Long customerId, Long contactId) {
        // 清除所有主要联系人
        LambdaUpdateWrapper<CrmCustomerContact> clearWrapper = new LambdaUpdateWrapper<>();
        clearWrapper.eq(CrmCustomerContact::getCustomerId, customerId);
        clearWrapper.set(CrmCustomerContact::getIsPrimary, 0);
        contactMapper.update(null, clearWrapper);

        // 设置指定联系人为主要
        LambdaUpdateWrapper<CrmCustomerContact> setWrapper = new LambdaUpdateWrapper<>();
        setWrapper.eq(CrmCustomerContact::getId, contactId);
        setWrapper.set(CrmCustomerContact::getIsPrimary, 1);
        contactMapper.update(null, setWrapper);
    }
}
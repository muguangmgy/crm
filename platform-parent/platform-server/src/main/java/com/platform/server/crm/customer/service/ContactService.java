package com.platform.server.crm.customer.service;

import com.platform.server.crm.customer.dto.ContactSaveDTO;
import com.platform.server.crm.customer.vo.ContactVO;

import java.util.List;

public interface ContactService {

    /** 获取客户联系人列表 */
    List<ContactVO> getContactsByCustomerId(Long customerId);

    /** 保存联系人 */
    Long saveContact(ContactSaveDTO dto);

    /** 删除联系人 */
    void deleteContact(Long id);

    /** 设置主要联系人 */
    void setPrimaryContact(Long customerId, Long contactId);
}
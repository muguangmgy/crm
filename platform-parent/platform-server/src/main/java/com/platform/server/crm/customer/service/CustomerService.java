package com.platform.server.crm.customer.service;

import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.customer.dto.*;
import com.platform.server.crm.customer.vo.*;

import java.util.List;

public interface CustomerService {

    /** 私有池客户分页查询 */
    PageVO<CustomerVO> getPrivatePoolPage(CustomerQueryDTO query);

    /** 公海池客户分页查询 */
    PageVO<CustomerVO> getPublicPoolPage(CustomerQueryDTO query);

    /** 客户详情 */
    CustomerDetailVO getCustomerDetail(Long id);

    /** 保存客户（新增/修改） */
    Long saveCustomer(CustomerSaveDTO dto);

    /** 删除客户 */
    void deleteCustomer(Long id);

    /** 从公海池领取客户 */
    void takeFromPool(Long customerId);

    /** 释放到公海池 */
    void releaseToPool(Long customerId);

    /** 转移客户 */
    void transferCustomer(PoolTransferDTO dto);

    /** 获取客户标签列表 */
    List<TagVO> getCustomerTags(Long customerId);

    /** 绑定客户标签 */
    void bindTags(Long customerId, List<Long> tagIds);

    /** 获取所有标签（租户下） */
    List<TagVO> getAllTags();
}
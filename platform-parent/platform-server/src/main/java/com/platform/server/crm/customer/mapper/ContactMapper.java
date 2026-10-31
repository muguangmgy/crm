package com.platform.server.crm.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.server.crm.customer.po.CrmCustomerContact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContactMapper extends BaseMapper<CrmCustomerContact> {

    /** 根据客户ID查询联系人列表 */
    List<CrmCustomerContact> selectByCustomerId(@Param("customerId") Long customerId);
}
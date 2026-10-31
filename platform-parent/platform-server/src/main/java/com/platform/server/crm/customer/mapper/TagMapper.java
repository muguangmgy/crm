package com.platform.server.crm.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.server.crm.customer.po.CrmCustomerTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<CrmCustomerTag> {

    /** 查询租户下所有启用标签 */
    List<CrmCustomerTag> selectByTenantId(@Param("tenantId") Long tenantId);

    /** 根据客户ID查询关联标签 */
    List<CrmCustomerTag> selectByCustomerId(@Param("customerId") Long customerId);
}
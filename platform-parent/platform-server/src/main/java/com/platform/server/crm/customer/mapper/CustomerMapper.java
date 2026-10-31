package com.platform.server.crm.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.annotation.DataScope;
import com.platform.server.crm.customer.dto.CustomerQueryDTO;
import com.platform.server.crm.customer.po.CrmCustomer;
import com.platform.server.crm.customer.vo.CustomerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CustomerMapper extends BaseMapper<CrmCustomer> {

    /**
     * 私有池客户分页查询
     */
    @DataScope(deptAlias = "c", userAlias = "c")
    IPage<CustomerVO> selectPrivatePoolPage(Page<CrmCustomer> page, @Param("query") CustomerQueryDTO query);

    /**
     * 公海池客户分页查询
     */
    IPage<CustomerVO> selectPublicPoolPage(Page<CrmCustomer> page, @Param("query") CustomerQueryDTO query);

    /**
     * 客户详情查询
     */
    @DataScope(deptAlias = "c", userAlias = "c")
    CustomerVO selectCustomerById(@Param("id") Long id);

    /**
     * 统计负责人私有池客户数量
     */
    int countByOwner(@Param("ownerId") Long ownerId);

    /**
     * 查询需要自动释放的客户（超过指定天数无跟进）
     */
    IPage<CrmCustomer> selectNeedRelease(Page<CrmCustomer> page, @Param("noFollowDays") int noFollowDays);
}
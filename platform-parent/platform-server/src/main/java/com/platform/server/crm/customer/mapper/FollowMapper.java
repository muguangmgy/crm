package com.platform.server.crm.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.server.crm.customer.po.CrmCustomerFollow;
import com.platform.server.crm.customer.vo.FollowVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FollowMapper extends BaseMapper<CrmCustomerFollow> {

    /** 根据客户ID查询跟进记录 */
    List<FollowVO> selectByCustomerId(@Param("customerId") Long customerId);

    /** 查询客户最后跟进时间 */
    CrmCustomerFollow selectLastFollow(@Param("customerId") Long customerId);
}
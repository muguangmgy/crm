package com.platform.server.crm.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.server.crm.customer.po.CrmCustomerTagRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagRelMapper extends BaseMapper<CrmCustomerTagRel> {

    /** 删除客户的所有标签关联 */
    int deleteByCustomerId(@Param("customerId") Long customerId);

    /** 批量插入标签关联 */
    int batchInsert(@Param("list") List<CrmCustomerTagRel> list);

    /** 查询客户的标签ID列表 */
    List<Long> selectTagIdsByCustomerId(@Param("customerId") Long customerId);
}
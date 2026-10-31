package com.platform.server.crm.opportunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.common.annotation.DataScope;
import com.platform.server.crm.opportunity.dto.OpportunityQueryDTO;
import com.platform.server.crm.opportunity.po.CrmOpportunity;
import com.platform.server.crm.opportunity.vo.OpportunityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商机Mapper
 */
@Mapper
public interface CrmOpportunityMapper extends BaseMapper<CrmOpportunity> {

    /**
     * 分页查询商机列表（关联客户、联系人、负责人名称）
     */
    @DataScope(deptAlias = "o", userAlias = "o")
    List<OpportunityVO> selectOpportunityPage(@Param("query") OpportunityQueryDTO query);

    /**
     * 查询商机详情（关联客户、联系人、负责人名称）
     */
    @DataScope(deptAlias = "o", userAlias = "o")
    OpportunityVO selectOpportunityById(@Param("id") Long id);
}
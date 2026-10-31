package com.platform.server.crm.lead.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.annotation.DataScope;
import com.platform.server.crm.lead.dto.LeadQueryDTO;
import com.platform.server.crm.lead.po.CrmLead;
import com.platform.server.crm.lead.vo.LeadVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CrmLeadMapper extends BaseMapper<CrmLead> {

    /**
     * 线索分页查询
     */
    @DataScope(deptAlias = "l", userAlias = "l")
    IPage<LeadVO> selectLeadPage(Page<CrmLead> page, @Param("query") LeadQueryDTO query);

    /**
     * 线索详情查询（含关联信息）
     */
    @DataScope(deptAlias = "l", userAlias = "l")
    LeadVO selectLeadById(@Param("id") Long id);
}
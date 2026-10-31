package com.platform.server.crm.quote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.annotation.DataScope;
import com.platform.server.crm.quote.dto.QuoteQueryDTO;
import com.platform.server.crm.quote.po.CrmQuote;
import com.platform.server.crm.quote.vo.QuoteVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuoteMapper extends BaseMapper<CrmQuote> {

    /**
     * 报价单分页查询（关联客户、商机、负责人名称）
     */
    @DataScope(deptAlias = "q", userAlias = "q")
    IPage<QuoteVO> selectQuotePage(Page<CrmQuote> page, @Param("query") QuoteQueryDTO query);

    /**
     * 报价单详情查询（关联客户、商机、负责人名称）
     */
    @DataScope(deptAlias = "q", userAlias = "q")
    QuoteVO selectQuoteById(@Param("id") Long id);
}

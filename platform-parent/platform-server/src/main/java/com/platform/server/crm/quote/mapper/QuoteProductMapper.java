package com.platform.server.crm.quote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.server.crm.quote.po.CrmQuoteProduct;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuoteProductMapper extends BaseMapper<CrmQuoteProduct> {
}
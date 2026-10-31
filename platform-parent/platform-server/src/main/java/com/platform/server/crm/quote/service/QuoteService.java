package com.platform.server.crm.quote.service;

import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.quote.dto.QuoteQueryDTO;
import com.platform.server.crm.quote.dto.QuoteSaveDTO;
import com.platform.server.crm.quote.vo.QuoteVO;

public interface QuoteService {

    /** 报价单分页查询 */
    PageVO<QuoteVO> getQuotePage(QuoteQueryDTO query);

    /** 报价单详情 */
    QuoteVO getQuoteById(Long id);

    /** 保存报价单（新增/修改） */
    Long saveQuote(QuoteSaveDTO dto);

    /** 删除报价单 */
    void deleteQuote(Long id);

    /** 发送报价单 */
    void sendQuote(Long id);

    /** 报价单中标 */
    void acceptQuote(Long id);

    /** 报价单拒绝 */
    void rejectQuote(Long id);
}
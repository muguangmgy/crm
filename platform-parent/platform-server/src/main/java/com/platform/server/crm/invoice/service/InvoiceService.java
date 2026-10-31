package com.platform.server.crm.invoice.service;

import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.invoice.dto.InvoiceQueryDTO;
import com.platform.server.crm.invoice.dto.InvoiceSaveDTO;
import com.platform.server.crm.invoice.vo.InvoiceVO;

public interface InvoiceService {

    /** 发票分页查询 */
    PageVO<InvoiceVO> getInvoicePage(InvoiceQueryDTO query);

    /** 发票详情 */
    InvoiceVO getInvoiceById(Long id);

    /** 保存发票（新增/修改） */
    Long saveInvoice(InvoiceSaveDTO dto);

    /** 删除发票 */
    void deleteInvoice(Long id);

    /** 作废发票 */
    void cancelInvoice(Long id);
}
package com.platform.server.crm.payment.service;

import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.payment.dto.PaymentRecordQueryDTO;
import com.platform.server.crm.payment.dto.PaymentRecordSaveDTO;
import com.platform.server.crm.payment.vo.PaymentRecordVO;

import java.util.List;

public interface PaymentRecordService {

    /**
     * 分页查询收款记录
     */
    PageVO<PaymentRecordVO> getRecordPage(PaymentRecordQueryDTO query);

    /**
     * 根据ID查询收款记录
     */
    PaymentRecordVO getRecordById(Long id);

    /**
     * 根据合同ID查询收款记录列表
     */
    List<PaymentRecordVO> getRecordsByContractId(Long contractId);

    /**
     * 保存收款记录
     */
    Long saveRecord(PaymentRecordSaveDTO dto);

    /**
     * 删除收款记录
     */
    void deleteRecord(Long id);

    /**
     * 确认收款（更新合同已收金额）
     */
    void confirmPayment(Long id);

    /**
     * 取消确认
     */
    void unconfirmPayment(Long id);
}
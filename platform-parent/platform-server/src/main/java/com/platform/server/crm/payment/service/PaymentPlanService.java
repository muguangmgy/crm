package com.platform.server.crm.payment.service;

import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.payment.dto.PaymentPlanQueryDTO;
import com.platform.server.crm.payment.dto.PaymentPlanSaveDTO;
import com.platform.server.crm.payment.vo.PaymentPlanVO;

import java.util.List;

public interface PaymentPlanService {

    /**
     * 分页查询收款计划
     */
    PageVO<PaymentPlanVO> getPlanPage(PaymentPlanQueryDTO query);

    /**
     * 根据ID查询收款计划
     */
    PaymentPlanVO getPlanById(Long id);

    /**
     * 根据合同ID查询收款计划列表
     */
    List<PaymentPlanVO> getPlansByContractId(Long contractId);

    /**
     * 保存收款计划
     */
    Long savePlan(PaymentPlanSaveDTO dto);

    /**
     * 删除收款计划
     */
    void deletePlan(Long id);

    /**
     * 更新收款计划已收金额和状态
     */
    void updateReceivedAmount(Long planId, java.math.BigDecimal amount);
}
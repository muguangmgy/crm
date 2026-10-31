package com.platform.server.crm.common.converter;

import com.platform.server.crm.contract.po.CrmContract;
import com.platform.server.crm.contract.vo.ContractVO;
import com.platform.server.crm.customer.po.*;
import com.platform.server.crm.customer.vo.*;
import com.platform.server.crm.invoice.po.CrmInvoice;
import com.platform.server.crm.invoice.vo.InvoiceVO;
import com.platform.server.crm.lead.po.CrmLead;
import com.platform.server.crm.lead.vo.LeadVO;
import com.platform.server.crm.opportunity.po.CrmOpportunity;
import com.platform.server.crm.opportunity.po.CrmOpportunityStage;
import com.platform.server.crm.opportunity.vo.OpportunityVO;
import com.platform.server.crm.opportunity.vo.StageVO;
import com.platform.server.crm.payment.po.CrmPaymentPlan;
import com.platform.server.crm.payment.po.CrmPaymentRecord;
import com.platform.server.crm.payment.vo.PaymentPlanVO;
import com.platform.server.crm.payment.vo.PaymentRecordVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * CRM模块统一转换器
 */
@Mapper(componentModel = "spring")
public interface CRMConverter {

    // ========== 客户模块 ==========
    CustomerVO toCustomerVO(CrmCustomer customer);

    List<CustomerVO> toCustomerVOList(List<CrmCustomer> customers);

    // ========== 联系人模块 ==========
    ContactVO toContactVO(CrmCustomerContact contact);

    List<ContactVO> toContactVOList(List<CrmCustomerContact> contacts);

    // ========== 标签模块 ==========
    TagVO toTagVO(CrmCustomerTag tag);

    List<TagVO> toTagVOList(List<CrmCustomerTag> tags);

    // ========== 跟进记录模块 ==========
    FollowVO toFollowVO(CrmCustomerFollow follow);

    List<FollowVO> toFollowVOList(List<CrmCustomerFollow> follows);

    // ========== 线索模块 ==========
    LeadVO toLeadVO(CrmLead lead);

    List<LeadVO> toLeadVOList(List<CrmLead> leads);

    // ========== 商机模块 ==========
    OpportunityVO toOpportunityVO(CrmOpportunity opportunity);

    List<OpportunityVO> toOpportunityVOList(List<CrmOpportunity> opportunities);

    StageVO toStageVO(CrmOpportunityStage stage);

    List<StageVO> toStageVOList(List<CrmOpportunityStage> stages);

    // ========== 发票模块 ==========
    InvoiceVO toInvoiceVO(CrmInvoice invoice);

    List<InvoiceVO> toInvoiceVOList(List<CrmInvoice> invoices);

    // ========== 合同模块 ==========
    ContractVO toContractVO(CrmContract contract);

    List<ContractVO> toContractVOList(List<CrmContract> contracts);

    // ========== 收款计划模块 ==========
    PaymentPlanVO toPaymentPlanVO(CrmPaymentPlan plan);

    List<PaymentPlanVO> toPaymentPlanVOList(List<CrmPaymentPlan> plans);

    // ========== 收款记录模块 ==========
    PaymentRecordVO toPaymentRecordVO(CrmPaymentRecord record);

    List<PaymentRecordVO> toPaymentRecordVOList(List<CrmPaymentRecord> records);
}
package com.platform.server.crm.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.context.UserContextHolder;
import com.platform.common.exception.BusinessException;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.contract.mapper.CrmContractMapper;
import com.platform.server.crm.contract.po.CrmContract;
import com.platform.server.crm.payment.dto.PaymentPlanQueryDTO;
import com.platform.server.crm.payment.dto.PaymentPlanSaveDTO;
import com.platform.server.crm.payment.mapper.PaymentPlanMapper;
import com.platform.server.crm.payment.po.CrmPaymentPlan;
import com.platform.server.crm.payment.service.PaymentPlanService;
import com.platform.server.crm.payment.vo.PaymentPlanVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentPlanServiceImpl implements PaymentPlanService {

    private final PaymentPlanMapper paymentPlanMapper;
    private final CrmContractMapper contractMapper;

    /** 收款状态：未收 */
    private static final int RECEIVED_STATUS_NONE = 0;
    /** 收款状态：部分收款 */
    private static final int RECEIVED_STATUS_PARTIAL = 1;
    /** 收款状态：已收完 */
    private static final int RECEIVED_STATUS_FULL = 2;

    @Override
    public PageVO<PaymentPlanVO> getPlanPage(PaymentPlanQueryDTO query) {
        Page<CrmPaymentPlan> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<CrmPaymentPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmPaymentPlan::getTenantId, UserContextHolder.getTenantId());

        if (query.getContractId() != null) {
            wrapper.eq(CrmPaymentPlan::getContractId, query.getContractId());
        }
        if (query.getPlanNo() != null) {
            wrapper.like(CrmPaymentPlan::getPlanNo, query.getPlanNo());
        }
        if (query.getReceivedStatus() != null) {
            wrapper.eq(CrmPaymentPlan::getReceivedStatus, query.getReceivedStatus());
        }
        if (query.getPlanDateStart() != null) {
            wrapper.ge(CrmPaymentPlan::getPlanDate, query.getPlanDateStart());
        }
        if (query.getPlanDateEnd() != null) {
            wrapper.le(CrmPaymentPlan::getPlanDate, query.getPlanDateEnd());
        }

        wrapper.orderByAsc(CrmPaymentPlan::getPlanDate);

        Page<CrmPaymentPlan> result = paymentPlanMapper.selectPage(page, wrapper);

        List<PaymentPlanVO> list = result.getRecords().stream()
                .map(this::toPaymentPlanVO)
                .collect(Collectors.toList());

        return PageVO.of(list, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PaymentPlanVO getPlanById(Long id) {
        CrmPaymentPlan plan = paymentPlanMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException("收款计划不存在");
        }
        return toPaymentPlanVO(plan);
    }

    @Override
    public List<PaymentPlanVO> getPlansByContractId(Long contractId) {
        List<CrmPaymentPlan> plans = paymentPlanMapper.selectList(
                new LambdaQueryWrapper<CrmPaymentPlan>()
                        .eq(CrmPaymentPlan::getTenantId, UserContextHolder.getTenantId())
                        .eq(CrmPaymentPlan::getContractId, contractId)
                        .orderByAsc(CrmPaymentPlan::getPlanDate)
        );
        return plans.stream()
                .map(this::toPaymentPlanVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long savePlan(PaymentPlanSaveDTO dto) {
        CrmPaymentPlan plan;

        if (dto.getId() != null) {
            // 修改
            plan = paymentPlanMapper.selectById(dto.getId());
            if (plan == null) {
                throw new BusinessException("收款计划不存在");
            }
        } else {
            // 新增
            plan = new CrmPaymentPlan();
            plan.setTenantId(UserContextHolder.getTenantId());
            plan.setContractId(dto.getContractId());
            plan.setPlanNo(generatePlanNo());
            plan.setReceivedAmount(BigDecimal.ZERO);
            plan.setReceivedStatus(RECEIVED_STATUS_NONE);
        }

        plan.setPlanAmount(dto.getPlanAmount());
        plan.setPlanDate(dto.getPlanDate());
        plan.setRemark(dto.getRemark());

        if (dto.getId() != null) {
            paymentPlanMapper.updateById(plan);
        } else {
            paymentPlanMapper.insert(plan);
        }

        return plan.getId();
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {
        CrmPaymentPlan plan = paymentPlanMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException("收款计划不存在");
        }
        // 已收款的计划不能删除
        if (plan.getReceivedStatus() != RECEIVED_STATUS_NONE) {
            throw new BusinessException("已收款的计划不能删除");
        }
        paymentPlanMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateReceivedAmount(Long planId, BigDecimal amount) {
        CrmPaymentPlan plan = paymentPlanMapper.selectById(planId);
        if (plan == null) {
            return;
        }

        BigDecimal newReceivedAmount = plan.getReceivedAmount().add(amount);
        if (newReceivedAmount.compareTo(BigDecimal.ZERO) < 0) {
            newReceivedAmount = BigDecimal.ZERO;
        }

        plan.setReceivedAmount(newReceivedAmount);

        // 更新收款状态
        if (newReceivedAmount.compareTo(plan.getPlanAmount()) >= 0) {
            plan.setReceivedStatus(RECEIVED_STATUS_FULL);
        } else if (newReceivedAmount.compareTo(BigDecimal.ZERO) > 0) {
            plan.setReceivedStatus(RECEIVED_STATUS_PARTIAL);
        } else {
            plan.setReceivedStatus(RECEIVED_STATUS_NONE);
        }

        paymentPlanMapper.updateById(plan);
    }

    // ===== 私有方法 =====

    private PaymentPlanVO toPaymentPlanVO(CrmPaymentPlan plan) {
        PaymentPlanVO vo = new PaymentPlanVO();
        vo.setId(plan.getId());
        vo.setContractId(plan.getContractId());
        vo.setPlanNo(plan.getPlanNo());
        vo.setPlanAmount(plan.getPlanAmount());
        vo.setPlanDate(plan.getPlanDate());
        vo.setReceivedAmount(plan.getReceivedAmount());
        vo.setReceivedStatus(plan.getReceivedStatus());
        vo.setReceivedStatusName(getReceivedStatusName(plan.getReceivedStatus()));
        vo.setRemark(plan.getRemark());
        vo.setCreateTime(plan.getCreateTime());
        vo.setUpdateTime(plan.getUpdateTime());
        if (plan.getContractId() != null) {
            CrmContract contract = contractMapper.selectById(plan.getContractId());
            if (contract != null) {
                vo.setContractNo(contract.getContractNo());
            }
        }
        return vo;
    }

    private String getReceivedStatusName(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case 0: return "未收款";
            case 1: return "部分收款";
            case 2: return "已收完";
            default: return "";
        }
    }

    private String generatePlanNo() {
        return "PP" + System.currentTimeMillis();
    }
}
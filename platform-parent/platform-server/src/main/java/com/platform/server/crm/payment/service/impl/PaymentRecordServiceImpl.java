package com.platform.server.crm.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.context.UserContextHolder;
import com.platform.common.exception.BusinessException;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.contract.mapper.CrmContractMapper;
import com.platform.server.crm.payment.dto.PaymentRecordQueryDTO;
import com.platform.server.crm.payment.dto.PaymentRecordSaveDTO;
import com.platform.server.crm.payment.mapper.PaymentPlanMapper;
import com.platform.server.crm.payment.mapper.PaymentRecordMapper;
import com.platform.server.crm.payment.po.CrmPaymentPlan;
import com.platform.server.crm.payment.po.CrmPaymentRecord;
import com.platform.server.crm.payment.service.PaymentPlanService;
import com.platform.server.crm.payment.service.PaymentRecordService;
import com.platform.server.crm.payment.vo.PaymentRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.server.crm.contract.po.CrmContract;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentRecordServiceImpl implements PaymentRecordService {

    private final PaymentRecordMapper paymentRecordMapper;
    private final PaymentPlanMapper paymentPlanMapper;
    private final PaymentPlanService paymentPlanService;
    private final CrmContractMapper contractMapper;

    /** 记录状态：已确认 */
    private static final int STATUS_CONFIRMED = 1;
    /** 记录状态：待确认 */
    private static final int STATUS_PENDING = 2;

    @Override
    public PageVO<PaymentRecordVO> getRecordPage(PaymentRecordQueryDTO query) {
        Page<CrmPaymentRecord> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<CrmPaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmPaymentRecord::getTenantId, UserContextHolder.getTenantId());

        if (query.getContractId() != null) {
            wrapper.eq(CrmPaymentRecord::getContractId, query.getContractId());
        }
        if (query.getPlanId() != null) {
            wrapper.eq(CrmPaymentRecord::getPlanId, query.getPlanId());
        }
        if (query.getPaymentNo() != null) {
            wrapper.like(CrmPaymentRecord::getPaymentNo, query.getPaymentNo());
        }
        if (query.getStatus() != null) {
            wrapper.eq(CrmPaymentRecord::getStatus, query.getStatus());
        }
        if (query.getPaymentDateStart() != null) {
            wrapper.ge(CrmPaymentRecord::getPaymentDate, query.getPaymentDateStart());
        }
        if (query.getPaymentDateEnd() != null) {
            wrapper.le(CrmPaymentRecord::getPaymentDate, query.getPaymentDateEnd());
        }
        if (query.getPaymentType() != null) {
            wrapper.eq(CrmPaymentRecord::getPaymentType, query.getPaymentType());
        }

        wrapper.orderByDesc(CrmPaymentRecord::getPaymentDate);

        Page<CrmPaymentRecord> result = paymentRecordMapper.selectPage(page, wrapper);

        List<PaymentRecordVO> list = result.getRecords().stream()
                .map(this::toPaymentRecordVO)
                .collect(Collectors.toList());

        return PageVO.of(list, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PaymentRecordVO getRecordById(Long id) {
        CrmPaymentRecord record = paymentRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("收款记录不存在");
        }
        PaymentRecordVO vo = toPaymentRecordVO(record);
        // 查询关联计划编号
        if (record.getPlanId() != null) {
            CrmPaymentPlan plan = paymentPlanMapper.selectById(record.getPlanId());
            if (plan != null) {
                vo.setPlanNo(plan.getPlanNo());
            }
        }
        return vo;
    }

    @Override
    public List<PaymentRecordVO> getRecordsByContractId(Long contractId) {
        List<CrmPaymentRecord> records = paymentRecordMapper.selectList(
                new LambdaQueryWrapper<CrmPaymentRecord>()
                        .eq(CrmPaymentRecord::getTenantId, UserContextHolder.getTenantId())
                        .eq(CrmPaymentRecord::getContractId, contractId)
                        .orderByDesc(CrmPaymentRecord::getPaymentDate)
        );
        return records.stream()
                .map(this::toPaymentRecordVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long saveRecord(PaymentRecordSaveDTO dto) {
        CrmPaymentRecord record;

        if (dto.getId() != null) {
            // 修改
            record = paymentRecordMapper.selectById(dto.getId());
            if (record == null) {
                throw new BusinessException("收款记录不存在");
            }
            // 已确认的记录不能修改
            if (record.getStatus() == STATUS_CONFIRMED) {
                throw new BusinessException("已确认的收款记录不能修改");
            }
            record.setContractId(dto.getContractId());
            record.setPlanId(dto.getPlanId());
        } else {
            // 新增
            record = new CrmPaymentRecord();
            record.setTenantId(UserContextHolder.getTenantId());
            record.setContractId(dto.getContractId());
            record.setPlanId(dto.getPlanId());
            record.setPaymentNo(generatePaymentNo());
            record.setStatus(STATUS_PENDING);
        }

        validatePaymentAmount(dto.getContractId(), dto.getPlanId(), dto.getPaymentAmount(), dto.getId());

        record.setPaymentAmount(dto.getPaymentAmount());
        record.setPaymentDate(dto.getPaymentDate());
        record.setPaymentType(dto.getPaymentType());
        record.setPayerName(dto.getPayerName());
        record.setPayerAccount(dto.getPayerAccount());
        record.setOurAccount(dto.getOurAccount());
        record.setVoucherUrl(dto.getVoucherUrl());
        record.setRemark(dto.getRemark());

        if (dto.getId() != null) {
            paymentRecordMapper.updateById(record);
        } else {
            paymentRecordMapper.insert(record);
        }

        return record.getId();
    }

    @Override
    @Transactional
    public void deleteRecord(Long id) {
        CrmPaymentRecord record = paymentRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("收款记录不存在");
        }
        // 已确认的记录不能删除
        if (record.getStatus() == STATUS_CONFIRMED) {
            throw new BusinessException("已确认的收款记录不能删除");
        }
        paymentRecordMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void confirmPayment(Long id) {
        CrmPaymentRecord record = paymentRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("收款记录不存在");
        }
        // 只有待确认状态才能确认
        if (record.getStatus() != STATUS_PENDING) {
            throw new BusinessException("只有待确认状态的记录才能确认");
        }

        // 确认前再校验：不含本条待确认占用，避免超收
        validatePaymentAmount(record.getContractId(), record.getPlanId(), record.getPaymentAmount(), record.getId());

        record.setStatus(STATUS_CONFIRMED);
        record.setConfirmTime(LocalDateTime.now());
        record.setConfirmBy(UserContextHolder.getUserId());
        paymentRecordMapper.updateById(record);

        // 更新收款计划已收金额
        if (record.getPlanId() != null) {
            paymentPlanService.updateReceivedAmount(record.getPlanId(), record.getPaymentAmount());
        }

        // 回写合同已收金额
        if (record.getContractId() != null) {
            contractMapper.updateReceivedAmount(record.getContractId(), record.getPaymentAmount());
        }
    }

    @Override
    @Transactional
    public void unconfirmPayment(Long id) {
        CrmPaymentRecord record = paymentRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("收款记录不存在");
        }
        // 只有已确认状态才能取消确认
        if (record.getStatus() != STATUS_CONFIRMED) {
            throw new BusinessException("只有已确认状态的记录才能取消确认");
        }

        record.setStatus(STATUS_PENDING);
        record.setConfirmTime(null);
        record.setConfirmBy(null);
        paymentRecordMapper.updateById(record);

        // 更新收款计划已收金额（减去）
        if (record.getPlanId() != null) {
            paymentPlanService.updateReceivedAmount(record.getPlanId(), record.getPaymentAmount().negate());
        }

        // 回写合同已收金额（减去）
        if (record.getContractId() != null) {
            contractMapper.updateReceivedAmount(record.getContractId(), record.getPaymentAmount().negate());
        }
    }

    // ===== 私有方法 =====

    /**
     * 校验收款金额：必须大于0，且不超过合同/计划待收余额
     * 待收余额 = 合同(或计划)金额 - 已确认金额 - 其他待确认记录金额
     */
    private void validatePaymentAmount(Long contractId, Long planId, BigDecimal paymentAmount, Long excludeRecordId) {
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("收款金额必须大于0");
        }
        if (contractId == null) {
            throw new BusinessException("请选择关联合同");
        }

        CrmContract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        BigDecimal contractAmount = defaultZero(contract.getContractAmount());
        BigDecimal contractReceived = defaultZero(contract.getReceivedAmount());
        BigDecimal contractPending = sumPendingAmount(contractId, null, excludeRecordId);
        BigDecimal contractRemain = contractAmount.subtract(contractReceived).subtract(contractPending);
        if (contractRemain.compareTo(BigDecimal.ZERO) < 0) {
            contractRemain = BigDecimal.ZERO;
        }
        if (paymentAmount.compareTo(contractRemain) > 0) {
            throw new BusinessException("收款金额不能超过合同待收金额 ¥" + contractRemain.toPlainString());
        }

        if (planId != null) {
            CrmPaymentPlan plan = paymentPlanMapper.selectById(planId);
            if (plan == null) {
                throw new BusinessException("收款计划不存在");
            }
            if (!contractId.equals(plan.getContractId())) {
                throw new BusinessException("收款计划与合同不匹配");
            }
            BigDecimal planAmount = defaultZero(plan.getPlanAmount());
            BigDecimal planReceived = defaultZero(plan.getReceivedAmount());
            BigDecimal planPending = sumPendingAmount(contractId, planId, excludeRecordId);
            BigDecimal planRemain = planAmount.subtract(planReceived).subtract(planPending);
            if (planRemain.compareTo(BigDecimal.ZERO) < 0) {
                planRemain = BigDecimal.ZERO;
            }
            if (paymentAmount.compareTo(planRemain) > 0) {
                throw new BusinessException("收款金额不能超过计划待收金额 ¥" + planRemain.toPlainString());
            }
        }
    }

    /** 统计待确认收款金额（可排除当前编辑/确认记录） */
    private BigDecimal sumPendingAmount(Long contractId, Long planId, Long excludeRecordId) {
        LambdaQueryWrapper<CrmPaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmPaymentRecord::getTenantId, UserContextHolder.getTenantId())
                .eq(CrmPaymentRecord::getContractId, contractId)
                .eq(CrmPaymentRecord::getStatus, STATUS_PENDING);
        if (planId != null) {
            wrapper.eq(CrmPaymentRecord::getPlanId, planId);
        }
        if (excludeRecordId != null) {
            wrapper.ne(CrmPaymentRecord::getId, excludeRecordId);
        }
        List<CrmPaymentRecord> pendingList = paymentRecordMapper.selectList(wrapper);
        return pendingList.stream()
                .map(r -> defaultZero(r.getPaymentAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private PaymentRecordVO toPaymentRecordVO(CrmPaymentRecord record) {
        PaymentRecordVO vo = new PaymentRecordVO();
        vo.setId(record.getId());
        vo.setContractId(record.getContractId());
        vo.setPlanId(record.getPlanId());
        vo.setPaymentNo(record.getPaymentNo());
        vo.setPaymentAmount(record.getPaymentAmount());
        vo.setPaymentDate(record.getPaymentDate());
        vo.setPaymentType(record.getPaymentType());
        vo.setPaymentTypeName(getPaymentTypeName(record.getPaymentType()));
        vo.setPayerName(record.getPayerName());
        vo.setPayerAccount(record.getPayerAccount());
        vo.setOurAccount(record.getOurAccount());
        vo.setStatus(record.getStatus());
        vo.setStatusName(getStatusName(record.getStatus()));
        vo.setVoucherUrl(record.getVoucherUrl());
        vo.setConfirmTime(record.getConfirmTime());
        vo.setConfirmBy(record.getConfirmBy());
        vo.setRemark(record.getRemark());
        vo.setCreateTime(record.getCreateTime());
        vo.setUpdateTime(record.getUpdateTime());
        if (record.getContractId() != null) {
            var contract = contractMapper.selectById(record.getContractId());
            if (contract != null) {
                vo.setContractNo(contract.getContractNo());
            }
        }
        if (record.getPlanId() != null) {
            CrmPaymentPlan plan = paymentPlanMapper.selectById(record.getPlanId());
            if (plan != null) {
                vo.setPlanNo(plan.getPlanNo());
            }
        }
        return vo;
    }

    private String getStatusName(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case 1: return "已确认";
            case 2: return "待确认";
            default: return "";
        }
    }

    private String getPaymentTypeName(String paymentType) {
        if (paymentType == null) {
            return "";
        }
        switch (paymentType) {
            case "bank_transfer": return "银行转账";
            case "cash": return "现金";
            case "check": return "支票";
            case "alipay": return "支付宝";
            case "wechat": return "微信支付";
            default: return paymentType;
        }
    }

    private String generatePaymentNo() {
        return "PR" + System.currentTimeMillis();
    }
}
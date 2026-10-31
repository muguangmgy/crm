package com.platform.server.crm.contract.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.exception.BusinessException;
import com.platform.common.context.UserContextHolder;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.common.enums.ContractStatusEnum;
import com.platform.server.crm.contract.dto.ContractQueryDTO;
import com.platform.server.crm.contract.dto.ContractSaveDTO;
import com.platform.server.crm.contract.mapper.CrmContractMapper;
import com.platform.server.crm.contract.po.CrmContract;
import com.platform.server.crm.contract.service.ContractService;
import com.platform.server.crm.contract.vo.ContractVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final CrmContractMapper contractMapper;

    @Override
    public PageVO<ContractVO> getContractPage(ContractQueryDTO query) {
        Page<ContractVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<ContractVO> result = contractMapper.selectContractPage(page, query);

        List<ContractVO> list = result.getRecords();
        list.forEach(this::fillStatusAndTypeName);

        return PageVO.of(list, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public List<ContractVO> getContractOptions(Integer status) {
        List<ContractVO> list = contractMapper.selectContractOptions(status);
        list.forEach(this::fillStatusAndTypeName);
        return list;
    }

    @Override
    public ContractVO getContractById(Long id) {
        ContractVO vo = contractMapper.selectContractById(id);
        if (vo == null) {
            throw new BusinessException("合同不存在");
        }
        fillStatusAndTypeName(vo);
        return vo;
    }

    @Override
    @Transactional
    public Long saveContract(ContractSaveDTO dto) {
        CrmContract contract = new CrmContract();

        if (dto.getId() != null) {
            // 修改
            contract = contractMapper.selectById(dto.getId());
            if (contract == null) {
                throw new BusinessException("合同不存在");
            }
            if (!ContractStatusEnum.DRAFT.getCode().equals(contract.getStatus())) {
                throw new BusinessException("只有草稿状态的合同才能修改");
            }
        } else {
            // 新增
            contract.setTenantId(UserContextHolder.getTenantId());
            contract.setStatus(ContractStatusEnum.DRAFT.getCode());
            contract.setReceivedAmount(java.math.BigDecimal.ZERO);
            contract.setInvoicedAmount(java.math.BigDecimal.ZERO);
            contract.setContractNo(generateContractNo());
        }

        // 设置字段
        contract.setCustomerId(dto.getCustomerId());
        contract.setOpportunityId(dto.getOpportunityId());
        contract.setQuoteId(dto.getQuoteId());
        contract.setContractName(dto.getContractName());
        contract.setContractType(dto.getContractType() != null ? dto.getContractType() : 1);
        contract.setContractAmount(dto.getContractAmount());
        contract.setSignDate(dto.getSignDate());
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        contract.setSignUserId(dto.getSignUserId());
        contract.setOurSignUserId(dto.getOurSignUserId());
        contract.setOwnerId(dto.getOwnerId());
        contract.setDeptId(UserContextHolder.getDeptId());
        contract.setFileUrl(dto.getFileUrl());
        contract.setRemark(dto.getRemark());

        if (dto.getId() != null) {
            contractMapper.updateById(contract);
        } else {
            contractMapper.insert(contract);
        }

        return contract.getId();
    }

    @Override
    @Transactional
    public void deleteContract(Long id) {
        CrmContract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        if (!ContractStatusEnum.DRAFT.getCode().equals(contract.getStatus())) {
            throw new BusinessException("只有草稿状态的合同才能删除");
        }
        contractMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void submitContract(Long id) {
        CrmContract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        if (!ContractStatusEnum.DRAFT.getCode().equals(contract.getStatus())) {
            throw new BusinessException("只有草稿状态的合同才能提交审批");
        }
        contract.setStatus(ContractStatusEnum.APPROVAL.getCode());
        contractMapper.updateById(contract);
    }

    @Override
    @Transactional
    public void approveContract(Long id) {
        CrmContract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        if (!ContractStatusEnum.APPROVAL.getCode().equals(contract.getStatus())) {
            throw new BusinessException("只有审批中状态的合同才能审批通过");
        }
        contract.setStatus(ContractStatusEnum.EFFECTIVE.getCode());
        contract.setSignDate(LocalDate.now());
        contractMapper.updateById(contract);
    }

    @Override
    @Transactional
    public void rejectContract(Long id) {
        CrmContract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        if (!ContractStatusEnum.APPROVAL.getCode().equals(contract.getStatus())) {
            throw new BusinessException("只有审批中状态的合同才能驳回");
        }
        contract.setStatus(ContractStatusEnum.DRAFT.getCode());
        contractMapper.updateById(contract);
    }

    @Override
    @Transactional
    public void terminateContract(Long id) {
        CrmContract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        if (!ContractStatusEnum.EFFECTIVE.getCode().equals(contract.getStatus())) {
            throw new BusinessException("只有生效状态的合同才能终止");
        }
        contract.setStatus(ContractStatusEnum.TERMINATED.getCode());
        contractMapper.updateById(contract);
    }

    /** 生成合同编号 */
    private String generateContractNo() {
        String prefix = "HT";
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long tenantId = UserContextHolder.getTenantId();

        LambdaQueryWrapper<CrmContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmContract::getTenantId, tenantId)
                .likeRight(CrmContract::getContractNo, prefix + dateStr)
                .orderByDesc(CrmContract::getId)
                .last("LIMIT 1");

        CrmContract lastContract = contractMapper.selectOne(wrapper);
        int seq = 1;
        if (lastContract != null && lastContract.getContractNo() != null) {
            String lastNo = lastContract.getContractNo();
            String seqStr = lastNo.substring(lastNo.length() - 4);
            seq = Integer.parseInt(seqStr) + 1;
        }

        return prefix + dateStr + String.format("%04d", seq);
    }

    /** 填充状态名称和类型名称 */
    private void fillStatusAndTypeName(ContractVO vo) {
        vo.setStatusName(ContractStatusEnum.getNameByCode(vo.getStatus()));
        if (vo.getContractType() != null) {
            if (vo.getContractType() == 1) {
                vo.setContractTypeName("销售合同");
            } else if (vo.getContractType() == 2) {
                vo.setContractTypeName("采购合同");
            }
        }
    }
}
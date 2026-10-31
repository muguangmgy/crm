package com.platform.server.crm.invoice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.context.UserContextHolder;
import com.platform.common.exception.BusinessException;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.contract.mapper.CrmContractMapper;
import com.platform.server.crm.contract.po.CrmContract;
import com.platform.server.crm.customer.mapper.CustomerMapper;
import com.platform.server.crm.customer.po.CrmCustomer;
import com.platform.server.crm.invoice.dto.InvoiceQueryDTO;
import com.platform.server.crm.invoice.dto.InvoiceSaveDTO;
import com.platform.server.crm.invoice.mapper.InvoiceMapper;
import com.platform.server.crm.invoice.po.CrmInvoice;
import com.platform.server.crm.invoice.service.InvoiceService;
import com.platform.server.crm.invoice.vo.InvoiceVO;
import com.platform.server.system.user.mapper.SysUserMapper;
import com.platform.server.system.user.po.SysUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceMapper invoiceMapper;
    private final CrmContractMapper contractMapper;
    private final CustomerMapper customerMapper;
    private final SysUserMapper sysUserMapper;

    /** 发票状态：已开票 */
    private static final int STATUS_INVOICED = 1;
    /** 发票状态：已作废 */
    private static final int STATUS_CANCELLED = 2;

    @Override
    public PageVO<InvoiceVO> getInvoicePage(InvoiceQueryDTO query) {
        Page<CrmInvoice> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<CrmInvoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmInvoice::getTenantId, UserContextHolder.getTenantId());

        if (query.getInvoiceNo() != null) {
            wrapper.like(CrmInvoice::getInvoiceNo, query.getInvoiceNo());
        }
        if (query.getContractId() != null) {
            wrapper.eq(CrmInvoice::getContractId, query.getContractId());
        }
        if (query.getInvoiceType() != null) {
            wrapper.eq(CrmInvoice::getInvoiceType, query.getInvoiceType());
        }
        if (query.getInvoiceStatus() != null) {
            wrapper.eq(CrmInvoice::getInvoiceStatus, query.getInvoiceStatus());
        }

        wrapper.orderByDesc(CrmInvoice::getCreateTime);

        Page<CrmInvoice> result = invoiceMapper.selectPage(page, wrapper);

        List<InvoiceVO> list = result.getRecords().stream()
                .map(this::toInvoiceVO)
                .collect(Collectors.toList());

        return PageVO.of(list, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public InvoiceVO getInvoiceById(Long id) {
        CrmInvoice invoice = invoiceMapper.selectById(id);
        if (invoice == null) {
            throw new BusinessException("发票不存在");
        }
        return toInvoiceVO(invoice);
    }

    @Override
    @Transactional
    public Long saveInvoice(InvoiceSaveDTO dto) {
        CrmInvoice invoice;
        BigDecimal oldAmount = BigDecimal.ZERO;

        if (dto.getId() != null) {
            invoice = invoiceMapper.selectById(dto.getId());
            if (invoice == null) {
                throw new BusinessException("发票不存在");
            }
            if (invoice.getInvoiceStatus() != STATUS_INVOICED) {
                throw new BusinessException("只有已开票状态的发票才能修改");
            }
            oldAmount = invoice.getInvoiceAmount() != null ? invoice.getInvoiceAmount() : BigDecimal.ZERO;
        } else {
            invoice = new CrmInvoice();
            invoice.setTenantId(UserContextHolder.getTenantId());
            invoice.setInvoiceNo(generateInvoiceNo());
            invoice.setInvoiceStatus(STATUS_INVOICED);
        }

        invoice.setContractId(dto.getContractId());
        invoice.setInvoiceType(dto.getInvoiceType());
        invoice.setInvoiceAmount(dto.getInvoiceAmount());
        invoice.setInvoiceDate(dto.getInvoiceDate());
        invoice.setRemark(dto.getRemark());
        invoice.setFileUrl(dto.getFileUrl());

        if (dto.getId() != null) {
            invoiceMapper.updateById(invoice);
            BigDecimal delta = dto.getInvoiceAmount().subtract(oldAmount);
            if (delta.compareTo(BigDecimal.ZERO) != 0 && invoice.getContractId() != null) {
                contractMapper.updateInvoicedAmount(invoice.getContractId(), delta);
            }
        } else {
            invoiceMapper.insert(invoice);
            if (invoice.getContractId() != null && dto.getInvoiceAmount() != null) {
                contractMapper.updateInvoicedAmount(invoice.getContractId(), dto.getInvoiceAmount());
            }
        }

        return invoice.getId();
    }

    @Override
    @Transactional
    public void deleteInvoice(Long id) {
        CrmInvoice invoice = invoiceMapper.selectById(id);
        if (invoice == null) {
            throw new BusinessException("发票不存在");
        }
        if (invoice.getInvoiceStatus() != STATUS_INVOICED) {
            throw new BusinessException("只有已开票状态的发票才能删除");
        }

        if (invoice.getContractId() != null && invoice.getInvoiceAmount() != null) {
            contractMapper.updateInvoicedAmount(invoice.getContractId(), invoice.getInvoiceAmount().negate());
        }
        invoiceMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void cancelInvoice(Long id) {
        CrmInvoice invoice = invoiceMapper.selectById(id);
        if (invoice == null) {
            throw new BusinessException("发票不存在");
        }
        if (invoice.getInvoiceStatus() != STATUS_INVOICED) {
            throw new BusinessException("只有已开票状态的发票才能作废");
        }

        invoice.setInvoiceStatus(STATUS_CANCELLED);
        invoiceMapper.updateById(invoice);

        if (invoice.getContractId() != null && invoice.getInvoiceAmount() != null) {
            contractMapper.updateInvoicedAmount(invoice.getContractId(), invoice.getInvoiceAmount().negate());
        }
    }

    private InvoiceVO toInvoiceVO(CrmInvoice invoice) {
        InvoiceVO vo = new InvoiceVO();
        vo.setId(invoice.getId());
        vo.setInvoiceNo(invoice.getInvoiceNo());
        vo.setContractId(invoice.getContractId());
        vo.setInvoiceType(invoice.getInvoiceType());
        vo.setInvoiceTypeName(getInvoiceTypeName(invoice.getInvoiceType()));
        vo.setInvoiceAmount(invoice.getInvoiceAmount());
        vo.setInvoiceDate(invoice.getInvoiceDate());
        vo.setInvoiceStatus(invoice.getInvoiceStatus());
        vo.setInvoiceStatusName(getInvoiceStatusName(invoice.getInvoiceStatus()));
        vo.setRemark(invoice.getRemark());
        vo.setFileUrl(invoice.getFileUrl());
        vo.setCreateTime(invoice.getCreateTime());
        vo.setUpdateTime(invoice.getUpdateTime());

        if (invoice.getContractId() != null) {
            CrmContract contract = contractMapper.selectById(invoice.getContractId());
            if (contract != null) {
                vo.setContractName(contract.getContractName());
                vo.setContractNo(contract.getContractNo());
                vo.setCustomerId(contract.getCustomerId());
                vo.setOwnerId(contract.getOwnerId());
                if (contract.getCustomerId() != null) {
                    CrmCustomer customer = customerMapper.selectById(contract.getCustomerId());
                    if (customer != null) {
                        vo.setCustomerName(customer.getCustomerName());
                    }
                }
                if (contract.getOwnerId() != null) {
                    SysUser owner = sysUserMapper.selectById(contract.getOwnerId());
                    if (owner != null) {
                        vo.setOwnerName(owner.getUserName());
                    }
                }
            }
        }
        return vo;
    }

    private String getInvoiceTypeName(Integer invoiceType) {
        if (invoiceType == null) {
            return "";
        }
        switch (invoiceType) {
            case 1: return "普通发票";
            case 2: return "增值税专用发票";
            default: return "";
        }
    }

    private String getInvoiceStatusName(Integer invoiceStatus) {
        if (invoiceStatus == null) {
            return "";
        }
        switch (invoiceStatus) {
            case 1: return "已开票";
            case 2: return "已作废";
            default: return "";
        }
    }

    private String generateInvoiceNo() {
        return "INV" + System.currentTimeMillis();
    }
}

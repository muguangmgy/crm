package com.platform.server.crm.quote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.context.UserContextHolder;
import com.platform.common.exception.BusinessException;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.quote.dto.QuoteProductSaveDTO;
import com.platform.server.crm.quote.dto.QuoteQueryDTO;
import com.platform.server.crm.quote.dto.QuoteSaveDTO;
import com.platform.server.crm.opportunity.dto.OpportunityCloseDTO;
import com.platform.server.crm.opportunity.service.OpportunityService;
import com.platform.server.crm.quote.mapper.QuoteMapper;
import com.platform.server.crm.quote.mapper.QuoteProductMapper;
import com.platform.server.crm.quote.po.CrmQuote;
import com.platform.server.crm.quote.po.CrmQuoteProduct;
import com.platform.server.crm.quote.service.QuoteService;
import com.platform.server.crm.quote.vo.QuoteProductVO;
import com.platform.server.crm.quote.vo.QuoteVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

    private final QuoteMapper quoteMapper;
    private final QuoteProductMapper quoteProductMapper;
    private final OpportunityService opportunityService;

    /** 报价状态：草稿 */
    private static final int STATUS_DRAFT = 1;
    /** 报价状态：已发送 */
    private static final int STATUS_SENT = 2;
    /** 报价状态：已接受 */
    private static final int STATUS_ACCEPTED = 3;
    /** 报价状态：已拒绝 */
    private static final int STATUS_REJECTED = 4;

    @Override
    public PageVO<QuoteVO> getQuotePage(QuoteQueryDTO query) {
        Page<CrmQuote> page = new Page<>(query.getPageNum(), query.getPageSize());
        var result = quoteMapper.selectQuotePage(page, query);
        result.getRecords().forEach(vo -> vo.setStatusName(getStatusName(vo.getStatus())));
        return PageVO.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public QuoteVO getQuoteById(Long id) {
        QuoteVO vo = quoteMapper.selectQuoteById(id);
        if (vo == null) {
            throw new BusinessException("报价单不存在");
        }
        vo.setStatusName(getStatusName(vo.getStatus()));

        // 查询产品明细
        List<CrmQuoteProduct> products = quoteProductMapper.selectList(
                new LambdaQueryWrapper<CrmQuoteProduct>()
                        .eq(CrmQuoteProduct::getQuoteId, id)
                        .orderByAsc(CrmQuoteProduct::getId)
        );

        vo.setProducts(products.stream()
                .map(this::toQuoteProductVO)
                .collect(Collectors.toList()));

        return vo;
    }

    @Override
    @Transactional
    public Long saveQuote(QuoteSaveDTO dto) {
        CrmQuote quote;

        if (dto.getId() != null) {
            // 修改
            quote = quoteMapper.selectById(dto.getId());
            if (quote == null) {
                throw new BusinessException("报价单不存在");
            }
            // 只有草稿状态才能修改
            if (quote.getStatus() != STATUS_DRAFT) {
                throw new BusinessException("只有草稿状态的报价单才能修改");
            }
        } else {
            // 新增
            quote = new CrmQuote();
            quote.setTenantId(UserContextHolder.getTenantId());
            quote.setQuoteNo(generateQuoteNo());
            quote.setStatus(STATUS_DRAFT);
        }

        quote.setOpportunityId(dto.getOpportunityId());
        quote.setCustomerId(dto.getCustomerId());
        quote.setQuoteName(dto.getQuoteName());
        quote.setTotalAmount(dto.getTotalAmount());
        quote.setDiscountRate(dto.getDiscountRate());
        quote.setFinalAmount(dto.getFinalAmount());
        quote.setExpireDate(dto.getExpireDate());
        quote.setRemark(dto.getRemark());
        quote.setOwnerId(dto.getOwnerId());
        quote.setDeptId(UserContextHolder.getDeptId());

        // 计算最终金额
        if (quote.getFinalAmount() == null) {
            if (quote.getDiscountRate() != null && quote.getDiscountRate().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discount = quote.getTotalAmount().multiply(quote.getDiscountRate()).divide(BigDecimal.valueOf(100));
                quote.setFinalAmount(quote.getTotalAmount().subtract(discount));
            } else {
                quote.setFinalAmount(quote.getTotalAmount());
            }
        }

        if (dto.getId() != null) {
            quoteMapper.updateById(quote);
        } else {
            quoteMapper.insert(quote);
        }

        // 处理产品明细
        saveQuoteProducts(quote.getId(), dto.getProducts());

        return quote.getId();
    }

    @Override
    @Transactional
    public void deleteQuote(Long id) {
        CrmQuote quote = quoteMapper.selectById(id);
        if (quote == null) {
            throw new BusinessException("报价单不存在");
        }
        // 只有草稿状态才能删除
        if (quote.getStatus() != STATUS_DRAFT) {
            throw new BusinessException("只有草稿状态的报价单才能删除");
        }

        // 删除产品明细
        quoteProductMapper.delete(
                new LambdaQueryWrapper<CrmQuoteProduct>()
                        .eq(CrmQuoteProduct::getQuoteId, id)
        );

        // 删除报价单
        quoteMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void sendQuote(Long id) {
        CrmQuote quote = quoteMapper.selectById(id);
        if (quote == null) {
            throw new BusinessException("报价单不存在");
        }
        // 只有草稿状态才能发送
        if (quote.getStatus() != STATUS_DRAFT) {
            throw new BusinessException("只有草稿状态的报价单才能发送");
        }

        quote.setStatus(STATUS_SENT);
        quote.setSendTime(LocalDateTime.now());
        quoteMapper.updateById(quote);
    }

    @Override
    @Transactional
    public void acceptQuote(Long id) {
        CrmQuote quote = quoteMapper.selectById(id);
        if (quote == null) {
            throw new BusinessException("报价单不存在");
        }
        // 只有已发送状态才能接受
        if (quote.getStatus() != STATUS_SENT) {
            throw new BusinessException("只有已发送状态的报价单才能接受");
        }

        quote.setStatus(STATUS_ACCEPTED);
        quote.setAcceptTime(LocalDateTime.now());
        quoteMapper.updateById(quote);

        // 中标后标记关联商机成交（已成交则仅回写中标报价）
        if (quote.getOpportunityId() != null) {
            var opportunity = opportunityService.getOpportunityById(quote.getOpportunityId());
            if (opportunity.getStatus() != null && opportunity.getStatus() == 1) {
                OpportunityCloseDTO closeDTO = new OpportunityCloseDTO();
                closeDTO.setIsWin(true);
                closeDTO.setActualAmount(quote.getFinalAmount() != null ? quote.getFinalAmount() : quote.getTotalAmount());
                closeDTO.setWinnerQuoteId(quote.getId());
                closeDTO.setRemark("报价中标自动成交");
                opportunityService.closeOpportunity(quote.getOpportunityId(), closeDTO);
            }
        }
    }

    @Override
    @Transactional
    public void rejectQuote(Long id) {
        CrmQuote quote = quoteMapper.selectById(id);
        if (quote == null) {
            throw new BusinessException("报价单不存在");
        }
        // 只有已发送状态才能拒绝
        if (quote.getStatus() != STATUS_SENT) {
            throw new BusinessException("只有已发送状态的报价单才能拒绝");
        }

        quote.setStatus(STATUS_REJECTED);
        quoteMapper.updateById(quote);
    }

    // ===== 私有方法 =====

    private QuoteProductVO toQuoteProductVO(CrmQuoteProduct product) {
        QuoteProductVO vo = new QuoteProductVO();
        vo.setId(product.getId());
        vo.setProductId(product.getProductId());
        vo.setProductName(product.getProductName());
        vo.setUnit(product.getUnit());
        vo.setQuantity(product.getQuantity());
        vo.setUnitPrice(product.getUnitPrice());
        vo.setDiscountRate(product.getDiscountRate());
        vo.setTotalPrice(product.getTotalPrice());
        vo.setRemark(product.getRemark());
        return vo;
    }

    private String getStatusName(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case 1: return "草稿";
            case 2: return "已发送";
            case 3: return "已接受";
            case 4: return "已拒绝";
            default: return "";
        }
    }

    private void saveQuoteProducts(Long quoteId, List<QuoteProductSaveDTO> products) {
        // 查询现有明细
        List<CrmQuoteProduct> existingProducts = quoteProductMapper.selectList(
                new LambdaQueryWrapper<CrmQuoteProduct>()
                        .eq(CrmQuoteProduct::getQuoteId, quoteId)
        );

        // 收集需要保留的ID
        List<Long> keepIds = products.stream()
                .filter(p -> p.getId() != null && p.getId() > 0)
                .map(QuoteProductSaveDTO::getId)
                .collect(Collectors.toList());

        // 删除不在保留列表中的明细
        for (CrmQuoteProduct existing : existingProducts) {
            if (!keepIds.contains(existing.getId())) {
                quoteProductMapper.deleteById(existing.getId());
            }
        }

        // 新增或修改明细
        for (QuoteProductSaveDTO dto : products) {
            CrmQuoteProduct product;

            if (dto.getId() != null && dto.getId() > 0) {
                // 修改
                product = quoteProductMapper.selectById(dto.getId());
                if (product == null || !product.getQuoteId().equals(quoteId)) {
                    continue;
                }
            } else {
                // 新增
                product = new CrmQuoteProduct();
                product.setQuoteId(quoteId);
            }

            product.setProductId(dto.getProductId());
            product.setProductName(dto.getProductName());
            product.setUnit(dto.getUnit());
            product.setQuantity(dto.getQuantity());
            product.setUnitPrice(dto.getUnitPrice());
            product.setDiscountRate(dto.getDiscountRate());
            product.setTotalPrice(dto.getTotalPrice());
            product.setRemark(dto.getRemark());

            // 计算行总价
            if (product.getTotalPrice() == null) {
                BigDecimal total = product.getQuantity().multiply(product.getUnitPrice());
                if (product.getDiscountRate() != null && product.getDiscountRate().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal discount = total.multiply(product.getDiscountRate()).divide(BigDecimal.valueOf(100));
                    total = total.subtract(discount);
                }
                product.setTotalPrice(total);
            }

            if (dto.getId() != null && dto.getId() > 0) {
                quoteProductMapper.updateById(product);
            } else {
                quoteProductMapper.insert(product);
            }
        }
    }

    private String generateQuoteNo() {
        return "QT" + System.currentTimeMillis();
    }
}
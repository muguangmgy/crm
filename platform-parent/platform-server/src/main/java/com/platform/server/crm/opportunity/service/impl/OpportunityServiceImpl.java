package com.platform.server.crm.opportunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.platform.common.context.UserContextHolder;
import com.platform.common.exception.BusinessException;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.opportunity.dto.OpportunityCloseDTO;
import com.platform.server.crm.opportunity.dto.OpportunityQueryDTO;
import com.platform.server.crm.opportunity.dto.OpportunitySaveDTO;
import com.platform.server.crm.opportunity.dto.StageSaveDTO;
import com.platform.server.crm.opportunity.mapper.CrmOpportunityMapper;
import com.platform.server.crm.opportunity.mapper.CrmOpportunityStageMapper;
import com.platform.server.crm.opportunity.po.CrmOpportunity;
import com.platform.server.crm.opportunity.po.CrmOpportunityStage;
import com.platform.server.crm.opportunity.service.OpportunityService;
import com.platform.server.crm.opportunity.vo.OpportunityVO;
import com.platform.server.crm.opportunity.vo.StageVO;
import com.platform.server.crm.quote.mapper.QuoteMapper;
import com.platform.server.crm.quote.po.CrmQuote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpportunityServiceImpl implements OpportunityService {

    private final CrmOpportunityMapper opportunityMapper;
    private final CrmOpportunityStageMapper stageMapper;
    private final QuoteMapper quoteMapper;

    @Override
    public PageVO<OpportunityVO> getOpportunityPage(OpportunityQueryDTO query) {

        List<OpportunityVO> list = opportunityMapper.selectOpportunityPage(query);

        // 使用子查询计算总数
        LambdaQueryWrapper<CrmOpportunity> countWrapper = new LambdaQueryWrapper<>();
        if (query.getOppName() != null && !query.getOppName().isEmpty()) {
            countWrapper.like(CrmOpportunity::getOppName, query.getOppName());
        }
        if (query.getCustomerId() != null) {
            countWrapper.eq(CrmOpportunity::getCustomerId, query.getCustomerId());
        }
        if (query.getStageId() != null) {
            countWrapper.eq(CrmOpportunity::getStageId, query.getStageId());
        }
        if (query.getStatus() != null) {
            countWrapper.eq(CrmOpportunity::getStatus, query.getStatus());
        }
        if (query.getOwnerId() != null) {
            countWrapper.eq(CrmOpportunity::getOwnerId, query.getOwnerId());
        }
        if (query.getSource() != null && !query.getSource().isEmpty()) {
            countWrapper.eq(CrmOpportunity::getSource, query.getSource());
        }
        Long total = opportunityMapper.selectCount(countWrapper);

        return PageVO.of(list, total, query.getPageNum().longValue(), query.getPageSize().longValue());
    }

    @Override
    public OpportunityVO getOpportunityById(Long id) {
        OpportunityVO vo = opportunityMapper.selectOpportunityById(id);
        if (vo == null) {
            throw new BusinessException("商机不存在");
        }
        fillWinnerQuoteInfo(vo);
        return vo;
    }

    /** 补全中标报价单编号/名称（优先展示单号） */
    private void fillWinnerQuoteInfo(OpportunityVO vo) {
        if (vo.getWinnerQuoteId() == null) {
            return;
        }
        if (StringUtils.hasText(vo.getWinnerQuoteNo()) && StringUtils.hasText(vo.getWinnerQuoteName())) {
            return;
        }
        CrmQuote quote = quoteMapper.selectById(vo.getWinnerQuoteId());
        if (quote == null) {
            return;
        }
        if (!StringUtils.hasText(vo.getWinnerQuoteNo())) {
            vo.setWinnerQuoteNo(quote.getQuoteNo());
        }
        if (!StringUtils.hasText(vo.getWinnerQuoteName())) {
            vo.setWinnerQuoteName(quote.getQuoteName());
        }
    }

    @Override
    @Transactional
    public Long saveOpportunity(OpportunitySaveDTO dto) {
        CrmOpportunity opportunity;

        if (dto.getId() != null) {
            // 修改
            opportunity = opportunityMapper.selectById(dto.getId());
            if (opportunity == null) {
                throw new BusinessException("商机不存在");
            }
            // 已成交或失败的商机不能修改
            if (opportunity.getStatus() != 1) {
                throw new BusinessException("商机已成交或失败，无法修改");
            }
        } else {
            // 新增
            opportunity = new CrmOpportunity();
            opportunity.setTenantId(UserContextHolder.getTenantId());
            opportunity.setOppNo(generateOppNo());
            opportunity.setStatus(1);
        }

        // 获取阶段信息
        CrmOpportunityStage stage = stageMapper.selectById(dto.getStageId());
        if (stage == null || stage.getStatus() != 1) {
            throw new BusinessException("阶段不存在或已禁用");
        }

        opportunity.setCustomerId(dto.getCustomerId());
        opportunity.setContactId(dto.getContactId());
        opportunity.setOppName(dto.getOppName());
        opportunity.setStageId(dto.getStageId());
        opportunity.setStageName(stage.getStageName());
        opportunity.setProbability(stage.getProbability());
        opportunity.setExpectedAmount(dto.getExpectedAmount());
        opportunity.setExpectedDate(dto.getExpectedDate());
        opportunity.setSource(dto.getSource());
        // 新增未指定负责人时，默认当前登录用户（与客户创建一致）
        if (dto.getId() == null && (dto.getOwnerId() == null || dto.getOwnerId() <= 0)) {
            opportunity.setOwnerId(UserContextHolder.getUserId());
        } else if (dto.getOwnerId() != null && dto.getOwnerId() > 0) {
            opportunity.setOwnerId(dto.getOwnerId());
        }
        opportunity.setDeptId(UserContextHolder.getDeptId());
        opportunity.setDescription(dto.getDescription());
        opportunity.setRemark(dto.getRemark());

        if (dto.getId() != null) {
            opportunityMapper.updateById(opportunity);
        } else {
            opportunityMapper.insert(opportunity);
        }

        return opportunity.getId();
    }

    @Override
    @Transactional
    public void deleteOpportunity(Long id) {
        CrmOpportunity opportunity = opportunityMapper.selectById(id);
        if (opportunity == null) {
            throw new BusinessException("商机不存在");
        }
        // 已成交的商机不能删除
        if (opportunity.getStatus() == 2) {
            throw new BusinessException("已成交的商机不能删除");
        }
        opportunityMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void advanceOpportunity(Long id, Long nextStageId) {
        CrmOpportunity opportunity = opportunityMapper.selectById(id);
        if (opportunity == null) {
            throw new BusinessException("商机不存在");
        }
        // 只有进行中的商机可以推进
        if (opportunity.getStatus() != 1) {
            throw new BusinessException("只有进行中的商机可以推进阶段");
        }

        // 获取当前阶段和目标阶段
        CrmOpportunityStage currentStage = stageMapper.selectById(opportunity.getStageId());
        CrmOpportunityStage nextStage = stageMapper.selectById(nextStageId);

        if (nextStage == null || nextStage.getStatus() != 1) {
            throw new BusinessException("目标阶段不存在或已禁用");
        }

        // 阶段只能向前推进（sort递增）
        if (nextStage.getSort() <= currentStage.getSort()) {
            throw new BusinessException("阶段只能向前推进，不能回退");
        }

        // 更新商机阶段
        opportunity.setStageId(nextStageId);
        opportunity.setStageName(nextStage.getStageName());
        opportunity.setProbability(nextStage.getProbability());

        // 如果推进到成交阶段，自动标记为成交
        if (nextStage.getIsWin() == 1) {
            opportunity.setStatus(2);
            opportunity.setCloseDate(LocalDate.now());
        }

        opportunityMapper.updateById(opportunity);
    }

    @Override
    @Transactional
    public void closeOpportunity(Long id, OpportunityCloseDTO dto) {
        CrmOpportunity opportunity = opportunityMapper.selectById(id);
        if (opportunity == null) {
            throw new BusinessException("商机不存在");
        }
        // 只有进行中的商机可以成交/失败
        if (opportunity.getStatus() != 1) {
            throw new BusinessException("只有进行中的商机可以标记成交或失败");
        }

        if (dto.getIsWin()) {
            // 成交
            opportunity.setStatus(2);
            opportunity.setCloseDate(LocalDate.now());
            opportunity.setActualAmount(dto.getActualAmount());
            opportunity.setWinnerQuoteId(dto.getWinnerQuoteId());

            // 获取成交阶段
            CrmOpportunityStage winStage = stageMapper.selectOne(
                    new LambdaQueryWrapper<CrmOpportunityStage>()
                            .eq(CrmOpportunityStage::getTenantId, opportunity.getTenantId())
                            .eq(CrmOpportunityStage::getIsWin, 1)
                            .eq(CrmOpportunityStage::getStatus, 1)
                            .last("LIMIT 1")
            );
            if (winStage != null) {
                opportunity.setStageId(winStage.getId());
                opportunity.setStageName(winStage.getStageName());
                opportunity.setProbability(100);
            }
        } else {
            // 失败
            opportunity.setStatus(3);
            opportunity.setCloseDate(LocalDate.now());

            // 获取失败阶段
            CrmOpportunityStage lostStage = stageMapper.selectOne(
                    new LambdaQueryWrapper<CrmOpportunityStage>()
                            .eq(CrmOpportunityStage::getTenantId, opportunity.getTenantId())
                            .eq(CrmOpportunityStage::getIsLost, 1)
                            .eq(CrmOpportunityStage::getStatus, 1)
                            .last("LIMIT 1")
            );
            if (lostStage != null) {
                opportunity.setStageId(lostStage.getId());
                opportunity.setStageName(lostStage.getStageName());
                opportunity.setProbability(0);
            }
        }

        if (dto.getRemark() != null) {
            opportunity.setRemark(dto.getRemark());
        }

        opportunityMapper.updateById(opportunity);
    }

    @Override
    public List<StageVO> getStageList() {
        List<CrmOpportunityStage> stages = stageMapper.selectList(
                new LambdaQueryWrapper<CrmOpportunityStage>()
                        .eq(CrmOpportunityStage::getTenantId, UserContextHolder.getTenantId())
                        .orderByAsc(CrmOpportunityStage::getSort)
        );

        return stages.stream()
                .map(this::toStageVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StageVO> getActiveStageList() {
        List<CrmOpportunityStage> stages = stageMapper.selectList(
                new LambdaQueryWrapper<CrmOpportunityStage>()
                        .eq(CrmOpportunityStage::getTenantId, UserContextHolder.getTenantId())
                        .eq(CrmOpportunityStage::getStatus, 1)
                        .orderByAsc(CrmOpportunityStage::getSort)
        );

        return stages.stream()
                .map(this::toStageVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long saveStage(StageSaveDTO dto) {
        CrmOpportunityStage stage;

        if (dto.getId() != null) {
            // 修改
            stage = stageMapper.selectById(dto.getId());
            if (stage == null) {
                throw new BusinessException("阶段不存在");
            }
        } else {
            // 新增
            stage = new CrmOpportunityStage();
            stage.setTenantId(UserContextHolder.getTenantId());
        }

        stage.setStageName(dto.getStageName());
        stage.setStageCode(dto.getStageCode());
        stage.setProbability(dto.getProbability() != null ? dto.getProbability() : 0);
        stage.setSort(dto.getSort() != null ? dto.getSort() : 0);
        stage.setIsWin(dto.getIsWin() != null ? dto.getIsWin() : 0);
        stage.setIsLost(dto.getIsLost() != null ? dto.getIsLost() : 0);
        stage.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        if (dto.getId() != null) {
            stageMapper.updateById(stage);
        } else {
            stageMapper.insert(stage);
        }

        return stage.getId();
    }

    @Override
    @Transactional
    public void deleteStage(Long id) {
        CrmOpportunityStage stage = stageMapper.selectById(id);
        if (stage == null) {
            throw new BusinessException("阶段不存在");
        }

        // 检查是否有商机在使用该阶段
        Long oppCount = opportunityMapper.selectCount(
                new LambdaQueryWrapper<CrmOpportunity>()
                        .eq(CrmOpportunity::getStageId, id)
        );
        if (oppCount > 0) {
            throw new BusinessException("该阶段下存在商机，无法删除");
        }

        stageMapper.deleteById(id);
    }

    @Override
    public String getStageName(Long stageId) {
        if (stageId == null) {
            return null;
        }
        CrmOpportunityStage stage = stageMapper.selectById(stageId);
        return stage != null ? stage.getStageName() : null;
    }

    // ===== 私有方法 =====

    private StageVO toStageVO(CrmOpportunityStage stage) {
        StageVO vo = new StageVO();
        vo.setId(stage.getId());
        vo.setStageName(stage.getStageName());
        vo.setStageCode(stage.getStageCode());
        vo.setProbability(stage.getProbability());
        vo.setSort(stage.getSort());
        vo.setIsWin(stage.getIsWin());
        vo.setIsLost(stage.getIsLost());
        vo.setStatus(stage.getStatus());
        vo.setCreateTime(stage.getCreateTime());
        vo.setUpdateTime(stage.getUpdateTime());
        return vo;
    }

    private String generateOppNo() {
        return "OPP" + System.currentTimeMillis();
    }
}
package com.platform.server.crm.opportunity.service;

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
import com.platform.server.crm.opportunity.service.impl.OpportunityServiceImpl;
import com.platform.server.crm.opportunity.vo.OpportunityVO;
import com.platform.server.crm.opportunity.vo.StageVO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 商机服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class OpportunityServiceTest {

    @Mock
    private CrmOpportunityMapper opportunityMapper;

    @Mock
    private CrmOpportunityStageMapper stageMapper;

    @InjectMocks
    private OpportunityServiceImpl opportunityService;

    private static MockedStatic<UserContextHolder> userContextHolderMock;

    @BeforeAll
    static void setUpStatic() {
        userContextHolderMock = mockStatic(UserContextHolder.class);
        userContextHolderMock.when(UserContextHolder::getTenantId).thenReturn(1L);
        userContextHolderMock.when(UserContextHolder::getUserId).thenReturn(100L);
    }

    @AfterAll
    static void tearDownStatic() {
        userContextHolderMock.close();
    }

    // ==================== saveOpportunity 测试 ====================

    @Test
    @DisplayName("正常保存新商机")
    void testSaveOpportunity_Success_New() {
        // 准备阶段数据
        CrmOpportunityStage stage = new CrmOpportunityStage();
        stage.setId(1L);
        stage.setStageName("初步接触");
        stage.setProbability(20);
        stage.setStatus(1);

        when(stageMapper.selectById(1L)).thenReturn(stage);
        when(opportunityMapper.insert(any(CrmOpportunity.class))).thenAnswer(invocation -> {
            CrmOpportunity opp = invocation.getArgument(0);
            opp.setId(1L);
            return 1;
        });

        // 准备商机数据
        OpportunitySaveDTO dto = new OpportunitySaveDTO();
        dto.setCustomerId(10L);
        dto.setOppName("测试商机");
        dto.setStageId(1L);
        dto.setExpectedAmount(new BigDecimal("100000.00"));
        dto.setExpectedDate(LocalDate.of(2026, 6, 30));
        dto.setSource("展会");
        dto.setOwnerId(100L);

        // 执行
        Long result = opportunityService.saveOpportunity(dto);

        // 验证
        assertEquals(1L, result);
        verify(opportunityMapper).insert(any(CrmOpportunity.class));
        verify(opportunityMapper, never()).updateById(any(CrmOpportunity.class));
    }

    @Test
    @DisplayName("正常修改现有商机")
    void testSaveOpportunity_Success_Update() {
        // 准备现有商机
        CrmOpportunity existingOpp = new CrmOpportunity();
        existingOpp.setId(1L);
        existingOpp.setStatus(1); // 进行中
        existingOpp.setStageId(1L);

        when(opportunityMapper.selectById(1L)).thenReturn(existingOpp);

        // 准备阶段
        CrmOpportunityStage stage = new CrmOpportunityStage();
        stage.setId(2L);
        stage.setStageName("需求确认");
        stage.setProbability(40);
        stage.setStatus(1);
        when(stageMapper.selectById(2L)).thenReturn(stage);

        when(opportunityMapper.updateById(any(CrmOpportunity.class))).thenReturn(1);

        // 准备修改数据
        OpportunitySaveDTO dto = new OpportunitySaveDTO();
        dto.setId(1L);
        dto.setCustomerId(10L);
        dto.setOppName("修改后的商机");
        dto.setStageId(2L);
        dto.setOwnerId(100L);

        // 执行
        Long result = opportunityService.saveOpportunity(dto);

        // 验证
        assertEquals(1L, result);
        verify(opportunityMapper).selectById(1L);
        verify(opportunityMapper).updateById(any(CrmOpportunity.class));
    }

    @Test
    @DisplayName("修改已成交商机抛出异常")
    void testSaveOpportunity_AlreadyClosed() {
        // 准备已成交商机
        CrmOpportunity closedOpp = new CrmOpportunity();
        closedOpp.setId(1L);
        closedOpp.setStatus(2); // 已成交

        when(opportunityMapper.selectById(1L)).thenReturn(closedOpp);

        // 准备修改数据
        OpportunitySaveDTO dto = new OpportunitySaveDTO();
        dto.setId(1L);
        dto.setCustomerId(10L);
        dto.setOppName("测试");
        dto.setStageId(1L);
        dto.setOwnerId(100L);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> opportunityService.saveOpportunity(dto));
        assertEquals("商机已成交或失败，无法修改", ex.getMessage());
    }

    @Test
    @DisplayName("阶段不存在或已禁用抛出异常")
    void testSaveOpportunity_InvalidStage() {
        // 准备禁用的阶段
        CrmOpportunityStage disabledStage = new CrmOpportunityStage();
        disabledStage.setId(1L);
        disabledStage.setStatus(0); // 禁用

        when(stageMapper.selectById(1L)).thenReturn(disabledStage);

        // 准备商机数据
        OpportunitySaveDTO dto = new OpportunitySaveDTO();
        dto.setCustomerId(10L);
        dto.setOppName("测试商机");
        dto.setStageId(1L);
        dto.setOwnerId(100L);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> opportunityService.saveOpportunity(dto));
        assertEquals("阶段不存在或已禁用", ex.getMessage());
    }

    // ==================== getOpportunityPage 测试 ====================

    @Test
    @DisplayName("分页查询商机列表")
    void testGetOpportunityPage_Pagination() {
        // 准备查询条件
        OpportunityQueryDTO query = new OpportunityQueryDTO();
        query.setPageNum(1);
        query.setPageSize(10);

        OpportunityVO oppVO = new OpportunityVO();
        oppVO.setId(1L);
        oppVO.setOppName("测试商机");

        when(opportunityMapper.selectOpportunityPage(any(OpportunityQueryDTO.class)))
                .thenReturn(Collections.singletonList(oppVO));
        when(opportunityMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // 执行
        PageVO<OpportunityVO> result = opportunityService.getOpportunityPage(query);

        // 验证
        assertEquals(1, result.getList().size());
        assertEquals(1L, result.getTotal());
    }

    // ==================== advanceOpportunity 测试 ====================

    @Test
    @DisplayName("商机阶段推进成功")
    void testAdvanceOpportunity_Success() {
        // 准备当前阶段
        CrmOpportunityStage currentStage = new CrmOpportunityStage();
        currentStage.setId(1L);
        currentStage.setStageName("初步接触");
        currentStage.setProbability(20);
        currentStage.setSort(1);
        currentStage.setIsWin(0);

        // 准备目标阶段
        CrmOpportunityStage nextStage = new CrmOpportunityStage();
        nextStage.setId(2L);
        nextStage.setStageName("需求确认");
        nextStage.setProbability(40);
        nextStage.setSort(2);
        nextStage.setStatus(1);
        nextStage.setIsWin(0);

        // 准备商机
        CrmOpportunity opp = new CrmOpportunity();
        opp.setId(1L);
        opp.setStatus(1);
        opp.setStageId(1L);
        opp.setTenantId(1L);

        when(opportunityMapper.selectById(1L)).thenReturn(opp);
        when(stageMapper.selectById(1L)).thenReturn(currentStage);
        when(stageMapper.selectById(2L)).thenReturn(nextStage);
        when(opportunityMapper.updateById(any(CrmOpportunity.class))).thenReturn(1);

        // 执行
        opportunityService.advanceOpportunity(1L, 2L);

        // 验证
        verify(opportunityMapper).updateById(ArgumentMatchers.<CrmOpportunity>argThat(updatedOpp -> {
            assertEquals(2L, updatedOpp.getStageId());
            assertEquals("需求确认", updatedOpp.getStageName());
            assertEquals(40, updatedOpp.getProbability());
            return true;
        }));
    }

    @Test
    @DisplayName("阶段不能回退")
    void testAdvanceOpportunity_CannotGoBack() {
        // 准备当前阶段（sort=2）
        CrmOpportunityStage currentStage = new CrmOpportunityStage();
        currentStage.setId(2L);
        currentStage.setStageName("需求确认");
        currentStage.setSort(2);

        // 准备目标阶段（sort=1，要回退）
        CrmOpportunityStage nextStage = new CrmOpportunityStage();
        nextStage.setId(1L);
        nextStage.setStageName("初步接触");
        nextStage.setSort(1);
        nextStage.setStatus(1);

        // 准备商机
        CrmOpportunity opp = new CrmOpportunity();
        opp.setId(1L);
        opp.setStatus(1);
        opp.setStageId(2L);

        when(opportunityMapper.selectById(1L)).thenReturn(opp);
        when(stageMapper.selectById(2L)).thenReturn(currentStage);
        when(stageMapper.selectById(1L)).thenReturn(nextStage);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class,
                () -> opportunityService.advanceOpportunity(1L, 1L));
        assertEquals("阶段只能向前推进，不能回退", ex.getMessage());
        verify(opportunityMapper, never()).updateById(any(CrmOpportunity.class));
    }

    @Test
    @DisplayName("推进已关闭商机抛出异常")
    void testAdvanceOpportunity_AlreadyClosed() {
        // 准备已成交商机
        CrmOpportunity closedOpp = new CrmOpportunity();
        closedOpp.setId(1L);
        closedOpp.setStatus(2);

        when(opportunityMapper.selectById(1L)).thenReturn(closedOpp);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class,
                () -> opportunityService.advanceOpportunity(1L, 2L));
        assertEquals("只有进行中的商机可以推进阶段", ex.getMessage());
    }

    @Test
    @DisplayName("推进到不存在阶段抛出异常")
    void testAdvanceOpportunity_InvalidStage() {
        // 准备商机
        CrmOpportunity opp = new CrmOpportunity();
        opp.setId(1L);
        opp.setStatus(1);
        opp.setStageId(1L);

        CrmOpportunityStage currentStage = new CrmOpportunityStage();
        currentStage.setId(1L);
        currentStage.setSort(1);

        when(opportunityMapper.selectById(1L)).thenReturn(opp);
        when(stageMapper.selectById(1L)).thenReturn(currentStage);
        when(stageMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class,
                () -> opportunityService.advanceOpportunity(1L, 999L));
        assertEquals("目标阶段不存在或已禁用", ex.getMessage());
    }

    // ==================== closeOpportunity 测试 ====================

    @Test
    @DisplayName("商机成交成功")
    void testCloseOpportunity_Win() {
        // 准备商机
        CrmOpportunity opp = new CrmOpportunity();
        opp.setId(1L);
        opp.setStatus(1);
        opp.setTenantId(1L);

        // 准备成交阶段
        CrmOpportunityStage winStage = new CrmOpportunityStage();
        winStage.setId(10L);
        winStage.setStageName("成交");
        winStage.setIsWin(1);
        winStage.setStatus(1);

        when(opportunityMapper.selectById(1L)).thenReturn(opp);
        when(stageMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(winStage);
        when(opportunityMapper.updateById(any(CrmOpportunity.class))).thenReturn(1);

        // 准备成交数据
        OpportunityCloseDTO dto = new OpportunityCloseDTO();
        dto.setIsWin(true);
        dto.setActualAmount(new BigDecimal("95000.00"));
        dto.setWinnerQuoteId(1L);
        dto.setRemark("成功成交");

        // 执行
        opportunityService.closeOpportunity(1L, dto);

        // 验证
        verify(opportunityMapper).updateById(ArgumentMatchers.<CrmOpportunity>argThat(updatedOpp -> {
            assertEquals(2, updatedOpp.getStatus());
            assertEquals(new BigDecimal("95000.00"), updatedOpp.getActualAmount());
            assertEquals(1L, updatedOpp.getWinnerQuoteId());
            assertNotNull(updatedOpp.getCloseDate());
            assertEquals("成功成交", updatedOpp.getRemark());
            return true;
        }));
    }

    @Test
    @DisplayName("商机失败成功")
    void testCloseOpportunity_Lost() {
        // 准备商机
        CrmOpportunity opp = new CrmOpportunity();
        opp.setId(1L);
        opp.setStatus(1);
        opp.setTenantId(1L);

        // 准备失败阶段
        CrmOpportunityStage lostStage = new CrmOpportunityStage();
        lostStage.setId(20L);
        lostStage.setStageName("失败");
        lostStage.setIsLost(1);
        lostStage.setStatus(1);

        when(opportunityMapper.selectById(1L)).thenReturn(opp);
        when(stageMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(lostStage);
        when(opportunityMapper.updateById(any(CrmOpportunity.class))).thenReturn(1);

        // 准备失败数据
        OpportunityCloseDTO dto = new OpportunityCloseDTO();
        dto.setIsWin(false);
        dto.setRemark("客户选择了其他供应商");

        // 执行
        opportunityService.closeOpportunity(1L, dto);

        // 验证
        verify(opportunityMapper).updateById(ArgumentMatchers.<CrmOpportunity>argThat(updatedOpp -> {
            assertEquals(3, updatedOpp.getStatus());
            assertNotNull(updatedOpp.getCloseDate());
            assertEquals("客户选择了其他供应商", updatedOpp.getRemark());
            return true;
        }));
    }

    @Test
    @DisplayName("已关闭商机不能再次操作")
    void testCloseOpportunity_AlreadyClosed() {
        // 准备已成交商机
        CrmOpportunity closedOpp = new CrmOpportunity();
        closedOpp.setId(1L);
        closedOpp.setStatus(2);

        when(opportunityMapper.selectById(1L)).thenReturn(closedOpp);

        // 准备数据
        OpportunityCloseDTO dto = new OpportunityCloseDTO();
        dto.setIsWin(true);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class,
                () -> opportunityService.closeOpportunity(1L, dto));
        assertEquals("只有进行中的商机可以标记成交或失败", ex.getMessage());
    }

    // ==================== getStageList 测试 ====================

    @Test
    @DisplayName("获取阶段列表")
    void testGetStageList() {
        // 准备阶段数据
        CrmOpportunityStage stage1 = new CrmOpportunityStage();
        stage1.setId(1L);
        stage1.setStageName("初步接触");
        stage1.setStageCode("INITIAL");
        stage1.setProbability(20);
        stage1.setSort(1);
        stage1.setIsWin(0);
        stage1.setIsLost(0);
        stage1.setStatus(1);

        when(stageMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(stage1));

        // 执行
        List<StageVO> result = opportunityService.getStageList();

        // 验证
        assertEquals(1, result.size());
        assertEquals("初步接触", result.get(0).getStageName());
    }

    // ==================== saveStage 测试 ====================

    @Test
    @DisplayName("保存新阶段成功")
    void testSaveStage_Success_New() {
        // 准备阶段数据
        StageSaveDTO dto = new StageSaveDTO();
        dto.setStageName("新阶段");
        dto.setStageCode("NEW_STAGE");
        dto.setProbability(30);
        dto.setSort(3);
        dto.setIsWin(0);
        dto.setIsLost(0);

        when(stageMapper.insert(any(CrmOpportunityStage.class))).thenAnswer(invocation -> {
            CrmOpportunityStage stage = invocation.getArgument(0);
            stage.setId(1L);
            return 1;
        });

        // 执行
        Long result = opportunityService.saveStage(dto);

        // 验证
        assertEquals(1L, result);
        verify(stageMapper).insert(any(CrmOpportunityStage.class));
    }

    @Test
    @DisplayName("修改阶段成功")
    void testSaveStage_Success_Update() {
        // 准备现有阶段
        CrmOpportunityStage existingStage = new CrmOpportunityStage();
        existingStage.setId(1L);
        existingStage.setStageName("原阶段名");

        when(stageMapper.selectById(1L)).thenReturn(existingStage);
        when(stageMapper.updateById(any(CrmOpportunityStage.class))).thenReturn(1);

        // 准备修改数据
        StageSaveDTO dto = new StageSaveDTO();
        dto.setId(1L);
        dto.setStageName("修改后的阶段名");
        dto.setProbability(50);

        // 执行
        Long result = opportunityService.saveStage(dto);

        // 验证
        assertEquals(1L, result);
        verify(stageMapper).updateById(ArgumentMatchers.<CrmOpportunityStage>argThat(updatedStage -> {
            assertEquals("修改后的阶段名", updatedStage.getStageName());
            assertEquals(50, updatedStage.getProbability());
            return true;
        }));
    }

    // ==================== deleteOpportunity 测试 ====================

    @Test
    @DisplayName("删除商机成功")
    void testDeleteOpportunity_Success() {
        // 准备商机
        CrmOpportunity opp = new CrmOpportunity();
        opp.setId(1L);
        opp.setStatus(1); // 进行中

        when(opportunityMapper.selectById(1L)).thenReturn(opp);
        when(opportunityMapper.deleteById(1L)).thenReturn(1);

        // 执行
        opportunityService.deleteOpportunity(1L);

        // 验证
        verify(opportunityMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除已成交商机抛出异常")
    void testDeleteOpportunity_AlreadyWon() {
        // 准备已成交商机
        CrmOpportunity wonOpp = new CrmOpportunity();
        wonOpp.setId(1L);
        wonOpp.setStatus(2);

        when(opportunityMapper.selectById(1L)).thenReturn(wonOpp);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class,
                () -> opportunityService.deleteOpportunity(1L));
        assertEquals("已成交的商机不能删除", ex.getMessage());
    }

    @Test
    @DisplayName("删除不存在商机抛出异常")
    void testDeleteOpportunity_NotFound() {
        when(opportunityMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class,
                () -> opportunityService.deleteOpportunity(999L));
        assertEquals("商机不存在", ex.getMessage());
    }
}
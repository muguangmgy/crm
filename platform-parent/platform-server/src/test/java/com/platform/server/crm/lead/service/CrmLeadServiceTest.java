package com.platform.server.crm.lead.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.ResultCode;
import com.platform.common.exception.BusinessException;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.common.enums.LeadStatusEnum;
import com.platform.server.crm.customer.service.CustomerService;
import com.platform.server.crm.lead.dto.LeadConvertDTO;
import com.platform.server.crm.lead.dto.LeadQueryDTO;
import com.platform.server.crm.lead.dto.LeadSaveDTO;
import com.platform.server.crm.lead.mapper.CrmLeadMapper;
import com.platform.server.crm.lead.po.CrmLead;
import com.platform.server.crm.lead.service.impl.CrmLeadServiceImpl;
import com.platform.server.crm.lead.vo.LeadVO;
import com.platform.server.crm.opportunity.dto.OpportunitySaveDTO;
import com.platform.server.crm.opportunity.service.OpportunityService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 线索服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class CrmLeadServiceTest {

    @Mock
    private CrmLeadMapper leadMapper;

    @Mock
    private OpportunityService opportunityService;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CrmLeadServiceImpl leadService;

    private StageVO mockFirstStage() {
        StageVO stage = new StageVO();
        stage.setId(1L);
        stage.setStageName("初步接触");
        stage.setIsWin(0);
        stage.setIsLost(0);
        stage.setStatus(1);
        return stage;
    }

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

    // ==================== saveLead 测试 ====================

    @Test
    @DisplayName("正常保存新线索")
    void testSaveLead_Success_New() {
        // 准备数据
        LeadSaveDTO dto = new LeadSaveDTO();
        dto.setLeadName("测试线索");
        dto.setSource("展会");
        dto.setPhone("13800138000");
        dto.setEmail("test@example.com");
        dto.setCompany("测试公司");
        dto.setDescription("测试描述");
        dto.setOwnerId(100L);

        // Mock mapper.insert返回
        when(leadMapper.insert(any(CrmLead.class))).thenAnswer(invocation -> {
            CrmLead lead = invocation.getArgument(0);
            lead.setId(1L);
            return 1;
        });

        // 执行
        Long result = leadService.saveLead(dto);

        // 验证
        assertEquals(1L, result);
        verify(leadMapper).insert(any(CrmLead.class));
        verify(leadMapper, never()).updateById(any(CrmLead.class));
    }

    @Test
    @DisplayName("正常修改现有线索")
    void testSaveLead_Success_Update() {
        // 准备数据
        LeadSaveDTO dto = new LeadSaveDTO();
        dto.setId(1L);
        dto.setLeadName("修改后的线索名称");
        dto.setOwnerId(100L);

        // Mock查询现有线索
        CrmLead existingLead = new CrmLead();
        existingLead.setId(1L);
        existingLead.setLeadName("原线索名称");
        existingLead.setStatus(LeadStatusEnum.NEW.getCode());
        when(leadMapper.selectById(1L)).thenReturn(existingLead);
        when(leadMapper.updateById(any(CrmLead.class))).thenReturn(1);

        // 执行
        Long result = leadService.saveLead(dto);

        // 验证
        assertEquals(1L, result);
        verify(leadMapper).selectById(1L);
        verify(leadMapper).updateById(any(CrmLead.class));
        verify(leadMapper, never()).insert(any(CrmLead.class));
    }

    @Test
    @DisplayName("修改不存在的线索抛出异常")
    void testSaveLead_NotFound() {
        // 准备数据
        LeadSaveDTO dto = new LeadSaveDTO();
        dto.setId(999L);
        dto.setLeadName("测试");
        dto.setOwnerId(100L);

        // Mock查询返回null
        when(leadMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> leadService.saveLead(dto));
        assertEquals(ResultCode.NOT_FOUND.getCode(), ex.getCode());
        assertEquals("线索不存在", ex.getMessage());
    }

    @Test
    @DisplayName("修改已转商机的线索抛出异常")
    void testSaveLead_AlreadyConverted() {
        // 准备数据
        LeadSaveDTO dto = new LeadSaveDTO();
        dto.setId(1L);
        dto.setLeadName("测试");
        dto.setOwnerId(100L);

        // Mock查询已转换线索
        CrmLead convertedLead = new CrmLead();
        convertedLead.setId(1L);
        convertedLead.setStatus(LeadStatusEnum.CONVERTED.getCode());
        when(leadMapper.selectById(1L)).thenReturn(convertedLead);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> leadService.saveLead(dto));
        assertEquals("已转商机的线索不能修改", ex.getMessage());
    }

    // ==================== getLeadPage 测试 ====================

    @Test
    @DisplayName("分页查询线索列表")
    void testGetLeadPage_Pagination() {
        // 准备数据
        LeadQueryDTO query = new LeadQueryDTO();
        query.setPageNum(1);
        query.setPageSize(10);

        LeadVO leadVO = new LeadVO();
        leadVO.setId(1L);
        leadVO.setLeadName("测试线索");

        Page<LeadVO> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.singletonList(leadVO));
        mockPage.setTotal(1);

        when(leadMapper.selectLeadPage(any(Page.class), any(LeadQueryDTO.class)))
                .thenReturn(mockPage);

        // 执行
        PageVO<LeadVO> result = leadService.getLeadPage(query);

        // 验证
        assertEquals(1, result.getList().size());
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
    }

    // ==================== getLeadDetail 测试 ====================

    @Test
    @DisplayName("获取线索详情成功")
    void testGetLeadDetail_Success() {
        // 准备数据
        LeadVO leadVO = new LeadVO();
        leadVO.setId(1L);
        leadVO.setLeadName("测试线索");
        leadVO.setStatus(LeadStatusEnum.NEW.getCode());

        when(leadMapper.selectLeadById(1L)).thenReturn(leadVO);

        // 执行
        LeadVO result = leadService.getLeadDetail(1L);

        // 验证
        assertEquals(1L, result.getId());
        assertEquals("测试线索", result.getLeadName());
    }

    @Test
    @DisplayName("获取不存在的线索详情抛出异常")
    void testGetLeadDetail_NotFound() {
        // Mock查询返回null
        when(leadMapper.selectLeadById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> leadService.getLeadDetail(999L));
        assertEquals(ResultCode.NOT_FOUND.getCode(), ex.getCode());
        assertEquals("线索不存在", ex.getMessage());
    }

    // ==================== deleteLead 测试 ====================

    @Test
    @DisplayName("删除线索成功")
    void testDeleteLead_Success() {
        // 准备数据
        CrmLead lead = new CrmLead();
        lead.setId(1L);
        lead.setStatus(LeadStatusEnum.NEW.getCode());

        when(leadMapper.selectById(1L)).thenReturn(lead);
        when(leadMapper.deleteById(1L)).thenReturn(1);

        // 执行
        leadService.deleteLead(1L);

        // 验证
        verify(leadMapper).selectById(1L);
        verify(leadMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除不存在的线索抛出异常")
    void testDeleteLead_NotFound() {
        // Mock查询返回null
        when(leadMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> leadService.deleteLead(999L));
        assertEquals(ResultCode.NOT_FOUND.getCode(), ex.getCode());
        assertEquals("线索不存在", ex.getMessage());
    }

    @Test
    @DisplayName("删除已转商机的线索抛出异常")
    void testDeleteLead_AlreadyConverted() {
        // 准备数据
        CrmLead convertedLead = new CrmLead();
        convertedLead.setId(1L);
        convertedLead.setStatus(LeadStatusEnum.CONVERTED.getCode());

        when(leadMapper.selectById(1L)).thenReturn(convertedLead);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> leadService.deleteLead(1L));
        assertEquals("已转商机的线索不能删除", ex.getMessage());
        verify(leadMapper, never()).deleteById(anyLong());
    }

    // ==================== convertToOpportunity 测试 ====================

    @Test
    @DisplayName("线索转商机成功")
    void testConvertToOpportunity_Success() {
        // 准备数据
        LeadConvertDTO dto = new LeadConvertDTO();
        dto.setLeadId(1L);
        dto.setOppName("测试商机");
        dto.setExpectedAmount(new BigDecimal("100000.00"));
        dto.setExpectedDate(LocalDate.of(2026, 6, 30));

        CrmLead lead = new CrmLead();
        lead.setId(1L);
        lead.setLeadName("原线索名称");
        lead.setStatus(LeadStatusEnum.NEW.getCode());
        lead.setCustomerId(10L);
        lead.setContactId(20L);
        lead.setSource("展会");
        lead.setDescription("线索描述");
        lead.setOwnerId(100L);

        when(leadMapper.selectById(1L)).thenReturn(lead);
        when(opportunityService.getActiveStageList()).thenReturn(List.of(mockFirstStage()));
        when(opportunityService.saveOpportunity(any(OpportunitySaveDTO.class))).thenReturn(100L);
        when(leadMapper.updateById(any(CrmLead.class))).thenReturn(1);

        // 执行
        Long result = leadService.convertToOpportunity(dto);

        // 验证
        assertEquals(100L, result);
        verify(opportunityService).saveOpportunity(argThat(oppDto -> {
            assertEquals("测试商机", oppDto.getOppName());
            assertEquals(10L, oppDto.getCustomerId());
            assertEquals(new BigDecimal("100000.00"), oppDto.getExpectedAmount());
            assertEquals(100L, oppDto.getOwnerId());
            return true;
        }));
        verify(leadMapper).updateById(ArgumentMatchers.<CrmLead>argThat(updatedLead -> {
            assertEquals(LeadStatusEnum.CONVERTED.getCode(), updatedLead.getStatus());
            assertEquals(100L, updatedLead.getConvertedOppId());
            assertEquals(10L, updatedLead.getCustomerId());
            assertNotNull(updatedLead.getConvertedTime());
            return true;
        }));
    }

    @Test
    @DisplayName("线索转商机-使用线索默认值")
    void testConvertToOpportunity_UseDefaultValues() {
        // 准备数据 - 不填写商机名称等，应使用线索的值
        LeadConvertDTO dto = new LeadConvertDTO();
        dto.setLeadId(1L);
        // 不设置oppName、expectedAmount等

        CrmLead lead = new CrmLead();
        lead.setId(1L);
        lead.setLeadName("线索名称作为商机名称");
        lead.setStatus(LeadStatusEnum.FOLLOWING.getCode());
        lead.setCustomerId(10L);
        lead.setContactId(20L);
        lead.setSource("官网");
        lead.setDescription("线索描述");
        lead.setOwnerId(100L);

        when(leadMapper.selectById(1L)).thenReturn(lead);
        when(opportunityService.getActiveStageList()).thenReturn(List.of(mockFirstStage()));
        when(opportunityService.saveOpportunity(any(OpportunitySaveDTO.class))).thenReturn(100L);
        when(leadMapper.updateById(any(CrmLead.class))).thenReturn(1);

        // 执行
        Long result = leadService.convertToOpportunity(dto);

        // 验证 - 商机名称使用线索名称
        assertEquals(100L, result);
        verify(opportunityService).saveOpportunity(argThat(oppDto -> {
            assertEquals("线索名称作为商机名称", oppDto.getOppName());
            assertEquals(10L, oppDto.getCustomerId());
            assertEquals("官网", oppDto.getSource());
            return true;
        }));
    }

    @Test
    @DisplayName("线索转商机-入参客户回写到线索")
    void testConvertToOpportunity_WriteBackCustomerIdFromDto() {
        LeadConvertDTO dto = new LeadConvertDTO();
        dto.setLeadId(1L);
        dto.setOppName("新商机");
        dto.setCustomerId(99L);

        CrmLead lead = new CrmLead();
        lead.setId(1L);
        lead.setLeadName("无线索客户");
        lead.setStatus(LeadStatusEnum.NEW.getCode());
        lead.setOwnerId(100L);
        // 线索本身无客户

        when(leadMapper.selectById(1L)).thenReturn(lead);
        when(opportunityService.getActiveStageList()).thenReturn(List.of(mockFirstStage()));
        when(opportunityService.saveOpportunity(any(OpportunitySaveDTO.class))).thenReturn(200L);
        when(leadMapper.updateById(any(CrmLead.class))).thenReturn(1);

        Long result = leadService.convertToOpportunity(dto);

        assertEquals(200L, result);
        verify(customerService, never()).saveCustomer(any());
        verify(leadMapper).updateById(ArgumentMatchers.<CrmLead>argThat(updatedLead -> {
            assertEquals(99L, updatedLead.getCustomerId());
            assertEquals(200L, updatedLead.getConvertedOppId());
            return true;
        }));
    }

    @Test
    @DisplayName("线索转商机-无线索客户时自动创建并回写")
    void testConvertToOpportunity_AutoCreateCustomer() {
        LeadConvertDTO dto = new LeadConvertDTO();
        dto.setLeadId(1L);
        dto.setOppName("自动创建客户商机");

        CrmLead lead = new CrmLead();
        lead.setId(1L);
        lead.setLeadName("张三要买电脑");
        lead.setCompany("张三公司");
        lead.setStatus(LeadStatusEnum.NEW.getCode());
        lead.setOwnerId(100L);

        when(leadMapper.selectById(1L)).thenReturn(lead);
        when(customerService.saveCustomer(any())).thenReturn(88L);
        when(opportunityService.getActiveStageList()).thenReturn(List.of(mockFirstStage()));
        when(opportunityService.saveOpportunity(any(OpportunitySaveDTO.class))).thenReturn(300L);
        when(leadMapper.updateById(any(CrmLead.class))).thenReturn(1);

        Long result = leadService.convertToOpportunity(dto);

        assertEquals(300L, result);
        verify(customerService).saveCustomer(argThat(customerDto -> {
            assertEquals("张三公司", customerDto.getCustomerName());
            return true;
        }));
        verify(leadMapper).updateById(ArgumentMatchers.<CrmLead>argThat(updatedLead ->
                Long.valueOf(88L).equals(updatedLead.getCustomerId())));
    }

    @Test
    @DisplayName("已转换的线索不能再转商机")
    void testConvertToOpportunity_AlreadyConverted() {
        // 准备数据
        LeadConvertDTO dto = new LeadConvertDTO();
        dto.setLeadId(1L);

        CrmLead convertedLead = new CrmLead();
        convertedLead.setId(1L);
        convertedLead.setStatus(LeadStatusEnum.CONVERTED.getCode());

        when(leadMapper.selectById(1L)).thenReturn(convertedLead);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> leadService.convertToOpportunity(dto));
        assertEquals("线索已转换为商机", ex.getMessage());
        verify(opportunityService, never()).saveOpportunity(any());
    }

    @Test
    @DisplayName("已失效的线索不能转商机")
    void testConvertToOpportunity_AlreadyInvalid() {
        // 准备数据
        LeadConvertDTO dto = new LeadConvertDTO();
        dto.setLeadId(1L);

        CrmLead invalidLead = new CrmLead();
        invalidLead.setId(1L);
        invalidLead.setStatus(LeadStatusEnum.INVALID.getCode());

        when(leadMapper.selectById(1L)).thenReturn(invalidLead);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> leadService.convertToOpportunity(dto));
        assertEquals("已失效的线索不能转换为商机", ex.getMessage());
        verify(opportunityService, never()).saveOpportunity(any());
    }

    @Test
    @DisplayName("线索不存在时转商机抛出异常")
    void testConvertToOpportunity_InvalidLeadId() {
        // 准备数据
        LeadConvertDTO dto = new LeadConvertDTO();
        dto.setLeadId(999L);

        // Mock查询返回null
        when(leadMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> leadService.convertToOpportunity(dto));
        assertEquals(ResultCode.NOT_FOUND.getCode(), ex.getCode());
        assertEquals("线索不存在", ex.getMessage());
    }
}
package com.platform.server.crm.contract.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.context.UserContextHolder;
import com.platform.common.exception.BusinessException;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.common.enums.ContractStatusEnum;
import com.platform.server.crm.contract.dto.ContractQueryDTO;
import com.platform.server.crm.contract.dto.ContractSaveDTO;
import com.platform.server.crm.contract.mapper.CrmContractMapper;
import com.platform.server.crm.contract.po.CrmContract;
import com.platform.server.crm.contract.service.impl.ContractServiceImpl;
import com.platform.server.crm.contract.vo.ContractVO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 合同服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock
    private CrmContractMapper contractMapper;

    @InjectMocks
    private ContractServiceImpl contractService;

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

    // ==================== saveContract 测试 ====================

    @Test
    @DisplayName("正常保存新合同")
    void testSaveContract_Success_New() {
        // Mock mapper.insert返回
        when(contractMapper.insert(any(CrmContract.class))).thenAnswer(invocation -> {
            CrmContract contract = invocation.getArgument(0);
            contract.setId(1L);
            return 1;
        });
        when(contractMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // 准备合同数据
        ContractSaveDTO dto = new ContractSaveDTO();
        dto.setCustomerId(10L);
        dto.setContractName("测试合同");
        dto.setContractAmount(new BigDecimal("100000.00"));
        dto.setStartDate(LocalDate.of(2026, 5, 1));
        dto.setEndDate(LocalDate.of(2027, 4, 30));
        dto.setOwnerId(100L);

        // 执行
        Long result = contractService.saveContract(dto);

        // 验证
        assertEquals(1L, result);
        verify(contractMapper).insert(any(CrmContract.class));
    }

    @Test
    @DisplayName("正常修改现有合同（草稿状态）")
    void testSaveContract_Success_Update() {
        // 准备现有合同（草稿状态）
        CrmContract existingContract = new CrmContract();
        existingContract.setId(1L);
        existingContract.setStatus(ContractStatusEnum.DRAFT.getCode());
        existingContract.setContractNo("HT202604300001");

        when(contractMapper.selectById(1L)).thenReturn(existingContract);
        when(contractMapper.updateById(any(CrmContract.class))).thenReturn(1);

        // 准备修改数据
        ContractSaveDTO dto = new ContractSaveDTO();
        dto.setId(1L);
        dto.setCustomerId(10L);
        dto.setContractName("修改后的合同名称");
        dto.setContractAmount(new BigDecimal("150000.00"));
        dto.setStartDate(LocalDate.of(2026, 5, 1));
        dto.setEndDate(LocalDate.of(2027, 4, 30));
        dto.setOwnerId(100L);

        // 执行
        Long result = contractService.saveContract(dto);

        // 验证
        assertEquals(1L, result);
        verify(contractMapper).selectById(1L);
        verify(contractMapper).updateById(any(CrmContract.class));
    }

    @Test
    @DisplayName("修改不存在的合同抛出异常")
    void testSaveContract_NotFound() {
        // Mock查询返回null
        when(contractMapper.selectById(999L)).thenReturn(null);

        // 准备修改数据
        ContractSaveDTO dto = new ContractSaveDTO();
        dto.setId(999L);
        dto.setCustomerId(10L);
        dto.setContractName("测试");
        dto.setContractAmount(new BigDecimal("100000.00"));
        dto.setStartDate(LocalDate.of(2026, 5, 1));
        dto.setEndDate(LocalDate.of(2027, 4, 30));
        dto.setOwnerId(100L);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.saveContract(dto));
        assertEquals("合同不存在", ex.getMessage());
    }

    @Test
    @DisplayName("修改审批中状态的合同抛出异常")
    void testSaveContract_ApprovalStatus() {
        // 准备审批中状态的合同
        CrmContract approvalContract = new CrmContract();
        approvalContract.setId(1L);
        approvalContract.setStatus(ContractStatusEnum.APPROVAL.getCode());

        when(contractMapper.selectById(1L)).thenReturn(approvalContract);

        // 准备修改数据
        ContractSaveDTO dto = new ContractSaveDTO();
        dto.setId(1L);
        dto.setCustomerId(10L);
        dto.setContractName("测试");
        dto.setContractAmount(new BigDecimal("100000.00"));
        dto.setStartDate(LocalDate.of(2026, 5, 1));
        dto.setEndDate(LocalDate.of(2027, 4, 30));
        dto.setOwnerId(100L);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.saveContract(dto));
        assertEquals("只有草稿状态的合同才能修改", ex.getMessage());
    }

    @Test
    @DisplayName("修改生效状态的合同抛出异常")
    void testSaveContract_EffectiveStatus() {
        // 准备生效状态的合同
        CrmContract effectiveContract = new CrmContract();
        effectiveContract.setId(1L);
        effectiveContract.setStatus(ContractStatusEnum.EFFECTIVE.getCode());

        when(contractMapper.selectById(1L)).thenReturn(effectiveContract);

        // 准备修改数据
        ContractSaveDTO dto = new ContractSaveDTO();
        dto.setId(1L);
        dto.setCustomerId(10L);
        dto.setContractName("测试");
        dto.setContractAmount(new BigDecimal("100000.00"));
        dto.setStartDate(LocalDate.of(2026, 5, 1));
        dto.setEndDate(LocalDate.of(2027, 4, 30));
        dto.setOwnerId(100L);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.saveContract(dto));
        assertEquals("只有草稿状态的合同才能修改", ex.getMessage());
    }

    // ==================== getContractPage 测试 ====================

    @Test
    @DisplayName("分页查询合同列表")
    void testGetContractPage_Pagination() {
        // 准备查询条件
        ContractQueryDTO query = new ContractQueryDTO();
        query.setPageNum(1);
        query.setPageSize(10);

        ContractVO contractVO = new ContractVO();
        contractVO.setId(1L);
        contractVO.setContractName("测试合同");
        contractVO.setStatus(ContractStatusEnum.DRAFT.getCode());

        Page<ContractVO> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.singletonList(contractVO));
        mockPage.setTotal(1);

        when(contractMapper.selectContractPage(any(), any(ContractQueryDTO.class)))
                .thenReturn(mockPage);

        // 执行
        PageVO<ContractVO> result = contractService.getContractPage(query);

        // 验证
        assertEquals(1, result.getList().size());
        assertEquals(1L, result.getTotal());
    }

    // ==================== getContractById 测试 ====================

    @Test
    @DisplayName("获取合同详情成功")
    void testGetContractById_Success() {
        // 准备合同详情
        ContractVO contractVO = new ContractVO();
        contractVO.setId(1L);
        contractVO.setContractName("测试合同");
        contractVO.setStatus(ContractStatusEnum.DRAFT.getCode());

        when(contractMapper.selectContractById(1L)).thenReturn(contractVO);

        // 执行
        ContractVO result = contractService.getContractById(1L);

        // 验证
        assertEquals(1L, result.getId());
        assertEquals("测试合同", result.getContractName());
    }

    @Test
    @DisplayName("获取不存在的合同详情抛出异常")
    void testGetContractById_NotFound() {
        // Mock查询返回null
        when(contractMapper.selectContractById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.getContractById(999L));
        assertEquals("合同不存在", ex.getMessage());
    }

    // ==================== deleteContract 测试 ====================

    @Test
    @DisplayName("删除草稿状态合同成功")
    void testDeleteContract_Success() {
        // 准备草稿状态合同
        CrmContract contract = new CrmContract();
        contract.setId(1L);
        contract.setStatus(ContractStatusEnum.DRAFT.getCode());

        when(contractMapper.selectById(1L)).thenReturn(contract);
        when(contractMapper.deleteById(anyLong())).thenReturn(1);

        // 执行
        contractService.deleteContract(1L);

        // 验证
        verify(contractMapper).selectById(1L);
        verify(contractMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除不存在的合同抛出异常")
    void testDeleteContract_NotFound() {
        // Mock查询返回null
        when(contractMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.deleteContract(999L));
        assertEquals("合同不存在", ex.getMessage());
    }

    @Test
    @DisplayName("删除审批中状态的合同抛出异常")
    void testDeleteContract_ApprovalStatus() {
        // 准备审批中状态合同
        CrmContract contract = new CrmContract();
        contract.setId(1L);
        contract.setStatus(ContractStatusEnum.APPROVAL.getCode());

        when(contractMapper.selectById(1L)).thenReturn(contract);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.deleteContract(1L));
        assertEquals("只有草稿状态的合同才能删除", ex.getMessage());
    }

    @Test
    @DisplayName("删除生效状态的合同抛出异常")
    void testDeleteContract_EffectiveStatus() {
        // 准备生效状态合同
        CrmContract contract = new CrmContract();
        contract.setId(1L);
        contract.setStatus(ContractStatusEnum.EFFECTIVE.getCode());

        when(contractMapper.selectById(1L)).thenReturn(contract);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.deleteContract(1L));
        assertEquals("只有草稿状态的合同才能删除", ex.getMessage());
    }

    // ==================== submitContract 测试 ====================

    @Test
    @DisplayName("提交审批成功（草稿→审批中）")
    void testSubmitContract_Success() {
        // 准备草稿状态合同
        CrmContract contract = new CrmContract();
        contract.setId(1L);
        contract.setStatus(ContractStatusEnum.DRAFT.getCode());

        when(contractMapper.selectById(1L)).thenReturn(contract);
        when(contractMapper.updateById(any(CrmContract.class))).thenReturn(1);

        // 执行
        contractService.submitContract(1L);

        // 验证
        verify(contractMapper).updateById(ArgumentMatchers.<CrmContract>argThat(updatedContract -> {
            assertEquals(ContractStatusEnum.APPROVAL.getCode(), updatedContract.getStatus());
            return true;
        }));
    }

    @Test
    @DisplayName("提交审批不存在的合同抛出异常")
    void testSubmitContract_NotFound() {
        // Mock查询返回null
        when(contractMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.submitContract(999L));
        assertEquals("合同不存在", ex.getMessage());
    }

    @Test
    @DisplayName("非草稿状态提交审批抛出异常")
    void testSubmitContract_NotDraft() {
        // 准备生效状态合同
        CrmContract contract = new CrmContract();
        contract.setId(1L);
        contract.setStatus(ContractStatusEnum.EFFECTIVE.getCode());

        when(contractMapper.selectById(1L)).thenReturn(contract);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.submitContract(1L));
        assertEquals("只有草稿状态的合同才能提交审批", ex.getMessage());
    }

    // ==================== approveContract 测试 ====================

    @Test
    @DisplayName("审批通过成功（审批中→生效）")
    void testApproveContract_Success() {
        // 准备审批中状态合同
        CrmContract contract = new CrmContract();
        contract.setId(1L);
        contract.setStatus(ContractStatusEnum.APPROVAL.getCode());

        when(contractMapper.selectById(1L)).thenReturn(contract);
        when(contractMapper.updateById(any(CrmContract.class))).thenReturn(1);

        // 执行
        contractService.approveContract(1L);

        // 验证
        verify(contractMapper).updateById(ArgumentMatchers.<CrmContract>argThat(updatedContract -> {
            assertEquals(ContractStatusEnum.EFFECTIVE.getCode(), updatedContract.getStatus());
            assertNotNull(updatedContract.getSignDate());
            return true;
        }));
    }

    @Test
    @DisplayName("审批通过不存在的合同抛出异常")
    void testApproveContract_NotFound() {
        // Mock查询返回null
        when(contractMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.approveContract(999L));
        assertEquals("合同不存在", ex.getMessage());
    }

    @Test
    @DisplayName("非审批中状态审批通过抛出异常")
    void testApproveContract_NotApproval() {
        // 准备草稿状态合同
        CrmContract contract = new CrmContract();
        contract.setId(1L);
        contract.setStatus(ContractStatusEnum.DRAFT.getCode());

        when(contractMapper.selectById(1L)).thenReturn(contract);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.approveContract(1L));
        assertEquals("只有审批中状态的合同才能审批通过", ex.getMessage());
    }

    @Test
    @DisplayName("生效状态审批通过抛出异常")
    void testApproveContract_AlreadyEffective() {
        // 准备生效状态合同
        CrmContract contract = new CrmContract();
        contract.setId(1L);
        contract.setStatus(ContractStatusEnum.EFFECTIVE.getCode());

        when(contractMapper.selectById(1L)).thenReturn(contract);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.approveContract(1L));
        assertEquals("只有审批中状态的合同才能审批通过", ex.getMessage());
    }

    // ==================== rejectContract 测试 ====================

    @Test
    @DisplayName("审批驳回成功（审批中→草稿）")
    void testRejectContract_Success() {
        // 准备审批中状态合同
        CrmContract contract = new CrmContract();
        contract.setId(1L);
        contract.setStatus(ContractStatusEnum.APPROVAL.getCode());

        when(contractMapper.selectById(1L)).thenReturn(contract);
        when(contractMapper.updateById(any(CrmContract.class))).thenReturn(1);

        // 执行
        contractService.rejectContract(1L);

        // 验证
        verify(contractMapper).updateById(ArgumentMatchers.<CrmContract>argThat(updatedContract -> {
            assertEquals(ContractStatusEnum.DRAFT.getCode(), updatedContract.getStatus());
            return true;
        }));
    }

    @Test
    @DisplayName("审批驳回不存在的合同抛出异常")
    void testRejectContract_NotFound() {
        // Mock查询返回null
        when(contractMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.rejectContract(999L));
        assertEquals("合同不存在", ex.getMessage());
    }

    @Test
    @DisplayName("非审批中状态驳回抛出异常")
    void testRejectContract_NotApproval() {
        // 准备草稿状态合同
        CrmContract contract = new CrmContract();
        contract.setId(1L);
        contract.setStatus(ContractStatusEnum.DRAFT.getCode());

        when(contractMapper.selectById(1L)).thenReturn(contract);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.rejectContract(1L));
        assertEquals("只有审批中状态的合同才能驳回", ex.getMessage());
    }

    // ==================== terminateContract 测试 ====================

    @Test
    @DisplayName("终止合同成功（生效→终止）")
    void testTerminateContract_Success() {
        // 准备生效状态合同
        CrmContract contract = new CrmContract();
        contract.setId(1L);
        contract.setStatus(ContractStatusEnum.EFFECTIVE.getCode());

        when(contractMapper.selectById(1L)).thenReturn(contract);
        when(contractMapper.updateById(any(CrmContract.class))).thenReturn(1);

        // 执行
        contractService.terminateContract(1L);

        // 验证
        verify(contractMapper).updateById(ArgumentMatchers.<CrmContract>argThat(updatedContract -> {
            assertEquals(ContractStatusEnum.TERMINATED.getCode(), updatedContract.getStatus());
            return true;
        }));
    }

    @Test
    @DisplayName("终止不存在的合同抛出异常")
    void testTerminateContract_NotFound() {
        // Mock查询返回null
        when(contractMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.terminateContract(999L));
        assertEquals("合同不存在", ex.getMessage());
    }

    @Test
    @DisplayName("非生效状态终止合同抛出异常")
    void testTerminateContract_NotEffective() {
        // 准备草稿状态合同
        CrmContract contract = new CrmContract();
        contract.setId(1L);
        contract.setStatus(ContractStatusEnum.DRAFT.getCode());

        when(contractMapper.selectById(1L)).thenReturn(contract);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.terminateContract(1L));
        assertEquals("只有生效状态的合同才能终止", ex.getMessage());
    }

    @Test
    @DisplayName("审批中状态终止合同抛出异常")
    void testTerminateContract_ApprovalStatus() {
        // 准备审批中状态合同
        CrmContract contract = new CrmContract();
        contract.setId(1L);
        contract.setStatus(ContractStatusEnum.APPROVAL.getCode());

        when(contractMapper.selectById(1L)).thenReturn(contract);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.terminateContract(1L));
        assertEquals("只有生效状态的合同才能终止", ex.getMessage());
    }

    @Test
    @DisplayName("已终止状态终止合同抛出异常")
    void testTerminateContract_AlreadyTerminated() {
        // 准备终止状态合同
        CrmContract contract = new CrmContract();
        contract.setId(1L);
        contract.setStatus(ContractStatusEnum.TERMINATED.getCode());

        when(contractMapper.selectById(1L)).thenReturn(contract);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> contractService.terminateContract(1L));
        assertEquals("只有生效状态的合同才能终止", ex.getMessage());
    }
}
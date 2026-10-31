package com.platform.server.crm.invoice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.context.UserContextHolder;
import com.platform.common.exception.BusinessException;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.invoice.dto.InvoiceQueryDTO;
import com.platform.server.crm.invoice.dto.InvoiceSaveDTO;
import com.platform.server.crm.invoice.mapper.InvoiceMapper;
import com.platform.server.crm.invoice.po.CrmInvoice;
import com.platform.server.crm.invoice.service.impl.InvoiceServiceImpl;
import com.platform.server.crm.invoice.vo.InvoiceVO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 发票服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

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

    // 发票状态常量
    private static final int STATUS_INVOICED = 1;  // 已开票
    private static final int STATUS_CANCELLED = 2; // 已作废

    // ==================== saveInvoice 测试 ====================

    @Test
    @DisplayName("正常保存新发票")
    void testSaveInvoice_Success_New() {
        // Mock mapper.insert返回
        when(invoiceMapper.insert(any(CrmInvoice.class))).thenAnswer(invocation -> {
            CrmInvoice invoice = invocation.getArgument(0);
            invoice.setId(1L);
            return 1;
        });

        // 准备发票数据
        InvoiceSaveDTO dto = new InvoiceSaveDTO();
        dto.setContractId(10L);
        dto.setInvoiceType(1); // 普通发票
        dto.setInvoiceAmount(new BigDecimal("10000.00"));
        dto.setInvoiceDate(LocalDate.of(2026, 4, 30));

        // 执行
        Long result = invoiceService.saveInvoice(dto);

        // 验证
        assertEquals(1L, result);
        verify(invoiceMapper).insert(any(CrmInvoice.class));
        verify(invoiceMapper, never()).updateById(any(CrmInvoice.class));
    }

    @Test
    @DisplayName("正常修改已开票状态发票")
    void testSaveInvoice_Success_Update() {
        // 准备现有发票（已开票状态）
        CrmInvoice existingInvoice = new CrmInvoice();
        existingInvoice.setId(1L);
        existingInvoice.setInvoiceStatus(STATUS_INVOICED);
        existingInvoice.setInvoiceNo("INV202604300001");

        when(invoiceMapper.selectById(1L)).thenReturn(existingInvoice);
        when(invoiceMapper.updateById(any(CrmInvoice.class))).thenReturn(1);

        // 准备修改数据
        InvoiceSaveDTO dto = new InvoiceSaveDTO();
        dto.setId(1L);
        dto.setContractId(10L);
        dto.setInvoiceType(2); // 增值税专用发票
        dto.setInvoiceAmount(new BigDecimal("15000.00"));
        dto.setInvoiceDate(LocalDate.of(2026, 5, 1));

        // 执行
        Long result = invoiceService.saveInvoice(dto);

        // 验证
        assertEquals(1L, result);
        verify(invoiceMapper).selectById(1L);
        verify(invoiceMapper).updateById(any(CrmInvoice.class));
        verify(invoiceMapper, never()).insert(any(CrmInvoice.class));
    }

    @Test
    @DisplayName("修改不存在的发票抛出异常")
    void testSaveInvoice_NotFound() {
        // Mock查询返回null
        when(invoiceMapper.selectById(999L)).thenReturn(null);

        // 准备修改数据
        InvoiceSaveDTO dto = new InvoiceSaveDTO();
        dto.setId(999L);
        dto.setContractId(10L);
        dto.setInvoiceType(1);
        dto.setInvoiceAmount(new BigDecimal("10000.00"));
        dto.setInvoiceDate(LocalDate.of(2026, 4, 30));

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> invoiceService.saveInvoice(dto));
        assertEquals("发票不存在", ex.getMessage());
    }

    @Test
    @DisplayName("修改已作废状态发票抛出异常")
    void testSaveInvoice_CancelledStatus() {
        // 准备已作废状态发票
        CrmInvoice cancelledInvoice = new CrmInvoice();
        cancelledInvoice.setId(1L);
        cancelledInvoice.setInvoiceStatus(STATUS_CANCELLED);

        when(invoiceMapper.selectById(1L)).thenReturn(cancelledInvoice);

        // 准备修改数据
        InvoiceSaveDTO dto = new InvoiceSaveDTO();
        dto.setId(1L);
        dto.setContractId(10L);
        dto.setInvoiceType(1);
        dto.setInvoiceAmount(new BigDecimal("10000.00"));
        dto.setInvoiceDate(LocalDate.of(2026, 4, 30));

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> invoiceService.saveInvoice(dto));
        assertEquals("只有已开票状态的发票才能修改", ex.getMessage());
    }

    // ==================== getInvoicePage 测试 ====================

    @Test
    @DisplayName("分页查询发票列表")
    void testGetInvoicePage_Pagination() {
        // 准备查询条件
        InvoiceQueryDTO query = new InvoiceQueryDTO();
        query.setPageNum(1);
        query.setPageSize(10);

        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceNo("INV001");
        invoice.setContractId(10L);
        invoice.setInvoiceStatus(STATUS_INVOICED);

        Page<CrmInvoice> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.singletonList(invoice));
        mockPage.setTotal(1);

        when(invoiceMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);

        // 执行
        PageVO<InvoiceVO> result = invoiceService.getInvoicePage(query);

        // 验证
        assertEquals(1, result.getList().size());
        assertEquals(1L, result.getTotal());
    }

    @Test
    @DisplayName("分页查询带条件过滤")
    void testGetInvoicePage_WithConditions() {
        // 准备查询条件
        InvoiceQueryDTO query = new InvoiceQueryDTO();
        query.setPageNum(1);
        query.setPageSize(10);
        query.setInvoiceNo("INV");
        query.setContractId(10L);
        query.setInvoiceType(1);
        query.setInvoiceStatus(STATUS_INVOICED);

        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceNo("INV001");

        Page<CrmInvoice> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.singletonList(invoice));
        mockPage.setTotal(1);

        when(invoiceMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);

        // 执行
        PageVO<InvoiceVO> result = invoiceService.getInvoicePage(query);

        // 验证
        assertEquals(1, result.getList().size());
        verify(invoiceMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    // ==================== getInvoiceById 测试 ====================

    @Test
    @DisplayName("获取发票详情成功")
    void testGetInvoiceById_Success() {
        // 准备发票数据
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceNo("INV001");
        invoice.setContractId(10L);
        invoice.setInvoiceType(1);
        invoice.setInvoiceAmount(new BigDecimal("10000.00"));
        invoice.setInvoiceStatus(STATUS_INVOICED);

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);

        // 执行
        InvoiceVO result = invoiceService.getInvoiceById(1L);

        // 验证
        assertEquals(1L, result.getId());
        assertEquals("INV001", result.getInvoiceNo());
        assertEquals("已开票", result.getInvoiceStatusName());
        assertEquals("普通发票", result.getInvoiceTypeName());
    }

    @Test
    @DisplayName("获取增值税专用发票详情")
    void testGetInvoiceById_VatInvoice() {
        // 准备增值税专用发票
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceNo("INV001");
        invoice.setInvoiceType(2); // 增值税专用发票
        invoice.setInvoiceStatus(STATUS_INVOICED);

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);

        // 执行
        InvoiceVO result = invoiceService.getInvoiceById(1L);

        // 验证
        assertEquals("增值税专用发票", result.getInvoiceTypeName());
    }

    @Test
    @DisplayName("获取作废状态发票详情")
    void testGetInvoiceById_CancelledStatus() {
        // 准备已作废发票
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceNo("INV001");
        invoice.setInvoiceStatus(STATUS_CANCELLED);

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);

        // 执行
        InvoiceVO result = invoiceService.getInvoiceById(1L);

        // 验证
        assertEquals("已作废", result.getInvoiceStatusName());
    }

    @Test
    @DisplayName("获取不存在的发票详情抛出异常")
    void testGetInvoiceById_NotFound() {
        // Mock查询返回null
        when(invoiceMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> invoiceService.getInvoiceById(999L));
        assertEquals("发票不存在", ex.getMessage());
    }

    // ==================== deleteInvoice 测试 ====================

    @Test
    @DisplayName("删除已开票状态发票成功")
    void testDeleteInvoice_Success() {
        // 准备已开票状态发票
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceStatus(STATUS_INVOICED);

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);
        when(invoiceMapper.deleteById(1L)).thenReturn(1);

        // 执行
        invoiceService.deleteInvoice(1L);

        // 验证
        verify(invoiceMapper).selectById(1L);
        verify(invoiceMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除不存在的发票抛出异常")
    void testDeleteInvoice_NotFound() {
        // Mock查询返回null
        when(invoiceMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> invoiceService.deleteInvoice(999L));
        assertEquals("发票不存在", ex.getMessage());
    }

    @Test
    @DisplayName("删除已作废状态发票抛出异常")
    void testDeleteInvoice_CancelledStatus() {
        // 准备已作废状态发票
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceStatus(STATUS_CANCELLED);

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> invoiceService.deleteInvoice(1L));
        assertEquals("只有已开票状态的发票才能删除", ex.getMessage());
        verify(invoiceMapper, never()).deleteById(anyLong());
    }

    // ==================== cancelInvoice 测试 ====================

    @Test
    @DisplayName("作废已开票状态发票成功")
    void testCancelInvoice_Success() {
        // 准备已开票状态发票
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceStatus(STATUS_INVOICED);

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);
        when(invoiceMapper.updateById(any(CrmInvoice.class))).thenReturn(1);

        // 执行
        invoiceService.cancelInvoice(1L);

        // 验证
        verify(invoiceMapper).updateById(ArgumentMatchers.<CrmInvoice>argThat(updatedInvoice -> {
            assertEquals(STATUS_CANCELLED, updatedInvoice.getInvoiceStatus());
            return true;
        }));
    }

    @Test
    @DisplayName("作废不存在的发票抛出异常")
    void testCancelInvoice_NotFound() {
        // Mock查询返回null
        when(invoiceMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> invoiceService.cancelInvoice(999L));
        assertEquals("发票不存在", ex.getMessage());
    }

    @Test
    @DisplayName("作废已作废状态发票抛出异常")
    void testCancelInvoice_AlreadyCancelled() {
        // 准备已作废状态发票
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceStatus(STATUS_CANCELLED);

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class, () -> invoiceService.cancelInvoice(1L));
        assertEquals("只有已开票状态的发票才能作废", ex.getMessage());
        verify(invoiceMapper, never()).updateById(any(CrmInvoice.class));
    }

    // ==================== 状态转换名称测试 ====================

    @Test
    @DisplayName("发票类型名称转换-普通发票")
    void testInvoiceTypeName_Normal() {
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceType(1);
        invoice.setInvoiceStatus(STATUS_INVOICED);

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);

        InvoiceVO result = invoiceService.getInvoiceById(1L);

        assertEquals("普通发票", result.getInvoiceTypeName());
    }

    @Test
    @DisplayName("发票类型名称转换-增值税专用发票")
    void testInvoiceTypeName_Vat() {
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceType(2);
        invoice.setInvoiceStatus(STATUS_INVOICED);

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);

        InvoiceVO result = invoiceService.getInvoiceById(1L);

        assertEquals("增值税专用发票", result.getInvoiceTypeName());
    }

    @Test
    @DisplayName("发票类型名称转换-未知类型")
    void testInvoiceTypeName_Unknown() {
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceType(99); // 未知类型
        invoice.setInvoiceStatus(STATUS_INVOICED);

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);

        InvoiceVO result = invoiceService.getInvoiceById(1L);

        assertEquals("", result.getInvoiceTypeName());
    }

    @Test
    @DisplayName("发票类型名称转换-空类型")
    void testInvoiceTypeName_Null() {
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceType(null);
        invoice.setInvoiceStatus(STATUS_INVOICED);

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);

        InvoiceVO result = invoiceService.getInvoiceById(1L);

        assertEquals("", result.getInvoiceTypeName());
    }

    @Test
    @DisplayName("发票状态名称转换-已开票")
    void testInvoiceStatusName_Invoiced() {
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceStatus(STATUS_INVOICED);

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);

        InvoiceVO result = invoiceService.getInvoiceById(1L);

        assertEquals("已开票", result.getInvoiceStatusName());
    }

    @Test
    @DisplayName("发票状态名称转换-已作废")
    void testInvoiceStatusName_Cancelled() {
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceStatus(STATUS_CANCELLED);

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);

        InvoiceVO result = invoiceService.getInvoiceById(1L);

        assertEquals("已作废", result.getInvoiceStatusName());
    }

    @Test
    @DisplayName("发票状态名称转换-未知状态")
    void testInvoiceStatusName_Unknown() {
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceStatus(99); // 未知状态

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);

        InvoiceVO result = invoiceService.getInvoiceById(1L);

        assertEquals("", result.getInvoiceStatusName());
    }

    @Test
    @DisplayName("发票状态名称转换-空状态")
    void testInvoiceStatusName_Null() {
        CrmInvoice invoice = new CrmInvoice();
        invoice.setId(1L);
        invoice.setInvoiceStatus(null);

        when(invoiceMapper.selectById(1L)).thenReturn(invoice);

        InvoiceVO result = invoiceService.getInvoiceById(1L);

        assertEquals("", result.getInvoiceStatusName());
    }
}
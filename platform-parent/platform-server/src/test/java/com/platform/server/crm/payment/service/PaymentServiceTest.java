package com.platform.server.crm.payment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.context.UserContextHolder;
import com.platform.common.exception.BusinessException;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.payment.dto.PaymentPlanQueryDTO;
import com.platform.server.crm.payment.dto.PaymentPlanSaveDTO;
import com.platform.server.crm.payment.dto.PaymentRecordQueryDTO;
import com.platform.server.crm.payment.dto.PaymentRecordSaveDTO;
import com.platform.server.crm.payment.mapper.PaymentPlanMapper;
import com.platform.server.crm.payment.mapper.PaymentRecordMapper;
import com.platform.server.crm.payment.po.CrmPaymentPlan;
import com.platform.server.crm.payment.po.CrmPaymentRecord;
import com.platform.server.crm.payment.service.impl.PaymentPlanServiceImpl;
import com.platform.server.crm.payment.service.impl.PaymentRecordServiceImpl;
import com.platform.server.crm.payment.vo.PaymentPlanVO;
import com.platform.server.crm.payment.vo.PaymentRecordVO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 收款服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

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

    // ==================== 收款计划测试 ====================

    @Nested
    @DisplayName("收款计划测试")
    class PaymentPlanTests {

        @Mock
        private PaymentPlanMapper paymentPlanMapper;

        @InjectMocks
        private PaymentPlanServiceImpl paymentPlanService;

        @Test
        @DisplayName("正常保存新收款计划")
        void testSavePlan_Success_New() {
            // Mock mapper.insert返回
            when(paymentPlanMapper.insert(any(CrmPaymentPlan.class))).thenAnswer(invocation -> {
                CrmPaymentPlan plan = invocation.getArgument(0);
                plan.setId(1L);
                return 1;
            });

            // 准备计划数据
            PaymentPlanSaveDTO dto = new PaymentPlanSaveDTO();
            dto.setContractId(10L);
            dto.setPlanAmount(new BigDecimal("50000.00"));
            dto.setPlanDate(LocalDate.of(2026, 5, 30));

            // 执行
            Long result = paymentPlanService.savePlan(dto);

            // 验证
            assertEquals(1L, result);
            verify(paymentPlanMapper).insert(any(CrmPaymentPlan.class));
        }

        @Test
        @DisplayName("正常修改现有收款计划")
        void testSavePlan_Success_Update() {
            // 准备现有计划
            CrmPaymentPlan existingPlan = new CrmPaymentPlan();
            existingPlan.setId(1L);
            existingPlan.setContractId(10L);
            existingPlan.setPlanNo("PP202604300001");
            existingPlan.setReceivedAmount(BigDecimal.ZERO);
            existingPlan.setReceivedStatus(0);

            when(paymentPlanMapper.selectById(1L)).thenReturn(existingPlan);
            when(paymentPlanMapper.updateById(any(CrmPaymentPlan.class))).thenReturn(1);

            // 准备修改数据
            PaymentPlanSaveDTO dto = new PaymentPlanSaveDTO();
            dto.setId(1L);
            dto.setContractId(10L);
            dto.setPlanAmount(new BigDecimal("60000.00"));
            dto.setPlanDate(LocalDate.of(2026, 6, 30));

            // 执行
            Long result = paymentPlanService.savePlan(dto);

            // 验证
            assertEquals(1L, result);
            verify(paymentPlanMapper).selectById(1L);
            verify(paymentPlanMapper).updateById(any(CrmPaymentPlan.class));
        }

        @Test
        @DisplayName("修改不存在的收款计划抛出异常")
        void testSavePlan_NotFound() {
            // Mock查询返回null
            when(paymentPlanMapper.selectById(999L)).thenReturn(null);

            // 准备修改数据
            PaymentPlanSaveDTO dto = new PaymentPlanSaveDTO();
            dto.setId(999L);
            dto.setContractId(10L);
            dto.setPlanAmount(new BigDecimal("50000.00"));
            dto.setPlanDate(LocalDate.of(2026, 5, 30));

            // 执行并验证
            BusinessException ex = assertThrows(BusinessException.class, () -> paymentPlanService.savePlan(dto));
            assertEquals("收款计划不存在", ex.getMessage());
        }

        @Test
        @DisplayName("分页查询收款计划列表")
        void testGetPlanPage_Pagination() {
            // 准备查询条件
            PaymentPlanQueryDTO query = new PaymentPlanQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            CrmPaymentPlan plan = new CrmPaymentPlan();
            plan.setId(1L);
            plan.setContractId(10L);
            plan.setPlanNo("PP001");
            plan.setPlanAmount(new BigDecimal("50000.00"));
            plan.setReceivedStatus(0);

            Page<CrmPaymentPlan> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.singletonList(plan));
            mockPage.setTotal(1);

            when(paymentPlanMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            // 执行
            PageVO<PaymentPlanVO> result = paymentPlanService.getPlanPage(query);

            // 验证
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("获取收款计划详情成功")
        void testGetPlanById_Success() {
            // 准备计划数据
            CrmPaymentPlan plan = new CrmPaymentPlan();
            plan.setId(1L);
            plan.setContractId(10L);
            plan.setPlanNo("PP001");
            plan.setPlanAmount(new BigDecimal("50000.00"));

            when(paymentPlanMapper.selectById(1L)).thenReturn(plan);

            // 执行
            PaymentPlanVO result = paymentPlanService.getPlanById(1L);

            // 验证
            assertEquals(1L, result.getId());
            assertEquals("PP001", result.getPlanNo());
        }

        @Test
        @DisplayName("获取不存在的收款计划详情抛出异常")
        void testGetPlanById_NotFound() {
            // Mock查询返回null
            when(paymentPlanMapper.selectById(999L)).thenReturn(null);

            // 执行并验证
            BusinessException ex = assertThrows(BusinessException.class, () -> paymentPlanService.getPlanById(999L));
            assertEquals("收款计划不存在", ex.getMessage());
        }

        @Test
        @DisplayName("根据合同ID查询收款计划列表")
        void testGetPlansByContractId() {
            // 准备计划数据
            CrmPaymentPlan plan = new CrmPaymentPlan();
            plan.setId(1L);
            plan.setContractId(10L);
            plan.setPlanNo("PP001");

            when(paymentPlanMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.singletonList(plan));

            // 执行
            List<PaymentPlanVO> result = paymentPlanService.getPlansByContractId(10L);

            // 验证
            assertEquals(1, result.size());
            assertEquals(10L, result.get(0).getContractId());
        }

        @Test
        @DisplayName("删除未收款状态计划成功")
        void testDeletePlan_Success() {
            // 准备未收款状态计划
            CrmPaymentPlan plan = new CrmPaymentPlan();
            plan.setId(1L);
            plan.setReceivedStatus(0); // 未收款

            when(paymentPlanMapper.selectById(1L)).thenReturn(plan);
            when(paymentPlanMapper.deleteById(anyLong())).thenReturn(1);

            // 执行
            paymentPlanService.deletePlan(1L);

            // 验证
            verify(paymentPlanMapper).selectById(1L);
            verify(paymentPlanMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除不存在的计划抛出异常")
        void testDeletePlan_NotFound() {
            // Mock查询返回null
            when(paymentPlanMapper.selectById(999L)).thenReturn(null);

            // 执行并验证
            BusinessException ex = assertThrows(BusinessException.class, () -> paymentPlanService.deletePlan(999L));
            assertEquals("收款计划不存在", ex.getMessage());
        }

        @Test
        @DisplayName("删除已收款状态计划抛出异常")
        void testDeletePlan_AlreadyReceived() {
            // 准备部分收款状态计划
            CrmPaymentPlan plan = new CrmPaymentPlan();
            plan.setId(1L);
            plan.setReceivedStatus(1); // 部分收款

            when(paymentPlanMapper.selectById(1L)).thenReturn(plan);

            // 执行并验证
            BusinessException ex = assertThrows(BusinessException.class, () -> paymentPlanService.deletePlan(1L));
            assertEquals("已收款的计划不能删除", ex.getMessage());
        }

        @Test
        @DisplayName("删除已收完状态计划抛出异常")
        void testDeletePlan_FullyReceived() {
            // 准备已收完状态计划
            CrmPaymentPlan plan = new CrmPaymentPlan();
            plan.setId(1L);
            plan.setReceivedStatus(2); // 已收完

            when(paymentPlanMapper.selectById(1L)).thenReturn(plan);

            // 执行并验证
            BusinessException ex = assertThrows(BusinessException.class, () -> paymentPlanService.deletePlan(1L));
            assertEquals("已收款的计划不能删除", ex.getMessage());
        }

        @Test
        @DisplayName("更新收款金额-未收→部分收款")
        void testUpdateReceivedAmount_Partial() {
            // 准备计划数据
            CrmPaymentPlan plan = new CrmPaymentPlan();
            plan.setId(1L);
            plan.setPlanAmount(new BigDecimal("50000.00"));
            plan.setReceivedAmount(BigDecimal.ZERO);
            plan.setReceivedStatus(0);

            when(paymentPlanMapper.selectById(1L)).thenReturn(plan);
            when(paymentPlanMapper.updateById(any(CrmPaymentPlan.class))).thenReturn(1);

            // 执行 - 收款30000
            paymentPlanService.updateReceivedAmount(1L, new BigDecimal("30000.00"));

            // 验证
            verify(paymentPlanMapper).updateById(ArgumentMatchers.<CrmPaymentPlan>argThat(updatedPlan -> {
                assertEquals(new BigDecimal("30000.00"), updatedPlan.getReceivedAmount());
                assertEquals(1, updatedPlan.getReceivedStatus()); // 部分收款
                return true;
            }));
        }

        @Test
        @DisplayName("更新收款金额-部分→已收完")
        void testUpdateReceivedAmount_Full() {
            // 准备计划数据（已有部分收款）
            CrmPaymentPlan plan = new CrmPaymentPlan();
            plan.setId(1L);
            plan.setPlanAmount(new BigDecimal("50000.00"));
            plan.setReceivedAmount(new BigDecimal("30000.00"));
            plan.setReceivedStatus(1);

            when(paymentPlanMapper.selectById(1L)).thenReturn(plan);
            when(paymentPlanMapper.updateById(any(CrmPaymentPlan.class))).thenReturn(1);

            // 执行 - 再收款20000
            paymentPlanService.updateReceivedAmount(1L, new BigDecimal("20000.00"));

            // 验证
            verify(paymentPlanMapper).updateById(ArgumentMatchers.<CrmPaymentPlan>argThat(updatedPlan -> {
                assertEquals(new BigDecimal("50000.00"), updatedPlan.getReceivedAmount());
                assertEquals(2, updatedPlan.getReceivedStatus()); // 已收完
                return true;
            }));
        }

        @Test
        @DisplayName("更新收款金额-超收后状态变为已收完")
        void testUpdateReceivedAmount_Overpaid() {
            // 准备计划数据
            CrmPaymentPlan plan = new CrmPaymentPlan();
            plan.setId(1L);
            plan.setPlanAmount(new BigDecimal("50000.00"));
            plan.setReceivedAmount(new BigDecimal("40000.00"));
            plan.setReceivedStatus(1);

            when(paymentPlanMapper.selectById(1L)).thenReturn(plan);
            when(paymentPlanMapper.updateById(any(CrmPaymentPlan.class))).thenReturn(1);

            // 执行 - 再收款20000（超收）
            paymentPlanService.updateReceivedAmount(1L, new BigDecimal("20000.00"));

            // 酉证
            verify(paymentPlanMapper).updateById(ArgumentMatchers.<CrmPaymentPlan>argThat(updatedPlan -> {
                assertEquals(new BigDecimal("60000.00"), updatedPlan.getReceivedAmount());
                assertEquals(2, updatedPlan.getReceivedStatus()); // 已收完
                return true;
            }));
        }

        @Test
        @DisplayName("更新收款金额-取消确认减金额")
        void testUpdateReceivedAmount_Negative() {
            // 准备计划数据
            CrmPaymentPlan plan = new CrmPaymentPlan();
            plan.setId(1L);
            plan.setPlanAmount(new BigDecimal("50000.00"));
            plan.setReceivedAmount(new BigDecimal("30000.00"));
            plan.setReceivedStatus(1);

            when(paymentPlanMapper.selectById(1L)).thenReturn(plan);
            when(paymentPlanMapper.updateById(any(CrmPaymentPlan.class))).thenReturn(1);

            // 执行 - 取消确认，减去10000
            paymentPlanService.updateReceivedAmount(1L, new BigDecimal("-10000.00"));

            // 验证
            verify(paymentPlanMapper).updateById(ArgumentMatchers.<CrmPaymentPlan>argThat(updatedPlan -> {
                assertEquals(new BigDecimal("20000.00"), updatedPlan.getReceivedAmount());
                assertEquals(1, updatedPlan.getReceivedStatus()); // 仍为部分收款
                return true;
            }));
        }

        @Test
        @DisplayName("更新收款金额-减到0变为未收款")
        void testUpdateReceivedAmount_ToZero() {
            // 准备计划数据
            CrmPaymentPlan plan = new CrmPaymentPlan();
            plan.setId(1L);
            plan.setPlanAmount(new BigDecimal("50000.00"));
            plan.setReceivedAmount(new BigDecimal("10000.00"));
            plan.setReceivedStatus(1);

            when(paymentPlanMapper.selectById(1L)).thenReturn(plan);
            when(paymentPlanMapper.updateById(any(CrmPaymentPlan.class))).thenReturn(1);

            // 执行 - 取消确认，减去10000
            paymentPlanService.updateReceivedAmount(1L, new BigDecimal("-10000.00"));

            // 验证
            verify(paymentPlanMapper).updateById(ArgumentMatchers.<CrmPaymentPlan>argThat(updatedPlan -> {
                assertEquals(0, updatedPlan.getReceivedAmount().compareTo(BigDecimal.ZERO));
                assertEquals(0, updatedPlan.getReceivedStatus()); // 未收款
                return true;
            }));
        }

        @Test
        @DisplayName("更新收款金额-计划不存在直接返回")
        void testUpdateReceivedAmount_NotFound() {
            // Mock查询返回null
            when(paymentPlanMapper.selectById(999L)).thenReturn(null);

            // 执行 - 不应抛异常
            paymentPlanService.updateReceivedAmount(999L, new BigDecimal("10000.00"));

            // 验证 - 不执行更新（由于没有匹配的selectById，updateById不会被调用）
        }
    }

    // ==================== 收款记录测试 ====================

    @Nested
    @DisplayName("收款记录测试")
    class PaymentRecordTests {

        @Mock
        private PaymentRecordMapper paymentRecordMapper;

        @Mock
        private PaymentPlanMapper planMapper;

        @Mock
        private PaymentPlanService paymentPlanService;

        @InjectMocks
        private PaymentRecordServiceImpl paymentRecordService;

        @Test
        @DisplayName("正常保存新收款记录")
        void testSaveRecord_Success_New() {
            // Mock mapper.insert返回
            when(paymentRecordMapper.insert(any(CrmPaymentRecord.class))).thenAnswer(invocation -> {
                CrmPaymentRecord record = invocation.getArgument(0);
                record.setId(1L);
                return 1;
            });

            // 准备记录数据
            PaymentRecordSaveDTO dto = new PaymentRecordSaveDTO();
            dto.setContractId(10L);
            dto.setPlanId(1L);
            dto.setPaymentAmount(new BigDecimal("10000.00"));
            dto.setPaymentDate(LocalDate.of(2026, 5, 1));
            dto.setPaymentType("bank_transfer");

            // 执行
            Long result = paymentRecordService.saveRecord(dto);

            // 验证
            assertEquals(1L, result);
            verify(paymentRecordMapper).insert(any(CrmPaymentRecord.class));
        }

        @Test
        @DisplayName("正常修改待确认状态记录")
        void testSaveRecord_Success_Update() {
            // 准备现有记录（待确认状态）
            CrmPaymentRecord existingRecord = new CrmPaymentRecord();
            existingRecord.setId(1L);
            existingRecord.setStatus(2); // 待确认
            existingRecord.setContractId(10L);

            when(paymentRecordMapper.selectById(1L)).thenReturn(existingRecord);
            when(paymentRecordMapper.updateById(any(CrmPaymentRecord.class))).thenReturn(1);

            // 准备修改数据
            PaymentRecordSaveDTO dto = new PaymentRecordSaveDTO();
            dto.setId(1L);
            dto.setContractId(10L);
            dto.setPaymentAmount(new BigDecimal("15000.00"));
            dto.setPaymentDate(LocalDate.of(2026, 5, 5));

            // 执行
            Long result = paymentRecordService.saveRecord(dto);

            // 验证
            assertEquals(1L, result);
            verify(paymentRecordMapper).selectById(1L);
            verify(paymentRecordMapper).updateById(any(CrmPaymentRecord.class));
        }

        @Test
        @DisplayName("修改不存在的记录抛出异常")
        void testSaveRecord_NotFound() {
            // Mock查询返回null
            when(paymentRecordMapper.selectById(999L)).thenReturn(null);

            // 准备修改数据
            PaymentRecordSaveDTO dto = new PaymentRecordSaveDTO();
            dto.setId(999L);
            dto.setContractId(10L);
            dto.setPaymentAmount(new BigDecimal("10000.00"));
            dto.setPaymentDate(LocalDate.of(2026, 5, 1));

            // 执行并验证
            BusinessException ex = assertThrows(BusinessException.class, () -> paymentRecordService.saveRecord(dto));
            assertEquals("收款记录不存在", ex.getMessage());
        }

        @Test
        @DisplayName("修改已确认状态记录抛出异常")
        void testSaveRecord_AlreadyConfirmed() {
            // 准备已确认状态记录
            CrmPaymentRecord confirmedRecord = new CrmPaymentRecord();
            confirmedRecord.setId(1L);
            confirmedRecord.setStatus(1); // 已确认

            when(paymentRecordMapper.selectById(1L)).thenReturn(confirmedRecord);

            // 准备修改数据
            PaymentRecordSaveDTO dto = new PaymentRecordSaveDTO();
            dto.setId(1L);
            dto.setContractId(10L);
            dto.setPaymentAmount(new BigDecimal("10000.00"));
            dto.setPaymentDate(LocalDate.of(2026, 5, 1));

            // 执行并验证
            BusinessException ex = assertThrows(BusinessException.class, () -> paymentRecordService.saveRecord(dto));
            assertEquals("已确认的收款记录不能修改", ex.getMessage());
        }

        @Test
        @DisplayName("分页查询收款记录列表")
        void testGetRecordPage_Pagination() {
            // 准备查询条件
            PaymentRecordQueryDTO query = new PaymentRecordQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            CrmPaymentRecord record = new CrmPaymentRecord();
            record.setId(1L);
            record.setContractId(10L);
            record.setPaymentNo("PR001");
            record.setPaymentAmount(new BigDecimal("10000.00"));
            record.setStatus(2);

            Page<CrmPaymentRecord> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.singletonList(record));
            mockPage.setTotal(1);

            when(paymentRecordMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            // 执行
            PageVO<PaymentRecordVO> result = paymentRecordService.getRecordPage(query);

            // 验证
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("获取收款记录详情成功")
        void testGetRecordById_Success() {
            // 准备记录数据
            CrmPaymentRecord record = new CrmPaymentRecord();
            record.setId(1L);
            record.setContractId(10L);
            record.setPlanId(5L);
            record.setPaymentNo("PR001");
            record.setPaymentAmount(new BigDecimal("10000.00"));

            CrmPaymentPlan plan = new CrmPaymentPlan();
            plan.setId(5L);
            plan.setPlanNo("PP001");

            when(paymentRecordMapper.selectById(1L)).thenReturn(record);
            when(planMapper.selectById(5L)).thenReturn(plan);

            // 执行
            PaymentRecordVO result = paymentRecordService.getRecordById(1L);

            // 验证
            assertEquals(1L, result.getId());
            assertEquals("PR001", result.getPaymentNo());
            assertEquals("PP001", result.getPlanNo());
        }

        @Test
        @DisplayName("获取不存在的收款记录详情抛出异常")
        void testGetRecordById_NotFound() {
            // Mock查询返回null
            when(paymentRecordMapper.selectById(999L)).thenReturn(null);

            // 执行并验证
            BusinessException ex = assertThrows(BusinessException.class, () -> paymentRecordService.getRecordById(999L));
            assertEquals("收款记录不存在", ex.getMessage());
        }

        @Test
        @DisplayName("根据合同ID查询收款记录列表")
        void testGetRecordsByContractId() {
            // 准备记录数据
            CrmPaymentRecord record = new CrmPaymentRecord();
            record.setId(1L);
            record.setContractId(10L);
            record.setPaymentNo("PR001");

            when(paymentRecordMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.singletonList(record));

            // 执行
            List<PaymentRecordVO> result = paymentRecordService.getRecordsByContractId(10L);

            // 验证
            assertEquals(1, result.size());
            assertEquals(10L, result.get(0).getContractId());
        }

        @Test
        @DisplayName("删除待确认状态记录成功")
        void testDeleteRecord_Success() {
            // 准备待确认状态记录
            CrmPaymentRecord record = new CrmPaymentRecord();
            record.setId(1L);
            record.setStatus(2); // 待确认

            when(paymentRecordMapper.selectById(1L)).thenReturn(record);
            when(paymentRecordMapper.deleteById(anyLong())).thenReturn(1);

            // 执行
            paymentRecordService.deleteRecord(1L);

            // 验证
            verify(paymentRecordMapper).selectById(1L);
            verify(paymentRecordMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除不存在的记录抛出异常")
        void testDeleteRecord_NotFound() {
            // Mock查询返回null
            when(paymentRecordMapper.selectById(999L)).thenReturn(null);

            // 执行并验证
            BusinessException ex = assertThrows(BusinessException.class, () -> paymentRecordService.deleteRecord(999L));
            assertEquals("收款记录不存在", ex.getMessage());
        }

        @Test
        @DisplayName("删除已确认状态记录抛出异常")
        void testDeleteRecord_AlreadyConfirmed() {
            // 准备已确认状态记录
            CrmPaymentRecord record = new CrmPaymentRecord();
            record.setId(1L);
            record.setStatus(1); // 已确认

            when(paymentRecordMapper.selectById(1L)).thenReturn(record);

            // 执行并验证
            BusinessException ex = assertThrows(BusinessException.class, () -> paymentRecordService.deleteRecord(1L));
            assertEquals("已确认的收款记录不能删除", ex.getMessage());
        }

        @Test
        @DisplayName("确认收款成功")
        void testConfirmPayment_Success() {
            // 准备待确认状态记录
            CrmPaymentRecord record = new CrmPaymentRecord();
            record.setId(1L);
            record.setStatus(2); // 待确认
            record.setPlanId(5L);
            record.setContractId(10L);
            record.setPaymentAmount(new BigDecimal("10000.00"));

            when(paymentRecordMapper.selectById(1L)).thenReturn(record);
            when(paymentRecordMapper.updateById(any(CrmPaymentRecord.class))).thenReturn(1);

            // 执行
            paymentRecordService.confirmPayment(1L);

            // 验证
            verify(paymentRecordMapper).updateById(ArgumentMatchers.<CrmPaymentRecord>argThat(updatedRecord -> {
                assertEquals(1, updatedRecord.getStatus()); // 已确认
                assertNotNull(updatedRecord.getConfirmTime());
                assertEquals(100L, updatedRecord.getConfirmBy());
                return true;
            }));
            verify(paymentPlanService).updateReceivedAmount(5L, new BigDecimal("10000.00"));
        }

        @Test
        @DisplayName("确认收款-不关联计划则不更新计划金额")
        void testConfirmPayment_NoPlan() {
            // 准备待确认状态记录（无关联计划）
            CrmPaymentRecord record = new CrmPaymentRecord();
            record.setId(1L);
            record.setStatus(2); // 待确认
            record.setPlanId(null); // 无关联计划
            record.setContractId(10L);
            record.setPaymentAmount(new BigDecimal("10000.00"));

            when(paymentRecordMapper.selectById(1L)).thenReturn(record);
            when(paymentRecordMapper.updateById(any(CrmPaymentRecord.class))).thenReturn(1);

            // 执行
            paymentRecordService.confirmPayment(1L);

            // 验证
            verify(paymentRecordMapper).updateById(any(CrmPaymentRecord.class));
        }

        @Test
        @DisplayName("确认收款不存在的记录抛出异常")
        void testConfirmPayment_NotFound() {
            // Mock查询返回null
            when(paymentRecordMapper.selectById(999L)).thenReturn(null);

            // 执行并验证
            BusinessException ex = assertThrows(BusinessException.class, () -> paymentRecordService.confirmPayment(999L));
            assertEquals("收款记录不存在", ex.getMessage());
        }

        @Test
        @DisplayName("确认已确认状态记录抛出异常")
        void testConfirmPayment_AlreadyConfirmed() {
            // 准备已确认状态记录
            CrmPaymentRecord record = new CrmPaymentRecord();
            record.setId(1L);
            record.setStatus(1); // 已确认

            when(paymentRecordMapper.selectById(1L)).thenReturn(record);

            // 执行并验证
            BusinessException ex = assertThrows(BusinessException.class, () -> paymentRecordService.confirmPayment(1L));
            assertEquals("只有待确认状态的记录才能确认", ex.getMessage());
        }

        @Test
        @DisplayName("取消确认成功")
        void testUnconfirmPayment_Success() {
            // 准备已确认状态记录
            CrmPaymentRecord record = new CrmPaymentRecord();
            record.setId(1L);
            record.setStatus(1); // 已确认
            record.setPlanId(5L);
            record.setContractId(10L);
            record.setPaymentAmount(new BigDecimal("10000.00"));
            record.setConfirmTime(LocalDateTime.now());
            record.setConfirmBy(100L);

            when(paymentRecordMapper.selectById(1L)).thenReturn(record);
            when(paymentRecordMapper.updateById(any(CrmPaymentRecord.class))).thenReturn(1);

            // 执行
            paymentRecordService.unconfirmPayment(1L);

            // 验证
            verify(paymentRecordMapper).updateById(ArgumentMatchers.<CrmPaymentRecord>argThat(updatedRecord -> {
                assertEquals(2, updatedRecord.getStatus()); // 待确认
                assertNull(updatedRecord.getConfirmTime());
                assertNull(updatedRecord.getConfirmBy());
                return true;
            }));
            verify(paymentPlanService).updateReceivedAmount(5L, new BigDecimal("-10000.00"));
        }

        @Test
        @DisplayName("取消确认-不关联计划则不更新计划金额")
        void testUnconfirmPayment_NoPlan() {
            // 准备已确认状态记录（无关联计划）
            CrmPaymentRecord record = new CrmPaymentRecord();
            record.setId(1L);
            record.setStatus(1); // 已确认
            record.setPlanId(null); // 无关联计划
            record.setContractId(10L);
            record.setPaymentAmount(new BigDecimal("10000.00"));

            when(paymentRecordMapper.selectById(1L)).thenReturn(record);
            when(paymentRecordMapper.updateById(any(CrmPaymentRecord.class))).thenReturn(1);

            // 执行
            paymentRecordService.unconfirmPayment(1L);

            // 验证
            verify(paymentRecordMapper).updateById(any(CrmPaymentRecord.class));
        }

        @Test
        @DisplayName("取消确认不存在的记录抛出异常")
        void testUnconfirmPayment_NotFound() {
            // Mock查询返回null
            when(paymentRecordMapper.selectById(999L)).thenReturn(null);

            // 执行并验证
            BusinessException ex = assertThrows(BusinessException.class, () -> paymentRecordService.unconfirmPayment(999L));
            assertEquals("收款记录不存在", ex.getMessage());
        }

        @Test
        @DisplayName("取消确认待确认状态记录抛出异常")
        void testUnconfirmPayment_NotConfirmed() {
            // 准备待确认状态记录
            CrmPaymentRecord record = new CrmPaymentRecord();
            record.setId(1L);
            record.setStatus(2); // 待确认

            when(paymentRecordMapper.selectById(1L)).thenReturn(record);

            // 执行并验证
            BusinessException ex = assertThrows(BusinessException.class, () -> paymentRecordService.unconfirmPayment(1L));
            assertEquals("只有已确认状态的记录才能取消确认", ex.getMessage());
        }
    }
}
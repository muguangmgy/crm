package com.platform.server.crm.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.server.crm.payment.po.CrmPaymentPlan;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentPlanMapper extends BaseMapper<CrmPaymentPlan> {
}
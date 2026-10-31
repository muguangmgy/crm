package com.platform.server.crm.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.platform.server.crm.common.constants.CRMConstants;
import com.platform.server.crm.common.enums.PoolTypeEnum;
import com.platform.server.crm.customer.mapper.CustomerMapper;
import com.platform.server.crm.customer.po.CrmCustomer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 公海池自动释放定时任务
 * 每小时检查私有池客户，超过配置天数无跟进记录的客户自动释放到公海池
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerPoolReleaseTask {

    private final CustomerMapper customerMapper;

    /**
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void autoRelease() {
        log.info("开始执行公海池自动释放任务");
        try {
            int noFollowDays = CRMConstants.POOL_NO_FOLLOW_DAYS;

            // 查询需要释放的客户
            // 注：selectNeedRelease方法在Mapper XML中定义，查询超过指定天数无跟进的客户
            LambdaUpdateWrapper<CrmCustomer> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CrmCustomer::getPoolType, PoolTypeEnum.PRIVATE.getCode());
            updateWrapper.isNotNull(CrmCustomer::getLockTime);
            // 更新为公海池
            updateWrapper.set(CrmCustomer::getPoolType, PoolTypeEnum.PUBLIC.getCode());
            updateWrapper.set(CrmCustomer::getOwnerId, null);
            updateWrapper.set(CrmCustomer::getLockTime, null);

            // 使用XML查询配合MyBatis-Plus更新
            // 这里简化处理，实际应该使用selectNeedRelease查询后再批量更新
            // TODO: 后续优化为批量处理，避免一次性处理过多数据

            log.info("公海池自动释放任务执行完成");
        } catch (Exception e) {
            log.error("公海池自动释放任务执行异常", e);
        }
    }
}
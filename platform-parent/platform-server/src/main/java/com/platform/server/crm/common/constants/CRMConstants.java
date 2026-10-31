package com.platform.server.crm.common.constants;

/**
 * CRM业务常量
 */
public class CRMConstants {

    /** 公海池锁定天数（领取后） */
    public static final int POOL_LOCK_DAYS = 30;

    /** 无跟进自动释放天数 */
    public static final int POOL_NO_FOLLOW_DAYS = 15;

    /** 每人最大私有客户数 */
    public static final int POOL_MAX_PER_USER = 100;

    /** 客户编号前缀 */
    public static final String CUSTOMER_NO_PREFIX = "CUS";

    /** Redis锁前缀 - 公海池领取 */
    public static final String LOCK_POOL_TAKE = "crm:pool:take:";

    /** 配置键 - 公海池锁定天数 */
    public static final String CONFIG_POOL_LOCK_DAYS = "crm_pool_lock_days";

    /** 配置键 - 无跟进释放天数 */
    public static final String CONFIG_POOL_NO_FOLLOW_DAYS = "crm_pool_no_follow_days";

    /** 配置键 - 每人最大客户数 */
    public static final String CONFIG_POOL_MAX_PER_USER = "crm_pool_max_per_user";
}
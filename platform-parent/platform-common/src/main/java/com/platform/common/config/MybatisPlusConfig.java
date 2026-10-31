package com.platform.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.DataScopeType;
import com.platform.common.handler.PlatformDataPermissionHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.List;

/**
 * MyBatis-Plus 配置类
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 多租户插件
        TenantLineInnerInterceptor tenantInterceptor = getTenantLineInnerInterceptor();
        interceptor.addInnerInterceptor(tenantInterceptor);

        // 2. 数据权限插件 
        DataPermissionInterceptor dataPermissionInterceptor = new DataPermissionInterceptor();
        dataPermissionInterceptor.setDataPermissionHandler(new PlatformDataPermissionHandler());
        interceptor.addInnerInterceptor(dataPermissionInterceptor);

        // 3. 添加乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 4.添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }


    /**
     * 多租户插件
     */
    @NonNull
    private static TenantLineInnerInterceptor getTenantLineInnerInterceptor() {
        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
        tenantInterceptor.setTenantLineHandler(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                Long tenantId = UserContextHolder.getTenantId();
                if (tenantId == null) {
                    return null;
                }
                return new LongValue(tenantId);
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            @Override
            public boolean ignoreTable(String tableName) {
                // 如果没有租户ID或者数据权限是全部的 → 忽略所有表
                if (UserContextHolder.getTenantId() == null || UserContextHolder.getDataScope() == DataScopeType.ALL.getCode()) {
                    return true;
                }

                // 忽略系统表、无租户字段的关联/明细表
                List<String> ignores = Arrays.asList(
                        "sys_menu", "sys_role_menu", "sys_user_role", "sys_tenant",
                        "crm_customer_tag_rel", "crm_quote_product"
                );
                return ignores.contains(tableName);
            }
        });
        return tenantInterceptor;
    }
}
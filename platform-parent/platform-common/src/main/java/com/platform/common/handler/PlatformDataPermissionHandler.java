package com.platform.common.handler;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.platform.common.annotation.DataPermissionIgnore;
import com.platform.common.annotation.DataScope;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.DataScopeType;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: PlatformDataPermissionHandler
 * @Description: 数据权限：全部/本部门/本部门及下级/本人/自定义
 * @Author: waynevip@foxmail.com
 * @Date: 2026/04/20 10:54
 */
@Slf4j
public class PlatformDataPermissionHandler implements DataPermissionHandler {

    //需要过滤的Mapper
    private static final String[] IGNORE_MAPPER = new String[]{
            "SysRoleMapper",
            "SysMenuMapper",
            "SysUserRoleMapper",
            "SysRoleMenuMapper",
            // 产品/分类按租户全员可见，不做 create_by 行级过滤
            "ProductMapper",
            "CategoryMapper",
            // 分析 SQL 含多层子查询，拦截器无法正确挂别名，改由业务层按 dataScope 过滤
            "AnalysisMapper"
    };

    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        try {
            // ===================== 【核心：系统表直接跳过】 =====================
            // sys_role、sys_menu等系统表 不做数据权限
            if (Arrays.stream(IGNORE_MAPPER).anyMatch(mapper -> mappedStatementId.contains(mapper))) {
                log.debug("数据权限：系统表直接跳过:{}", mappedStatementId);
                return where;
            }

            // ===================== 解析方法 + 判断忽略注解 =====================
            String className = StrUtil.subBefore(mappedStatementId, ".", true);
            String methodName = StrUtil.subAfter(mappedStatementId, ".", true);
            Class<?> mapperClass = ClassUtil.getClassLoader().loadClass(className);
            DataScope dataScopeAnno = null;
            Method targetMethod = null;
            for (Method method : mapperClass.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    dataScopeAnno = method.getAnnotation(DataScope.class);
                    targetMethod = method;
                    break;
                }
            }

            // 有忽略注解，直接返回
            if (targetMethod != null && targetMethod.isAnnotationPresent(DataPermissionIgnore.class)) {
                log.debug("数据权限：忽略注解:{}", mappedStatementId);
                return where;
            }

            // 2. 获取当前登录用户信息（你自己实现上下文）
            Long userId = UserContextHolder.getUserId();
            Long deptId = UserContextHolder.getDeptId();
            String deptIds = UserContextHolder.getDeptIds();
            Integer dataScope = UserContextHolder.getDataScope(); // ALL/DEPT/SELF
            DataScopeType dataScopeType = DataScopeType.getByCode(dataScope);

            // 3. 取注解上的表别名
            String deptAlias = dataScopeAnno != null ? dataScopeAnno.deptAlias() + "." : "";
            String userAlias = dataScopeAnno != null ? dataScopeAnno.userAlias() + "." : "";

            //通过 mappedStatementId 获取实体类的所有字段
            Map<String, Field> entityFields = getEntityFields(mapperClass);

            // 3. 拼接权限条件
            // 有 @DataScope 注解时信任注解配置，不做字段存在性检查（支持非 BaseMapper 的自定义查询）
            // 无注解时仍检查字段是否存在，避免系统表被误加条件
            boolean trustAlias = dataScopeAnno != null;
            StringBuilder sql = new StringBuilder();
            switch (dataScopeType) {
                case ALL:
                    // 全部数据，不加条件
                    break;
                case DEPT_AND_CHILD: // 本部门及下级
                    if (StringUtils.hasText(deptIds) && (trustAlias || entityFields.containsKey("deptId")))
                        sql.append(deptAlias).append("dept_id in (").append(deptIds).append(")");
                    break;
                case DEPT: // 本部门
                    if (deptId != null && (trustAlias || entityFields.containsKey("deptId")))
                        sql.append(deptAlias).append("dept_id = ").append(deptId);
                    break;
                case SELF: // 仅本人（CRM 业务表按 owner_id；有 @DataScope 时默认 owner_id）
                    if (userId != null) {
                        if (entityFields.containsKey("ownerId") || trustAlias) {
                            sql.append(userAlias).append("owner_id = ").append(userId);
                        } else if (entityFields.containsKey("createBy")) {
                            sql.append(userAlias).append("create_by = ").append(userId);
                        }
                    }
                    break;
                default:
                    break;
            }

            // 4. 没有条件，则返回原SQL
            if (sql.length() == 0) {
                return where;
            }
            Expression dataScopeExpression = CCJSqlParserUtil.parseCondExpression(sql.toString());
            if (where == null) {
                return dataScopeExpression;
            }
            return new AndExpression(where, dataScopeExpression);
        } catch (Exception e) {
            log.error("数据权限处理异常：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    /**
     * 通过Mapper类获取对应实体类的所有字段
     */
    private Map<String, Field> getEntityFields(Class<?> mapperClass) {
        Map<String, Field> fieldMap = new HashMap<>();

        try {
            // 遍历 Mapper 的泛型参数，找到实体类
            Type[] genericInterfaces = mapperClass.getGenericInterfaces();
            for (Type type : genericInterfaces) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

                    for (Type actualType : actualTypeArguments) {
                        if (actualType instanceof Class) {
                            Class<?> entityClass = (Class<?>) actualType;

                            // 检查是否有 @TableName 注解来判断是否是实体类
                            if (entityClass.isAnnotationPresent(TableName.class)) {
                                // 获取实体类的所有字段
                                Field[] fields = entityClass.getDeclaredFields();
                                for (Field field : fields) {
                                    field.setAccessible(true);
                                    fieldMap.put(field.getName(), field);
                                }
                                return fieldMap;
                            }
                        }
                    }
                }
            }


            // 方法2: 如果方法1失败，尝试从方法返回类型推断
            Method[] methods = mapperClass.getDeclaredMethods();
            for (Method method : methods) {
                Type returnType = method.getGenericReturnType();
                if (returnType instanceof Class) {
                    Class<?> returnClass = (Class<?>) returnType;
                    String returnClassName = returnClass.getName();

                    // 判断是否是常见的实体类返回类型（不是 List、Page 等包装类型）
                    if (!returnClassName.startsWith("java.") &&
                            !returnClassName.contains("List") &&
                            !returnClassName.contains("Page") &&
                            !returnClassName.contains("Map")) {

                        // 检查是否有 @TableName 注解
                        if (returnClass.isAnnotationPresent(TableName.class)) {
                            Field[] fields = returnClass.getDeclaredFields();
                            for (Field field : fields) {
                                field.setAccessible(true);
                                fieldMap.put(field.getName(), field);
                            }
                            return fieldMap;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取实体类字段失败: {}", e.getMessage());
        }
        return fieldMap;
    }


}
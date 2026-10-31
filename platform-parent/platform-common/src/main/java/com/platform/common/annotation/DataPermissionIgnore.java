package com.platform.common.annotation;

import java.lang.annotation.*;

/**
 * 标记某个Mapper方法 忽略数据权限
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermissionIgnore {
}
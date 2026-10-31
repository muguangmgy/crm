package com.platform.common.aspect;

import com.platform.common.config.FastDfsService;
import com.platform.common.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FastDFS防盗链URL处理切面
 * 自动为Controller返回结果中的FastDFS URL添加防盗链token
 * <p>
 * 执行顺序：优先于RequestLogAspect执行，确保日志记录的是带token的完整URL
 */
@Slf4j
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "fdfs.enabled", havingValue = "true")
public class FastDfsAntiStealAspect {

    private final FastDfsService fastDfsService;

    /**
     * 切点：所有Controller层方法
     */
    @Pointcut("execution(* com.platform..controller..*.*(..))")
    public void controllerPointcut() {
    }

    /**
     * 后置通知：在Controller方法返回后处理结果
     */
    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void afterReturning(Object result) {
        // 未开启防盗链则跳过
        if (!fastDfsService.isAntiStealEnabled()) {
            return;
        }

        try {
            processObject(result);
        } catch (Exception e) {
            log.warn("处理防盗链URL失败: {}", e.getMessage());
        }
    }

    /**
     * 递归处理对象，查找并替换FastDFS URL
     */
    private void processObject(Object obj) {
        if (obj == null) {
            return;
        }

        // 跳过基本类型和包装类
        Class<?> clazz = obj.getClass();
        if (isSimpleType(clazz)) {
            return;
        }

        // 处理Result包装类
        if (obj instanceof Result<?> result) {
            processObject(result.getData());
            return;
        }

        // 处理Map
        if (obj instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof String strValue) {
                    String newUrl = processUrl(strValue);
                    if (!strValue.equals(newUrl)) {
                        // Map的value需要通过反射或重新put
                        try {
                            ((Map) obj).put(entry.getKey(), newUrl);
                        } catch (UnsupportedOperationException e) {
                            // 不可修改的Map，跳过
                        }
                    }
                } else {
                    processObject(value);
                }
            }
            return;
        }

        // 处理Collection
        if (obj instanceof Collection<?> collection) {
            for (Object item : collection) {
                processObject(item);
            }
            return;
        }

        // 处理普通对象的字段
        processFields(obj);
    }

    /**
     * 处理对象的所有字段
     */
    private void processFields(Object obj) {
        Class<?> clazz = obj.getClass();

        // 跳过Java原生类和Spring类
        String className = clazz.getName();
        if (className.startsWith("java.") || className.startsWith("org.springframework.")) {
            return;
        }

        // 遍历所有字段（包括父类）
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);

                    if (value == null) {
                        continue;
                    }

                    if (value instanceof String strValue) {
                        // 处理字符串字段
                        String newUrl = processUrl(strValue);
                        if (!strValue.equals(newUrl)) {
                            field.set(obj, newUrl);
                        }
                    } else if (!isSimpleType(value.getClass())) {
                        // 递归处理复杂对象
                        processObject(value);
                    }
                } catch (Exception e) {
                    // 忽略无法访问的字段
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * 处理字符串中的FastDFS URL，支持单个URL和富文本中的多个URL
     * <p>
     * 支持场景：
     * 1. 字段值为单个FastDFS URL（如logo、avatar等）
     * 2. 富文本内容中包含多个FastDFS URL（如文章详情、资源介绍中的图片）
     * <p>
     * URL格式：{webUrlPrefix}/group{X}/.../file.ext
     * 支持已带token参数的URL，会先移除旧参数再生成新token
     */
    private String processUrl(String strValue) {
        if (strValue == null || strValue.isEmpty()) {
            return strValue;
        }

        String webUrlPrefix = fastDfsService.getWebUrlPrefix();
        if (webUrlPrefix == null || webUrlPrefix.isEmpty()) {
            return strValue;
        }

        // 构建匹配FastDFS URL的正则表达式
        // 匹配格式：{webUrlPrefix}/group{X}/路径/文件名[?查询参数]
        // 注意：需要匹配查询参数（包括旧的token/ts），以便完整替换
        String escapedPrefix = webUrlPrefix.replace(".", "\\.");
        // 文件路径部分：字母、数字、下划线、斜杠、点、减号
        // 查询参数部分：可选，以?开头，直到遇到空白或结束引号
        String fastDfsUrlPattern = escapedPrefix + "group[0-9]+/[\\w/.-]+(?:\\?[^\\s\"'<>]*)?";

        // 查找所有匹配的FastDFS URL
        Pattern pattern = Pattern.compile(fastDfsUrlPattern);
        Matcher matcher = pattern.matcher(strValue);

        StringBuilder result = new StringBuilder();
        boolean replaced = false;

        while (matcher.find()) {
            String originalUrl = matcher.group();
            // 提取纯净的文件路径（移除旧的token等查询参数）
            String filePath = extractFilePath(originalUrl, webUrlPrefix);
            if (filePath != null && !filePath.isEmpty()) {
                // 生成带新防盗链token的URL
                String newUrl = fastDfsService.generateAntiStealUrl(filePath);
                // 使用quoteReplacement避免替换字符串中的特殊字符（如$）被误解析
                matcher.appendReplacement(result, Matcher.quoteReplacement(newUrl));
                replaced = true;
            }
        }

        if (replaced) {
            matcher.appendTail(result);
            return result.toString();
        }

        return strValue;
    }

    /**
     * 从URL中提取文件路径
     */
    private String extractFilePath(String url, String webUrlPrefix) {
        // 移除前缀
        String path = url.substring(webUrlPrefix.length());

        // 去掉开头的斜杠
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        // 移除查询参数
        int queryIndex = path.indexOf('?');
        if (queryIndex != -1) {
            path = path.substring(0, queryIndex);
        }

        return path;
    }

    /**
     * 判断是否是简单类型
     */
    private boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || Number.class.isAssignableFrom(clazz)
                || clazz == Boolean.class
                || clazz == Character.class
                || clazz.isEnum();
    }
}
package com.platform.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * JSON 资源类
 * 基于 Jackson 实现
 */
@Slf4j
public final class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 日期格式
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 注册 Java 8 时间模块
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        // 忽略未知属性
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 禁用日期序列化为时间戳
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private JsonUtil() {
    }

    /**
     * 对象转 JSON 字符串
     *
     * @param obj 对象
     * @return JSON 字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转JSON失败: {}", e.getMessage());
            throw new RuntimeException("对象转JSON失败", e);
        }
    }

    /**
     * 对象转 JSON 字符串（美化格式）
     *
     * @param obj 对象
     * @return 格式化的 JSON 字符串
     */
    public static String toJsonPretty(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转JSON失败: {}", e.getMessage());
            throw new RuntimeException("对象转JSON失败", e);
        }
    }

    /**
     * JSON 字符串转对象
     *
     * @param json  JSON 字符串
     * @param clazz 目标类型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            log.error("JSON转对象失败: {}", e.getMessage());
            throw new RuntimeException("JSON转对象失败", e);
        }
    }

    /**
     * JSON 字符串转复杂类型对象（如 List、Map）
     *
     * @param json          JSON 字符串
     * @param typeReference 类型引用
     * @return 对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("JSON转对象失败: {}", e.getMessage());
            throw new RuntimeException("JSON转对象失败", e);
        }
    }

    /**
     * JSON 字符串转 List
     *
     * @param json  JSON 字符串
     * @param clazz 元素类型
     * @return List
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json,
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            log.error("JSON转List失败: {}", e.getMessage());
            throw new RuntimeException("JSON转List失败", e);
        }
    }

    /**
     * JSON 字符串转 Map
     *
     * @param json JSON 字符串
     * @return Map
     */
    public static Map<String, Object> fromJsonToMap(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            log.error("JSON转Map失败: {}", e.getMessage());
            throw new RuntimeException("JSON转Map失败", e);
        }
    }

    /**
     * 对象转 Map
     *
     * @param obj 对象
     * @return Map
     */
    public static Map<String, Object> toMap(Object obj) {
        if (obj == null) {
            return null;
        }
        return OBJECT_MAPPER.convertValue(obj, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * Map 转对象
     *
     * @param map   Map
     * @param clazz 目标类型
     * @return 对象
     */
    public static <T> T fromMap(Map<String, ?> map, Class<T> clazz) {
        if (map == null) {
            return null;
        }
        return OBJECT_MAPPER.convertValue(map, clazz);
    }

    /**
     * 获取 ObjectMapper 实例（用于自定义配置）
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
package com.pain.apple.lab.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@Slf4j
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 序列化所有字段

        // 不序列化空字段

        // 取消默认转换 timestamps

        // 忽略空 Bean 转 json 的错误

        // 忽略 json 字符串中存在而对象中不存在的错误

        // 所有日期同一格式：yyyy-MM-dd HH:mm:ss
    }

    public static <T> String objToStr(T obj) {
        if (null == obj) {
            return null;
        }

        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Parse Object to String error: ", e);
            return null;
        }
    }

    public static <T> String objToPrettyStr(T obj) {
        if (null == obj) {
            return null;
        }

        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Parse Object to Pretty String error: ", e);
            return null;
        }
    }

    public static <T> T strToObj(String value, Class<T> clazz) {
        if (StringUtils.isBlank(value) || null == clazz) {
            return null;
        }

        try {
            return clazz.equals(String.class) ? (T) value : objectMapper.readValue(value, clazz);
        } catch (IOException e) {
            log.warn("Parse str to Object error: ", e);
            return null;
        }
    }

    public static <T> T strToObj(String value, TypeReference<T> type) {
        if (StringUtils.isBlank(value) || null == type) {
            return null;
        }

        try {
            return type.getType().equals(String.class) ? (T) value : (T) objectMapper.readValue(value, type);
        } catch (IOException e) {
            log.warn("Parse str to Object error: ", e);
            return null;
        }
    }

    public static <T> T strToObj(String value, Class<?> collectionClazz, Class<?>... elementClazz) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClazz, elementClazz);

        try {
            return objectMapper.readValue(value, javaType);
        } catch (IOException e) {
            log.warn("Parse str to Object error: ", e);
            return null;
        }
    }
}

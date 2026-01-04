package com.sfs.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 提供对象与JWT之间的相互转换功能
 * 
 * @author sfs
 * @since 2024-01-01
 */
@Slf4j
public class JwtUtil {
    
    private static final String SECRET_KEY = "SFS_JWT_SECRET_KEY_2024"; // JWT签名密钥
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 将对象转换为JWT字符串
     * 
     * @param object 要转换的对象
     * @return JWT字符串
     */
    public static String objectToJwt(Object object) {
        try {
            if (object == null) {
                throw new IllegalArgumentException("对象不能为空");
            }
            
            // 将对象转换为JSON字符串
            String json = objectMapper.writeValueAsString(object);
            
            // 构建JWT载荷
            Map<String, Object> payload = new HashMap<>();
            payload.put("data", json);
            payload.put("timestamp", System.currentTimeMillis());
            
            // 生成JWT
            return JWTUtil.createToken(payload, SECRET_KEY.getBytes());
            
        } catch (Exception e) {
            log.error("对象转JWT失败", e);
            throw new RuntimeException("对象转JWT失败", e);
        }
    }
    
    /**
     * 将JWT字符串转换为指定类型的对象
     * 
     * @param jwt JWT字符串
     * @param clazz 目标对象类型
     * @return 转换后的对象
     */
    public static <T> T jwtToObject(String jwt, Class<T> clazz) {
        try {
            if (StrUtil.isBlank(jwt)) {
                throw new IllegalArgumentException("JWT字符串不能为空");
            }
            
            // 验证JWT
            JWT jwtObj = JWTUtil.parseToken(jwt);
            if (!jwtObj.verify(SECRET_KEY.getBytes())) {
                throw new RuntimeException("JWT签名验证失败");
            }
            
            // 获取数据
            Map<String, Object> payload = (Map<String, Object>) jwtObj.getPayload();
            String json = (String) payload.get("data");
            if (StrUtil.isBlank(json)) {
                throw new RuntimeException("JWT载荷中没有数据");
            }
            
            // 将JSON转换为对象
            return objectMapper.readValue(json, clazz);
            
        } catch (Exception e) {
            log.error("JWT转对象失败", e);
            throw new RuntimeException("JWT转对象失败", e);
        }
    }
    
    /**
     * 将JWT字符串转换为复杂类型的对象（支持泛型）
     * 
     * @param jwt JWT字符串
     * @param typeReference 类型引用，用于处理复杂类型
     * @return 转换后的对象
     */
    public static <T> T jwtToObject(String jwt, TypeReference<T> typeReference) {
        try {
            if (StrUtil.isBlank(jwt)) {
                throw new IllegalArgumentException("JWT字符串不能为空");
            }
            
            // 验证JWT
            JWT jwtObj = JWTUtil.parseToken(jwt);
            if (!jwtObj.verify(SECRET_KEY.getBytes())) {
                throw new RuntimeException("JWT签名验证失败");
            }
            
            // 获取数据
            Map<String, Object> payload = (Map<String, Object>) jwtObj.getPayload();
            String json = (String) payload.get("data");
            if (StrUtil.isBlank(json)) {
                throw new RuntimeException("JWT载荷中没有数据");
            }
            
            // 将JSON转换为对象
            return objectMapper.readValue(json, typeReference);
            
        } catch (Exception e) {
            log.error("JWT转对象失败", e);
            throw new RuntimeException("JWT转对象失败", e);
        }
    }
    
    /**
     * 验证JWT字符串是否有效
     * 
     * @param jwt JWT字符串
     * @return true=有效，false=无效
     */
    public static boolean validateJwt(String jwt) {
        try {
            if (StrUtil.isBlank(jwt)) {
                return false;
            }
            
            // 验证JWT
            JWT jwtObj = JWTUtil.parseToken(jwt);
            if (!jwtObj.verify(SECRET_KEY.getBytes())) {
                return false;
            }
            
            // 检查是否有有效数据
            Map<String, Object> payload = (Map<String, Object>) jwtObj.getPayload();
            String json = (String) payload.get("data");
            if (StrUtil.isBlank(json)) {
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            log.debug("JWT验证失败", e);
            return false;
        }
    }
    
    /**
     * 从JWT中获取用户ID（假设对象中有uid字段）
     * 
     * @param jwt JWT字符串
     * @return 用户ID
     */
    public static Long getUserIdFromJwt(String jwt) {
        try {
            JWT jwtObj = JWTUtil.parseToken(jwt);
            Map<String, Object> payload = (Map<String, Object>) jwtObj.getPayload();
            String json = (String) payload.get("data");
            
            // 简单解析JSON获取uid
            if (StrUtil.isNotBlank(json) && json.contains("\"uid\":")) {
                // 使用正则表达式提取uid值
                String uidStr = json.replaceAll(".*\"uid\":\\s*(\\d+).*", "$1");
                if (StrUtil.isNumeric(uidStr)) {
                    return Long.parseLong(uidStr);
                }
            }
            
            return null;
            
        } catch (Exception e) {
            log.debug("从JWT获取用户ID失败", e);
            return null;
        }
    }
    
    /**
     * 重新生成JWT（更新timestamp）
     * 
     * @param jwt 原JWT字符串
     * @return 新的JWT字符串
     */
    public static String refreshJwt(String jwt) {
        try {
            if (!validateJwt(jwt)) {
                throw new RuntimeException("原JWT无效");
            }
            
            // 从原JWT中获取对象
            JWT jwtObj = JWTUtil.parseToken(jwt);
            String json = (String) jwtObj.getPayload().get("data");
            
            if (StrUtil.isBlank(json)) {
                throw new RuntimeException("原JWT中没有有效数据");
            }
            
            // 重新生成JWT
            Map<String, Object> newPayload = new HashMap<>();
            newPayload.put("data", json);
            newPayload.put("timestamp", System.currentTimeMillis());
            
            return JWTUtil.createToken(newPayload, SECRET_KEY.getBytes());
            
        } catch (Exception e) {
            log.error("刷新JWT失败", e);
            throw new RuntimeException("刷新JWT失败", e);
        }
    }
}
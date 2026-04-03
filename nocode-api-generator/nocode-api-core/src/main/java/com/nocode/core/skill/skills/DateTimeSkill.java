package com.nocode.core.skill.skills;

import com.nocode.core.entity.ColumnInfo;
import com.nocode.core.orchestrator.ConversionResult;
import com.nocode.core.skill.TypeConversionSkill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 时间类型转换技能
 * 处理 Timestamp, Date, Time, LocalDateTime 等类型
 */
@Slf4j
@Component
public class DateTimeSkill implements TypeConversionSkill {
    
    private static final Set<String> SUPPORTED_TYPES = new HashSet<>(Arrays.asList(
            "Timestamp",
            "Date",
            "Time",
            "LocalDateTime",
            "LocalDate",
            "LocalTime",
            "java.sql.Timestamp",
            "java.util.Date",
            "java.sql.Date",
            "java.sql.Time"
    ));
    
    /**
     * 常用日期时间格式
     */
    private static final String[] DATE_FORMATS = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd"
    };
    
    @Override
    public Set<String> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }
    
    @Override
    public String getName() {
        return "DateTimeSkill";
    }
    
    @Override
    public int getOrder() {
        return 30; // 较高优先级
    }
    
    @Override
    public ConversionResult tryConvert(Object value, ColumnInfo columnInfo) {
        if (value == null) {
            if (columnInfo.isNullable()) {
                return ConversionResult.success(null);
            }
            return ConversionResult.failed(columnInfo.getName(), "字段不能为空");
        }
        
        String javaType = columnInfo.getJavaType();
        
        // 已经是时间类型
        if (value instanceof Timestamp) {
            return convertTimestamp((Timestamp) value, javaType);
        }
        if (value instanceof java.sql.Date) {
            return convertSqlDate((java.sql.Date) value, javaType);
        }
        if (value instanceof java.sql.Time) {
            return convertSqlTime((java.sql.Time) value, javaType);
        }
        if (value instanceof Date) {
            return convertUtilDate((Date) value, javaType);
        }
        if (value instanceof LocalDateTime) {
            return convertLocalDateTime((LocalDateTime) value, javaType);
        }
        if (value instanceof java.time.LocalDate) {
            return convertLocalDate((java.time.LocalDate) value, javaType);
        }
        
        // 数值类型（毫秒时间戳）
        if (value instanceof Number) {
            long timestamp = ((Number) value).longValue();
            return convertTimestamp(new Timestamp(timestamp), javaType);
        }
        
        // 字符串处理
        if (value instanceof String) {
            String str = ((String) value).trim();
            
            // 空字符串
            if (str.isEmpty()) {
                if (columnInfo.isNullable()) {
                    return ConversionResult.success(null);
                }
                return ConversionResult.failed(columnInfo.getName(), "字段不能为空");
            }
            
            // 尝试解析
            return parseDateTime(str, javaType, columnInfo);
        }
        
        // 其他类型
        return ConversionResult.needAgent();
    }
    
    /**
     * 解析日期时间字符串
     */
    private ConversionResult parseDateTime(String str, String javaType, ColumnInfo columnInfo) {
        // 尝试 ISO 格式（Java 8+）
        try {
            if (javaType.contains("LocalDateTime")) {
                LocalDateTime ldt = LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME);
                return ConversionResult.success(ldt);
            }
        } catch (DateTimeParseException ignored) {
        }
        
        // 尝试 ISO 日期格式
        try {
            if (javaType.contains("LocalDate")) {
                java.time.LocalDate ld = java.time.LocalDate.parse(str);
                return ConversionResult.success(ld);
            }
        } catch (DateTimeParseException ignored) {
        }
        
        // 尝试各种格式解析
        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                Date date = sdf.parse(str);
                return convertUtilDate(date, javaType);
            } catch (ParseException ignored) {
            }
        }
        
        // 无法解析 → 需要 Agent
        log.debug("无法解析日期时间字符串: {} -> {}", str, javaType);
        return ConversionResult.needAgent();
    }
    
    private ConversionResult convertTimestamp(Timestamp ts, String javaType) {
        if (javaType.contains("LocalDateTime")) {
            return ConversionResult.success(ts.toLocalDateTime());
        }
        if (javaType.contains("LocalDate")) {
            return ConversionResult.success(ts.toLocalDateTime().toLocalDate());
        }
        if (javaType.contains("LocalTime")) {
            return ConversionResult.success(ts.toLocalDateTime().toLocalTime());
        }
        if (javaType.contains("Date") && !javaType.contains("sql")) {
            return ConversionResult.success(new Date(ts.getTime()));
        }
        if (javaType.contains("sql.Date")) {
            return ConversionResult.success(new java.sql.Date(ts.getTime()));
        }
        if (javaType.contains("Time")) {
            return ConversionResult.success(new java.sql.Time(ts.getTime()));
        }
        return ConversionResult.success(ts);
    }
    
    private ConversionResult convertSqlDate(java.sql.Date date, String javaType) {
        if (javaType.contains("LocalDate")) {
            return ConversionResult.success(date.toLocalDate());
        }
        if (javaType.contains("Timestamp")) {
            return ConversionResult.success(new Timestamp(date.getTime()));
        }
        return ConversionResult.success(date);
    }
    
    private ConversionResult convertSqlTime(java.sql.Time time, String javaType) {
        if (javaType.contains("LocalTime")) {
            return ConversionResult.success(time.toLocalTime());
        }
        return ConversionResult.success(time);
    }
    
    private ConversionResult convertUtilDate(Date date, String javaType) {
        if (javaType.contains("Timestamp")) {
            return ConversionResult.success(new Timestamp(date.getTime()));
        }
        if (javaType.contains("LocalDateTime")) {
            return ConversionResult.success(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
        }
        if (javaType.contains("LocalDate")) {
            return ConversionResult.success(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        if (javaType.contains("sql.Date")) {
            return ConversionResult.success(new java.sql.Date(date.getTime()));
        }
        // java.util.Date 统一转为 java.sql.Timestamp，确保 JDBC 兼容性
        // PostgreSQL 的 setObject() 不支持直接传入 java.util.Date 给 timestamp 列
        return ConversionResult.success(new Timestamp(date.getTime()));
    }
    
    private ConversionResult convertLocalDateTime(LocalDateTime ldt, String javaType) {
        if (javaType.contains("Timestamp")) {
            return ConversionResult.success(Timestamp.valueOf(ldt));
        }
        if (javaType.contains("Date")) {
            return ConversionResult.success(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()));
        }
        return ConversionResult.success(ldt);
    }
    
    private ConversionResult convertLocalDate(java.time.LocalDate ld, String javaType) {
        if (javaType.contains("Timestamp")) {
            return ConversionResult.success(Timestamp.valueOf(ld.atStartOfDay()));
        }
        if (javaType.contains("sql.Date")) {
            return ConversionResult.success(java.sql.Date.valueOf(ld));
        }
        return ConversionResult.success(ld);
    }
}

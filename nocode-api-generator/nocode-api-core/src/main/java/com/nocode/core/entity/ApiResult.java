package com.nocode.core.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * API响应结果
 */
@Data
public class ApiResult<T> {
    private boolean success = true;
    private String message = "success";
    private T data;
    private Long total;
    private Integer page;
    private Integer size;
    private Map<String, Object> metadata;

    public static <T> ApiResult<T> ok(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setData(data);
        return result;
    }

    public static <T> ApiResult<T> ok(List<?> data, Long total, Integer page, Integer size) {
        ApiResult<T> result = new ApiResult<>();
        result.setData((T) data);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        return result;
    }

    public static <T> ApiResult<T> fail(String message) {
        ApiResult<T> result = new ApiResult<>();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public static <T> ApiResult<T> fail(String message, T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setSuccess(false);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    // 兼容success/error方法别名
    public static <T> ApiResult<T> success() {
        return ok(null);
    }

    public static <T> ApiResult<T> success(T data) {
        return ok(data);
    }

    public static <T> ApiResult<T> error(String message) {
        return fail(message);
    }

    public static <T> ApiResult<T> error(String message, T data) {
        return fail(message, data);
    }
}

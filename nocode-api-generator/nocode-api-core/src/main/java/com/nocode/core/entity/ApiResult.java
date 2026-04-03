package com.nocode.core.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * API响应结果
 */
@Data
public class ApiResult {
    private boolean success = true;
    private String message = "success";
    private Object data;
    private Long total;
    private Integer page;
    private Integer size;
    private Map<String, Object> metadata;

    public static ApiResult ok(Object data) {
        ApiResult result = new ApiResult();
        result.setData(data);
        return result;
    }

    public static ApiResult ok(List<?> data, Long total, Integer page, Integer size) {
        ApiResult result = new ApiResult();
        result.setData(data);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        return result;
    }

    public static ApiResult fail(String message) {
        ApiResult result = new ApiResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public static ApiResult fail(String message, Object data) {
        ApiResult result = new ApiResult();
        result.setSuccess(false);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    // 兼容success/error方法别名
    public static ApiResult success() {
        return ok(null);
    }

    public static ApiResult success(Object data) {
        return ok(data);
    }

    public static ApiResult error(String message) {
        return fail(message);
    }

    public static ApiResult error(String message, Object data) {
        return fail(message, data);
    }
}

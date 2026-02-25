package com.ruoyi.nocode.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应结果
 * 
 * @author ruoyi-nocode
 */
@Data
public class AjaxResult extends HashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = 200;
    public static final int ERROR = 500;

    /** 状态码 */
    private static final String CODE_TAG = "code";

    /** 返回消息 */
    private static final String MSG_TAG = "msg";

    /** 返回数据 */
    private static final String DATA_TAG = "data";

    public AjaxResult() {
    }

    public AjaxResult(int code, String msg) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    public AjaxResult(int code, String msg, Object data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (data != null) {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 返回成功
     */
    public static AjaxResult success() {
        return new AjaxResult(SUCCESS, "操作成功");
    }

    /**
     * 返回成功（带消息）
     */
    public static AjaxResult success(String msg) {
        return new AjaxResult(SUCCESS, msg);
    }

    /**
     * 返回成功（带数据）
     */
    public static AjaxResult success(Object data) {
        return new AjaxResult(SUCCESS, "操作成功", data);
    }

    /**
     * 返回成功（带消息和数据）
     */
    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult(SUCCESS, msg, data);
    }

    /**
     * 返回错误
     */
    public static AjaxResult error() {
        return new AjaxResult(ERROR, "操作失败");
    }

    /**
     * 返回错误（带消息）
     */
    public static AjaxResult error(String msg) {
        return new AjaxResult(ERROR, msg);
    }

    /**
     * 返回错误（带状态码和消息）
     */
    public static AjaxResult error(int code, String msg) {
        return new AjaxResult(code, msg);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        Object code = super.get(CODE_TAG);
        return code != null && (int) code == SUCCESS;
    }

    /**
     * 获取状态码
     */
    public int getCode() {
        Object code = super.get(CODE_TAG);
        return code != null ? (int) code : 0;
    }

    /**
     * 获取消息
     */
    public String getMsg() {
        Object msg = super.get(MSG_TAG);
        return msg != null ? msg.toString() : null;
    }

    /**
     * 获取数据
     */
    public Object getData() {
        return super.get(DATA_TAG);
    }
}

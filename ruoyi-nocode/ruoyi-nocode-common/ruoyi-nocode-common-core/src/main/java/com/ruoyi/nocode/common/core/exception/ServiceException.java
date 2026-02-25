package com.ruoyi.nocode.common.core.exception;

import java.io.Serial;

/**
 * 业务异常
 * 
 * @author ruoyi-nocode
 */
public class ServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public ServiceException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

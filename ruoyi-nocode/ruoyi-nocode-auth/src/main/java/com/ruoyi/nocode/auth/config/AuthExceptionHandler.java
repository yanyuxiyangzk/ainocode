package com.ruoyi.nocode.auth.config;

import com.ruoyi.nocode.auth.controller.R;
import com.ruoyi.nocode.auth.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 
 * @author ruoyi-nocode
 */
@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    /**
     * 服务异常
     */
    @ExceptionHandler(ServiceException.class)
    public R<Void> handleServiceException(ServiceException e) {
        log.error("服务异常: {}", e.getMessage(), e);
        return R.<Void>fail(e.getCode(), e.getMessage());
    }

    /**
     * 参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数验证异常: {}", e.getMessage());
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数验证失败";
        return R.<Void>fail(400, message);
    }

    /**
     * 绑定异常
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e) {
        log.error("绑定异常: {}", e.getMessage());
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数绑定失败";
        return R.<Void>fail(400, message);
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return R.<Void>fail(500, "系统异常，请联系管理员");
    }
}

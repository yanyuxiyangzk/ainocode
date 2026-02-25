package com.ruoyi.nocode.auth.service;

/**
 * 服务异常（已废弃，请使用 com.ruoyi.nocode.common.core.exception.ServiceException）
 * 
 * @author ruoyi-nocode
 * @deprecated 请使用 {@link com.ruoyi.nocode.common.core.exception.ServiceException}
 */
@Deprecated
public class ServiceException extends com.ruoyi.nocode.common.core.exception.ServiceException {

    private static final long serialVersionUID = 1L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Integer code, String message) {
        super(code, message);
    }
}

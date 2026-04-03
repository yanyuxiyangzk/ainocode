package com.nocode.core.orchestrator;

import java.util.Collections;
import java.util.List;

/**
 * 类型转换异常
 */
public class ConversionException extends RuntimeException {
    
    /**
     * 错误信息列表
     */
    private final List<String> errors;
    
    public ConversionException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = errors;
    }
    
    public ConversionException(String message) {
        super(message);
        this.errors = Collections.singletonList(message);
    }
    
    public List<String> getErrors() {
        return errors;
    }
}

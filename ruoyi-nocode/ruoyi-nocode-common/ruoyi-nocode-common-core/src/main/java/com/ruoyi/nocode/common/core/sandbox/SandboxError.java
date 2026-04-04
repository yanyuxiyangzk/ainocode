package com.ruoyi.nocode.common.core.sandbox;

/**
 * 沙箱错误码枚举
 *
 * @author ruoyi-nocode
 */
public enum SandboxError {
    
    /**
     * 成功
     */
    SUCCESS("0000", "Success"),
    
    /**
     * 未知错误
     */
    UNKNOWN("9999", "Unknown error"),
    
    /**
     * 执行超时
     */
    TIMEOUT("0001", "Execution timeout"),
    
    /**
     * 执行被中断
     */
    INTERRUPTED("0002", "Execution interrupted"),
    
    /**
     * 执行错误
     */
    EXECUTION_ERROR("0003", "Execution error"),
    
    /**
     * 安全违规
     */
    SECURITY_ERROR("0004", "Security violation"),
    
    /**
     * 内存超限
     */
    MEMORY_EXCEEDED("0005", "Memory limit exceeded"),
    
    /**
     * CPU超时
     */
    CPU_EXCEEDED("0006", "CPU time limit exceeded"),
    
    /**
     * 网络访问被禁止
     */
    NETWORK_FORBIDDEN("1001", "Network access is forbidden"),
    
    /**
     * 文件访问被禁止
     */
    FILE_FORBIDDEN("1002", "File access is forbidden"),
    
    /**
     * 反射访问被禁止
     */
    REFLECTION_FORBIDDEN("1003", "Reflection access is forbidden"),
    
    /**
     * 系统调用被禁止
     */
    SYSTEM_CALL_FORBIDDEN("1004", "System call is forbidden");

    private final String code;
    private final String description;

    SandboxError(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

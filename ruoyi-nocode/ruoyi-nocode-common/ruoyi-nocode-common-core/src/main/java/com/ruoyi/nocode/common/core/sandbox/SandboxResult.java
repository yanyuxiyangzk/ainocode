package com.ruoyi.nocode.common.core.sandbox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 沙箱执行结果
 *
 * @author ruoyi-nocode
 */
public class SandboxResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 返回值
     */
    private Object returnValue;

    /**
     * 控制台输出
     */
    private String output;

    /**
     * 错误信息列表
     */
    private List<String> errors;

    /**
     * 错误信息（单条）
     */
    private String error;

    /**
     * 警告信息
     */
    private List<String> warnings;

    /**
     * 是否超时
     */
    private boolean timeout;

    /**
     * 安全检查是否通过
     */
    private boolean securityCheckPassed = true;

    /**
     * 错误码
     */
    private SandboxError errorCode;

    /**
     * 执行时间（毫秒）
     */
    private long executionTime;

    public SandboxResult() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    private SandboxResult(boolean success, Object returnValue, String output, List<String> errors, String error, SandboxError errorCode, long executionTime, boolean timeout, boolean securityCheckPassed) {
        this.success = success;
        this.returnValue = returnValue;
        this.output = output;
        this.errors = errors != null ? errors : new ArrayList<>();
        this.error = error;
        this.errorCode = errorCode;
        this.executionTime = executionTime;
        this.timeout = timeout;
        this.securityCheckPassed = securityCheckPassed;
        this.warnings = new ArrayList<>();
    }

    /**
     * 创建成功结果
     */
    public static SandboxResult success(Object returnValue, long executionTime) {
        SandboxResult result = new SandboxResult();
        result.success = true;
        result.returnValue = returnValue;
        result.executionTime = executionTime;
        result.timeout = false;
        result.securityCheckPassed = true;
        return result;
    }

    /**
     * 创建成功结果（带输出）
     */
    public static SandboxResult success(Object returnValue, String output, long executionTime) {
        SandboxResult result = success(returnValue, executionTime);
        result.output = output;
        return result;
    }

    /**
     * 创建失败结果
     */
    public static SandboxResult failure(String error) {
        SandboxResult result = new SandboxResult();
        result.success = false;
        result.error = error;
        result.errors = new ArrayList<>();
        result.errors.add(error);
        result.errorCode = SandboxError.UNKNOWN;
        return result;
    }

    /**
     * 创建失败结果（错误列表）
     */
    public static SandboxResult failure(List<String> errors) {
        SandboxResult result = new SandboxResult();
        result.success = false;
        result.errors = errors != null ? errors : new ArrayList<>();
        result.error = errors != null && !errors.isEmpty() ? errors.get(0) : null;
        result.errorCode = SandboxError.UNKNOWN;
        return result;
    }

    /**
     * 创建超时结果
     */
    public static SandboxResult timeout(long executionTime) {
        SandboxResult result = new SandboxResult();
        result.success = false;
        result.timeout = true;
        result.error = "Execution timeout";
        result.errorCode = SandboxError.TIMEOUT;
        result.executionTime = executionTime;
        return result;
    }

    /**
     * 创建安全失败结果
     */
    public static SandboxResult securityFailure(List<String> errors) {
        SandboxResult result = new SandboxResult();
        result.success = false;
        result.securityCheckPassed = false;
        result.errors = errors != null ? errors : new ArrayList<>();
        result.error = errors != null && !errors.isEmpty() ? errors.get(0) : "Security violation";
        result.errorCode = SandboxError.SECURITY_ERROR;
        return result;
    }

    // Getters and Setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Object getResult() {
        return returnValue;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public void addWarning(String warning) {
        if (this.warnings == null) {
            this.warnings = new ArrayList<>();
        }
        this.warnings.add(warning);
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public boolean isSecurityCheckPassed() {
        return securityCheckPassed;
    }

    public void setSecurityCheckPassed(boolean securityCheckPassed) {
        this.securityCheckPassed = securityCheckPassed;
    }

    public SandboxError getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(SandboxError errorCode) {
        this.errorCode = errorCode;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * 获取格式化的错误信息
     */
    public String getFormattedErrors() {
        if (errors == null || errors.isEmpty()) {
            return "";
        }
        return String.join("\n", errors);
    }
}

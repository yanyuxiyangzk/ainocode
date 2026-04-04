package com.ruoyi.nocode.common.core.sandbox;

/**
 * 沙箱执行异常
 *
 * @author ruoyi
 */
public class SandboxException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final SandboxError error;

    public SandboxException(SandboxError error) {
        super(error.getDescription());
        this.error = error;
    }

    public SandboxException(SandboxError error, String message) {
        super(message);
        this.error = error;
    }

    public SandboxException(SandboxError error, Throwable cause) {
        super(error.getDescription(), cause);
        this.error = error;
    }

    public SandboxException(SandboxError error, String message, Throwable cause) {
        super(message, cause);
        this.error = error;
    }

    public SandboxError getError() {
        return error;
    }
}

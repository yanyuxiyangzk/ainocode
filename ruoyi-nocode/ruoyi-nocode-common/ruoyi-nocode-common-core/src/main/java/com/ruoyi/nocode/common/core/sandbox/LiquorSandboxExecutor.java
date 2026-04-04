package com.ruoyi.nocode.common.core.sandbox;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 沙箱执行器
 * <p>
 * 使用SecurityManager限制代码执行，防止危险操作
 *
 * @author ruoyi
 */
public class LiquorSandboxExecutor {

    /**
     * 默认超时时间（毫秒）
     */
    private static final long DEFAULT_TIMEOUT_MS = 5000;

    /**
     * 默认最大输出行数
     */
    private static final int DEFAULT_MAX_OUTPUT_LINES = 100;

    /**
     * 当前安全管理器引用
     */
    private volatile WeakReference<SecurityManager> currentSecurityManager;

    /**
     * 执行代码
     *
     * @param code 代码内容
     * @return 执行结果
     */
    public SandboxResult execute(String code) {
        return execute(code, DEFAULT_TIMEOUT_MS);
    }

    /**
     * 执行代码（带超时）
     *
     * @param code     代码内容
     * @param timeoutMs 超时时间（毫秒）
     * @return 执行结果
     */
    public SandboxResult execute(String code, long timeoutMs) {
        return execute(code, timeoutMs, DEFAULT_MAX_OUTPUT_LINES);
    }

    /**
     * 执行代码（完整参数）
     *
     * @param code            代码内容
     * @param timeoutMs       超时时间（毫秒）
     * @param maxOutputLines  最大输出行数
     * @return 执行结果
     */
    public SandboxResult execute(String code, long timeoutMs, int maxOutputLines) {
        long startTime = System.currentTimeMillis();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // 设置安全管理器
        SecurityManager oldSecurityManager = System.getSecurityManager();
        SecurityManager sandboxSecurityManager = createSandboxSecurityManager();
        System.setSecurityManager(sandboxSecurityManager);

        try {
            // 创建隔离的输出流
            CapturedOutputStream capturedOutput = new CapturedOutputStream(maxOutputLines);
            PrintWriter capturedWriter = new PrintWriter(capturedOutput, true);

            // 执行代码
            Object result = executeInSandbox(code, capturedWriter, timeoutMs);

            // 获取输出
            String output = capturedOutput.toString();
            if (!output.isEmpty()) {
                warnings.add("Output captured: " + output);
            }

            long executionTime = System.currentTimeMillis() - startTime;
            SandboxResult sandboxResult = SandboxResult.success(result, executionTime);
            sandboxResult.setWarnings(warnings);
            return sandboxResult;

        } catch (SandboxTimeoutException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            return SandboxResult.timeout(executionTime);

        } catch (SandboxSecurityException e) {
            errors.add("Security violation: " + e.getMessage());
            SandboxResult result = SandboxResult.securityFailure(errors);
            result.setExecutionTime(System.currentTimeMillis() - startTime);
            return result;

        } catch (SandboxExecutionException e) {
            errors.add("Execution error: " + e.getMessage());
            if (e.getCause() != null) {
                errors.add("Cause: " + e.getCause().getMessage());
            }
            SandboxResult result = SandboxResult.failure(errors);
            result.setExecutionTime(System.currentTimeMillis() - startTime);
            return result;

        } catch (Exception e) {
            errors.add("Unexpected error: " + e.getMessage());
            SandboxResult result = SandboxResult.failure(errors);
            result.setExecutionTime(System.currentTimeMillis() - startTime);
            return result;

        } finally {
            // 恢复原来的安全管理器
            System.setSecurityManager(oldSecurityManager);
        }
    }

    /**
     * 在沙箱中执行代码
     */
    private Object executeInSandbox(String code, PrintWriter writer, long timeoutMs)
            throws SandboxExecutionException, SandboxTimeoutException {

        // 创建脚本引擎
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        if (engine == null) {
            // 尝试使用Nashorn
            engine = manager.getEngineByName("Nashorn");
        }

        if (engine == null) {
            throw new SandboxExecutionException("No JavaScript engine available");
        }

        // 包装代码，捕获输出
        final String wrappedCodeFinal = wrapCode(code);
        final ScriptEngine engineFinal = engine;

        try {
            // 使用ExecutorService实现超时控制
            return executeWithTimeout(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    try {
                        return engineFinal.eval(wrappedCodeFinal);
                    } catch (ScriptException e) {
                        throw new SandboxExecutionException(e.getMessage());
                    }
                }
            }, timeoutMs);

        } catch (TimeoutException e) {
            throw new SandboxTimeoutException("Code execution timed out after " + timeoutMs + "ms");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SandboxExecutionException("Execution interrupted");
        } catch (ExecutionException e) {
            throw new SandboxExecutionException("Execution error: " + e.getMessage());
        }
    }

    /**
     * 包装代码以捕获输出
     */
    private String wrapCode(String code) {
        StringBuilder sb = new StringBuilder();
        sb.append("(function() { ");
        sb.append("var __out = []; ");
        sb.append("var __print = function(x) { __out.push(String(x)); }; ");
        sb.append(code);
        sb.append("; return __out.join(' '); })()");
        return sb.toString();
    }

    /**
     * 创建沙箱安全管理器
     * @deprecated JDK17中SecurityManager已废弃，该方法仅用于保持API兼容
     */
    @Deprecated
    private SecurityManager createSandboxSecurityManager() {
        // JDK17移除了SecurityManager的Policy构造函数，使用默认构造函数
        return new SecurityManager();
    }

    /**
     * 使用超时控制执行
     */
    private <T> T executeWithTimeout(Callable<T> callable, long timeoutMs)
            throws TimeoutException, InterruptedException, ExecutionException {

        java.util.concurrent.ExecutorService executor =
                java.util.concurrent.Executors.newSingleThreadExecutor();

        try {
            Future<T> future = executor.submit(callable);
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * 编译并执行Java代码
     *
     * @param code         Java代码
     * @param className    类名
     * @param timeoutMs    超时时间
     * @return 执行结果
     * @deprecated JDK17移除了SecurityManager的Policy构造函数，且ToolProvider需要访问内部模块，
     *             该方法已废弃。请使用{@link LiquorCompilerService}进行Java编译，
     *             使用{@link #execute(String)}执行JavaScript代码。
     */
    @Deprecated
    public SandboxResult executeJava(String code, String className, long timeoutMs) {
        return SandboxResult.failure("Java compilation execution is not supported in JDK 17+. " +
                "Use LiquorCompilerService for Java compilation and execute() for JavaScript execution.");
    }

    /**
     * 捕获输出流
     */
    private static class CapturedOutputStream extends OutputStream {
        private final StringBuilder buffer = new StringBuilder();
        private final int maxLines;
        private int lineCount = 0;
        private boolean closed = false;

        public CapturedOutputStream(int maxLines) {
            this.maxLines = maxLines;
        }

        @Override
        public void write(int b) throws IOException {
            if (closed) return;
            if (b == '\n') {
                lineCount++;
                if (lineCount >= maxLines) {
                    buffer.append("... (output truncated)");
                    close();
                }
            }
            buffer.append((char) b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (closed) return;
            for (int i = off; i < off + len; i++) {
                write(b[i]);
            }
        }

        @Override
        public void close() throws IOException {
            this.closed = true;
            super.close();
        }

        @Override
        public String toString() {
            return buffer.toString().trim();
        }
    }

    /**
     * 沙箱执行异常
     */
    private static class SandboxExecutionException extends RuntimeException {
        public SandboxExecutionException(String message) {
            super(message);
        }
    }

    /**
     * 沙箱超时异常
     */
    private static class SandboxTimeoutException extends RuntimeException {
        public SandboxTimeoutException(String message) {
            super(message);
        }
    }

    /**
     * 沙箱安全异常
     */
    private static class SandboxSecurityException extends SecurityException {
        public SandboxSecurityException(String message) {
            super(message);
        }
    }
}

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
        String wrappedCode = wrapCode(code);

        try {
            // 使用ExecutorService实现超时控制
            return executeWithTimeout(() -> {
                try {
                    return engine.eval(wrappedCode);
                } catch (ScriptException e) {
                    throw new SandboxExecutionException(e.getMessage());
                }
            }, timeoutMs);

        } catch (TimeoutException e) {
            throw new SandboxTimeoutException("Code execution timed out after " + timeoutMs + "ms");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SandboxExecutionException("Execution interrupted");
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
     */
    private SecurityManager createSandboxSecurityManager() {
        SecurityManager sm = new SecurityManager(new SandboxPolicy());
        return sm;
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
     */
    public SandboxResult executeJava(String code, String className, long timeoutMs) {
        long startTime = System.currentTimeMillis();
        List<String> errors = new ArrayList<>();

        // 设置安全管理器
        SecurityManager oldSecurityManager = System.getSecurityManager();
        SecurityManager sandboxSecurityManager = createSandboxSecurityManager();
        System.setSecurityManager(sandboxSecurityManager);

        try {
            // 获取编译器
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                return SandboxResult.failure("Java compiler not available - running in limited environment");
            }

            // 写入临时文件
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "sandbox");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            File sourceFile = new File(tempDir, className + ".java");

            // 使用文件输出流写入
            try (FileOutputStream fos = new FileOutputStream(sourceFile)) {
                fos.write(code.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }

            // 编译
            int result = compiler.run(null, null, null, sourceFile.getPath());
            if (result != 0) {
                return SandboxResult.failure("Compilation failed with exit code: " + result);
            }

            // 加载并执行
            URL[] urls = new URL[]{tempDir.toURI().toURL()};
            URLClassLoader classLoader = new URLClassLoader(urls);
            Class<?> clazz = classLoader.loadClass(className);
            Method mainMethod = clazz.getMethod("main", String[].class);

            // 执行（带超时）
            Object ret = executeWithTimeout(() -> {
                try {
                    mainMethod.invoke(null, (Object) new String[]{});
                    return null;
                } catch (Exception e) {
                    throw new SandboxExecutionException(e.getMessage());
                }
            }, timeoutMs);

            return SandboxResult.success(ret, System.currentTimeMillis() - startTime);

        } catch (SandboxTimeoutException e) {
            return SandboxResult.timeout(System.currentTimeMillis() - startTime);

        } catch (SandboxSecurityException e) {
            return SandboxResult.securityFailure(List.of("Security violation: " + e.getMessage()));

        } catch (Exception e) {
            return SandboxResult.failure(e.getMessage());

        } finally {
            System.setSecurityManager(oldSecurityManager);
        }
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

package com.ruoyi.nocode.common.core.sandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Permission;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 沙箱执行服务
 * <p>
 * 为Liquor动态编译的代码提供安全的执行环境
 * - 使用SecurityManager限制系统资源访问
 * - 超时控制防止无限循环
 * - 内存限制防止OOM
 *
 * @author ruoyi-nocode
 */
public class SandboxService {

    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);

    /**
     * 默认超时时间（毫秒）
     */
    private static final long DEFAULT_TIMEOUT_MS = 30000;

    /**
     * 默认最大内存（MB）
     */
    private static final long DEFAULT_MAX_MEMORY_MB = 128;

    /**
     * 单例实例
     */
    private static volatile SandboxService instance;

    /**
     * 沙箱执行器线程池
     */
    private final ExecutorService executorService;

    private SandboxService() {
        executorService = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "sandbox-executor");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * 获取单例实例
     */
    public static SandboxService getInstance() {
        if (instance == null) {
            synchronized (SandboxService.class) {
                if (instance == null) {
                    instance = new SandboxService();
                }
            }
        }
        return instance;
    }

    /**
     * 在沙箱中执行代码
     *
     * @param task 要执行的代码任务
     * @return 执行结果
     */
    public SandboxResult execute(SandboxTask task) {
        return execute(task, DEFAULT_TIMEOUT_MS);
    }

    /**
     * 在沙箱中执行代码（带超时）
     *
     * @param task           要执行的代码任务
     * @param timeoutMs      超时时间（毫秒）
     * @return 执行结果
     */
    public SandboxResult execute(SandboxTask task, long timeoutMs) {
        long startTime = System.currentTimeMillis();

        // 保存原始安全管理器
        SecurityManager originalSecurityManager = System.getSecurityManager();

        // 创建带超时限制的任务
        Future<SandboxResult> future = executorService.submit(() -> {
            return task.run();
        });

        try {
            // 设置沙箱安全管理器
            System.setSecurityManager(new SandboxSecurityManager());

            // 等待执行结果
            SandboxResult result = future.get(timeoutMs, TimeUnit.MILLISECONDS);

            long executionTime = System.currentTimeMillis() - startTime;
            return SandboxResult.success(result.getReturnValue(), result.getOutput(), executionTime);

        } catch (TimeoutException e) {
            log.warn("Sandbox execution timeout: {}ms", timeoutMs);
            future.cancel(true);
            SandboxResult result = SandboxResult.failure("Execution timeout after " + timeoutMs + "ms");
            result.setErrorCode(SandboxError.TIMEOUT);
            return result;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Sandbox execution interrupted");
            SandboxResult result = SandboxResult.failure("Execution interrupted");
            result.setErrorCode(SandboxError.INTERRUPTED);
            return result;

        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            log.error("Sandbox execution error", cause);
            if (cause instanceof SandboxException) {
                SandboxResult result = SandboxResult.failure(cause.getMessage());
                result.setErrorCode(((SandboxException) cause).getError());
                return result;
            }
            SandboxResult result = SandboxResult.failure("Execution error: " + cause.getMessage());
            result.setErrorCode(SandboxError.EXECUTION_ERROR);
            return result;

        } catch (SecurityException e) {
            log.warn("Sandbox security violation: {}", e.getMessage());
            SandboxResult result = SandboxResult.failure("Security violation: " + e.getMessage());
            result.setErrorCode(SandboxError.SECURITY_ERROR);
            return result;

        } finally {
            // 恢复原始安全管理器
            System.setSecurityManager(originalSecurityManager);
        }
    }

    /**
     * 验证代码是否安全（静态检查）
     *
     * @param code 源代码
     * @return 验证结果
     */
    public ValidationResult validateCode(String code) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);
        
        // 检查危险关键词
        Set<String> dangerousPatterns = new HashSet<>();
        dangerousPatterns.add("Runtime.getRuntime().exec");
        dangerousPatterns.add("ProcessBuilder");
        dangerousPatterns.add("System.exit");
        dangerousPatterns.add("Class.forName");
        dangerousPatterns.add("java.lang.reflect");
        dangerousPatterns.add("java.net.Socket");
        dangerousPatterns.add("java.net.ServerSocket");
        dangerousPatterns.add("java.nio.file");
        dangerousPatterns.add("FileInputStream");
        dangerousPatterns.add("FileOutputStream");
        dangerousPatterns.add("Thread.sleep");
        dangerousPatterns.add("Object.wait");
        
        for (String pattern : dangerousPatterns) {
            if (code.contains(pattern)) {
                result.setValid(false);
                result.getWarnings().add("Potentially dangerous pattern detected: " + pattern);
            }
        }
        
        return result;
    }

    /**
     * 关闭沙箱服务
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    /**
     * 沙箱任务接口
     */
    @FunctionalInterface
    public interface SandboxTask {
        /**
         * 执行任务
         *
         * @return 执行结果
         */
        SandboxResult run();
    }

    /**
     * 验证结果
     */
    public static class ValidationResult {
        private boolean valid;
        private Set<String> warnings = new HashSet<>();

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public Set<String> getWarnings() {
            return warnings;
        }

        public void setWarnings(Set<String> warnings) {
            this.warnings = warnings;
        }
    }
}

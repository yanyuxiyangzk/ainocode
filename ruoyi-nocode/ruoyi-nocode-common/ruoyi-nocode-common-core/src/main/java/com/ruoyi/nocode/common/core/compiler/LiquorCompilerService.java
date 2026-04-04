package com.ruoyi.nocode.common.core.compiler;

import org.noear.liquor.Liquor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Liquor即时编译服务
 * <p>
 * 支持运行时编译Java源码并加载为Class
 * 集成PF4J类加载器隔离机制，确保线程安全
 *
 * @author ruoyi
 */
public class LiquorCompilerService {

    private static final Logger log = LoggerFactory.getLogger(LiquorCompilerService.class);

    /**
     * 类名提取正则：public class X 或 public interface X
     */
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile(
            "(?:public\\s+)?(?:class|interface|enum|record)\\s+(\\w+)",
            Pattern.MULTILINE
    );

    /**
     * 线程安全的编译缓存（按源码hash缓存编译后的类）
     */
    private final Map<String, Class<?>> compilationCache = new ConcurrentHashMap<>();

    /**
     * 是否启用编译缓存
     */
    private boolean cacheEnabled = true;

    /**
     * 单例实例
     */
    private static volatile LiquorCompilerService instance;

    private LiquorCompilerService() {
    }

    /**
     * 获取单例实例
     */
    public static LiquorCompilerService getInstance() {
        if (instance == null) {
            synchronized (LiquorCompilerService.class) {
                if (instance == null) {
                    instance = new LiquorCompilerService();
                }
            }
        }
        return instance;
    }

    /**
     * 编译Java源码
     * <p>
     * 使用默认类加载器（当前线程的上下文类加载器）
     *
     * @param sourceCode Java源代码
     * @return 编译结果
     */
    public LiquorCompilerResult compile(String sourceCode) {
        return compile(sourceCode, Thread.currentThread().getContextClassLoader());
    }

    /**
     * 编译Java源码
     * <p>
     * 使用指定的父类加载器，确保类加载器隔离
     *
     * @param sourceCode       Java源代码
     * @param parentClassLoader 父类加载器
     * @return 编译结果
     */
    public LiquorCompilerResult compile(String sourceCode, ClassLoader parentClassLoader) {
        if (sourceCode == null || sourceCode.isBlank()) {
            return LiquorCompilerResult.failure("Source code cannot be null or empty", sourceCode);
        }

        // 提取类名
        String className = extractClassName(sourceCode);
        if (className == null) {
            return LiquorCompilerResult.failure(
                    "Cannot extract class name from source code. Ensure you have 'public class X' or 'public interface X' declaration.",
                    sourceCode
            );
        }

        // 检查缓存
        String cacheKey = buildCacheKey(sourceCode, parentClassLoader);
        if (cacheEnabled && compilationCache.containsKey(cacheKey)) {
            Class<?> cachedClass = compilationCache.get(cacheKey);
            log.debug("Using cached compilation for class: {}", className);
            return LiquorCompilerResult.success(cachedClass, className, sourceCode);
        }

        try {
            // 使用Liquor进行编译
            log.info("Compiling Java source: {}", className);

            Liquor liquor = Liquor.newInstance(parentClassLoader);
            Class<?> compiledClass = liquor.compile(sourceCode);

            // 缓存结果
            if (cacheEnabled) {
                compilationCache.put(cacheKey, compiledClass);
            }

            log.info("Successfully compiled class: {}", className);
            return LiquorCompilerResult.success(compiledClass, className, sourceCode);

        } catch (Exception e) {
            log.error("Failed to compile source code: {}", className, e);
            List<String> errors = extractErrors(e, sourceCode);
            return LiquorCompilerResult.failure(errors, sourceCode);
        }
    }

    /**
     * 编译并实例化对象
     *
     * @param sourceCode Java源代码（必须包含无参构造函数）
     * @return 编译并实例化后的对象
     */
    public CompileAndInstantiateResult compileAndInstantiate(String sourceCode) {
        return compileAndInstantiate(sourceCode, Thread.currentThread().getContextClassLoader());
    }

    /**
     * 编译并实例化对象
     *
     * @param sourceCode       Java源代码（必须包含无参构造函数）
     * @param parentClassLoader 父类加载器
     * @return 编译并实例化后的对象
     */
    public CompileAndInstantiateResult compileAndInstantiate(String sourceCode, ClassLoader parentClassLoader) {
        LiquorCompilerResult result = compile(sourceCode, parentClassLoader);

        if (!result.isSuccess()) {
            return CompileAndInstantiateResult.failure(result.getErrors());
        }

        try {
            Object instance = result.getCompiledClass().getDeclaredConstructor().newInstance();
            return CompileAndInstantiateResult.success(instance, result.getCompiledClass());
        } catch (Exception e) {
            log.error("Failed to instantiate class: {}", result.getClassName(), e);
            return CompileAndInstantiateResult.failure(List.of("Failed to instantiate: " + e.getMessage()));
        }
    }

    /**
     * 清除编译缓存
     */
    public void clearCache() {
        compilationCache.clear();
        log.info("Compilation cache cleared");
    }

    /**
     * 清除指定源码的缓存
     *
     * @param sourceCode 源码
     */
    public void clearCache(String sourceCode) {
        if (sourceCode != null) {
            compilationCache.keySet().removeIf(key -> key.contains(Integer.toHexString(sourceCode.hashCode())));
            log.debug("Cache cleared for source with hash: {}", Integer.toHexString(sourceCode.hashCode()));
        }
    }

    /**
     * 设置是否启用缓存
     */
    public void setCacheEnabled(boolean enabled) {
        this.cacheEnabled = enabled;
    }

    /**
     * 获取当前缓存大小
     */
    public int getCacheSize() {
        return compilationCache.size();
    }

    /**
     * 从源码中提取类名
     */
    private String extractClassName(String sourceCode) {
        Matcher matcher = CLASS_NAME_PATTERN.matcher(sourceCode);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 构建缓存键
     */
    private String buildCacheKey(String sourceCode, ClassLoader classLoader) {
        int hash = sourceCode.hashCode();
        String loaderId = classLoader.getClass().getName() + "@" + System.identityHashCode(classLoader);
        return Integer.toHexString(hash) + "_" + loaderId;
    }

    /**
     * 从异常中提取错误信息
     */
    private List<String> extractErrors(Exception e, String sourceCode) {
        List<String> errors = new ArrayList<>();

        // 优先使用Liquor提供的编译错误
        String message = e.getMessage();
        if (message != null && !message.isEmpty()) {
            // 尝试解析Liquor的错误格式
            for (String line : message.split("\n")) {
                if (line.contains("error") || line.contains("Error") || line.contains("ERROR")) {
                    errors.add(line.trim());
                }
            }
            if (errors.isEmpty()) {
                errors.add(message);
            }
        } else {
            errors.add("Compilation failed: " + e.getClass().getSimpleName());
        }

        return errors;
    }

    /**
     * 编译并实例化结果
     */
    public static class CompileAndInstantiateResult {
        private final boolean success;
        private final Object instance;
        private final Class<?> clazz;
        private final List<String> errors;

        private CompileAndInstantiateResult(boolean success, Object instance, Class<?> clazz, List<String> errors) {
            this.success = success;
            this.instance = instance;
            this.clazz = clazz;
            this.errors = errors;
        }

        public static CompileAndInstantiateResult success(Object instance, Class<?> clazz) {
            return new CompileAndInstantiateResult(true, instance, clazz, null);
        }

        public static CompileAndInstantiateResult failure(List<String> errors) {
            return new CompileAndInstantiateResult(false, null, null, errors);
        }

        public boolean isSuccess() {
            return success;
        }

        public Object getInstance() {
            return instance;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public List<String> getErrors() {
            return errors;
        }
    }
}

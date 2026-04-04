package com.ruoyi.nocode.common.core.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Liquor类热替换服务
 * <p>
 * 支持运行时热更新已加载的类，采用以下策略：
 * 1. 每个热替换域(domain)维护独立的ClassLoader链
 * 2. 每次热替换创建新的ClassLoader实例
 * 3. 新实例使用新加载的类定义，旧实例保留原类定义（Java语言限制）
 * 4. 支持版本追踪和回滚
 * <p>
 * 注意：由于Java语言特性，已加载的类无法真正被替换，只有新创建的实例会使用新的类定义。
 * 这是Java HotSpot VM的固有限制，不是本实现的缺陷。
 *
 * @author ruoyi
 */
public class LiquorHotSwapService {

    private static final Logger log = LoggerFactory.getLogger(LiquorHotSwapService.class);

    /**
     * 单例实例
     */
    private static volatile LiquorHotSwapService instance;

    /**
     * 热替换域注册表：domain -> HotSwapDomain
     */
    private final Map<String, HotSwapDomain> domainRegistry = new ConcurrentHashMap<>();

    private LiquorHotSwapService() {
    }

    /**
     * 获取单例实例
     */
    public static LiquorHotSwapService getInstance() {
        if (instance == null) {
            synchronized (LiquorHotSwapService.class) {
                if (instance == null) {
                    instance = new LiquorHotSwapService();
                }
            }
        }
        return instance;
    }

    /**
     * 热替换Java类
     * <p>
     * 在指定域中热替换类。使用默认域"default"。
     *
     * @param sourceCode 新的Java源代码（必须是完整类定义）
     * @return 热替换结果
     */
    public HotSwapResult hotSwap(String sourceCode) {
        return hotSwap("default", sourceCode);
    }

    /**
     * 热替换Java类
     * <p>
     * 在指定域中热替换类。如果域不存在则自动创建。
     *
     * @param domain     热替换域标识
     * @param sourceCode 新的Java源代码
     * @return 热替换结果
     */
    public HotSwapResult hotSwap(String domain, String sourceCode) {
        return hotSwap(domain, sourceCode, Thread.currentThread().getContextClassLoader());
    }

    /**
     * 热替换Java类
     * <p>
     * 使用指定的父类加载器创建隔离的ClassLoader进行热替换。
     *
     * @param domain              热替换域标识
     * @param sourceCode          新的Java源代码
     * @param parentClassLoader   父类加载器
     * @return 热替换结果
     */
    public HotSwapResult hotSwap(String domain, String sourceCode, ClassLoader parentClassLoader) {
        if (domain == null || domain.isBlank()) {
            domain = "default";
        }
        if (sourceCode == null || sourceCode.isBlank()) {
            return HotSwapResult.failure("Source code cannot be null or empty", null);
        }

        // 提取类名
        String className = extractClassName(sourceCode);
        if (className == null) {
            return HotSwapResult.failure(
                    "Cannot extract class name from source code. Ensure you have 'public class X' declaration.",
                    null
            );
        }

        log.info("Hot swapping class '{}' in domain '{}'", className, domain);

        try {
            // 获取或创建域
            HotSwapDomain hotSwapDomain = domainRegistry.computeIfAbsent(domain,
                    k -> new HotSwapDomain(domain, parentClassLoader));

            // 执行热替换
            HotSwapInfo swapInfo = hotSwapDomain.doHotSwap(sourceCode, className);

            log.info("Successfully hot swapped class '{}' to version {} in domain '{}'",
                    className, swapInfo.version, domain);

            return HotSwapResult.success(
                    swapInfo.compiledClass,
                    buildSwapId(domain, swapInfo.version),
                    swapInfo.version,
                    hotSwapDomain.getClassLoaderId(),
                    className
            );

        } catch (Exception e) {
            log.error("Failed to hot swap class '{}' in domain '{}'", className, domain, e);
            List<String> errors = extractErrors(e);
            return HotSwapResult.failure(errors, className);
        }
    }

    /**
     * 获取域中当前版本的类
     *
     * @param domain    热替换域标识
     * @param className 类名
     * @return 类的Class对象，如果不存在返回null
     */
    public Class<?> getCurrentClass(String domain, String className) {
        HotSwapDomain hotSwapDomain = domainRegistry.get(domain);
        if (hotSwapDomain == null) {
            return null;
        }
        return hotSwapDomain.getCurrentClass(className);
    }

    /**
     * 获取域的当前版本号
     *
     * @param domain 热替换域标识
     * @return 版本号，如果域不存在返回-1
     */
    public int getCurrentVersion(String domain) {
        HotSwapDomain hotSwapDomain = domainRegistry.get(domain);
        return hotSwapDomain != null ? hotSwapDomain.getCurrentVersion() : -1;
    }

    /**
     * 回滚到指定版本
     *
     * @param domain  热替换域标识
     * @param version 要回滚到的版本号
     * @return 回滚结果
     */
    public HotSwapResult rollback(String domain, int version) {
        HotSwapDomain hotSwapDomain = domainRegistry.get(domain);
        if (hotSwapDomain == null) {
            return HotSwapResult.failure("Domain not found: " + domain, null);
        }

        return hotSwapDomain.rollback(version);
    }

    /**
     * 回滚到上一个版本
     *
     * @param domain 热替换域标识
     * @return 回滚结果
     */
    public HotSwapResult rollbackPrevious(String domain) {
        HotSwapDomain hotSwapDomain = domainRegistry.get(domain);
        if (hotSwapDomain == null) {
            return HotSwapResult.failure("Domain not found: " + domain, null);
        }

        return hotSwapDomain.rollbackPrevious();
    }

    /**
     * 销毁热替换域
     * <p>
     * 释放该域的所有ClassLoader资源
     *
     * @param domain 热替换域标识
     */
    public void destroyDomain(String domain) {
        HotSwapDomain removed = domainRegistry.remove(domain);
        if (removed != null) {
            removed.destroy();
            log.info("Destroyed hot swap domain: {}", domain);
        }
    }

    /**
     * 清除所有热替换域
     */
    public void clearAllDomains() {
        domainRegistry.values().forEach(HotSwapDomain::destroy);
        domainRegistry.clear();
        log.info("All hot swap domains cleared");
    }

    /**
     * 获取域的信息
     *
     * @param domain 热替换域标识
     * @return 域信息，不存在返回null
     */
    public DomainInfo getDomainInfo(String domain) {
        HotSwapDomain hotSwapDomain = domainRegistry.get(domain);
        return hotSwapDomain != null ? hotSwapDomain.getInfo() : null;
    }

    /**
     * 获取所有域的标识
     */
    public List<String> getAllDomains() {
        return new ArrayList<>(domainRegistry.keySet());
    }

    /**
     * 从源码中提取类名
     */
    private String extractClassName(String sourceCode) {
        java.util.regex.Matcher matcher = LiquorCompilerService.CLASS_NAME_PATTERN.matcher(sourceCode);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 构建交换ID
     */
    private String buildSwapId(String domain, int version) {
        return domain + ":" + version;
    }

    /**
     * 从异常中提取错误信息
     */
    private List<String> extractErrors(Exception e) {
        List<String> errors = new ArrayList<>();
        String message = e.getMessage();
        if (message != null && !message.isEmpty()) {
            errors.add(message);
        } else {
            errors.add("Hot swap failed: " + e.getClass().getSimpleName());
        }
        return errors;
    }

    /**
     * 热替换信息
     */
    private static class HotSwapInfo {
        final Class<?> compiledClass;
        final int version;
        final String className;
        final long timestamp;

        HotSwapInfo(Class<?> compiledClass, int version, String className) {
            this.compiledClass = compiledClass;
            this.version = version;
            this.className = className;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * 热替换域
     */
    private static class HotSwapDomain {
        private final String domain;
        private final ClassLoader parentClassLoader;
        private final String classLoaderId;
        private final AtomicInteger versionCounter;
        private final Map<String, List<HotSwapInfo>> versionHistory; // className -> versions
        private volatile ClassLoader currentClassLoader;
        private volatile int currentVersion;

        HotSwapDomain(String domain, ClassLoader parentClassLoader) {
            this.domain = domain;
            this.parentClassLoader = parentClassLoader != null ? parentClassLoader :
                    Thread.currentThread().getContextClassLoader();
            this.classLoaderId = domain + "@" + System.identityHashCode(this);
            this.versionCounter = new AtomicInteger(0);
            this.versionHistory = new ConcurrentHashMap<>();
            this.currentVersion = 0;
            this.currentClassLoader = createNewClassLoader();
        }

        String getClassLoaderId() {
            return classLoaderId;
        }

        int getCurrentVersion() {
            return currentVersion;
        }

        Class<?> getCurrentClass(String className) {
            if (currentClassLoader == null) {
                return null;
            }
            try {
                return currentClassLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        synchronized HotSwapResult doHotSwap(String sourceCode, String className) {
            // 每次热替换都创建新的ClassLoader
            // 这样可以确保旧版本的类定义仍然被旧实例使用
            this.currentClassLoader = createNewClassLoader();
            this.currentVersion = versionCounter.incrementAndGet();

            try {
                // 使用新的ClassLoader编译
                Liquor liquor = Liquor.newInstance(currentClassLoader);
                Class<?> newClass = liquor.compile(sourceCode);

                // 记录版本历史
                HotSwapInfo info = new HotSwapInfo(newClass, currentVersion, className);
                versionHistory.computeIfAbsent(className, k -> new ArrayList<>()).add(info);

                log.debug("Hot swap created new version {} for class {} in domain {}",
                        currentVersion, className, domain);

                return info;

            } catch (Exception e) {
                // 版本创建失败，回滚版本号
                versionCounter.decrementAndGet();
                throw e;
            }
        }

        synchronized HotSwapResult rollback(int targetVersion) {
            if (targetVersion < 0 || targetVersion >= currentVersion) {
                return HotSwapResult.failure(
                        "Invalid rollback version: " + targetVersion + ", current version: " + currentVersion,
                        null
                );
            }

            // 查找目标版本的类
            for (List<HotSwapInfo> history : versionHistory.values()) {
                for (HotSwapInfo info : history) {
                    if (info.version == targetVersion) {
                        // 重新加载该版本（通过重新编译或缓存）
                        // 注意：这里简化处理，实际可能需要更复杂的版本存储
                        return HotSwapResult.success(
                                info.compiledClass,
                                buildSwapId(domain, targetVersion),
                                targetVersion,
                                classLoaderId,
                                info.className
                        );
                    }
                }
            }

            return HotSwapResult.failure("Version not found: " + targetVersion, null);
        }

        synchronized HotSwapResult rollbackPrevious() {
            if (currentVersion <= 1) {
                return HotSwapResult.failure("Cannot rollback: already at initial version", null);
            }
            return rollback(currentVersion - 1);
        }

        DomainInfo getInfo() {
            List<Integer> versions = new ArrayList<>();
            for (List<HotSwapInfo> history : versionHistory.values()) {
                for (HotSwapInfo info : history) {
                    versions.add(info.version);
                }
            }
            return new DomainInfo(domain, classLoaderId, currentVersion,
                    versions.isEmpty() ? 0 : versions.stream().mapToInt(v -> v).max().getAsInt(),
                    versionHistory.size());
        }

        void destroy() {
            this.currentClassLoader = null;
            this.versionHistory.clear();
        }

        private String buildSwapId(String domain, int version) {
            return domain + ":" + version;
        }

        private ClassLoader createNewClassLoader() {
            List<URL> urls = new ArrayList<>();
            if (parentClassLoader instanceof java.net.URLClassLoader urlClassLoader) {
                for (URL url : urlClassLoader.getURLs()) {
                    urls.add(url);
                }
            }
            return new java.net.URLClassLoader(
                    urls.toArray(new URL[0]),
                    parentClassLoader
            );
        }
    }

    /**
     * 域信息
     */
    public static class DomainInfo {
        private final String domain;
        private final String classLoaderId;
        private final int currentVersion;
        private final int latestVersion;
        private final int classCount;

        DomainInfo(String domain, String classLoaderId, int currentVersion,
                   int latestVersion, int classCount) {
            this.domain = domain;
            this.classLoaderId = classLoaderId;
            this.currentVersion = currentVersion;
            this.latestVersion = latestVersion;
            this.classCount = classCount;
        }

        public String getDomain() {
            return domain;
        }

        public String getClassLoaderId() {
            return classLoaderId;
        }

        public int getCurrentVersion() {
            return currentVersion;
        }

        public int getLatestVersion() {
            return latestVersion;
        }

        public int getClassCount() {
            return classCount;
        }
    }
}

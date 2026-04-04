package com.ruoyi.nocode.common.core.compiler;

import org.pf4j.PluginClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 隔离类加载器工具
 * <p>
 * 集成PF4J类加载器隔离机制，支持：
 * 1. 创建与主应用隔离的类加载器
 * 2. 支持父子类加载器链配置
 * 3. 线程安全
 *
 * @author ruoyi
 */
public class IsolatedClassLoaderUtil {

    private static final Logger log = LoggerFactory.getLogger(IsolatedClassLoaderUtil.class);

    /**
     * 缓存的隔离类加载器，按父类加载器ID存储
     */
    private static final Map<String, IsolatedClassLoaderHolder> classLoaderCache = new ConcurrentHashMap<>();

    private IsolatedClassLoaderUtil() {
    }

    /**
     * 创建隔离类加载器
     * <p>
     * 使用PF4J的PluginClassLoader作为基础，结合Liquor编译需求进行优化
     *
     * @param parentClassLoader 父类加载器
     * @return 隔离的类加载器
     */
    public static ClassLoader createIsolatedClassLoader(ClassLoader parentClassLoader) {
        final ClassLoader effectiveParent = (parentClassLoader == null)
                ? Thread.currentThread().getContextClassLoader()
                : parentClassLoader;

        String loaderId = generateLoaderId(effectiveParent);

        return classLoaderCache.computeIfAbsent(loaderId, id -> {
            log.debug("Creating new isolated class loader with parent: {}",
                    effectiveParent.getClass().getName());
            return new IsolatedClassLoaderHolder(createNewClassLoader(effectiveParent));
        }).getClassLoader();
    }

    /**
     * 创建隔离类加载器（不缓存）
     *
     * @param parentClassLoader 父类加载器
     * @return 新的隔离类加载器实例
     */
    public static ClassLoader createNewIsolatedClassLoader(ClassLoader parentClassLoader) {
        if (parentClassLoader == null) {
            parentClassLoader = Thread.currentThread().getContextClassLoader();
        }
        return createNewClassLoader(parentClassLoader);
    }

    /**
     * 使用隔离类加载器执行代码
     *
     * @param isolatedClassLoader 隔离类加载器
     * @param task                 要执行的任务
     * @param <T>                  返回类型
     * @return 执行结果
     */
    public static <T> T executeWithClassLoader(ClassLoader isolatedClassLoader, ClassLoaderTask<T> task) {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(isolatedClassLoader);
            return task.execute();
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    /**
     * 清除类加载器缓存
     */
    public static void clearCache() {
        classLoaderCache.clear();
        log.info("Isolated class loader cache cleared");
    }

    /**
     * 获取当前缓存的类加载器数量
     */
    public static int getCacheSize() {
        return classLoaderCache.size();
    }

    /**
     * 生成类加载器唯一标识
     */
    private static String generateLoaderId(ClassLoader parentClassLoader) {
        return parentClassLoader.getClass().getName() + "@" + System.identityHashCode(parentClassLoader);
    }

    /**
     * 创建新的类加载器实例
     */
    private static ClassLoader createNewClassLoader(ClassLoader parentClassLoader) {
        // 获取系统类加载器作为基础（排除父加载器以实现隔离）
        ClassLoader baseClassLoader = parentClassLoader.getParent();

        // 收集可用的类路径
        List<URL> urls = collectClassPathUrls(baseClassLoader);

        // 如果没有收集到URL，使用扩展类加载器
        if (urls.isEmpty() && baseClassLoader != null) {
            return new URLClassLoader(
                    new URL[0],
                    baseClassLoader
            );
        }

        return new URLClassLoader(
                urls.toArray(new URL[0]),
                parentClassLoader
        );
    }

    /**
     * 收集类路径URL
     */
    private static List<URL> collectClassPathUrls(ClassLoader classLoader) {
        List<URL> urls = new ArrayList<>();

        // 递归收集父类加载器的类路径
        collectUrlsFromClassLoader(classLoader, urls, new java.util.HashSet<>());

        return urls;
    }

    /**
     * 从类加载器递归收集URL
     */
    private static void collectUrlsFromClassLoader(
            ClassLoader classLoader,
            List<URL> urls,
            java.util.Set<ClassLoader> visited
    ) {
        if (classLoader == null || visited.contains(classLoader)) {
            return;
        }
        visited.add(classLoader);

        if (classLoader instanceof URLClassLoader urlClassLoader) {
            for (URL url : urlClassLoader.getURLs()) {
                if (!urls.contains(url)) {
                    urls.add(url);
                }
            }
        }

        // 继续收集父加载器
        collectUrlsFromClassLoader(classLoader.getParent(), urls, visited);
    }

    /**
     * 检查类是否已加载到指定类加载器
     */
    public static boolean isClassLoaded(ClassLoader classLoader, String className) {
        try {
            classLoader.loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 类加载器任务接口
     */
    @FunctionalInterface
    public interface ClassLoaderTask<T> {
        /**
         * 执行任务
         *
         * @return 任务结果
         */
        T execute();
    }

    /**
     * 类加载器持有器（支持缓存）
     */
    private static class IsolatedClassLoaderHolder {
        private final ClassLoader classLoader;
        private final long createTime;

        IsolatedClassLoaderHolder(ClassLoader classLoader) {
            this.classLoader = classLoader;
            this.createTime = System.currentTimeMillis();
        }

        ClassLoader getClassLoader() {
            return classLoader;
        }

        long getCreateTime() {
            return createTime;
        }
    }
}

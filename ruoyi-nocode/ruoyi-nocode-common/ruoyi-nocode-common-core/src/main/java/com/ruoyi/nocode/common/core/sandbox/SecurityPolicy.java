package com.ruoyi.nocode.common.core.sandbox;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 安全策略配置
 *
 * @author ruoyi
 */
@Data
public class SecurityPolicy implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 是否启用安全检查
     */
    private boolean enabled = true;

    /**
     * 最大执行时间（毫秒）
     */
    private long maxExecutionTime = 5000;

    /**
     * 最大内存（MB）
     */
    private long maxMemory = 128;

    /**
     * 允许访问的系统类
     */
    private List<String> allowedSystemClasses = new ArrayList<>();

    /**
     * 禁止访问的系统类（优先级高于允许）
     */
    private List<String> forbiddenSystemClasses = new ArrayList<>();

    /**
     * 禁止的关键字/模式
     */
    private List<String> forbiddenPatterns = new ArrayList<>();

    /**
     * 是否允许文件操作
     */
    private boolean allowFileOperation = false;

    /**
     * 是否允许网络访问
     */
    private boolean allowNetworkAccess = false;

    /**
     * 是否允许反射
     */
    private boolean allowReflection = false;

    /**
     * 是否允许执行系统命令
     */
    private boolean allowSystemCommand = false;

    /**
     * 是否允许加载本地库
     */
    private boolean allowNativeLibrary = false;

    /**
     * 是否允许线程创建
     */
    private boolean allowThreadCreation = false;

    /**
     * 最大线程数
     */
    private int maxThreadCount = 1;

    /**
     * 默认安全策略（严格）
     */
    public static SecurityPolicy strict() {
        SecurityPolicy policy = new SecurityPolicy();
        policy.setEnabled(true);
        policy.setMaxExecutionTime(5000);
        policy.setMaxMemory(128);
        policy.setAllowFileOperation(false);
        policy.setAllowNetworkAccess(false);
        policy.setAllowReflection(false);
        policy.setAllowSystemCommand(false);
        policy.setAllowNativeLibrary(false);
        policy.setAllowThreadCreation(false);
        policy.setMaxThreadCount(1);

        // 设置默认允许的系统类
        policy.setAllowedSystemClasses(List.of(
                "java.lang.*",
                "java.util.*",
                "java.math.*",
                "java.text.*",
                "java.time.*",
                "java.io.*"
        ));

        // 设置默认禁止的系统类
        policy.setForbiddenSystemClasses(List.of(
                "java.lang.Runtime",
                "java.lang.System",
                "java.lang.Process",
                "java.lang.ProcessBuilder",
                "java.lang.ClassLoader",
                "java.lang.reflect.Field",
                "java.lang.reflect.Method",
                "java.net.Socket",
                "java.net.ServerSocket",
                "java.net.URL",
                "java.nio.file.*",
                "java.io.File",
                "java.io.FileInputStream",
                "java.io.FileOutputStream",
                "java.lang.Thread",
                "java.lang.ThreadGroup",
                "java.util.concurrent.*",
                "sun.*",
                "com.sun.*"
        ));

        // 设置默认禁止的模式
        policy.setForbiddenPatterns(List.of(
                "Runtime\\.getRuntime",
                "System\\.getProperty",
                "System\\.setProperty",
                "ProcessBuilder",
                "ClassLoader",
                "Socket",
                "ServerSocket",
                "URLConnection",
                "FileInputStream",
                "FileOutputStream",
                "new File",
                "Thread\\.",
                "ThreadGroup",
                "exec\\(",
                "eval\\(",
                "loadLibrary",
                "defineClass"
        ));

        return policy;
    }

    /**
     * 宽松安全策略（用于信任的代码）
     */
    public static SecurityPolicy permissive() {
        SecurityPolicy policy = new SecurityPolicy();
        policy.setEnabled(true);
        policy.setMaxExecutionTime(10000);
        policy.setMaxMemory(256);
        policy.setAllowFileOperation(true);
        policy.setAllowNetworkAccess(false);
        policy.setAllowReflection(true);
        policy.setAllowSystemCommand(false);
        policy.setAllowNativeLibrary(false);
        policy.setAllowThreadCreation(true);
        policy.setMaxThreadCount(5);
        policy.setAllowedSystemClasses(List.of("*"));
        return policy;
    }
}

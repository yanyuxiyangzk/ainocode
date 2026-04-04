package com.ruoyi.nocode.common.core.sandbox;

import java.security.Permission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 沙箱自定义安全策略
 * <p>
 * 限制文件访问、网络访问、反射等危险操作
 *
 * @author ruoyi
 */
public class SandboxPolicy extends java.security.Policy {

    /**
     * 允许的文件操作目录
     */
    private static final Set<String> ALLOWED_FILE_PATHS = new HashSet<>(Arrays.asList(
            "/tmp/",
            "/var/tmp/"
    ));

    /**
     * 允许的协议
     */
    private static final Set<String> ALLOWED_PROTOCOLS = new HashSet<>(Arrays.asList(
            "http",
            "https"
    ));

    /**
     * 禁止的反射操作
     */
    private static final Set<String> BLOCKED_REFLECTION_TARGETS = new HashSet<>(Arrays.asList(
            "java.lang.System",
            "java.lang.Runtime",
            "java.lang.ClassLoader",
            "java.lang.reflect.Method",
            "java.lang.reflect.Field",
            "java.lang.reflect.Constructor"
    ));

    /**
     * 检查权限
     */
    @Override
    public boolean implies(java.security.ProtectionDomain domain, Permission permission) {
        String permName = permission.getName();

        // 检查文件访问权限
        if (permission instanceof java.io.FilePermission) {
            return checkFilePermission(permission);
        }

        // 检查网络访问权限
        if (permission instanceof java.net.SocketPermission) {
            return checkSocketPermission(permission);
        }

        // 检查运行时权限
        if (permission instanceof java.lang.RuntimePermission) {
            return checkRuntimePermission(permission);
        }

        // 检查链接权限
        if (permission instanceof java.net.NetPermission) {
            return checkNetPermission(permission);
        }

        // 检查属性权限
        if (permission instanceof java.util.PropertyPermission) {
            return checkPropertyPermission(permission);
        }

        // 默认拒绝所有其他权限
        return false;
    }

    /**
     * 检查文件权限
     */
    private boolean checkFilePermission(Permission permission) {
        String actions = permission.getActions();
        String path = permission.getName();

        // 只读操作允许
        if ("read".equals(actions)) {
            // 允许读取临时目录
            for (String allowedPath : ALLOWED_FILE_PATHS) {
                if (path.startsWith(allowedPath)) {
                    return true;
                }
            }
            // 允许读取类路径资源
            if (path.startsWith("/BOOT-INF/classes/") || path.startsWith("BOOT-INF/classes/")) {
                return true;
            }
            if (path.startsWith("org/springframework/boot/loader/")) {
                return true;
            }
            return false;
        }

        // 禁止写入、删除、执行
        if ("write".equals(actions) || "delete".equals(actions) || "execute".equals(actions)) {
            return false;
        }

        return false;
    }

    /**
     * 检查网络权限
     */
    private boolean checkSocketPermission(Permission permission) {
        String actions = permission.getActions();
        String path = permission.getName();

        // 禁止所有网络连接
        if ("connect".equals(actions) || "accept".equals(actions) || "listen".equals(actions)) {
            return false;
        }

        // 禁止解析主机
        if ("resolve".equals(actions)) {
            return false;
        }

        return false;
    }

    /**
     * 检查运行时权限
     */
    private boolean checkRuntimePermission(Permission permission) {
        String permName = permission.getName();

        // 禁止创建类加载器
        if ("createClassLoader".equals(permName)) {
            return false;
        }

        // 禁止访问成员
        if ("accessDeclaredMembers".equals(permName)) {
            return false;
        }

        // 禁止生成代码
        if ("defineClass".equals(permName)) {
            return false;
        }

        // 禁止修改线程
        if ("modifyThread".equals(permName) || "modifyThreadGroup".equals(permName)) {
            return false;
        }

        // 禁止退出虚拟机
        if ("exitVM".equals(permName)) {
            return false;
        }

        // 禁止加载库
        if ("loadLibrary".equals(permName)) {
            return false;
        }

        // 禁止设置安全管理器
        if ("setSecurityManager".equals(permName)) {
            return false;
        }

        // 允许线程访问（有限制）
        if ("getThreadGroup".equals(permName)) {
            return true;
        }

        return false;
    }

    /**
     * 检查网络权限
     */
    private boolean checkNetPermission(Permission permission) {
        // 禁止所有网络相关权限
        return false;
    }

    /**
     * 检查属性权限
     */
    private boolean checkPropertyPermission(Permission permission) {
        String permName = permission.getName();

        // 只允许读取部分系统属性
        Set<String> allowedProps = new HashSet<>(Arrays.asList(
                "java.version",
                "java.vendor",
                "java.class.version",
                "os.name",
                "os.version",
                "os.arch",
                "file.separator",
                "path.separator",
                "line.separator",
                "user.name",
                "user.home",
                "user.dir"
        ));

        if ("read".equals(permission.getActions()) && allowedProps.contains(permName)) {
            return true;
        }

        return false;
    }

    /**
     * 检查反射是否包含被禁止的目标
     */
    public static boolean isBlockedReflectionTarget(String className) {
        return BLOCKED_REFLECTION_TARGETS.contains(className);
    }

    /**
     * 获取被禁止的反射目标
     */
    public static Set<String> getBlockedReflectionTargets() {
        return BLOCKED_REFLECTION_TARGETS;
    }
}

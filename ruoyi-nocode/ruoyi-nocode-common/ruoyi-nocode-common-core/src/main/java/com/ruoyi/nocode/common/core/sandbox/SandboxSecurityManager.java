package com.ruoyi.nocode.common.core.sandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FilePermission;
import java.net.InetAddress;
import java.net.NetPermission;
import java.net.SocketPermission;
import java.net.URLPermission;
import java.security.Permission;
import java.security.Policy;
import java.security.Security;
import java.util.PropertyPermission;

/**
 * 沙箱安全管理器
 * <p>
 * 使用SecurityManager限制代码的以下行为：
 * - 文件读写操作
 * - 网络访问
 * - 反射操作
 * - 系统命令执行
 * - 线程操作
 *
 * @author ruoyi-nocode
 */
public class SandboxSecurityManager extends SecurityManager {

    private static final Logger log = LoggerFactory.getLogger(SandboxSecurityManager.class);

    /**
     * 允许的权限
     */
    private static final Permission ALLOWED_PERMISSION = new RuntimePermission("accessDeclaredMembers");

    public SandboxSecurityManager() {
        super();
    }

    /**
     * 检查权限
     */
    @Override
    public void checkPermission(Permission perm) {
        String permName = perm.getName();

        // 允许基本运行时权限
        if (perm instanceof RuntimePermission) {
            // 允许访问声明的成员
            if ("accessDeclaredMembers".equals(permName)) {
                return;
            }
            // 允许设置上下文类加载器
            if ("setContextClassLoader".equals(permName)) {
                return;
            }
            // 允许获取类加载器
            if ("getClassLoader".equals(permName)) {
                return;
            }
        }

        // 允许基本反射权限
        if (perm instanceof java.lang.reflect.ReflectPermission) {
            if ("suppressAccessChecks".equals(permName)) {
                // 禁止深度反射
                throw new SecurityException("Reflection access denied: " + permName);
            }
            return;
        }

        // 拒绝文件操作
        if (perm instanceof FilePermission) {
            throw new SecurityException("File access denied: " + permName);
        }

        // 拒绝网络操作
        if (perm instanceof SocketPermission) {
            throw new SecurityException("Network access denied: " + perm.getActions());
        }

        if (perm instanceof NetPermission) {
            throw new SecurityException("Net access denied: " + permName);
        }

        if (perm instanceof URLPermission) {
            throw new SecurityException("URL access denied: " + permName);
        }

        // 拒绝属性读取（敏感属性）
        if (perm instanceof PropertyPermission) {
            String actions = perm.getActions();
            if ("read".equals(actions)) {
                // 允许读取一般属性
                String[] safeProperties = {
                    "java.version", "java.vendor", "os.name", "os.version",
                    "user.name", "user.home", "user.dir"
                };
                for (String safe : safeProperties) {
                    if (safe.equals(permName)) {
                        return;
                    }
                }
                // 禁止读取敏感属性
                throw new SecurityException("Property read denied: " + permName);
            }
            if ("write".equals(actions)) {
                throw new SecurityException("Property write denied: " + permName);
            }
        }

        // 拒绝系统命令执行
        if (perm instanceof RuntimePermission) {
            if (permName.startsWith("exec") || permName.startsWith("loadLibrary")
                    || permName.startsWith("setFactory") || permName.startsWith("shutdownHooks")) {
                throw new SecurityException("Runtime access denied: " + permName);
            }
        }

        // 拒绝线程操作
        if (perm instanceof RuntimePermission) {
            if (permName.startsWith("modifyThread") || permName.startsWith("stopThread")) {
                throw new SecurityException("Thread access denied: " + permName);
            }
        }

        // 拒绝类加载器操作
        if (perm instanceof RuntimePermission) {
            if (permName.startsWith("createClassLoader") || permName.startsWith("getClassLoader")) {
                throw new SecurityException("ClassLoader access denied: " + permName);
            }
        }

        // 拒绝进程操作
        if (perm instanceof RuntimePermission) {
            if (permName.equals("exitVM") || permName.startsWith("killThread")) {
                throw new SecurityException("Process access denied: " + permName);
            }
        }

        // 拒绝安全管理器操作
        if (perm instanceof SecurityPermission) {
            if (!"getPolicy".equals(permName) && ! "getProperty".equals(permName)) {
                throw new SecurityException("Security configuration access denied: " + permName);
            }
        }

        // 允许其他基本权限
        log.debug("Allowed permission: {}", perm);
    }

    /**
     * 检查权限（带上下文）
     */
    @Override
    public void checkPermission(Permission perm, Object context) {
        checkPermission(perm);
    }

    /**
     * 检查exec执行
     */
    @Override
    public void checkExec(String cmd) {
        throw new SecurityException("Command execution denied: " + cmd);
    }

    /**
     * 检查文件读
     */
    @Override
    public void checkRead(String file) {
        throw new SecurityException("File read denied: " + file);
    }

    /**
     * 检查文件写
     */
    @Override
    public void checkWrite(String file) {
        throw new SecurityException("File write denied: " + file);
    }

    /**
     * 检查删除
     */
    @Override
    public void checkDelete(String file) {
        throw new SecurityException("File delete denied: " + file);
    }

    /**
     * 检查网络连接
     */
    @Override
    public void checkConnect(String host, int port) {
        throw new SecurityException("Network connection denied: " + host + ":" + port);
    }

    /**
     * 检查网络监听
     */
    @Override
    public void checkListen(int port) {
        throw new SecurityException("Network listen denied on port: " + port);
    }

    /**
     * 检查.accept连接
     */
    @Override
    public void checkAccept(String host, int port) {
        throw new SecurityException("Network accept denied: " + host + ":" + port);
    }

    /**
     * 检查反射
     */
    @Override
    public void checkAccess(Thread t) {
        // 允许访问当前线程
        if (Thread.currentThread().equals(t)) {
            return;
        }
        throw new SecurityException("Thread access denied");
    }

    /**
     * 检查包访问
     */
    @Override
    public void checkPackageAccess(String pkgName) {
        // 限制访问敏感包
        String[] restrictedPackages = {
            "java.lang.invoke", "sun.misc", "sun.reflect",
            "com.sun.nio", "jdk.internal"
        };
        for (String restricted : restrictedPackages) {
            if (pkgName.startsWith(restricted)) {
                throw new SecurityException("Package access denied: " + pkgName);
            }
        }
    }

    /**
     * 检查包定义
     */
    @Override
    public void checkPackageDefinition(String pkgName) {
        // 限制在敏感包中定义类
        String[] restrictedPackages = {
            "java.", "sun.", "javax.", "jdk.", "com.sun.", "oracle."
        };
        for (String restricted : restrictedPackages) {
            if (pkgName.startsWith(restricted)) {
                throw new SecurityException("Package definition denied: " + pkgName);
            }
        }
    }

    /**
     * 检查类加载器
     */
    @Override
    public void checkCreateClassLoader() {
        throw new SecurityException("ClassLoader creation denied");
    }

    /**
     * 检查设置安全管理器
     */
    @Override
    public void checkSetFactory() {
        throw new SecurityException("Factory setting denied");
    }

    /**
     * 检查MemberAccess
     */
    @Override
    public void checkMemberAccess(Class<?> clazz, int type) {
        if (type != Member.PUBLIC) {
            throw new SecurityException("Member access denied to: " + clazz);
        }
    }
}

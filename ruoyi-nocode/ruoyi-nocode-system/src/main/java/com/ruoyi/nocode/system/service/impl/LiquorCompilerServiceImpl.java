package com.ruoyi.nocode.system.service.impl;

import com.ruoyi.nocode.system.entity.DynamicCode;
import com.ruoyi.nocode.system.mapper.DynamicCodeMapper;
import com.ruoyi.nocode.system.service.ILiquorCompilerService;
import lombok.extern.slf4j.Slf4j;
import org.noear.liquor.DynamicCompiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FilePermission;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.NetPermission;
import java.security.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Liquor动态编译服务实现
 * 使用org.noear:liquor实现Java源码即时编译
 * 
 * @author ruoyi-nocode
 */
@Slf4j
@Service
public class LiquorCompilerServiceImpl implements ILiquorCompilerService {

    @Autowired
    private DynamicCodeMapper dynamicCodeMapper;

    /**
     * 动态编译器 - Liquor核心组件
     */
    private DynamicCompiler compiler;

    /**
     * 类缓存 - 存储已编译的类
     */
    private final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    /**
     * 自定义类加载器 - 支持热替换
     */
    private final HotReloadableClassLoader classLoader = new HotReloadableClassLoader();

    /**
     * 初始化编译器
     */
    private synchronized DynamicCompiler getCompiler() {
        if (compiler == null) {
            compiler = new DynamicCompiler();
        }
        return compiler;
    }

    @Override
    public CompileResult compile(String sourceCode, String className) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.debug("开始编译类: {}", className);
            
            DynamicCompiler localCompiler = getCompiler();
            
            // 添加源代码
            localCompiler.addSource(className, sourceCode);
            
            // 编译
            localCompiler.build();
            
            // 从 DynamicClassLoader 获取编译后的字节码
            org.noear.liquor.DynamicClassLoader loader = localCompiler.getClassLoader();
            org.noear.liquor.MemoryByteCode byteCode = loader.getClassBytes(className);
            
            if (byteCode == null) {
                return CompileResult.failure("编译失败: 未找到类 " + className + " 的字节码");
            }
            
            byte[] bytecode = byteCode.getByteCode();
            long compileTime = System.currentTimeMillis() - startTime;
            log.debug("编译成功: {}, 耗时: {}ms", className, compileTime);
            
            return CompileResult.success(className, bytecode, compileTime);
            
        } catch (Exception e) {
            log.error("编译失败: {}", className, e);
            return CompileResult.failure("编译异常: " + e.getMessage());
        }
    }

    @Override
    public CompileResult compileAndCache(DynamicCode dynamicCode) {
        String className = dynamicCode.getClassName();
        String sourceCode = dynamicCode.getSourceCode();
        
        CompileResult result = compile(sourceCode, className);
        
        if (result.success()) {
            try {
                // 缓存字节码到数据库
                String encodedBytes = Base64.getEncoder().encodeToString(result.bytecode());
                dynamicCode.setCompiledBytes(encodedBytes);
                dynamicCode.setStatus(DynamicCode.STATUS_COMPILED);
                dynamicCode.setLastCompileTime(LocalDateTime.now());
                dynamicCode.setCompileError(null);
                dynamicCode.setCodeVersion(
                        dynamicCode.getCodeVersion() == null ? 1 : dynamicCode.getCodeVersion() + 1
                );
                dynamicCodeMapper.updateById(dynamicCode);
                
                // 加载到内存
                Class<?> clazz = classLoader.defineClass(result.className(), result.bytecode());
                classCache.put(result.className(), clazz);
                
                log.info("编译并缓存成功: {}", className);
                
            } catch (Exception e) {
                log.error("缓存编译结果失败: {}", className, e);
                dynamicCode.setStatus(DynamicCode.STATUS_ERROR);
                dynamicCode.setCompileError("缓存失败: " + e.getMessage());
                dynamicCodeMapper.updateById(dynamicCode);
                return CompileResult.failure("缓存失败: " + e.getMessage());
            }
        } else {
            // 更新编译错误
            dynamicCode.setStatus(DynamicCode.STATUS_ERROR);
            dynamicCode.setCompileError(result.error());
            dynamicCode.setLastCompileTime(LocalDateTime.now());
            dynamicCodeMapper.updateById(dynamicCode);
        }
        
        return result;
    }

    @Override
    public Class<?> loadClass(String className) {
        // 先从缓存中查找
        Class<?> cachedClass = classCache.get(className);
        if (cachedClass != null) {
            return cachedClass;
        }
        
        // 从数据库加载已编译的字节码
        DynamicCode dynamicCode = dynamicCodeMapper.selectByClassName(className);
        if (dynamicCode == null || dynamicCode.getCompiledBytes() == null) {
            throw new RuntimeException("未找到已编译的类: " + className);
        }
        
        try {
            byte[] bytecode = Base64.getDecoder().decode(dynamicCode.getCompiledBytes());
            Class<?> clazz = classLoader.defineClass(className, bytecode);
            classCache.put(className, clazz);
            return clazz;
            
        } catch (Exception e) {
            throw new RuntimeException("加载类失败: " + className, e);
        }
    }

    @Override
    public Object executeStaticMethod(String className, String methodName, Object... args) {
        try {
            Class<?> clazz = loadClass(className);
            
            // 查找静态方法
            Method method = findMethod(clazz, methodName, args);
            if (method == null) {
                throw new NoSuchMethodException("未找到方法: " + methodName);
            }
            
            if (!Modifier.isStatic(method.getModifiers())) {
                throw new IllegalArgumentException("方法不是静态的: " + methodName);
            }
            
            method.setAccessible(true);
            return method.invoke(null, args);
            
        } catch (Exception e) {
            log.error("执行静态方法失败: {}.{}", className, methodName, e);
            throw new RuntimeException("执行方法失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Object newInstance(String className, Object... args) {
        try {
            Class<?> clazz = loadClass(className);
            
            // 查找匹配的构造函数
            Class<?>[] paramTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
            }
            
            return clazz.getDeclaredConstructor(paramTypes).newInstance(args);
            
        } catch (Exception e) {
            log.error("创建实例失败: {}", className, e);
            throw new RuntimeException("创建实例失败: " + e.getMessage(), e);
        }
    }

    @Override
    public SandboxResult executeInSandbox(DynamicCode dynamicCode, String methodName, Object... args) {
        long startTime = System.currentTimeMillis();
        
        // 解析安全配置
        SecurityManager originalSecurityManager = System.getSecurityManager();
        
        try {
            // 创建沙箱环境
            if (dynamicCode.getSandboxMode() != null && dynamicCode.getSandboxMode()) {
                SecurityManager sandboxManager = createSandboxSecurityManager();
                System.setSecurityManager(sandboxManager);
            }
            
            // 执行方法
            Object result = executeStaticMethod(dynamicCode.getClassName(), methodName, args);
            
            long executionTime = System.currentTimeMillis() - startTime;
            return SandboxResult.success(result, executionTime);
            
        } catch (Exception e) {
            log.error("沙箱执行失败: {}.{}", dynamicCode.getClassName(), methodName, e);
            return SandboxResult.failure(e.getMessage());
            
        } finally {
            // 恢复原安全管理器
            System.setSecurityManager(originalSecurityManager);
        }
    }

    @Override
    public boolean hotReplace(DynamicCode dynamicCode) {
        try {
            // 重新编译
            CompileResult result = compile(dynamicCode.getSourceCode(), dynamicCode.getClassName());
            
            if (!result.success()) {
                log.error("热替换编译失败: {}", dynamicCode.getClassName());
                return false;
            }
            
            // 更新缓存
            Class<?> newClass = classLoader.defineClass(result.className(), result.bytecode());
            classCache.put(result.className(), newClass);
            
            // 更新数据库
            String encodedBytes = Base64.getEncoder().encodeToString(result.bytecode());
            dynamicCode.setCompiledBytes(encodedBytes);
            dynamicCode.setStatus(DynamicCode.STATUS_COMPILED);
            dynamicCode.setLastCompileTime(LocalDateTime.now());
            dynamicCode.setCodeVersion(
                    dynamicCode.getCodeVersion() == null ? 1 : dynamicCode.getCodeVersion() + 1
            );
            dynamicCodeMapper.updateById(dynamicCode);
            
            log.info("热替换成功: {}", dynamicCode.getClassName());
            return true;
            
        } catch (Exception e) {
            log.error("热替换失败: {}", dynamicCode.getClassName(), e);
            return false;
        }
    }

    @Override
    public void removeCachedClass(String className) {
        classCache.remove(className);
        classLoader.undefineClass(className);
        log.info("移除缓存类: {}", className);
    }

    @Override
    public Set<String> getCachedClasses() {
        return Set.copyOf(classCache.keySet());
    }

    /**
     * 查找匹配的方法
     */
    private Method findMethod(Class<?> clazz, String methodName, Object[] args) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && 
                method.getParameterCount() == args.length) {
                // 简单的类型匹配
                boolean match = true;
                Class<?>[] paramTypes = method.getParameterTypes();
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null && !paramTypes[i].isAssignableFrom(args[i].getClass())) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 创建沙箱安全管理器
     */
    private SecurityManager createSandboxSecurityManager() {
        return new SecurityManager() {
            @Override
            public void checkPermission(Permission perm) {
                // 允许基本权限
                if (perm instanceof RuntimePermission) {
                    String name = perm.getName();
                    if (name.equals("accessDeclaredMembers") ||
                        name.equals("setContextClassLoader") ||
                        name.equals("createClassLoader")) {
                        return;
                    }
                }
                if (perm instanceof java.lang.reflect.ReflectPermission) {
                    return;
                }
                // 禁止文件操作、网络操作等危险权限
                if (perm instanceof FilePermission || perm instanceof NetPermission) {
                    throw new SecurityException("沙箱模式下禁止操作: " + perm);
                }
            }
            
            @Override
            public void checkPermission(Permission perm, Object context) {
                checkPermission(perm);
            }
        };
    }

    /**
     * 支持热替换的类加载器
     */
    private static class HotReloadableClassLoader extends ClassLoader {
        
        public HotReloadableClassLoader() {
            super(Thread.currentThread().getContextClassLoader());
        }
        
        /**
         * 定义类
         */
        public Class<?> defineClass(String name, byte[] bytecode) {
            return defineClass(name, bytecode, 0, bytecode.length);
        }
        
        /**
         * 移除类定义
         */
        public void undefineClass(String name) {
            // JVM不支持真正的卸载，但可以通过新的类加载器实现
            // 这里仅作标记
        }
    }
}

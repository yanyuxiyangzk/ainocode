package com.ruoyi.nocode.system.service;

import com.ruoyi.nocode.system.entity.DynamicCode;

import java.util.Map;

/**
 * Liquor动态编译服务接口
 * 
 * @author ruoyi-nocode
 */
public interface ILiquorCompilerService {

    /**
     * 编译Java源代码
     *
     * @param sourceCode   Java源代码
     * @param className    类全限定名
     * @return 编译结果
     */
    CompileResult compile(String sourceCode, String className);

    /**
     * 编译并缓存动态代码
     *
     * @param dynamicCode 动态代码实体
     * @return 编译结果
     */
    CompileResult compileAndCache(DynamicCode dynamicCode);

    /**
     * 加载已编译的类
     *
     * @param className 类全限定名
     * @return 类对象
     */
    Class<?> loadClass(String className);

    /**
     * 执行动态代码的静态方法
     *
     * @param className  类名
     * @param methodName 方法名
     * @param args       参数
     * @return 执行结果
     */
    Object executeStaticMethod(String className, String methodName, Object... args);

    /**
     * 创建动态类的实例
     *
     * @param className 类名
     * @param args      构造函数参数
     * @return 实例对象
     */
    Object newInstance(String className, Object... args);

    /**
     * 在沙箱环境中执行代码
     *
     * @param dynamicCode 动态代码
     * @param methodName  方法名
     * @param args        参数
     * @return 执行结果
     */
    SandboxResult executeInSandbox(DynamicCode dynamicCode, String methodName, Object... args);

    /**
     * 热替换类（重新编译并替换）
     *
     * @param dynamicCode 动态代码
     * @return 是否成功
     */
    boolean hotReplace(DynamicCode dynamicCode);

    /**
     * 移除缓存的类
     *
     * @param className 类名
     */
    void removeCachedClass(String className);

    /**
     * 获取缓存的类列表
     *
     * @return 类名列表
     */
    java.util.Set<String> getCachedClasses();

    /**
     * 编译结果
     */
    record CompileResult(
            boolean success,
            String className,
            byte[] bytecode,
            String error,
            long compileTime
    ) {
        public static CompileResult success(String className, byte[] bytecode, long compileTime) {
            return new CompileResult(true, className, bytecode, null, compileTime);
        }

        public static CompileResult failure(String error) {
            return new CompileResult(false, null, null, error, 0);
        }
    }

    /**
     * 沙箱执行结果
     */
    record SandboxResult(
            boolean success,
            Object result,
            String error,
            long executionTime
    ) {
        public static SandboxResult success(Object result, long executionTime) {
            return new SandboxResult(true, result, null, executionTime);
        }

        public static SandboxResult failure(String error) {
            return new SandboxResult(false, null, error, 0);
        }
    }
}

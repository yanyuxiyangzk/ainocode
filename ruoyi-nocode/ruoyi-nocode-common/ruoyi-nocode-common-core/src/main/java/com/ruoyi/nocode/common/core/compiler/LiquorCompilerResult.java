package com.ruoyi.nocode.common.core.compiler;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Liquor即时编译结果
 *
 * @author ruoyi
 */
@Data
public class LiquorCompilerResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编译是否成功
     */
    private boolean success;

    /**
     * 编译后的Class对象
     */
    private Class<?> compiledClass;

    /**
     * 错误信息列表
     */
    private List<String> errors;

    /**
     * 警告信息列表
     */
    private List<String> warnings;

    /**
     * 源代码
     */
    private String sourceCode;

    /**
     * 类全名
     */
    private String className;

    /**
     * 创建成功结果
     */
    public static LiquorCompilerResult success(Class<?> compiledClass, String className, String sourceCode) {
        LiquorCompilerResult result = new LiquorCompilerResult();
        result.setSuccess(true);
        result.setCompiledClass(compiledClass);
        result.setClassName(className);
        result.setSourceCode(sourceCode);
        return result;
    }

    /**
     * 创建失败结果
     */
    public static LiquorCompilerResult failure(List<String> errors, String sourceCode) {
        LiquorCompilerResult result = new LiquorCompilerResult();
        result.setSuccess(false);
        result.setErrors(errors);
        result.setSourceCode(sourceCode);
        return result;
    }

    /**
     * 创建失败结果（单条错误）
     */
    public static LiquorCompilerResult failure(String error, String sourceCode) {
        return failure(List.of(error), sourceCode);
    }
}

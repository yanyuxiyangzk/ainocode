package com.ruoyi.nocode.common.core.compiler;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 类热替换结果
 *
 * @author ruoyi
 */
@Data
public class HotSwapResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 热替换是否成功
     */
    private boolean success;

    /**
     * 热替换后的Class对象
     */
    private Class<?> swappedClass;

    /**
     * 热替换的唯一标识（domain + version）
     */
    private String swapId;

    /**
     * 热替换版本号
     */
    private int version;

    /**
     * 关联的类加载器ID
     */
    private String classLoaderId;

    /**
     * 错误信息列表
     */
    private List<String> errors;

    /**
     * 替换前的类名
     */
    private String originalClassName;

    /**
     * 创建成功结果
     */
    public static HotSwapResult success(Class<?> swappedClass, String swapId, int version,
                                         String classLoaderId, String originalClassName) {
        HotSwapResult result = new HotSwapResult();
        result.setSuccess(true);
        result.setSwappedClass(swappedClass);
        result.setSwapId(swapId);
        result.setVersion(version);
        result.setClassLoaderId(classLoaderId);
        result.setOriginalClassName(originalClassName);
        return result;
    }

    /**
     * 创建失败结果
     */
    public static HotSwapResult failure(List<String> errors, String originalClassName) {
        HotSwapResult result = new HotSwapResult();
        result.setSuccess(false);
        result.setErrors(errors);
        result.setOriginalClassName(originalClassName);
        return result;
    }

    /**
     * 创建失败结果（单条错误）
     */
    public static HotSwapResult failure(String error, String originalClassName) {
        return failure(List.of(error), originalClassName);
    }
}

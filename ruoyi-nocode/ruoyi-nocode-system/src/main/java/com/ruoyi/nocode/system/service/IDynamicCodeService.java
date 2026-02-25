package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.DynamicCode;
import com.ruoyi.nocode.system.service.ILiquorCompilerService.CompileResult;
import com.ruoyi.nocode.system.service.ILiquorCompilerService.SandboxResult;

import java.util.List;

/**
 * 动态代码Service接口
 * 
 * @author ruoyi-nocode
 */
public interface IDynamicCodeService extends IService<DynamicCode> {

    /**
     * 查询动态代码列表
     *
     * @param dynamicCode 查询条件
     * @return 动态代码列表
     */
    List<DynamicCode> selectDynamicCodeList(DynamicCode dynamicCode);

    /**
     * 根据代码编码查询
     *
     * @param codeCode 代码编码
     * @return 动态代码
     */
    DynamicCode selectByCodeCode(String codeCode);

    /**
     * 根据类名查询
     *
     * @param className 类全限定名
     * @return 动态代码
     */
    DynamicCode selectByClassName(String className);

    /**
     * 查询已启用的动态代码
     *
     * @return 动态代码列表
     */
    List<DynamicCode> selectEnabledCodes();

    /**
     * 校验代码编码是否唯一
     *
     * @param dynamicCode 动态代码
     * @return 结果
     */
    boolean checkCodeCodeUnique(DynamicCode dynamicCode);

    /**
     * 校验类名是否唯一
     *
     * @param dynamicCode 动态代码
     * @return 结果
     */
    boolean checkClassNameUnique(DynamicCode dynamicCode);

    /**
     * 编译动态代码
     *
     * @param codeId 代码ID
     * @return 编译结果
     */
    CompileResult compileCode(Long codeId);

    /**
     * 编译所有草稿状态的代码
     *
     * @return 编译结果统计
     */
    CompileStatistics compileAllDraft();

    /**
     * 执行动态代码方法
     *
     * @param codeId    代码ID
     * @param methodName 方法名
     * @param args      参数
     * @return 执行结果
     */
    Object executeMethod(Long codeId, String methodName, Object... args);

    /**
     * 在沙箱中执行
     *
     * @param codeId    代码ID
     * @param methodName 方法名
     * @param args      参数
     * @return 沙箱执行结果
     */
    SandboxResult executeInSandbox(Long codeId, String methodName, Object... args);

    /**
     * 热替换代码
     *
     * @param codeId 代码ID
     * @return 是否成功
     */
    boolean hotReplace(Long codeId);

    /**
     * 发布动态代码
     *
     * @param codeId 代码ID
     * @return 是否成功
     */
    boolean publishCode(Long codeId);

    /**
     * 创建动态代码实例
     *
     * @param codeId 代码ID
     * @param args   构造参数
     * @return 实例对象
     */
    Object createInstance(Long codeId, Object... args);

    /**
     * 编译统计
     */
    record CompileStatistics(
            int total,
            int success,
            int failed,
            List<String> failedCodes
    ) {
        public static CompileStatistics of(int total, int success, int failed, List<String> failedCodes) {
            return new CompileStatistics(total, success, failed, failedCodes);
        }
    }
}

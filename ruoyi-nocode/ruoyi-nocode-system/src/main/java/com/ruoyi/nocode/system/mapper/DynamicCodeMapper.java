package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.DynamicCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 动态代码Mapper接口
 * 
 * @author ruoyi-nocode
 */
@Mapper
public interface DynamicCodeMapper extends BaseMapper<DynamicCode> {

    /**
     * 根据代码编码查询
     *
     * @param codeCode 代码编码
     * @return 动态代码
     */
    DynamicCode selectByCodeCode(@Param("codeCode") String codeCode);

    /**
     * 根据类全限定名查询
     *
     * @param className 类全限定名
     * @return 动态代码
     */
    DynamicCode selectByClassName(@Param("className") String className);

    /**
     * 查询所有已启用的动态代码
     *
     * @return 动态代码列表
     */
    List<DynamicCode> selectEnabledCodes();

    /**
     * 根据插件ID查询关联的动态代码
     *
     * @param pluginId 插件ID
     * @return 动态代码列表
     */
    List<DynamicCode> selectByPluginId(@Param("pluginId") Long pluginId);

    /**
     * 根据状态查询
     *
     * @param status 状态
     * @return 动态代码列表
     */
    List<DynamicCode> selectByStatus(@Param("status") String status);

    /**
     * 校验代码编码是否唯一
     *
     * @param codeCode 代码编码
     * @param codeId   代码ID（编辑时排除自身）
     * @return 动态代码
     */
    DynamicCode checkCodeCodeUnique(@Param("codeCode") String codeCode, @Param("codeId") Long codeId);

    /**
     * 校验类名是否唯一
     *
     * @param className 类全限定名
     * @param codeId    代码ID（编辑时排除自身）
     * @return 动态代码
     */
    DynamicCode checkClassNameUnique(@Param("className") String className, @Param("codeId") Long codeId);

    /**
     * 更新执行统计
     *
     * @param codeId 代码ID
     * @return 影响行数
     */
    int incrementExecuteCount(@Param("codeId") Long codeId);
}

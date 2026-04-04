package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.FormComponentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 表单组件Mapper接口
 *
 * @author ruoyi-nocode
 */
@Mapper
public interface FormComponentMapper extends BaseMapper<FormComponentEntity> {

    /**
     * 根据表单ID查询组件列表
     */
    List<FormComponentEntity> selectByFormId(@Param("formId") Long formId);

    /**
     * 根据表单ID删除组件
     */
    int deleteByFormId(@Param("formId") Long formId);

    /**
     * 批量插入组件
     */
    int batchInsert(@Param("components") List<FormComponentEntity> components);

    /**
     * 根据组件编码查询
     */
    FormComponentEntity selectByComponentCode(@Param("formId") Long formId, @Param("componentCode") String componentCode);

    /**
     * 更新组件排序
     */
    int updateSort(@Param("componentId") Long componentId, @Param("sort") Integer sort);
}
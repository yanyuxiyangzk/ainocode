package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.FormConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 表单配置Mapper接口
 *
 * @author ruoyi-nocode
 */
@Mapper
public interface FormConfigMapper extends BaseMapper<FormConfigEntity> {

    /**
     * 查询表单配置列表
     */
    List<FormConfigEntity> selectFormConfigList(FormConfigEntity formConfig);

    /**
     * 根据表单编码查询
     */
    FormConfigEntity selectByFormCode(@Param("formCode") String formCode);

    /**
     * 查询启用的表单
     */
    List<FormConfigEntity> selectEnabledFormList();

    /**
     * 物理删除表单配置
     */
    int physicallyDeleteById(@Param("formId") Long formId);

    /**
     * 批量物理删除
     */
    int physicallyDeleteByIds(@Param("formIds") List<Long> formIds);
}
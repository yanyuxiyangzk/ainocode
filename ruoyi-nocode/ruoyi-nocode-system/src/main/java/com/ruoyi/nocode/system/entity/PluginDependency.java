package com.ruoyi.nocode.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.nocode.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插件依赖关系实体类
 * 用于管理插件之间的依赖关系
 * 
 * @author ruoyi-nocode
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("nocode_plugin_dependency")
public class PluginDependency extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 依赖ID（自增）
     */
    @TableId(value = "dependency_id", type = IdType.AUTO)
    private Long dependencyId;

    /**
     * 插件ID（依赖方）
     */
    private Long pluginId;

    /**
     * 依赖的插件编码
     */
    private String dependPluginCode;

    /**
     * 依赖的插件名称
     */
    private String dependPluginName;

    /**
     * 最低版本要求
     */
    private String minVersion;

    /**
     * 最高版本要求（可选）
     */
    private String maxVersion;

    /**
     * 是否必需（必需缺失时插件无法启动）
     */
    private Boolean required;

    /**
     * 依赖状态: RESOLVED-已解析, MISSING-缺失, VERSION_MISMATCH-版本不匹配, SATISFIED-已满足
     */
    private String status;

    /**
     * 解析消息
     */
    private String resolveMessage;

    // ==================== 依赖状态常量 ====================

    /**
     * 依赖状态 - 已解析
     */
    public static final String STATUS_RESOLVED = "RESOLVED";

    /**
     * 依赖状态 - 缺失
     */
    public static final String STATUS_MISSING = "MISSING";

    /**
     * 依赖状态 - 版本不匹配
     */
    public static final String STATUS_VERSION_MISMATCH = "VERSION_MISMATCH";

    /**
     * 依赖状态 - 已满足
     */
    public static final String STATUS_SATISFIED = "SATISFIED";
}

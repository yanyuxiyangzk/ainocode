package com.ruoyi.nocode.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.nocode.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 插件信息实体类
 * 
 * @author ruoyi-nocode
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("nocode_plugin_info")
public class PluginInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 插件ID（自增）
     */
    @TableId(value = "plugin_id", type = IdType.AUTO)
    private Long pluginId;

    /**
     * 插件编码（唯一标识）
     */
    private String pluginCode;

    /**
     * 插件名称
     */
    private String pluginName;

    /**
     * 插件版本
     */
    private String pluginVersion;

    /**
     * 插件描述
     */
    private String pluginDesc;

    /**
     * 插件主类全限定名
     */
    private String pluginClass;

    /**
     * 插件提供者
     */
    private String pluginProvider;

    /**
     * 插件依赖（JSON格式）
     */
    private String pluginDependency;

    /**
     * 插件JAR路径
     */
    private String pluginPath;

    /**
     * 插件文件大小（字节）
     */
    private Long pluginSize;

    /**
     * 文件MD5哈希值
     */
    private String fileHash;

    /**
     * 安装方式: JAR-文件上传, MAVEN-Maven坐标
     */
    private String installType;

    /**
     * Maven GroupId
     */
    private String mavenGroup;

    /**
     * Maven ArtifactId
     */
    private String mavenArtifact;

    /**
     * Maven版本号
     */
    private String mavenVersion;

    /**
     * 状态: DISABLED-已停用, ENABLED-已启用, ERROR-异常
     */
    private String status;

    /**
     * 运行模式: STATIC-静态加载, DYNAMIC-动态编译
     */
    private String runMode;

    /**
     * 是否自动启动
     */
    private Boolean autoStart;

    /**
     * 启动顺序
     */
    private Integer startOrder;

    /**
     * 插件配置（JSON格式）
     */
    private String configJson;

    /**
     * 最后加载时间
     */
    private LocalDateTime lastLoadTime;

    /**
     * 最后卸载时间
     */
    private LocalDateTime lastUnloadTime;

    /**
     * 错误信息
     */
    private String errorMsg;

    // ==================== 插件状态常量 ====================

    /**
     * 插件状态 - 已停用
     */
    public static final String STATUS_DISABLED = "DISABLED";

    /**
     * 插件状态 - 已启用
     */
    public static final String STATUS_ENABLED = "ENABLED";

    /**
     * 插件状态 - 异常
     */
    public static final String STATUS_ERROR = "ERROR";

    /**
     * 安装方式 - JAR文件上传
     */
    public static final String INSTALL_TYPE_JAR = "JAR";

    /**
     * 安装方式 - Maven坐标
     */
    public static final String INSTALL_TYPE_MAVEN = "MAVEN";

    /**
     * 运行模式 - 静态加载
     */
    public static final String RUN_MODE_STATIC = "STATIC";

    /**
     * 运行模式 - 动态编译
     */
    public static final String RUN_MODE_DYNAMIC = "DYNAMIC";
}

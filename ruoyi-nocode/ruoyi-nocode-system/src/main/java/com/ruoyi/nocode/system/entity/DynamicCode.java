package com.ruoyi.nocode.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.nocode.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 动态代码实体类
 * 用于存储Liquor动态编译的Java源码
 * 
 * @author ruoyi-nocode
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("nocode_dynamic_code")
public class DynamicCode extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 动态代码ID
     */
    @TableId(value = "code_id", type = IdType.AUTO)
    private Long codeId;

    /**
     * 代码编码（唯一标识）
     */
    private String codeCode;

    /**
     * 代码名称
     */
    private String codeName;

    /**
     * 关联插件ID（可选）
     */
    private Long pluginId;

    /**
     * 类全限定名
     */
    private String className;

    /**
     * 包名
     */
    private String packageName;

    /**
     * Java源代码
     */
    private String sourceCode;

    /**
     * 编译后的字节码（Base64编码）
     */
    @TableField(value = "compiled_bytes")
    private String compiledBytes;

    /**
     * 代码类型: CLASS-类, INTERFACE-接口, ENUM-枚举, SCRIPT-脚本
     */
    private String codeType;

    /**
     * 代码版本
     */
    private Integer codeVersion;

    /**
     * 状态: DRAFT-草稿, COMPILED-已编译, PUBLISHED-已发布, ERROR-编译错误
     */
    private String status;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 沙箱执行标志
     */
    private Boolean sandboxMode;

    /**
     * 安全限制配置（JSON格式）
     */
    private String securityConfig;

    /**
     * 依赖类列表（逗号分隔）
     */
    private String dependencies;

    /**
     * 最后编译时间
     */
    private LocalDateTime lastCompileTime;

    /**
     * 编译错误信息
     */
    private String compileError;

    /**
     * 执行次数
     */
    private Long executeCount;

    /**
     * 最后执行时间
     */
    private LocalDateTime lastExecuteTime;

    /**
     * 上一次执行结果
     */
    @TableField(value = "last_result")
    private String lastResult;

    // ==================== 代码类型常量 ====================

    /**
     * 代码类型 - 类
     */
    public static final String CODE_TYPE_CLASS = "CLASS";

    /**
     * 代码类型 - 接口
     */
    public static final String CODE_TYPE_INTERFACE = "INTERFACE";

    /**
     * 代码类型 - 枚举
     */
    public static final String CODE_TYPE_ENUM = "ENUM";

    /**
     * 代码类型 - 脚本
     */
    public static final String CODE_TYPE_SCRIPT = "SCRIPT";

    // ==================== 状态常量 ====================

    /**
     * 状态 - 草稿
     */
    public static final String STATUS_DRAFT = "DRAFT";

    /**
     * 状态 - 已编译
     */
    public static final String STATUS_COMPILED = "COMPILED";

    /**
     * 状态 - 已发布
     */
    public static final String STATUS_PUBLISHED = "PUBLISHED";

    /**
     * 状态 - 编译错误
     */
    public static final String STATUS_ERROR = "ERROR";
}

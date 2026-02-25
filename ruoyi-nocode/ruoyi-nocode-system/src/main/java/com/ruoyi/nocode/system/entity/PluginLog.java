package com.ruoyi.nocode.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 插件操作日志实体类
 * 
 * @author ruoyi-nocode
 */
@Data
@TableName("nocode_plugin_log")
public class PluginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 插件ID
     */
    private Long pluginId;

    /**
     * 插件编码
     */
    private String pluginCode;

    /**
     * 插件名称
     */
    private String pluginName;

    /**
     * 操作类型: INSTALL-安装, UNINSTALL-卸载, ENABLE-启用, DISABLE-停用, RELOAD-重载
     */
    private String operationType;

    /**
     * 操作状态: SUCCESS-成功, FAIL-失败
     */
    private String operationStatus;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * 操作前状态
     */
    private String oldStatus;

    /**
     * 操作后状态
     */
    private String newStatus;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 操作IP
     */
    private String operationIp;

    /**
     * 操作人
     */
    private String operationBy;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;

    // ==================== 操作类型常量 ====================

    /**
     * 操作类型 - 安装
     */
    public static final String OPERATION_INSTALL = "INSTALL";

    /**
     * 操作类型 - 卸载
     */
    public static final String OPERATION_UNINSTALL = "UNINSTALL";

    /**
     * 操作类型 - 启用
     */
    public static final String OPERATION_ENABLE = "ENABLE";

    /**
     * 操作类型 - 停用
     */
    public static final String OPERATION_DISABLE = "DISABLE";

    /**
     * 操作类型 - 重载
     */
    public static final String OPERATION_RELOAD = "RELOAD";

    /**
     * 操作状态 - 成功
     */
    public static final String STATUS_SUCCESS = "SUCCESS";

    /**
     * 操作状态 - 失败
     */
    public static final String STATUS_FAIL = "FAIL";
}

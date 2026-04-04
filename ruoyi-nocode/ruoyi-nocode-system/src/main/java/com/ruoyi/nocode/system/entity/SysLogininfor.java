package com.ruoyi.nocode.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统访问记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_logininfor")
public class SysLogininfor extends Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问ID
     */
    @TableId(value = "info_id", type = IdType.AUTO)
    private Long infoId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 登录账号状态（0成功 1失败）
     */
    private String status;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;

    /**
     * 删除标志
     */
    private String delFlag;

    /**
     * 开始时间（查询用）
     */
    @TableField(exist = false)
    private LocalDateTime beginTime;

    /**
     * 结束时间（查询用）
     */
    @TableField(exist = false)
    private LocalDateTime endTime;
}

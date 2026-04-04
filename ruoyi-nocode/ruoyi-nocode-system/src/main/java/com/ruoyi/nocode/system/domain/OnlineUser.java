package com.ruoyi.nocode.system.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 在线用户信息
 */
@Data
public class OnlineUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会话token
     */
    private String tokenId;

    /**
     * 用户名
     */
    private String userName;

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
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 会话超时时间（秒）
     */
    private long timeout;
}

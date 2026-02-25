package com.ruoyi.nocode.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import com.ruoyi.nocode.auth.form.LoginVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Token 服务
 * 
 * 提供 Token 管理功能
 * 
 * @author ruoyi-nocode
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String USER_ID_KEY = "userId";
    private static final String USER_NAME_KEY = "userName";
    private static final String NICK_NAME_KEY = "nickName";
    private static final String TOKEN_TYPE = "Bearer";

    /**
     * 获取当前登录用户ID
     */
    public Long getUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前登录用户名
     */
    public String getUsername() {
        try {
            return (String) StpUtil.getSession().get(USER_NAME_KEY);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 创建登录响应
     */
    public LoginVo createLoginVo(Long userId, String username, String nickname) {
        String token = StpUtil.getTokenValue();
        long timeout = StpUtil.getTokenTimeout();

        return LoginVo.builder()
                .accessToken(token)
                .tokenType(TOKEN_TYPE)
                .expiresIn(timeout)
                .userId(userId)
                .username(username)
                .nickname(nickname)
                .build();
    }

    /**
     * 设置用户会话信息
     */
    public void setSessionInfo(Long userId, String username, String nickname) {
        StpUtil.getSession().set(USER_ID_KEY, userId);
        StpUtil.getSession().set(USER_NAME_KEY, username);
        StpUtil.getSession().set(NICK_NAME_KEY, nickname);
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Object loginId = StpUtil.getLoginIdByToken(token);
            return loginId != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新 Token 有效期
     */
    public void refreshToken() {
        StpUtil.renewTimeout(StpUtil.getTokenTimeout());
    }

    /**
     * 获取 Token 剩余有效时间（秒）
     */
    public long getTokenTimeout() {
        return StpUtil.getTokenTimeout();
    }
}

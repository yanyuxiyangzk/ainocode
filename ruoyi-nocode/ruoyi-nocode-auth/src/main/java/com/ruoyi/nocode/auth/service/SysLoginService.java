package com.ruoyi.nocode.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import com.ruoyi.nocode.auth.form.LoginBody;
import com.ruoyi.nocode.auth.form.LoginVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 登录服务
 * 
 * 处理用户登录逻辑
 * 
 * @author ruoyi-nocode
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLoginService {

    private final TokenService tokenService;
    private final SysPasswordService passwordService;
    private final SysUserDetailService userDetailService;

    /**
     * 用户登录
     * 
     * @param loginBody 登录请求
     * @return 登录响应
     */
    public LoginVo login(LoginBody loginBody) {
        String username = loginBody.getUsername();
        String password = loginBody.getPassword();

        log.info("用户登录: {}", username);

        // 1. 查询用户
        SysUserDetails userDetails = userDetailService.loadUserByUsername(username);
        if (userDetails == null) {
            log.warn("用户不存在: {}", username);
            throw new ServiceException("用户不存在");
        }

        // 2. 验证密码
        if (!passwordService.matches(password, userDetails.getPassword())) {
            log.warn("密码错误: {}", username);
            throw new ServiceException("密码错误");
        }

        // 3. 检查账号状态
        if (!userDetails.isEnabled()) {
            log.warn("账号已被禁用: {}", username);
            throw new ServiceException("账号已被禁用");
        }

        if (!userDetails.isAccountNonLocked()) {
            log.warn("账号已被锁定: {}", username);
            throw new ServiceException("账号已被锁定");
        }

        // 4. Sa-Token 登录
        StpUtil.login(userDetails.getUserId());

        // 5. 设置会话信息
        tokenService.setSessionInfo(
                userDetails.getUserId(),
                userDetails.getUsername(),
                userDetails.getNickname()
        );

        // 6. 创建登录响应
        LoginVo loginVo = tokenService.createLoginVo(
                userDetails.getUserId(),
                userDetails.getUsername(),
                userDetails.getNickname()
        );

        log.info("用户登录成功: {}, userId: {}", username, userDetails.getUserId());
        return loginVo;
    }

    /**
     * 用户登出
     */
    public void logout() {
        Long userId = tokenService.getUserId();
        if (userId != null) {
            log.info("用户登出: userId: {}", userId);
        }
        StpUtil.logout();
    }
}

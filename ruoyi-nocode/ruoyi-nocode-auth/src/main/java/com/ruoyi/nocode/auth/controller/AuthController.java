package com.ruoyi.nocode.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ruoyi.nocode.auth.form.LoginBody;
import com.ruoyi.nocode.auth.form.LoginVo;
import com.ruoyi.nocode.auth.service.SysLoginService;
import com.ruoyi.nocode.auth.service.SysUserDetailService;
import com.ruoyi.nocode.auth.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 
 * 提供登录、登出、用户信息等接口
 * 
 * @author ruoyi-nocode
 */
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final SysLoginService loginService;
    private final TokenService tokenService;
    private final SysUserDetailService userDetailService;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public R<LoginVo> login(@Valid @RequestBody LoginBody loginBody) {
        LoginVo loginVo = loginService.login(loginBody);
        return R.ok(loginVo);
    }

    /**
     * 登出接口
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        loginService.logout();
        return R.ok();
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public R<Map<String, Object>> info() {
        Long userId = tokenService.getUserId();
        if (userId == null) {
            return R.fail("未登录");
        }

        var userDetails = userDetailService.loadUserByUserId(userId);
        if (userDetails == null) {
            return R.fail("用户不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userDetails.getUserId());
        data.put("username", userDetails.getUsername());
        data.put("nickname", userDetails.getNickname());
        data.put("roles", userDetails.getRoles());
        data.put("permissions", userDetails.getPermissions());

        return R.ok(data);
    }

    /**
     * 获取当前 Token 信息
     */
    @GetMapping("/token/info")
    public R<Map<String, Object>> tokenInfo() {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", StpUtil.getLoginIdDefaultNull());
        data.put("tokenValue", StpUtil.getTokenValue());
        data.put("tokenTimeout", StpUtil.getTokenTimeout());
        return R.ok(data);
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/token/refresh")
    public R<Void> refreshToken() {
        tokenService.refreshToken();
        return R.ok();
    }

    /**
     * 获取验证码
     */
    @GetMapping("/captcha")
    public R<Map<String, Object>> captcha() {
        // TODO: 实现验证码生成
        Map<String, Object> data = new HashMap<>();
        data.put("enabled", false);
        data.put("captchaEnabled", false);
        return R.ok(data);
    }
}

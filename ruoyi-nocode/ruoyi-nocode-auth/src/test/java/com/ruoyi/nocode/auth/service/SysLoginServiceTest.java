package com.ruoyi.nocode.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import com.ruoyi.nocode.auth.form.LoginBody;
import com.ruoyi.nocode.auth.form.LoginVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SysLoginService 单元测试
 * 
 * @author ruoyi-nocode
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SysLoginServiceTest {

    @Mock
    private SysUserDetailService userDetailService;

    @Mock
    private SysPasswordService passwordService;

    @Mock
    private TokenService tokenService;

    @Mock
    private SysUserDetails userDetails;

    @InjectMocks
    private SysLoginService loginService;

    private LoginVo mockLoginVo;

    @BeforeEach
    void setUp() {
        // 设置测试用户详情
        when(userDetails.getUserId()).thenReturn(1L);
        when(userDetails.getUsername()).thenReturn("admin");
        when(userDetails.getPassword()).thenReturn("$2a$10$hashedpassword");
        when(userDetails.getNickname()).thenReturn("管理员");
        when(userDetails.isEnabled()).thenReturn(true);
        when(userDetails.isAccountNonLocked()).thenReturn(true);

        // 设置登录响应
        mockLoginVo = LoginVo.builder()
                .token("token-abc-123")
                .tokenType("Bearer")
                .expiresIn(7200L)
                .userId(1L)
                .userName("admin")
                .nickname("管理员")
                .build();
    }

    @Test
    @DisplayName("登录成功")
    void testLoginSuccess() {
        LoginBody loginBody = new LoginBody();
        loginBody.setUsername("admin");
        loginBody.setPassword("123456");

        when(userDetailService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(passwordService.matches("123456", "$2a$10$hashedpassword")).thenReturn(true);
        when(tokenService.createLoginVo(1L, "admin", "管理员")).thenReturn(mockLoginVo);

        try (MockedStatic<StpUtil> mockedStpUtil = mockStatic(StpUtil.class)) {
            mockedStpUtil.when(() -> StpUtil.login(1L)).then(invocation -> null);

            LoginVo result = loginService.login(loginBody);

            assertNotNull(result);
            assertEquals("token-abc-123", result.getToken());
            assertEquals(1L, result.getUserId());
        }
    }

    @Test
    @DisplayName("用户不存在抛出异常")
    void testUserNotFound() {
        LoginBody loginBody = new LoginBody();
        loginBody.setUsername("notexist");
        loginBody.setPassword("123456");

        when(userDetailService.loadUserByUsername("notexist")).thenReturn(null);

        assertThrows(ServiceException.class, () -> {
            loginService.login(loginBody);
        });
    }

    @Test
    @DisplayName("密码错误抛出异常")
    void testWrongPassword() {
        LoginBody loginBody = new LoginBody();
        loginBody.setUsername("admin");
        loginBody.setPassword("wrongpassword");

        when(userDetailService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(passwordService.matches("wrongpassword", "$2a$10$hashedpassword")).thenReturn(false);

        assertThrows(ServiceException.class, () -> {
            loginService.login(loginBody);
        });
    }

    @Test
    @DisplayName("账号禁用抛出异常")
    void testAccountDisabled() {
        LoginBody loginBody = new LoginBody();
        loginBody.setUsername("admin");
        loginBody.setPassword("123456");

        when(userDetails.isEnabled()).thenReturn(false);
        when(userDetailService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(passwordService.matches("123456", "$2a$10$hashedpassword")).thenReturn(true);

        assertThrows(ServiceException.class, () -> {
            loginService.login(loginBody);
        });
    }

    @Test
    @DisplayName("账号锁定抛出异常")
    void testAccountLocked() {
        LoginBody loginBody = new LoginBody();
        loginBody.setUsername("admin");
        loginBody.setPassword("123456");

        when(userDetails.isAccountNonLocked()).thenReturn(false);
        when(userDetailService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(passwordService.matches("123456", "$2a$10$hashedpassword")).thenReturn(true);

        assertThrows(ServiceException.class, () -> {
            loginService.login(loginBody);
        });
    }

    @Test
    @DisplayName("登出成功")
    void testLogoutSuccess() {
        try (MockedStatic<StpUtil> mockedStpUtil = mockStatic(StpUtil.class)) {
            when(tokenService.getUserId()).thenReturn(1L);
            mockedStpUtil.when(StpUtil::logout).then(invocation -> null);

            assertDoesNotThrow(() -> {
                loginService.logout();
            });
        }
    }
}

package com.ruoyi.nocode.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TokenService 单元测试
 * 
 * @author ruoyi-nocode
 */
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Test
    @DisplayName("获取用户ID - 成功")
    void testGetUserIdSuccess() {
        try (MockedStatic<StpUtil> mockedStpUtil = mockStatic(StpUtil.class)) {
            mockedStpUtil.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

            Long userId = tokenService.getUserId();

            assertEquals(1L, userId);
        }
    }

    @Test
    @DisplayName("获取用户ID - 异常返回null")
    void testGetUserIdException() {
        try (MockedStatic<StpUtil> mockedStpUtil = mockStatic(StpUtil.class)) {
            mockedStpUtil.when(StpUtil::getLoginIdAsLong).thenThrow(new RuntimeException("Not logged in"));

            Long userId = tokenService.getUserId();

            assertNull(userId);
        }
    }

    @Test
    @DisplayName("验证Token有效性 - 有效")
    void testValidateTokenValid() {
        try (MockedStatic<StpUtil> mockedStpUtil = mockStatic(StpUtil.class)) {
            mockedStpUtil.when(() -> StpUtil.getLoginIdByToken("valid-token")).thenReturn(1L);

            boolean valid = tokenService.validateToken("valid-token");

            assertTrue(valid);
        }
    }

    @Test
    @DisplayName("验证Token有效性 - 无效")
    void testValidateTokenInvalid() {
        try (MockedStatic<StpUtil> mockedStpUtil = mockStatic(StpUtil.class)) {
            mockedStpUtil.when(() -> StpUtil.getLoginIdByToken("invalid-token"))
                    .thenThrow(new RuntimeException("Token invalid"));

            boolean valid = tokenService.validateToken("invalid-token");

            assertFalse(valid);
        }
    }

    @Test
    @DisplayName("刷新Token有效期")
    void testRefreshToken() {
        try (MockedStatic<StpUtil> mockedStpUtil = mockStatic(StpUtil.class)) {
            mockedStpUtil.when(StpUtil::getTokenTimeout).thenReturn(7200L);

            // refreshToken() 方法是 void，不应抛出异常
            assertDoesNotThrow(() -> tokenService.refreshToken());
        }
    }

    @Test
    @DisplayName("获取Token过期时间")
    void testGetTokenTimeout() {
        try (MockedStatic<StpUtil> mockedStpUtil = mockStatic(StpUtil.class)) {
            mockedStpUtil.when(StpUtil::getTokenTimeout).thenReturn(7200L);

            long timeout = tokenService.getTokenTimeout();

            assertEquals(7200L, timeout);
        }
    }
}

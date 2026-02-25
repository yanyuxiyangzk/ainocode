package com.ruoyi.nocode.auth.config;

import cn.dev33.satoken.stp.StpInterface;
import com.ruoyi.nocode.auth.service.SysUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限验证实现
 * 
 * 实现权限和角色加载
 * 
 * @author ruoyi-nocode
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SysUserDetailService userDetailService;

    /**
     * 获取权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        var userDetails = userDetailService.loadUserByUserId(userId);
        if (userDetails != null && userDetails.getPermissions() != null) {
            return List.copyOf(userDetails.getPermissions());
        }
        return Collections.emptyList();
    }

    /**
     * 获取角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        var userDetails = userDetailService.loadUserByUserId(userId);
        if (userDetails != null && userDetails.getRoles() != null) {
            return List.copyOf(userDetails.getRoles());
        }
        return Collections.emptyList();
    }
}

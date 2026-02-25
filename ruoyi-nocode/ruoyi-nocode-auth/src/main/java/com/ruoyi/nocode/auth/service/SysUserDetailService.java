package com.ruoyi.nocode.auth.service;

import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 用户详情服务
 * 
 * 加载用户信息和权限
 * 
 * @author ruoyi-nocode
 */
@Service
public class SysUserDetailService {

    /**
     * 根据用户名加载用户详情
     * 
     * TODO: 后续从数据库或远程服务加载
     * 
     * @param username 用户名
     * @return 用户详情
     */
    public SysUserDetails loadUserByUsername(String username) {
        // 模拟数据 - 后续需要从数据库或 System 服务获取
        if ("admin".equals(username)) {
            return SysUserDetails.builder()
                    .userId(1L)
                    .username("admin")
                    .password("$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU.qHPjj/TG") // admin123
                    .nickname("超级管理员")
                    .status("0")
                    .delFlag("0")
                    .roles(Set.of("admin", "user"))
                    .permissions(Set.of("*:*:*"))
                    .build();
        }

        if ("user".equals(username)) {
            return SysUserDetails.builder()
                    .userId(2L)
                    .username("user")
                    .password("$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU.qHPjj/TG") // admin123
                    .nickname("普通用户")
                    .status("0")
                    .delFlag("0")
                    .roles(Set.of("user"))
                    .permissions(Set.of("system:user:list", "system:user:query"))
                    .build();
        }

        return null;
    }

    /**
     * 根据用户ID加载用户详情
     * 
     * @param userId 用户ID
     * @return 用户详情
     */
    public SysUserDetails loadUserByUserId(Long userId) {
        if (userId == 1L) {
            return SysUserDetails.builder()
                    .userId(1L)
                    .username("admin")
                    .nickname("超级管理员")
                    .status("0")
                    .delFlag("0")
                    .roles(Set.of("admin", "user"))
                    .permissions(Set.of("*:*:*"))
                    .build();
        }

        if (userId == 2L) {
            return SysUserDetails.builder()
                    .userId(2L)
                    .username("user")
                    .nickname("普通用户")
                    .status("0")
                    .delFlag("0")
                    .roles(Set.of("user"))
                    .permissions(Set.of("system:user:list", "system:user:query"))
                    .build();
        }

        return null;
    }
}

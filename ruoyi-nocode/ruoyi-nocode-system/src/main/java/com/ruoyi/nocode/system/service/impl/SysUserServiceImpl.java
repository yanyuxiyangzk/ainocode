package com.ruoyi.nocode.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.common.core.constant.UserConstants;
import com.ruoyi.nocode.common.core.domain.BaseEntity;
import com.ruoyi.nocode.system.entity.*;
import com.ruoyi.nocode.system.mapper.*;
import com.ruoyi.nocode.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 用户Service实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysUserPostMapper userPostMapper;

    @Override
    public IPage<SysUser> selectUserPage(Page<SysUser> page, SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(user.getUserName())) {
            wrapper.like(SysUser::getUserName, user.getUserName());
        }
        if (StrUtil.isNotBlank(user.getNickName())) {
            wrapper.like(SysUser::getNickName, user.getNickName());
        }
        if (StrUtil.isNotBlank(user.getStatus())) {
            wrapper.eq(SysUser::getStatus, user.getStatus());
        }
        if (StrUtil.isNotBlank(user.getPhonenumber())) {
            wrapper.like(SysUser::getPhonenumber, user.getPhonenumber());
        }
        if (user.getDeptId() != null) {
            wrapper.eq(SysUser::getDeptId, user.getDeptId());
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public List<SysUser> selectUserList(SysUser user) {
        return userMapper.selectUserList(user);
    }

    @Override
    public SysUser selectUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public SysUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    @Override
    public SysUser selectUserByPhonenumber(String phonenumber) {
        return userMapper.selectUserByPhonenumber(phonenumber);
    }

    @Override
    public SysUser selectUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUser user) {
        int rows = userMapper.insert(user);
        if (user.getRoleIds() != null && user.getRoleIds().length > 0) {
            insertUserRole(user.getUserId(), user.getRoleIds());
        }
        if (user.getPostIds() != null && user.getPostIds().length > 0) {
            insertUserPost(user.getUserId(), user.getPostIds());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUser user) {
        int rows = userMapper.updateById(user);
        if (user.getRoleIds() != null && user.getRoleIds().length > 0) {
            userRoleMapper.deleteUserRoleByUserId(user.getUserId());
            insertUserRole(user.getUserId(), user.getRoleIds());
        }
        if (user.getPostIds() != null && user.getPostIds().length > 0) {
            userPostMapper.deleteUserPostByUserId(user.getUserId());
            insertUserPost(user.getUserId(), user.getPostIds());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserById(Long userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setDelFlag("1");
        return userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(Long[] userIds) {
        Arrays.asList(userIds).forEach(this::deleteUserById);
        return userIds.length;
    }

    @Override
    public int resetUserPassword(Long userId, String password) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(password);
        return userMapper.updateById(user);
    }

    @Override
    public int updateUserStatus(Long userId, String status) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setStatus(status);
        return userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUserRole(Long userId, Long[] roleIds) {
        SysUserRole userRole = new SysUserRole();
        for (Long roleId : roleIds) {
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
        return roleIds.length;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUserPost(Long userId, Long[] postIds) {
        if (postIds == null || postIds.length == 0) {
            return 0;
        }
        SysUserPost userPost = new SysUserPost();
        for (Long postId : postIds) {
            userPost.setUserId(userId);
            userPost.setPostId(postId);
            userPostMapper.insert(userPost);
        }
        return postIds.length;
    }

    @Override
    public boolean checkUserNameUnique(String userName) {
        SysUser user = userMapper.selectUserByUserName(userName);
        return user == null;
    }

    @Override
    public boolean checkPhoneUnique(SysUser user) {
        SysUser dbUser = userMapper.selectUserByPhonenumber(user.getPhonenumber());
        if (dbUser == null) {
            return true;
        }
        return dbUser.getUserId().equals(user.getUserId());
    }

    @Override
    public boolean checkEmailUnique(SysUser user) {
        SysUser dbUser = userMapper.selectUserByEmail(user.getEmail());
        if (dbUser == null) {
            return true;
        }
        return dbUser.getUserId().equals(user.getUserId());
    }
}

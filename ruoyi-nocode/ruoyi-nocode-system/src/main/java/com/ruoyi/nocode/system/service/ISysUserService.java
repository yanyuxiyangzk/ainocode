package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.SysUser;

import java.util.List;

/**
 * 用户Service接口
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 分页查询用户列表
     */
    IPage<SysUser> selectUserPage(Page<SysUser> page, SysUser user);

    /**
     * 查询用户列表
     */
    List<SysUser> selectUserList(SysUser user);

    /**
     * 根据用户ID查询用户
     */
    SysUser selectUserById(Long userId);

    /**
     * 根据用户名查询用户
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 根据手机号码查询用户
     */
    SysUser selectUserByPhonenumber(String phonenumber);

    /**
     * 根据邮箱查询用户
     */
    SysUser selectUserByEmail(String email);

    /**
     * 新增用户
     */
    int insertUser(SysUser user);

    /**
     * 修改用户
     */
    int updateUser(SysUser user);

    /**
     * 删除用户（逻辑删除）
     */
    int deleteUserById(Long userId);

    /**
     * 批量删除用户（逻辑删除）
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 重置用户密码
     */
    int resetUserPassword(Long userId, String password);

    /**
     * 修改用户状态
     */
    int updateUserStatus(Long userId, String status);

    /**
     * 给用户分配角色
     */
    int insertUserRole(Long userId, Long[] roleIds);

    /**
     * 给用户分配岗位
     */
    int insertUserPost(Long userId, Long[] postIds);

    /**
     * 校验用户名称是否唯一
     */
    boolean checkUserNameUnique(String userName);

    /**
     * 校验手机号码是否唯一
     */
    boolean checkPhoneUnique(SysUser user);

    /**
     * 校验邮箱是否唯一
     */
    boolean checkEmailUnique(SysUser user);
}

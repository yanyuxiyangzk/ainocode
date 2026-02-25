package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户ID查询用户
     */
    SysUser selectUserById(@Param("userId") Long userId);

    /**
     * 查询用户所属角色组
     */
    List<String> selectUserRoleGroup(Long userId);

    /**
     * 查询用户所属岗位组
     */
    List<String> selectUserPostGroup(Long userId);

    /**
     * 根据用户名查询用户
     */
    SysUser selectUserByUserName(@Param("userName") String userName);

    /**
     * 根据手机号码查询用户
     */
    SysUser selectUserByPhonenumber(@Param("phonenumber") String phonenumber);

    /**
     * 根据邮箱查询用户
     */
    SysUser selectUserByEmail(@Param("email") String email);

    /**
     * 查询用户列表
     */
    List<SysUser> selectUserList(SysUser user);
}

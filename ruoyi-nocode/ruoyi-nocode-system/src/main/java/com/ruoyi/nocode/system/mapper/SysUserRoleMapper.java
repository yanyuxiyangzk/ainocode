package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户和角色关联Mapper接口
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 删除用户角色关联
     */
    int deleteUserRoleByUserId(@Param("userId") Long userId);

    /**
     * 删除用户角色关联
     */
    int deleteUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 批量新增用户角色信息
     */
    int batchUserRole(@Param("userRoleList") List<SysUserRole> userRoleList);

    /**
     * 查询用户角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
}

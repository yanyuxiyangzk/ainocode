package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户ID查询角色
     */
    List<SysRole> selectRolePermissionByUserId(Long userId);

    /**
     * 查询所有角色
     */
    List<SysRole> selectRoleAll();

    /**
     * 根据角色ID查询角色
     */
    SysRole selectRoleById(@Param("roleId") Long roleId);

    /**
     * 查询角色列表
     */
    List<SysRole> selectRoleList(SysRole role);
}

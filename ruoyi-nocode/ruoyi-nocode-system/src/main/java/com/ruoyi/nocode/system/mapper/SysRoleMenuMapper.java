package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色和菜单关联Mapper接口
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 删除角色菜单关联
     */
    int deleteRoleMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    /**
     * 批量删除角色菜单
     */
    int deleteRoleMenuByRoleIds(@Param("roleIds") Long[] roleIds);

    /**
     * 批量新增角色菜单信息
     */
    int batchRoleMenu(@Param("roleMenuList") List<SysRoleMenu> roleMenuList);

    /**
     * 查询菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
}

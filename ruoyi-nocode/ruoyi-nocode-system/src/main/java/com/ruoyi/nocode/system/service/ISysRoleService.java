package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.SysRole;

import java.util.List;

/**
 * 角色Service接口
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 分页查询角色列表
     */
    IPage<SysRole> selectRolePage(Page<SysRole> page, SysRole role);

    /**
     * 查询角色列表
     */
    List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据用户ID查询角色
     */
    List<SysRole> selectRolesByUserId(Long userId);

    /**
     * 查询所有角色
     */
    List<SysRole> selectRoleAll();

    /**
     * 根据ID查询角色
     */
    SysRole selectRoleById(Long roleId);

    /**
     * 新增角色
     */
    int insertRole(SysRole role);

    /**
     * 修改角色
     */
    int updateRole(SysRole role);

    /**
     * 删除角色（逻辑删除）
     */
    int deleteRoleById(Long roleId);

    /**
     * 批量删除角色（逻辑删除）
     */
    int deleteRoleByIds(Long[] roleIds);

    /**
     * 修改角色状态
     */
    int updateRoleStatus(Long roleId, String status);

    /**
     * 修改数据范围
     */
    int updateDataScope(Long roleId, String dataScope, Long[] deptIds);

    /**
     * 给角色分配菜单
     */
    int insertRoleMenu(Long roleId, Long[] menuIds);

    /**
     * 校验角色名称是否唯一
     */
    boolean checkRoleNameUnique(SysRole role);

    /**
     * 校验角色权限字符串是否唯一
     */
    boolean checkRoleKeyUnique(SysRole role);
}

package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.SysMenu;

import java.util.List;

/**
 * 菜单Service接口
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 查询菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 查询菜单树形结构
     */
    List<SysMenu> selectMenuTreeList(SysMenu menu);

    /**
     * 根据菜单ID查询菜单
     */
    SysMenu selectMenuById(Long menuId);

    /**
     * 查询菜单树结构信息
     */
    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建前端所需要树结构
     */
    List<SysMenu> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * 根据用户ID查询菜单
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 新增菜单
     */
    int insertMenu(SysMenu menu);

    /**
     * 修改菜单
     */
    int updateMenu(SysMenu menu);

    /**
     * 删除菜单
     */
    int deleteMenuById(Long menuId);

    /**
     * 修改菜单状态
     */
    int updateMenuStatus(Long menuId, String status);

    /**
     * 是否存在菜单子节点
     */
    boolean hasChildByMenuId(Long menuId);

    /**
     * 校验菜单名称是否唯一
     */
    boolean checkMenuNameUnique(SysMenu menu);

    /**
     * 查询菜单权限
     */
    List<String> selectMenuPermsByUserId(Long userId);

    /**
     * 查询所有菜单权限
     */
    List<String> selectMenuPerms();

    /**
     * 根据角色ID查询菜单树信息
     */
    List<Long> selectMenuListByRoleId(Long roleId);
}

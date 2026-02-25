package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单Mapper接口
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 查询菜单权限
     */
    List<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据用户ID查询菜单
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 查询所有菜单权限
     */
    List<String> selectMenuPerms();

    /**
     * 根据角色ID查询菜单树信息
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询菜单管理
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 根据ID查询菜单
     */
    SysMenu selectMenuById(@Param("menuId") Long menuId);

    /**
     * 查询子菜单数量
     */
    int selectCountMenuByParentId(@Param("parentId") Long parentId);

    /**
     * 是否存在菜单子节点
     */
    boolean hasChildByMenuId(@Param("menuId") Long menuId);

    /**
     * 校验菜单名称是否唯一
     */
    SysMenu checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);
}

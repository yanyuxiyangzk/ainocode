package com.ruoyi.nocode.system.controller;

import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.system.entity.SysMenu;
import com.ruoyi.nocode.system.service.ISysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单Controller
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final ISysMenuService menuService;

    /**
     * 查询菜单列表
     */
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public AjaxResult list(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu);
        return AjaxResult.success(menus);
    }

    /**
     * 查询菜单树形结构
     */
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/tree")
    public AjaxResult tree(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuTreeList(menu);
        return AjaxResult.success(menus);
    }

    /**
     * 获取菜单详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping("/{menuId}")
    public AjaxResult getInfo(@PathVariable("menuId") Long menuId) {
        return AjaxResult.success(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu);
        return AjaxResult.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysMenu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return AjaxResult.error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        return AjaxResult.success(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysMenu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return AjaxResult.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        if (menu.getMenuId().equals(menu.getParentId())) {
            return AjaxResult.error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        return AjaxResult.success(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @DeleteMapping("/{menuId}")
    public AjaxResult remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return AjaxResult.error("存在子菜单,不允许删除");
        }
        return AjaxResult.success(menuService.deleteMenuById(menuId));
    }

    /**
     * 修改菜单状态
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysMenu menu) {
        return AjaxResult.success(menuService.updateMenuStatus(menu.getMenuId(), menu.getStatus()));
    }
}

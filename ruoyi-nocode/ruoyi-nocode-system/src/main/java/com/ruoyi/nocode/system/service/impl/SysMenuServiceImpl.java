package com.ruoyi.nocode.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.system.entity.SysMenu;
import com.ruoyi.nocode.system.mapper.SysMenuMapper;
import com.ruoyi.nocode.system.service.ISysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单Service实现
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    private final SysMenuMapper menuMapper;

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        return menuMapper.selectMenuList(menu);
    }

    @Override
    public List<SysMenu> selectMenuTreeList(SysMenu menu) {
        List<SysMenu> menuList = selectMenuList(menu);
        return buildMenuTree(menuList);
    }

    @Override
    public SysMenu selectMenuById(Long menuId) {
        return menuMapper.selectMenuById(menuId);
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<>();
        List<Long> tempList = menus.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
        
        for (SysMenu menu : menus) {
            // 如果是顶级节点，遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        
        // 按orderNum排序
        returnList.sort(Comparator.comparing(SysMenu::getOrderNum));
        return returnList;
    }

    @Override
    public List<SysMenu> buildMenuTreeSelect(List<SysMenu> menus) {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees;
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        return menuMapper.selectMenuTreeByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertMenu(SysMenu menu) {
        return menuMapper.insert(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateMenu(SysMenu menu) {
        return menuMapper.updateById(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMenuById(Long menuId) {
        return menuMapper.deleteById(menuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateMenuStatus(Long menuId, String status) {
        SysMenu menu = new SysMenu();
        menu.setMenuId(menuId);
        menu.setStatus(status);
        return menuMapper.updateById(menu);
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        return menuMapper.hasChildByMenuId(menuId);
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        Long menuId = menu.getMenuId() == null ? -1L : menu.getMenuId();
        SysMenu info = menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
        if (info != null && !info.getMenuId().equals(menuId)) {
            return false;
        }
        return true;
    }

    @Override
    public List<String> selectMenuPermsByUserId(Long userId) {
        return menuMapper.selectMenuPermsByUserId(userId);
    }

    @Override
    public List<String> selectMenuPerms() {
        return menuMapper.selectMenuPerms();
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        return menuMapper.selectMenuListByRoleId(roleId);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext()) {
            SysMenu n = it.next();
            if (n.getParentId() != null && n.getParentId().equals(t.getMenuId())) {
                tlist.add(n);
            }
        }
        // 按orderNum排序
        tlist.sort(Comparator.comparing(SysMenu::getOrderNum));
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }
}

package com.ruoyi.nocode.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.system.entity.SysRole;
import com.ruoyi.nocode.system.entity.SysRoleMenu;
import com.ruoyi.nocode.system.mapper.SysRoleMapper;
import com.ruoyi.nocode.system.mapper.SysRoleMenuMapper;
import com.ruoyi.nocode.system.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 角色Service实现
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;

    @Override
    public IPage<SysRole> selectRolePage(Page<SysRole> page, SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(role.getRoleName())) {
            wrapper.like(SysRole::getRoleName, role.getRoleName());
        }
        if (StrUtil.isNotBlank(role.getRoleKey())) {
            wrapper.like(SysRole::getRoleKey, role.getRoleKey());
        }
        if (StrUtil.isNotBlank(role.getStatus())) {
            wrapper.eq(SysRole::getStatus, role.getStatus());
        }
        wrapper.orderByDesc(SysRole::getRoleSort);
        return page(page, wrapper);
    }

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        return roleMapper.selectRoleList(role);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        return roleMapper.selectRolePermissionByUserId(userId);
    }

    @Override
    public List<SysRole> selectRoleAll() {
        return roleMapper.selectRoleAll();
    }

    @Override
    public SysRole selectRoleById(Long roleId) {
        return roleMapper.selectRoleById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRole role) {
        int rows = roleMapper.insert(role);
        if (role.getMenuIds() != null && role.getMenuIds().length > 0) {
            insertRoleMenu(role.getRoleId(), role.getMenuIds());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(SysRole role) {
        int rows = roleMapper.updateById(role);
        if (role.getMenuIds() != null && role.getMenuIds().length > 0) {
            roleMenuMapper.deleteRoleMenu(role.getRoleId(), null);
            insertRoleMenu(role.getRoleId(), role.getMenuIds());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleById(Long roleId) {
        SysRole role = new SysRole();
        role.setRoleId(roleId);
        role.setDelFlag("1");
        return roleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleByIds(Long[] roleIds) {
        Arrays.asList(roleIds).forEach(this::deleteRoleById);
        return roleIds.length;
    }

    @Override
    public int updateRoleStatus(Long roleId, String status) {
        SysRole role = new SysRole();
        role.setRoleId(roleId);
        role.setStatus(status);
        return roleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDataScope(Long roleId, String dataScope, Long[] deptIds) {
        SysRole role = new SysRole();
        role.setRoleId(roleId);
        role.setDataScope(dataScope);
        int rows = roleMapper.updateById(role);
        // TODO: 处理部门数据范围关联
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRoleMenu(Long roleId, Long[] menuIds) {
        SysRoleMenu roleMenu = new SysRoleMenu();
        for (Long menuId : menuIds) {
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
        return menuIds.length;
    }

    @Override
    public boolean checkRoleNameUnique(SysRole role) {
        SysRole dbRole = roleMapper.selectRoleById(role.getRoleId());
        if (dbRole == null) {
            return true;
        }
        return dbRole.getRoleId().equals(role.getRoleId());
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        SysRole dbRole = roleMapper.selectRoleById(role.getRoleId());
        if (dbRole == null) {
            return true;
        }
        return dbRole.getRoleId().equals(role.getRoleId());
    }
}

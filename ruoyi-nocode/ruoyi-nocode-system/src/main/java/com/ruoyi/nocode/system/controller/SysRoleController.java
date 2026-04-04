package com.ruoyi.nocode.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.SysRole;
import com.ruoyi.nocode.system.service.ISysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色Controller
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final ISysRoleService roleService;

    /**
     * 查询角色列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysRole role,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        IPage<SysRole> result = roleService.selectRolePage(page, role);
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setRows(result.getRecords());
        dataTable.setTotal(result.getTotal());
        return dataTable;
    }

    /**
     * 获取角色详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/{roleId}")
    public AjaxResult getInfo(@PathVariable("roleId") Long roleId) {
        return AjaxResult.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysRole role) {
        if (!roleService.checkRoleNameUnique(role)) {
            return AjaxResult.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        if (!roleService.checkRoleKeyUnique(role)) {
            return AjaxResult.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        return AjaxResult.success(roleService.insertRole(role));
    }

    /**
     * 修改角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysRole role) {
        if (!roleService.checkRoleNameUnique(role)) {
            return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        if (!roleService.checkRoleKeyUnique(role)) {
            return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        return AjaxResult.success(roleService.updateRole(role));
    }

    /**
     * 删除角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @DeleteMapping("/{roleIds}")
    public AjaxResult remove(@PathVariable Long[] roleIds) {
        return AjaxResult.success(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 修改角色状态
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysRole role) {
        return AjaxResult.success(roleService.updateRoleStatus(role.getRoleId(), role.getStatus()));
    }

    /**
     * 修改数据范围
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping("/dataScope")
    public AjaxResult dataScope(@RequestBody SysRole role) {
        return AjaxResult.success(roleService.updateDataScope(role.getRoleId(), role.getDataScope(), role.getMenuIds()));
    }

    /**
     * 获取角色选择列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/selectList")
    public AjaxResult selectList() {
        return AjaxResult.success(roleService.selectRoleAll());
    }
}

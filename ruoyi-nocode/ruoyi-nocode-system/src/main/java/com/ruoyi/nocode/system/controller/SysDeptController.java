package com.ruoyi.nocode.system.controller;

import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.system.entity.SysDept;
import com.ruoyi.nocode.system.service.ISysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门Controller
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final ISysDeptService deptService;

    /**
     * 查询部门列表
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public AjaxResult list(SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return AjaxResult.success(depts);
    }

    /**
     * 查询部门树形结构
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/tree")
    public AjaxResult tree(SysDept dept) {
        List<SysDept> depts = deptService.selectDeptTreeList(dept);
        return AjaxResult.success(depts);
    }

    /**
     * 获取部门详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping("/{deptId}")
    public AjaxResult getInfo(@PathVariable("deptId") Long deptId) {
        return AjaxResult.success(deptService.selectDeptById(deptId));
    }

    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect(SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return AjaxResult.success(deptService.buildDeptTreeSelect(depts));
    }

    /**
     * 根据角色ID查询部门树信息
     */
    @GetMapping("/roleDeptTreeselect/{roleId}")
    public AjaxResult roleDeptTreeselect(@PathVariable("roleId") Long roleId) {
        List<SysDept> depts = deptService.selectDeptAll();
        AjaxResult ajax = AjaxResult.success();
        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        ajax.put("depts", deptService.buildDeptTreeSelect(depts));
        return ajax;
    }

    /**
     * 新增部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysDept dept) {
        if (!deptService.checkDeptNameUnique(dept)) {
            return AjaxResult.error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        return AjaxResult.success(deptService.insertDept(dept));
    }

    /**
     * 修改部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysDept dept) {
        if (!deptService.checkDeptNameUnique(dept)) {
            return AjaxResult.error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        if (dept.getDeptId().equals(dept.getParentId())) {
            return AjaxResult.error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能选择自己");
        }
        return AjaxResult.success(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @DeleteMapping("/{deptId}")
    public AjaxResult remove(@PathVariable("deptId") Long deptId) {
        if (deptService.hasChildByDeptId(deptId)) {
            return AjaxResult.error("存在子部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId)) {
            return AjaxResult.error("部门存在用户,不允许删除");
        }
        return AjaxResult.success(deptService.deleteDeptById(deptId));
    }

    /**
     * 修改部门状态
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysDept dept) {
        return AjaxResult.success(deptService.updateDeptStatus(dept.getDeptId(), dept.getStatus()));
    }
}

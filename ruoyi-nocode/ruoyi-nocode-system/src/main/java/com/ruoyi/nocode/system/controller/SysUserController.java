package com.ruoyi.nocode.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.SysUser;
import com.ruoyi.nocode.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户Controller
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final ISysUserService userService;

    /**
     * 查询用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        IPage<SysUser> result = userService.selectUserPage(page, user);
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setRows(result.getRecords());
        dataTable.setTotal(result.getTotal());
        return dataTable;
    }

    /**
     * 获取用户详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId) {
        return AjaxResult.success(userService.selectUserById(userId));
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysUser user) {
        if (!userService.checkUserNameUnique(user.getUserName())) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，账号已存在");
        }
        if (!userService.checkPhoneUnique(user)) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (!userService.checkEmailUnique(user)) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱已存在");
        }
        return AjaxResult.success(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysUser user) {
        if (!userService.checkPhoneUnique(user)) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (!userService.checkEmailUnique(user)) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱已存在");
        }
        return AjaxResult.success(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        return AjaxResult.success(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置用户密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        return AjaxResult.success(userService.resetUserPassword(user.getUserId(), user.getPassword()));
    }

    /**
     * 修改用户状态
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        return AjaxResult.success(userService.updateUserStatus(user.getUserId(), user.getStatus()));
    }

    /**
     * 获取用户选择列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/selectList")
    public AjaxResult selectList() {
        SysUser user = new SysUser();
        user.setStatus("0");
        return AjaxResult.success(userService.selectUserList(user));
    }
}

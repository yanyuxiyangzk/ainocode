package com.ruoyi.nocode.system.controller.monitor;

import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.system.domain.OnlineUser;
import com.ruoyi.nocode.system.service.IOnlineUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线用户监控Controller
 */
@Tag(name = "在线用户监控")
@RestController
@RequestMapping("/monitor/online")
@RequiredArgsConstructor
public class OnlineController {

    private final IOnlineUserService onlineUserService;

    /**
     * 查询在线用户列表
     */
    @Operation(summary = "查询在线用户列表")
    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    public AjaxResult list() {
        List<OnlineUser> list = onlineUserService.list();
        return AjaxResult.success(list);
    }

    /**
     * 获取用户详细信息
     */
    @Operation(summary = "获取用户详细信息")
    @PreAuthorize("@ss.hasPermi('monitor:online:query')")
    @GetMapping("/{tokenId}")
    public AjaxResult getInfo(@PathVariable("tokenId") String tokenId) {
        OnlineUser onlineUser = onlineUserService.getByToken(tokenId);
        if (onlineUser == null) {
            return AjaxResult.error("用户不存在或已下线");
        }
        return AjaxResult.success(onlineUser);
    }

    /**
     * 强制下线指定用户
     */
    @Operation(summary = "强制下线指定用户")
    @PreAuthorize("@ss.hasPermi('monitor:online:kickout')")
    @DeleteMapping("/{tokenId}")
    public AjaxResult kickout(@PathVariable("tokenId") String tokenId) {
        if (onlineUserService.kickout(tokenId)) {
            return AjaxResult.success();
        }
        return AjaxResult.error("操作失败，用户可能已下线");
    }

    /**
     * 批量强制下线
     */
    @Operation(summary = "批量强制下线")
    @PreAuthorize("@ss.hasPermi('monitor:online:kickout')")
    @DeleteMapping("/batch")
    public AjaxResult batchKickout(@RequestParam String[] tokenIds) {
        int count = onlineUserService.batchKickout(tokenIds);
        return AjaxResult.success("共下线 " + count + " 个用户");
    }
}

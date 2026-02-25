package com.ruoyi.nocode.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.nocode.common.core.domain.AjaxResult;
import com.ruoyi.nocode.common.core.domain.TableDataInfo;
import com.ruoyi.nocode.system.entity.SysPost;
import com.ruoyi.nocode.system.service.ISysPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位Controller
 */
@Tag(name = "岗位管理")
@RestController
@RequestMapping("/system/post")
@RequiredArgsConstructor
public class SysPostController {

    private final ISysPostService postService;

    /**
     * 查询岗位列表
     */
    @Operation(summary = "查询岗位列表")
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysPost post,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysPost> page = new Page<>(pageNum, pageSize);
        IPage<SysPost> result = postService.selectPostPage(page, post);
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setRows(result.getRecords());
        dataTable.setTotal(result.getTotal());
        return dataTable;
    }

    /**
     * 获取岗位详细信息
     */
    @Operation(summary = "获取岗位详细信息")
    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping("/{postId}")
    public AjaxResult getInfo(@PathVariable("postId") Long postId) {
        return AjaxResult.success(postService.selectPostById(postId));
    }

    /**
     * 新增岗位
     */
    @Operation(summary = "新增岗位")
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysPost post) {
        if (!postService.checkPostNameUnique(post)) {
            return AjaxResult.error("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        if (!postService.checkPostCodeUnique(post)) {
            return AjaxResult.error("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        return AjaxResult.success(postService.insertPost(post));
    }

    /**
     * 修改岗位
     */
    @Operation(summary = "修改岗位")
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysPost post) {
        if (!postService.checkPostNameUnique(post)) {
            return AjaxResult.error("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        if (!postService.checkPostCodeUnique(post)) {
            return AjaxResult.error("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        return AjaxResult.success(postService.updatePost(post));
    }

    /**
     * 删除岗位
     */
    @Operation(summary = "删除岗位")
    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @DeleteMapping("/{postIds}")
    public AjaxResult remove(@PathVariable Long[] postIds) {
        return AjaxResult.success(postService.deletePostByIds(postIds));
    }

    /**
     * 修改岗位状态
     */
    @Operation(summary = "修改岗位状态")
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysPost post) {
        return AjaxResult.success(postService.updatePostStatus(post.getPostId(), post.getStatus()));
    }

    /**
     * 获取岗位选择列表
     */
    @Operation(summary = "获取岗位选择列表")
    @GetMapping("/selectList")
    public AjaxResult selectList() {
        SysPost post = new SysPost();
        post.setStatus("0");
        List<SysPost> list = postService.selectPostList(post);
        return AjaxResult.success(list);
    }
}

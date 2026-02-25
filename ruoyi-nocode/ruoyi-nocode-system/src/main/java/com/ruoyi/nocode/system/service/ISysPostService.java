package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.SysPost;

import java.util.List;

/**
 * 岗位Service接口
 */
public interface ISysPostService extends IService<SysPost> {

    /**
     * 分页查询岗位列表
     */
    IPage<SysPost> selectPostPage(Page<SysPost> page, SysPost post);

    /**
     * 查询岗位列表
     */
    List<SysPost> selectPostList(SysPost post);

    /**
     * 根据岗位ID查询岗位
     */
    SysPost selectPostById(Long postId);

    /**
     * 新增岗位
     */
    int insertPost(SysPost post);

    /**
     * 修改岗位
     */
    int updatePost(SysPost post);

    /**
     * 删除岗位（逻辑删除）
     */
    int deletePostById(Long postId);

    /**
     * 批量删除岗位（逻辑删除）
     */
    int deletePostByIds(Long[] postIds);

    /**
     * 修改岗位状态
     */
    int updatePostStatus(Long postId, String status);

    /**
     * 校验岗位名称是否唯一
     */
    boolean checkPostNameUnique(SysPost post);

    /**
     * 校验岗位编码是否唯一
     */
    boolean checkPostCodeUnique(SysPost post);
}

package com.ruoyi.nocode.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.system.entity.SysPost;
import com.ruoyi.nocode.system.mapper.SysPostMapper;
import com.ruoyi.nocode.system.service.ISysPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 岗位Service实现
 */
@Service
@RequiredArgsConstructor
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements ISysPostService {

    private final SysPostMapper postMapper;

    @Override
    public IPage<SysPost> selectPostPage(Page<SysPost> page, SysPost post) {
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(post.getPostCode())) {
            wrapper.like(SysPost::getPostCode, post.getPostCode());
        }
        if (StrUtil.isNotBlank(post.getPostName())) {
            wrapper.like(SysPost::getPostName, post.getPostName());
        }
        if (StrUtil.isNotBlank(post.getStatus())) {
            wrapper.eq(SysPost::getStatus, post.getStatus());
        }
        wrapper.orderByAsc(SysPost::getPostSort);
        return page(page, wrapper);
    }

    @Override
    public List<SysPost> selectPostList(SysPost post) {
        return postMapper.selectPostList(post);
    }

    @Override
    public SysPost selectPostById(Long postId) {
        return postMapper.selectPostById(postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPost(SysPost post) {
        return postMapper.insert(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePost(SysPost post) {
        return postMapper.updateById(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePostById(Long postId) {
        SysPost post = new SysPost();
        post.setPostId(postId);
        post.setDelFlag("1");
        return postMapper.updateById(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePostByIds(Long[] postIds) {
        Arrays.asList(postIds).forEach(this::deletePostById);
        return postIds.length;
    }

    @Override
    public int updatePostStatus(Long postId, String status) {
        SysPost post = new SysPost();
        post.setPostId(postId);
        post.setStatus(status);
        return postMapper.updateById(post);
    }

    @Override
    public boolean checkPostNameUnique(SysPost post) {
        SysPost dbPost = postMapper.checkPostNameUnique(post.getPostName());
        if (dbPost == null) {
            return true;
        }
        // 修改时，如果是同一条记录则允许
        return dbPost.getPostId().equals(post.getPostId());
    }

    @Override
    public boolean checkPostCodeUnique(SysPost post) {
        SysPost dbPost = postMapper.checkPostCodeUnique(post.getPostCode());
        if (dbPost == null) {
            return true;
        }
        // 修改时，如果是同一条记录则允许
        return dbPost.getPostId().equals(post.getPostId());
    }
}

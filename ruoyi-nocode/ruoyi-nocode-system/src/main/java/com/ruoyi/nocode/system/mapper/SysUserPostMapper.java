package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysUserPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户和岗位关联Mapper接口
 */
@Mapper
public interface SysUserPostMapper extends BaseMapper<SysUserPost> {

    /**
     * 删除用户岗位关联
     */
    int deleteUserPostByUserId(@Param("userId") Long userId);

    /**
     * 删除用户岗位关联
     */
    int deleteUserPost(@Param("userId") Long userId, @Param("postId") Long postId);

    /**
     * 批量新增用户岗位信息
     */
    int batchUserPost(@Param("userPostList") java.util.List<SysUserPost> userPostList);
}

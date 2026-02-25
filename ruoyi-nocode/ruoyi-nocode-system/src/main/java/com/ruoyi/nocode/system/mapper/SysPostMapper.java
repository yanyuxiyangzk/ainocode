package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岗位Mapper接口
 */
@Mapper
public interface SysPostMapper extends BaseMapper<SysPost> {

    /**
     * 查询岗位数据
     */
    List<SysPost> selectPostList(SysPost post);

    /**
     * 根据ID查询岗位
     */
    SysPost selectPostById(@Param("postId") Long postId);

    /**
     * 校验岗位名称是否唯一
     */
    SysPost checkPostNameUnique(@Param("postName") String postName);

    /**
     * 校验岗位编码是否唯一
     */
    SysPost checkPostCodeUnique(@Param("postCode") String postCode);
}

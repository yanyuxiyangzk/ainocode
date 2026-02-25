package com.ruoyi.nocode.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户和岗位关联实体类
 */
@Data
@TableName("sys_user_post")
public class SysUserPost {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 岗位ID
     */
    private Long postId;
}

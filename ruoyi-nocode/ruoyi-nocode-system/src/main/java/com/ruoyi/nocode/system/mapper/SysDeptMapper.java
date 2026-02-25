package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门Mapper接口
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询部门管理数据
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据ID查询所有子部门
     */
    List<SysDept> selectChildrenDeptById(@Param("deptId") Long deptId);

    /**
     * 根据ID查询部门
     */
    SysDept selectDeptById(@Param("deptId") Long deptId);

    /**
     * 查询部门是否存在用户
     */
    boolean checkDeptExistUser(@Param("deptId") Long deptId);

    /**
     * 校验部门名称是否唯一
     */
    SysDept checkDeptNameUnique(@Param("deptName") String deptName, @Param("parentId") Long parentId);

    /**
     * 修改子元素关系
     */
    int updateDeptChildren(@Param("deptId") Long deptId, @Param("newAncestors") String newAncestors, @Param("oldAncestors") String oldAncestors);

    /**
     * 根据角色ID查询部门树信息
     */
    List<Long> selectDeptListByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询部门
     */
    SysDept selectDeptByUserId(@Param("userId") Long userId);
}

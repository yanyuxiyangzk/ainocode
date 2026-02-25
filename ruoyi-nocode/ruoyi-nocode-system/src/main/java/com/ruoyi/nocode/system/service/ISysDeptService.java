package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.SysDept;

import java.util.List;

/**
 * 部门Service接口
 */
public interface ISysDeptService extends IService<SysDept> {

    /**
     * 查询部门列表
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 查询部门树形结构
     */
    List<SysDept> selectDeptTreeList(SysDept dept);

    /**
     * 根据部门ID查询部门
     */
    SysDept selectDeptById(Long deptId);

    /**
     * 构建前端所需要树结构
     */
    List<SysDept> buildDeptTree(List<SysDept> depts);

    /**
     * 构建前端所需要下拉树结构
     */
    List<SysDept> buildDeptTreeSelect(List<SysDept> depts);

    /**
     * 根据角色ID查询部门树信息
     */
    List<Long> selectDeptListByRoleId(Long roleId);

    /**
     * 新增部门
     */
    int insertDept(SysDept dept);

    /**
     * 修改部门
     */
    int updateDept(SysDept dept);

    /**
     * 删除部门
     */
    int deleteDeptById(Long deptId);

    /**
     * 修改部门状态
     */
    int updateDeptStatus(Long deptId, String status);

    /**
     * 是否存在部门子节点
     */
    boolean hasChildByDeptId(Long deptId);

    /**
     * 查询部门是否存在用户
     */
    boolean checkDeptExistUser(Long deptId);

    /**
     * 校验部门名称是否唯一
     */
    boolean checkDeptNameUnique(SysDept dept);

    /**
     * 根据用户ID查询部门
     */
    SysDept selectDeptByUserId(Long userId);

    /**
     * 查询所有部门
     */
    List<SysDept> selectDeptAll();
}

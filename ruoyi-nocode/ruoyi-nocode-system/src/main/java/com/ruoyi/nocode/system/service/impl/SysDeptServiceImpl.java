package com.ruoyi.nocode.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.system.entity.SysDept;
import com.ruoyi.nocode.system.mapper.SysDeptMapper;
import com.ruoyi.nocode.system.service.ISysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门Service实现
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    private final SysDeptMapper deptMapper;

    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        return deptMapper.selectDeptList(dept);
    }

    @Override
    public List<SysDept> selectDeptTreeList(SysDept dept) {
        List<SysDept> deptList = selectDeptList(dept);
        return buildDeptTree(deptList);
    }

    @Override
    public SysDept selectDeptById(Long deptId) {
        return deptMapper.selectDeptById(deptId);
    }

    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        
        for (SysDept dept : depts) {
            // 如果是顶级节点，遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        
        // 按orderNum排序
        returnList.sort(Comparator.comparing(SysDept::getOrderNum));
        return returnList;
    }

    @Override
    public List<SysDept> buildDeptTreeSelect(List<SysDept> depts) {
        List<SysDept> deptTrees = buildDeptTree(depts);
        return deptTrees;
    }

    @Override
    public List<Long> selectDeptListByRoleId(Long roleId) {
        return deptMapper.selectDeptListByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDept(SysDept dept) {
        SysDept info = deptMapper.selectDeptById(dept.getParentId());
        // 如果父节点不为空，则设置祖级列表
        if (info != null) {
            dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        } else {
            dept.setAncestors("0");
        }
        return deptMapper.insert(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDept(SysDept dept) {
        SysDept newParentDept = deptMapper.selectDeptById(dept.getParentId());
        SysDept oldDept = deptMapper.selectDeptById(dept.getDeptId());
        if (newParentDept != null && oldDept != null) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            // 修改子元素关系
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        return deptMapper.updateById(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDeptById(Long deptId) {
        return deptMapper.deleteById(deptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDeptStatus(Long deptId, String status) {
        SysDept dept = new SysDept();
        dept.setDeptId(deptId);
        dept.setStatus(status);
        return deptMapper.updateById(dept);
    }

    @Override
    public boolean hasChildByDeptId(Long deptId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, deptId);
        return deptMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean checkDeptExistUser(Long deptId) {
        return deptMapper.checkDeptExistUser(deptId);
    }

    @Override
    public boolean checkDeptNameUnique(SysDept dept) {
        Long deptId = dept.getDeptId() == null ? -1L : dept.getDeptId();
        SysDept info = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        if (info != null && !info.getDeptId().equals(deptId)) {
            return false;
        }
        return true;
    }

    @Override
    public SysDept selectDeptByUserId(Long userId) {
        return deptMapper.selectDeptByUserId(userId);
    }

    @Override
    public List<SysDept> selectDeptAll() {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getStatus, "0");
        wrapper.orderByAsc(SysDept::getParentId, SysDept::getOrderNum);
        return deptMapper.selectList(wrapper);
    }

    /**
     * 递归修改子元素关系
     */
    private void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        List<SysDept> children = deptMapper.selectChildrenDeptById(deptId);
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (!children.isEmpty()) {
            deptMapper.updateDeptChildren(deptId, newAncestors, oldAncestors);
        }
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept t) {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tlist = new ArrayList<>();
        Iterator<SysDept> it = list.iterator();
        while (it.hasNext()) {
            SysDept n = it.next();
            if (n.getParentId() != null && n.getParentId().equals(t.getDeptId())) {
                tlist.add(n);
            }
        }
        // 按orderNum排序
        tlist.sort(Comparator.comparing(SysDept::getOrderNum));
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t) {
        return getChildList(list, t).size() > 0;
    }
}

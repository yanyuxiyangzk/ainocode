package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.SysDictType;

import java.util.List;

/**
 * 字典类型Service接口
 */
public interface ISysDictTypeService extends IService<SysDictType> {

    /**
     * 查询字典类型列表
     */
    List<SysDictType> selectDictTypeList(SysDictType dictType);

    /**
     * 根据ID查询字典类型
     */
    SysDictType selectDictTypeById(Long dictId);

    /**
     * 根据字典类型查询字典
     */
    SysDictType selectDictTypeByType(String dictType);

    /**
     * 新增字典类型
     */
    boolean insertDictType(SysDictType dictType);

    /**
     * 修改字典类型
     */
    boolean updateDictType(SysDictType dictType);

    /**
     * 删除字典类型
     */
    boolean deleteDictTypeByIds(Long[] dictIds);

    /**
     * 校验字典类型是否唯一
     */
    boolean checkDictTypeUnique(String dictType);
}

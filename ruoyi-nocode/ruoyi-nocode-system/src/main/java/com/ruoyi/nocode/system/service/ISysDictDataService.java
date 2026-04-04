package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.SysDictData;

import java.util.List;

/**
 * 字典数据Service接口
 */
public interface ISysDictDataService extends IService<SysDictData> {

    /**
     * 查询字典数据列表
     */
    List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 根据ID查询字典数据
     */
    SysDictData selectDictDataById(Long dictCode);

    /**
     * 根据字典类型查询字典数据
     */
    List<SysDictData> selectDictDataByType(String dictType);

    /**
     * 新增字典数据
     */
    boolean insertDictData(SysDictData dictData);

    /**
     * 修改字典数据
     */
    boolean updateDictData(SysDictData dictData);

    /**
     * 删除字典数据
     */
    boolean deleteDictDataByIds(Long[] dictCodes);

    /**
     * 校验字典数据键值是否唯一
     */
    boolean checkDictDataUnique(String dictType, String dictValue);
}

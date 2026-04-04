package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysDictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典数据Mapper接口
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 查询字典数据列表
     */
    List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 根据ID查询字典数据
     */
    SysDictData selectDictDataById(@Param("dictCode") Long dictCode);

    /**
     * 根据字典类型查询字典数据
     */
    List<SysDictData> selectDictDataByType(@Param("dictType") String dictType);

    /**
     * 校验字典数据键值是否唯一
     */
    SysDictData checkDictDataUnique(@Param("dictType") String dictType, @Param("dictValue") String dictValue);
}

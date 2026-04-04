package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.SysDictType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典类型Mapper接口
 */
@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    /**
     * 查询字典类型列表
     */
    List<SysDictType> selectDictTypeList(SysDictType dictType);

    /**
     * 根据ID查询字典类型
     */
    SysDictType selectDictTypeById(@Param("dictId") Long dictId);

    /**
     * 根据字典类型查询字典
     */
    SysDictType selectDictTypeByType(@Param("dictType") String dictType);

    /**
     * 校验字典类型是否唯一
     */
    SysDictType checkDictTypeUnique(@Param("dictType") String dictType);
}

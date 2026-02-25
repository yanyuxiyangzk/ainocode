package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.GenTableColumn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 代码生成列Mapper
 * 
 * @author ruoyi-nocode
 */
@Mapper
public interface GenTableColumnMapper extends BaseMapper<GenTableColumn> {

    /**
     * 根据表ID查询列信息
     *
     * @param tableId 表ID
     * @return 列信息列表
     */
    List<GenTableColumn> selectGenTableColumnByTableId(@Param("tableId") Long tableId);

    /**
     * 批量插入列信息
     *
     * @param columns 列信息列表
     * @return 结果
     */
    int batchInsertGenTableColumn(@Param("columns") List<GenTableColumn> columns);

    /**
     * 删除业务表字段
     *
     * @param tableId 表ID
     * @return 结果
     */
    int deleteGenTableColumnByTableId(@Param("tableId") Long tableId);
}

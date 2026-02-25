package com.ruoyi.nocode.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.nocode.system.entity.GenTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 代码生成表Mapper
 * 
 * @author ruoyi-nocode
 */
@Mapper
public interface GenTableMapper extends BaseMapper<GenTable> {

    /**
     * 查询业务表列表
     *
     * @param genTable 查询条件
     * @return 业务表列表
     */
    List<GenTable> selectGenTableList(GenTable genTable);

    /**
     * 查询数据库表列表
     *
     * @param tableName 表名称
     * @return 数据库表列表
     */
    List<Map<String, Object>> selectDbTableList(@Param("tableName") String tableName);

    /**
     * 根据表名查询表信息
     *
     * @param tableName 表名称
     * @return 表信息
     */
    Map<String, Object> selectDbTableByName(@Param("tableName") String tableName);

    /**
     * 查询表字段信息
     *
     * @param tableName 表名称
     * @return 字段信息列表
     */
    List<Map<String, Object>> selectTableColumns(@Param("tableName") String tableName);

    /**
     * 根据表ID查询列信息
     *
     * @param tableId 表ID
     * @return 列信息列表
     */
    List<Map<String, Object>> selectGenTableColumnByTableId(@Param("tableId") Long tableId);

    /**
     * 删除业务表
     *
     * @param tableId 表ID
     * @return 结果
     */
    int deleteGenTableById(@Param("tableId") Long tableId);

    /**
     * 删除业务表字段
     *
     * @param tableId 表ID
     * @return 结果
     */
    int deleteGenTableColumnByTableId(@Param("tableId") Long tableId);
}

package com.ruoyi.nocode.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.nocode.system.entity.GenTable;
import com.ruoyi.nocode.system.entity.GenTableColumn;

import java.util.List;
import java.util.Map;

/**
 * 代码生成服务接口
 * 
 * @author ruoyi-nocode
 */
public interface IGenCodeService extends IService<GenTable> {

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
    List<Map<String, Object>> selectDbTableList(String tableName);

    /**
     * 根据表名查询表信息
     *
     * @param tableName 表名称
     * @return 表信息
     */
    Map<String, Object> selectDbTableByName(String tableName);

    /**
     * 导入表结构
     *
     * @param tableNames 表名称列表
     */
    void importGenTable(List<String> tableNames);

    /**
     * 查询表信息（含列信息）
     *
     * @param tableId 表ID
     * @return 表信息
     */
    GenTable selectGenTableById(Long tableId);

    /**
     * 更新表信息
     *
     * @param genTable 表信息
     * @return 结果
     */
    int updateGenTable(GenTable genTable);

    /**
     * 删除表信息
     *
     * @param tableIds 表ID列表
     * @return 结果
     */
    int deleteGenTableByIds(Long[] tableIds);

    /**
     * 预览代码
     *
     * @param tableId 表ID
     * @return 代码预览结果
     */
    Map<String, String> previewCode(Long tableId);

    /**
     * 生成代码（下载）
     *
     * @param tableId 表ID
     * @return 代码字节数组
     */
    byte[] downloadCode(Long tableId);

    /**
     * 生成代码（自定义路径）
     *
     * @param tableId 表ID
     */
    void generatorCode(Long tableId);

    /**
     * 批量生成代码
     *
     * @param tableIds 表ID列表
     * @return 代码字节数组
     */
    byte[] downloadCodeBatch(Long[] tableIds);

    /**
     * 同步表结构
     *
     * @param tableId 表ID
     */
    void synchDbTable(Long tableId);

    /**
     * 获取表字段信息
     *
     * @param tableName 表名称
     * @return 字段信息列表
     */
    List<Map<String, Object>> selectTableColumns(String tableName);

    /**
     * 设置表字段信息
     *
     * @param tableId 表ID
     * @param columns 字段信息
     */
    void setTableColumns(Long tableId, List<GenTableColumn> columns);
}

package com.nocode.core.executor;

import com.nocode.core.entity.ApiQueryParam;
import com.nocode.core.entity.ColumnInfo;
import com.nocode.core.entity.DatabaseType;
import com.nocode.core.entity.TableInfo;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * SQL查询构建器
 */
public class QueryBuilder {
    private static final Pattern SAFE_SQL_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    private final TableInfo tableInfo;
    private final DatabaseType databaseType;
    private String tableName;
    private String alias = "t";
    private List<Object> params = new ArrayList<>();
    private StringBuilder whereClause = new StringBuilder();
    private StringBuilder orderClause = new StringBuilder();

    public QueryBuilder(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
        this.tableName = tableInfo.getTableName();
        this.databaseType = tableInfo.getDatasourceConfig() != null
                ? tableInfo.getDatasourceConfig().getDatabaseType()
                : DatabaseType.MYSQL;
    }

    /**
     * 设置表名（带前缀）
     */
    public QueryBuilder table(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * 设置别名
     */
    public QueryBuilder alias(String alias) {
        this.alias = alias;
        return this;
    }

    /**
     * 添加相等条件
     */
    public QueryBuilder eq(String column, Object value) {
        if (value != null) {
            appendWhere(" AND ");
            whereClause.append(alias).append(".").append(escapeColumn(column)).append(" = ?");
            params.add(value);
        }
        return this;
    }

    /**
     * 添加LIKE条件
     */
    public QueryBuilder like(String column, String value) {
        if (StringUtils.hasText(value)) {
            appendWhere(" AND ");
            whereClause.append(alias).append(".").append(escapeColumn(column)).append(" LIKE ?");
            params.add("%" + value + "%");
        }
        return this;
    }

    /**
     * 添加左LIKE条件（%在值前面）
     */
    public QueryBuilder likeLeft(String column, String value) {
        if (StringUtils.hasText(value)) {
            appendWhere(" AND ");
            whereClause.append(alias).append(".").append(escapeColumn(column)).append(" LIKE ?");
            params.add("%" + value);
        }
        return this;
    }

    /**
     * 添加右LIKE条件（%在值后面）
     */
    public QueryBuilder likeRight(String column, String value) {
        if (StringUtils.hasText(value)) {
            appendWhere(" AND ");
            whereClause.append(alias).append(".").append(escapeColumn(column)).append(" LIKE ?");
            params.add(value + "%");
        }
        return this;
    }

    /**
     * 添加IN条件
     */
    public QueryBuilder in(String column, List<?> values) {
        if (values != null && !values.isEmpty()) {
            appendWhere(" AND ");
            String placeholders = String.join(",", java.util.Collections.nCopies(values.size(), "?"));
            whereClause.append(alias).append(".").append(escapeColumn(column)).append(" IN (").append(placeholders).append(")");
            params.addAll(values);
        }
        return this;
    }

    /**
     * 添加大于条件
     */
    public QueryBuilder gt(String column, Object value) {
        if (value != null) {
            appendWhere(" AND ");
            whereClause.append(alias).append(".").append(escapeColumn(column)).append(" > ?");
            params.add(value);
        }
        return this;
    }

    /**
     * 添加大于等于条件
     */
    public QueryBuilder gte(String column, Object value) {
        if (value != null) {
            appendWhere(" AND ");
            whereClause.append(alias).append(".").append(escapeColumn(column)).append(" >= ?");
            params.add(value);
        }
        return this;
    }

    /**
     * 添加小于条件
     */
    public QueryBuilder lt(String column, Object value) {
        if (value != null) {
            appendWhere(" AND ");
            whereClause.append(alias).append(".").append(escapeColumn(column)).append(" < ?");
            params.add(value);
        }
        return this;
    }

    /**
     * 添加小于等于条件
     */
    public QueryBuilder lte(String column, Object value) {
        if (value != null) {
            appendWhere(" AND ");
            whereClause.append(alias).append(".").append(escapeColumn(column)).append(" <= ?");
            params.add(value);
        }
        return this;
    }

    /**
     * 添加IS NULL条件
     */
    public QueryBuilder isNull(String column) {
        appendWhere(" AND ");
        whereClause.append(alias).append(".").append(escapeColumn(column)).append(" IS NULL");
        return this;
    }

    /**
     * 添加IS NOT NULL条件
     */
    public QueryBuilder isNotNull(String column) {
        appendWhere(" AND ");
        whereClause.append(alias).append(".").append(escapeColumn(column)).append(" IS NOT NULL");
        return this;
    }

    /**
     * 添加自定义WHERE条件
     */
    public QueryBuilder where(String condition, Object... values) {
        if (StringUtils.hasText(condition)) {
            appendWhere(" AND ");
            whereClause.append(" (").append(condition).append(")");
            for (Object value : values) {
                params.add(value);
            }
        }
        return this;
    }

    /**
     * 添加排序
     */
    public QueryBuilder orderBy(String column, String direction) {
        if (StringUtils.hasText(column) && StringUtils.hasText(direction)) {
            if (orderClause.length() > 0) {
                orderClause.append(", ");
            }
            orderClause.append(alias).append(".").append(escapeColumn(column)).append(" ").append(direction.toLowerCase());
        }
        return this;
    }

    /**
     * 构建查询COUNT SQL
     */
    public String buildCountSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM ").append(escapeTable(tableName)).append(" ").append(alias);
        if (whereClause.length() > 0) {
            sql.append(" WHERE 1=1").append(whereClause);
        }
        return sql.toString();
    }

    /**
     * 构建查询列表SQL
     */
    public String buildQuerySql(ApiQueryParam queryParam) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");

        // 字段选择
        if (queryParam.getFields() != null && queryParam.getFields().length > 0) {
            StringBuilder fields = new StringBuilder();
            for (String field : queryParam.getFields()) {
                if (fields.length() > 0) fields.append(", ");
                fields.append(alias).append(".").append(escapeColumn(field));
            }
            sql.append(fields);
        } else {
            sql.append(alias).append(".*");
        }

        sql.append(" FROM ").append(escapeTable(tableName)).append(" ").append(alias);

        // WHERE条件
        if (whereClause.length() > 0) {
            sql.append(" WHERE 1=1").append(whereClause);
        }

        // ORDER BY
        if (orderClause.length() > 0) {
            sql.append(" ORDER BY ").append(orderClause);
        } else if (StringUtils.hasText(queryParam.getOrderBy())) {
            sql.append(" ORDER BY ").append(alias).append(".").append(escapeColumn(queryParam.getOrderBy()));
            if (StringUtils.hasText(queryParam.getOrderDirection())) {
                sql.append(" ").append(queryParam.getOrderDirection().toLowerCase());
            }
        }

        // 分页 - 根据数据库类型使用不同的语法
        sql.append(buildPagination(queryParam.getPage(), queryParam.getSize()));

        return sql.toString();
    }

    /**
     * 构建分页SQL
     */
    private String buildPagination(int page, int size) {
        int offset = (page - 1) * size;

        switch (databaseType) {
            case MYSQL:
            case SQLSERVER:
                // MySQL: LIMIT offset, size
                return " LIMIT " + offset + ", " + size;
            case POSTGRESQL:
                // PostgreSQL: LIMIT size OFFSET offset
                return " LIMIT " + size + " OFFSET " + offset;
            case ORACLE:
                // Oracle: 使用 ROWNUM，需要在外层包装
                // 这里简化处理，实际使用需要包装 SELECT * FROM (SELECT t.*, ROWNUM rn FROM ...) WHERE rn > offset AND rn <= offset + size
                return " LIMIT " + size + " OFFSET " + offset;
            default:
                return " LIMIT " + offset + ", " + size;
        }
    }

    /**
     * 构建查询单条记录SQL
     */
    public String buildQueryOneSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(alias).append(".* FROM ").append(escapeTable(tableName)).append(" ").append(alias);
        if (whereClause.length() > 0) {
            // whereClause 可能以 " 1=1" 开头，需要处理避免重复
            String where = whereClause.toString().trim();
            if (where.startsWith("1=1")) {
                sql.append(" WHERE ").append(where);
            } else {
                sql.append(" WHERE 1=1").append(whereClause);
            }
        }
        // 根据数据库类型使用不同的 LIMIT 语法
        sql.append(buildPagination(1, 1));
        return sql.toString();
    }

    /**
     * 构建插入SQL
     */
    public String buildInsertSql() {
        List<ColumnInfo> columns = tableInfo.getColumns();

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(escapeTable(tableName)).append(" (");

        StringBuilder placeholders = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (int i = 0; i < columns.size(); i++) {
            ColumnInfo col = columns.get(i);
            // 跳过自增列
            if (col.isAutoIncrement()) continue;

            if (i > 0) {
                sql.append(", ");
                placeholders.append(", ");
            }
            sql.append(escapeColumn(col.getName()));
            placeholders.append("?");
        }

        sql.append(") VALUES (").append(placeholders).append(")");

        // PostgreSQL 使用 RETURNING 获取自增ID
        if (databaseType == DatabaseType.POSTGRESQL) {
            sql.append(" RETURNING ").append(escapeColumn(tableInfo.getPrimaryKeyColumn()));
        }

        return sql.toString();
    }

    /**
     * 获取插入SQL的列名列表
     */
    public List<String> getInsertColumns() {
        List<ColumnInfo> columns = tableInfo.getColumns();
        List<String> result = new ArrayList<>();
        for (ColumnInfo col : columns) {
            if (!col.isAutoIncrement()) {
                result.add(col.getName());
            }
        }
        return result;
    }

    /**
     * 构建更新SQL
     */
    public String buildUpdateSql(Object id) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(escapeTable(tableName)).append(" SET ");

        String pkColumn = tableInfo.getPrimaryKeyColumn();

        boolean first = true;
        for (ColumnInfo col : tableInfo.getColumns()) {
            // 跳过主键和自增列
            if (col.getName().equals(pkColumn) || col.isAutoIncrement()) continue;

            if (!first) sql.append(", ");
            first = false;
            sql.append(escapeColumn(col.getName())).append(" = ?");
        }

        sql.append(" WHERE ").append(escapeColumn(pkColumn)).append(" = ?");
        return sql.toString();
    }

    /**
     * 构建删除SQL
     */
    public String buildDeleteSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(escapeTable(tableName));
        if (whereClause.length() > 0) {
            sql.append(" WHERE 1=1").append(whereClause);
        }
        return sql.toString();
    }

    /**
     * 构建按主键删除SQL
     */
    public String buildDeleteByIdSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(escapeTable(tableName));
        sql.append(" WHERE ").append(escapeColumn(tableInfo.getPrimaryKeyColumn())).append(" = ?");
        return sql.toString();
    }

    /**
     * 获取参数列表
     */
    public List<Object> getParams() {
        return new ArrayList<>(params);
    }

    /**
     * 获取 WHERE 子句
     */
    public StringBuilder getWhereClause() {
        return whereClause;
    }

    /**
     * 获取别名
     */
    public String getAlias() {
        return alias;
    }

    /**
     * 重置
     */
    public void reset() {
        params.clear();
        whereClause.setLength(0);
        orderClause.setLength(0);
    }

    private void appendWhere(String operator) {
        if (whereClause.length() == 0) {
            whereClause.append(" WHERE 1=1");
        }
        whereClause.append(operator);
    }

    /**
     * 转义表名
     */
    public String escapeTable(String table) {
        String quote = databaseType.getIdentifierQuote();
        return quote + table.replace(quote, "") + quote;
    }

    /**
     * 转义列名
     */
    public String escapeColumn(String column) {
        if (!SAFE_SQL_PATTERN.matcher(column).matches()) {
            throw new IllegalArgumentException("非法的列名: " + column);
        }
        String quote = databaseType.getIdentifierQuote();
        return quote + column + quote;
    }
}

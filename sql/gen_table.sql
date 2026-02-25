-- =============================================================
-- 代码生成表设计
-- 数据库: PostgreSQL
-- 版本: 1.0.0
-- 创建时间: 2026-02-15
-- =============================================================

-- 代码生成业务表
DROP TABLE IF EXISTS gen_table;
CREATE TABLE gen_table (
    table_id          BIGSERIAL PRIMARY KEY,                    -- 表ID
    table_name        VARCHAR(200) NOT NULL,                    -- 表名称
    table_comment     VARCHAR(500),                             -- 表描述
    class_name        VARCHAR(100) NOT NULL,                    -- 实体类名称
    package_name      VARCHAR(100),                             -- 生成包路径
    module_name       VARCHAR(30),                              -- 生成模块名
    business_name     VARCHAR(30),                              -- 生成业务名
    function_name     VARCHAR(50),                              -- 生成功能名
    function_author   VARCHAR(50),                              -- 生成功能作者
    gen_type          CHAR(1) DEFAULT '0',                      -- 生成代码方式（0zip压缩包 1自定义路径）
    gen_path          VARCHAR(200) DEFAULT '/',                 -- 生成路径（不填默认项目路径）
    pk_column         VARCHAR(50),                              -- 主键列
    tree_code         VARCHAR(50),                              -- 树编码字段
    tree_parent_code  VARCHAR(50),                              -- 树父编码字段
    tree_name         VARCHAR(50),                              -- 树名称字段
    parent_menu_id    BIGINT,                                   -- 上级菜单ID
    db_type           VARCHAR(50) DEFAULT 'PostgreSQL',         -- 数据库类型
    create_by         VARCHAR(64) DEFAULT '',                   -- 创建者
    create_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      -- 创建时间
    update_by         VARCHAR(64) DEFAULT '',                   -- 更新者
    update_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      -- 更新时间
    remark            VARCHAR(500),                             -- 备注
    del_flag          VARCHAR(1) DEFAULT '0'                    -- 删除标志（0存在 2删除）
);

-- 表注释
COMMENT ON TABLE gen_table IS '代码生成业务表';
COMMENT ON COLUMN gen_table.table_id IS '表ID';
COMMENT ON COLUMN gen_table.table_name IS '表名称';
COMMENT ON COLUMN gen_table.table_comment IS '表描述';
COMMENT ON COLUMN gen_table.class_name IS '实体类名称';
COMMENT ON COLUMN gen_table.package_name IS '生成包路径';
COMMENT ON COLUMN gen_table.module_name IS '生成模块名';
COMMENT ON COLUMN gen_table.business_name IS '生成业务名';
COMMENT ON COLUMN gen_table.function_name IS '生成功能名';
COMMENT ON COLUMN gen_table.function_author IS '生成功能作者';
COMMENT ON COLUMN gen_table.gen_type IS '生成代码方式（0zip压缩包 1自定义路径）';
COMMENT ON COLUMN gen_table.gen_path IS '生成路径（不填默认项目路径）';
COMMENT ON COLUMN gen_table.pk_column IS '主键列';
COMMENT ON COLUMN gen_table.tree_code IS '树编码字段';
COMMENT ON COLUMN gen_table.tree_parent_code IS '树父编码字段';
COMMENT ON COLUMN gen_table.tree_name IS '树名称字段';
COMMENT ON COLUMN gen_table.parent_menu_id IS '上级菜单ID';

-- 代码生成业务表字段
DROP TABLE IF EXISTS gen_table_column;
CREATE TABLE gen_table_column (
    column_id       BIGSERIAL PRIMARY KEY,                      -- 列ID
    table_id        BIGINT,                                     -- 归属表ID
    column_name     VARCHAR(200),                               -- 列名称
    column_comment  VARCHAR(500),                               -- 列描述
    column_type     VARCHAR(100),                               -- 列类型
    java_type       VARCHAR(500),                               -- JAVA类型
    java_field      VARCHAR(200),                               -- JAVA字段名
    is_pk           CHAR(1) DEFAULT '0',                        -- 是否主键（1是）
    is_increment    CHAR(1) DEFAULT '0',                        -- 是否自增（1是）
    is_required     CHAR(1) DEFAULT '0',                        -- 是否必填（1是）
    is_insert       CHAR(1) DEFAULT '0',                        -- 是否为插入字段（1是）
    is_edit         CHAR(1) DEFAULT '0',                        -- 是否编辑字段（1是）
    is_list         CHAR(1) DEFAULT '0',                        -- 是否列表字段（1是）
    is_query        CHAR(1) DEFAULT '0',                        -- 是否查询字段（1是）
    query_type      VARCHAR(200) DEFAULT 'EQ',                  -- 查询方式（等于、不等于、大于、小于、范围）
    html_type       VARCHAR(200) DEFAULT 'input',               -- 显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）
    dict_type       VARCHAR(200) DEFAULT '',                    -- 字典类型
    sort            INTEGER,                                    -- 排序
    create_by       VARCHAR(64) DEFAULT '',                     -- 创建者
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,        -- 创建时间
    update_by       VARCHAR(64) DEFAULT '',                     -- 更新者
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,        -- 更新时间
    
    CONSTRAINT fk_gen_table_column_table FOREIGN KEY (table_id) REFERENCES gen_table(table_id) ON DELETE CASCADE
);

-- 表注释
COMMENT ON TABLE gen_table_column IS '代码生成业务表字段';
COMMENT ON COLUMN gen_table_column.column_id IS '列ID';
COMMENT ON COLUMN gen_table_column.table_id IS '归属表ID';
COMMENT ON COLUMN gen_table_column.column_name IS '列名称';
COMMENT ON COLUMN gen_table_column.column_comment IS '列描述';
COMMENT ON COLUMN gen_table_column.column_type IS '列类型';
COMMENT ON COLUMN gen_table_column.java_type IS 'JAVA类型';
COMMENT ON COLUMN gen_table_column.java_field IS 'JAVA字段名';
COMMENT ON COLUMN gen_table_column.is_pk IS '是否主键（1是）';
COMMENT ON COLUMN gen_table_column.is_increment IS '是否自增（1是）';
COMMENT ON COLUMN gen_table_column.is_required IS '是否必填（1是）';
COMMENT ON COLUMN gen_table_column.is_insert IS '是否为插入字段（1是）';
COMMENT ON COLUMN gen_table_column.is_edit IS '是否编辑字段（1是）';
COMMENT ON COLUMN gen_table_column.is_list IS '是否列表字段（1是）';
COMMENT ON COLUMN gen_table_column.is_query IS '是否查询字段（1是）';
COMMENT ON COLUMN gen_table_column.query_type IS '查询方式（等于、不等于、大于、小于、范围）';
COMMENT ON COLUMN gen_table_column.html_type IS '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）';
COMMENT ON COLUMN gen_table_column.dict_type IS '字典类型';
COMMENT ON COLUMN gen_table_column.sort IS '排序';

-- 索引
CREATE INDEX idx_gen_table_name ON gen_table(table_name);
CREATE INDEX idx_gen_table_column_table ON gen_table_column(table_id);

-- 菜单数据
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, del_flag)
VALUES ('代码生成', 1, 14, 'genCode', 'system/genCode/index', NULL, TRUE, FALSE, 'C', TRUE, '0', 'system:genCode:list', 'code', 'admin', CURRENT_TIMESTAMP, '代码生成菜单', '0');

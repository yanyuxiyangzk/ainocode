-- V3__plugin_tables.sql
-- Flyway Migration: 插件系统表

-- 代码生成表
CREATE TABLE gen_table (
    table_id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(200) DEFAULT '',
    table_comment VARCHAR(500) DEFAULT '',
    sub_table_name VARCHAR(64) DEFAULT '',
    sub_table_fk_name VARCHAR(64) DEFAULT '',
    class_name VARCHAR(100) DEFAULT '',
    tpl_category VARCHAR(200) DEFAULT 'crud',
    package_name VARCHAR(100) DEFAULT '',
    module_name VARCHAR(30) DEFAULT '',
    business_name VARCHAR(30) DEFAULT '',
    function_name VARCHAR(50) DEFAULT '',
    function_author VARCHAR(50) DEFAULT '',
    gen_type VARCHAR(1) DEFAULT '0',
    gen_path VARCHAR(200) DEFAULT '/',
    options VARCHAR(1000) DEFAULT '',
    create_by VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark VARCHAR(500) DEFAULT ''
);

-- 代码生成明细表
CREATE TABLE gen_table_column (
    column_id BIGSERIAL PRIMARY KEY,
    table_id BIGINT,
    column_name VARCHAR(200) DEFAULT '',
    column_comment VARCHAR(500) DEFAULT '',
    column_type VARCHAR(100) DEFAULT '',
    java_type VARCHAR(500) DEFAULT '',
    java_field VARCHAR(200) DEFAULT '',
    is_pk CHAR(1),
    is_increment CHAR(1),
    is_required CHAR(1),
    is_insert CHAR(1),
    is_edit CHAR(1),
    is_list CHAR(1),
    is_query CHAR(1),
    query_type VARCHAR(200) DEFAULT 'EQ',
    html_type VARCHAR(200) DEFAULT '',
    dict_type VARCHAR(200) DEFAULT '',
    sort INT DEFAULT 0,
    create_by VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插件表
CREATE TABLE sys_plugin (
    plugin_id BIGSERIAL PRIMARY KEY,
    plugin_name VARCHAR(100) NOT NULL,
    plugin_key VARCHAR(100) NOT NULL UNIQUE,
    plugin_version VARCHAR(50) DEFAULT '',
    plugin_type VARCHAR(50) DEFAULT '',
    plugin_status CHAR(1) DEFAULT '0',
    plugin_path VARCHAR(500) DEFAULT '',
    plugin_config TEXT,
    del_flag CHAR(1) DEFAULT '0',
    create_by VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark VARCHAR(500) DEFAULT ''
);

-- 索引
CREATE INDEX idx_gen_table_name ON gen_table(table_name);
CREATE INDEX idx_gen_table_column_table ON gen_table_column(table_id);
CREATE INDEX idx_plugin_status ON sys_plugin(plugin_status);
CREATE INDEX idx_plugin_key ON sys_plugin(plugin_key);

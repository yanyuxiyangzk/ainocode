-- V2__system_config_tables.sql
-- Flyway Migration: 系统配置表

-- 操作日志表
CREATE TABLE sys_oper_log (
    oper_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(50) DEFAULT '',
    business_type VARCHAR(20) DEFAULT '',
    method VARCHAR(100) DEFAULT '',
    request_method VARCHAR(10) DEFAULT '',
    operator_type VARCHAR(20) DEFAULT '',
    oper_name VARCHAR(50) DEFAULT '',
    dept_name VARCHAR(50) DEFAULT '',
    oper_url VARCHAR(255) DEFAULT '',
    oper_ip VARCHAR(128) DEFAULT '',
    oper_location VARCHAR(255) DEFAULT '',
    oper_param VARCHAR(2000) DEFAULT '',
    json_result VARCHAR(2000) DEFAULT '',
    status INT DEFAULT 0,
    error_msg VARCHAR(2000) DEFAULT '',
    oper_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 字典类型表
CREATE TABLE sys_dict_type (
    dict_id BIGSERIAL PRIMARY KEY,
    dict_name VARCHAR(100) DEFAULT '',
    dict_type VARCHAR(100) DEFAULT '' UNIQUE,
    status CHAR(1) DEFAULT '0',
    del_flag CHAR(1) DEFAULT '0',
    create_by VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark VARCHAR(500) DEFAULT ''
);

-- 字典数据表
CREATE TABLE sys_dict_data (
    dict_code BIGSERIAL PRIMARY KEY,
    dict_sort INT DEFAULT 0,
    dict_label VARCHAR(100) DEFAULT '',
    dict_value VARCHAR(100) DEFAULT '',
    dict_type VARCHAR(100) DEFAULT '',
    css_class VARCHAR(100) DEFAULT '',
    list_class VARCHAR(100) DEFAULT '',
    is_default CHAR(1) DEFAULT 'N',
    status CHAR(1) DEFAULT '0',
    del_flag CHAR(1) DEFAULT '0',
    create_by VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark VARCHAR(500) DEFAULT ''
);

-- 参数配置表
CREATE TABLE sys_config (
    config_id BIGSERIAL PRIMARY KEY,
    config_name VARCHAR(100) DEFAULT '',
    config_key VARCHAR(100) DEFAULT '',
    config_value VARCHAR(500) DEFAULT '',
    config_type CHAR(1) DEFAULT 'N',
    del_flag CHAR(1) DEFAULT '0',
    create_by VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark VARCHAR(500) DEFAULT ''
);

-- 通知公告表
CREATE TABLE sys_notice (
    notice_id BIGSERIAL PRIMARY KEY,
    notice_title VARCHAR(50) NOT NULL,
    notice_type VARCHAR(20) NOT NULL,
    notice_content TEXT,
    status CHAR(1) DEFAULT '0',
    del_flag CHAR(1) DEFAULT '0',
    create_by VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark VARCHAR(255) DEFAULT ''
);

-- 索引
CREATE INDEX idx_oper_log_status ON sys_oper_log(status);
CREATE INDEX idx_oper_log_time ON sys_oper_log(oper_time);
CREATE INDEX idx_dict_type_status ON sys_dict_type(status);
CREATE INDEX idx_dict_data_type ON sys_dict_data(dict_type);
CREATE INDEX idx_config_type ON sys_config(config_type);
CREATE INDEX idx_notice_status ON sys_notice(status);

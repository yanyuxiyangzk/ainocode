-- V1__initial_schema.sql
-- Flyway Migration: 初始表结构

-- 创建扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 部门表
CREATE TABLE sys_dept (
    dept_id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    ancestors VARCHAR(500) DEFAULT '',
    dept_name VARCHAR(30) NOT NULL,
    order_num INT DEFAULT 0,
    leader VARCHAR(20),
    phone VARCHAR(11),
    email VARCHAR(50),
    status CHAR(1) DEFAULT '0',
    del_flag CHAR(1) DEFAULT '0',
    create_by VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_dept_name_parent UNIQUE (dept_name, parent_id)
);

-- 用户表
CREATE TABLE sys_user (
    user_id BIGSERIAL PRIMARY KEY,
    dept_id BIGINT,
    user_name VARCHAR(30) NOT NULL UNIQUE,
    nick_name VARCHAR(30) NOT NULL,
    user_type VARCHAR(2) DEFAULT '00',
    email VARCHAR(50) DEFAULT '',
    phone VARCHAR(11) DEFAULT '',
    sex CHAR(1) DEFAULT '0',
    avatar VARCHAR(100) DEFAULT '',
    password VARCHAR(100) DEFAULT '',
    status CHAR(1) DEFAULT '0',
    del_flag CHAR(1) DEFAULT '0',
    login_ip VARCHAR(128) DEFAULT '',
    login_date TIMESTAMP,
    create_by VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_dept FOREIGN KEY (dept_id) REFERENCES sys_dept(dept_id) ON DELETE SET NULL
);

-- 角色表
CREATE TABLE sys_role (
    role_id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(30) NOT NULL UNIQUE,
    role_key VARCHAR(100) NOT NULL UNIQUE,
    role_sort INT NOT NULL,
    data_scope CHAR(1) DEFAULT '1',
    menu_check_strictly INT DEFAULT 1,
    dept_check_strictly INT DEFAULT 1,
    status CHAR(1) NOT NULL,
    del_flag CHAR(1) DEFAULT '0',
    create_by VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 菜单权限表
CREATE TABLE sys_menu (
    menu_id BIGSERIAL PRIMARY KEY,
    menu_name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    order_num INT DEFAULT 0,
    path VARCHAR(200) DEFAULT '',
    component VARCHAR(255),
    query VARCHAR(255),
    is_frame INT DEFAULT 1,
    is_cache INT DEFAULT 0,
    menu_type CHAR(1) DEFAULT '',
    visible CHAR(1) DEFAULT '0',
    status CHAR(1) DEFAULT '0',
    perms VARCHAR(100),
    icon VARCHAR(100) DEFAULT '#',
    create_by VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark VARCHAR(500) DEFAULT ''
);

-- 用户和角色关联表
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

-- 角色和菜单关联表
CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
);

-- 角色部门关联表
CREATE TABLE sys_role_dept (
    role_id BIGINT NOT NULL,
    dept_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, dept_id)
);

-- 索引
CREATE INDEX idx_user_dept ON sys_user(dept_id);
CREATE INDEX idx_user_status ON sys_user(status);
CREATE INDEX idx_user_del ON sys_user(del_flag);
CREATE INDEX idx_role_status ON sys_role(status);
CREATE INDEX idx_role_del ON sys_role(del_flag);
CREATE INDEX idx_menu_status ON sys_menu(status);
CREATE INDEX idx_menu_del ON sys_menu(del_flag);
CREATE INDEX idx_menu_parent ON sys_menu(parent_id);
CREATE INDEX idx_dept_status ON sys_dept(status);
CREATE INDEX idx_dept_del ON sys_dept(del_flag);
CREATE INDEX idx_dept_parent ON sys_dept(parent_id);

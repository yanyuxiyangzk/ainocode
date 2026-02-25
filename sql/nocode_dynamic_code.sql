-- =============================================================
-- Liquor 动态代码管理表设计
-- 数据库: PostgreSQL
-- 版本: 1.0.0
-- 创建时间: 2026-02-14
-- =============================================================

-- 动态代码表
DROP TABLE IF EXISTS nocode_dynamic_code;
CREATE TABLE nocode_dynamic_code (
    code_id            BIGSERIAL PRIMARY KEY,                    -- 动态代码ID
    code_code          VARCHAR(100) NOT NULL,                    -- 代码编码（唯一标识）
    code_name          VARCHAR(200) NOT NULL,                    -- 代码名称
    plugin_id          BIGINT,                                   -- 关联插件ID（可选）
    class_name         VARCHAR(500) NOT NULL,                    -- 类全限定名
    package_name       VARCHAR(200),                             -- 包名
    source_code        TEXT NOT NULL,                            -- Java源代码
    compiled_bytes     TEXT,                                     -- 编译后的字节码（Base64编码）
    code_type          VARCHAR(20) DEFAULT 'CLASS',              -- 代码类型: CLASS-类, INTERFACE-接口, ENUM-枚举, SCRIPT-脚本
    code_version       INTEGER DEFAULT 0,                        -- 代码版本
    status             VARCHAR(20) DEFAULT 'DRAFT',              -- 状态: DRAFT-草稿, COMPILED-已编译, PUBLISHED-已发布, ERROR-编译错误
    enabled            BOOLEAN DEFAULT FALSE,                    -- 是否启用
    sandbox_mode       BOOLEAN DEFAULT TRUE,                     -- 沙箱执行标志
    security_config    TEXT,                                     -- 安全限制配置（JSON格式）
    dependencies       TEXT,                                     -- 依赖类列表（逗号分隔）
    last_compile_time  TIMESTAMP,                                -- 最后编译时间
    compile_error      TEXT,                                     -- 编译错误信息
    execute_count      BIGINT DEFAULT 0,                         -- 执行次数
    last_execute_time  TIMESTAMP,                                -- 最后执行时间
    last_result        TEXT,                                     -- 上一次执行结果
    create_by          VARCHAR(64),                              -- 创建者
    create_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      -- 创建时间
    update_by          VARCHAR(64),                              -- 更新者
    update_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      -- 更新时间
    remark             VARCHAR(500),                             -- 备注
    del_flag           VARCHAR(1) DEFAULT '0',                   -- 删除标志（0存在 2删除）
    
    CONSTRAINT uk_code_code UNIQUE (code_code),
    CONSTRAINT uk_class_name UNIQUE (class_name),
    CONSTRAINT fk_dynamic_code_plugin FOREIGN KEY (plugin_id) REFERENCES nocode_plugin_info(plugin_id) ON DELETE SET NULL
);

-- 表注释
COMMENT ON TABLE nocode_dynamic_code IS '动态代码表';
COMMENT ON COLUMN nocode_dynamic_code.code_id IS '动态代码ID';
COMMENT ON COLUMN nocode_dynamic_code.code_code IS '代码编码（唯一标识）';
COMMENT ON COLUMN nocode_dynamic_code.code_name IS '代码名称';
COMMENT ON COLUMN nocode_dynamic_code.plugin_id IS '关联插件ID（可选）';
COMMENT ON COLUMN nocode_dynamic_code.class_name IS '类全限定名';
COMMENT ON COLUMN nocode_dynamic_code.package_name IS '包名';
COMMENT ON COLUMN nocode_dynamic_code.source_code IS 'Java源代码';
COMMENT ON COLUMN nocode_dynamic_code.compiled_bytes IS '编译后的字节码（Base64编码）';
COMMENT ON COLUMN nocode_dynamic_code.code_type IS '代码类型: CLASS-类, INTERFACE-接口, ENUM-枚举, SCRIPT-脚本';
COMMENT ON COLUMN nocode_dynamic_code.code_version IS '代码版本';
COMMENT ON COLUMN nocode_dynamic_code.status IS '状态: DRAFT-草稿, COMPILED-已编译, PUBLISHED-已发布, ERROR-编译错误';
COMMENT ON COLUMN nocode_dynamic_code.enabled IS '是否启用';
COMMENT ON COLUMN nocode_dynamic_code.sandbox_mode IS '沙箱执行标志';
COMMENT ON COLUMN nocode_dynamic_code.security_config IS '安全限制配置（JSON格式）';
COMMENT ON COLUMN nocode_dynamic_code.dependencies IS '依赖类列表（逗号分隔）';
COMMENT ON COLUMN nocode_dynamic_code.last_compile_time IS '最后编译时间';
COMMENT ON COLUMN nocode_dynamic_code.compile_error IS '编译错误信息';
COMMENT ON COLUMN nocode_dynamic_code.execute_count IS '执行次数';
COMMENT ON COLUMN nocode_dynamic_code.last_execute_time IS '最后执行时间';
COMMENT ON COLUMN nocode_dynamic_code.last_result IS '上一次执行结果';

-- 索引
CREATE INDEX idx_dynamic_code_status ON nocode_dynamic_code(status);
CREATE INDEX idx_dynamic_code_enabled ON nocode_dynamic_code(enabled);
CREATE INDEX idx_dynamic_code_plugin_id ON nocode_dynamic_code(plugin_id);
CREATE INDEX idx_dynamic_code_create_time ON nocode_dynamic_code(create_time);

-- 菜单数据（动态代码管理）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, del_flag)
VALUES ('动态代码', 1, 13, 'dynamicCode', 'system/dynamicCode/index', NULL, TRUE, FALSE, 'C', TRUE, '0', 'system:dynamicCode:list', 'code', 'admin', CURRENT_TIMESTAMP, '动态代码管理菜单', '0');

-- 示例动态代码（演示用）
-- INSERT INTO nocode_dynamic_code (code_code, code_name, class_name, package_name, source_code, code_type, status, enabled, sandbox_mode)
-- VALUES (
--     'demo-calculator',
--     '演示计算器',
--     'com.ruoyi.nocode.dynamic.Calculator',
--     'com.ruoyi.nocode.dynamic',
--     'package com.ruoyi.nocode.dynamic;

-- public class Calculator {
--     public int add(int a, int b) {
--         return a + b;
--     }
--     
--     public int subtract(int a, int b) {
--         return a - b;
--     }
--     
--     public static int multiply(int a, int b) {
--         return a * b;
--     }
-- }',
--     'CLASS',
--     'DRAFT',
--     FALSE,
--     TRUE
-- );

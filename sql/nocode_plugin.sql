-- =============================================================
-- PF4J 插件管理表设计
-- 数据库: PostgreSQL
-- 版本: 1.0.0
-- 创建时间: 2026-02-14
-- =============================================================

-- 1. 插件信息表
DROP TABLE IF EXISTS nocode_plugin_info;
CREATE TABLE nocode_plugin_info (
    plugin_id          BIGSERIAL PRIMARY KEY,                    -- 插件ID（自增）
    plugin_code        VARCHAR(100) NOT NULL,                    -- 插件编码（唯一标识）
    plugin_name        VARCHAR(200) NOT NULL,                    -- 插件名称
    plugin_version     VARCHAR(50) NOT NULL,                     -- 插件版本
    plugin_desc        VARCHAR(500),                             -- 插件描述
    plugin_class       VARCHAR(500) NOT NULL,                    -- 插件主类全限定名
    plugin_provider    VARCHAR(100),                             -- 插件提供者
    plugin_dependency  TEXT,                                     -- 插件依赖（JSON格式）
    plugin_path        VARCHAR(500),                             -- 插件JAR路径
    plugin_size        BIGINT DEFAULT 0,                         -- 插件文件大小（字节）
    file_hash          VARCHAR(64),                              -- 文件MD5哈希值
    install_type       VARCHAR(20) DEFAULT 'JAR',                -- 安装方式: JAR-文件上传, MAVEN-Maven坐标
    maven_group        VARCHAR(200),                             -- Maven GroupId
    maven_artifact     VARCHAR(200),                             -- Maven ArtifactId
    maven_version      VARCHAR(50),                              -- Maven版本号
    status             VARCHAR(20) DEFAULT 'DISABLED',           -- 状态: DISABLED-已停用, ENABLED-已启用, ERROR-异常
    run_mode           VARCHAR(20) DEFAULT 'STATIC',             -- 运行模式: STATIC-静态加载, DYNAMIC-动态编译
    auto_start         BOOLEAN DEFAULT FALSE,                    -- 是否自动启动
    start_order        INTEGER DEFAULT 0,                        -- 启动顺序
    config_json        TEXT,                                     -- 插件配置（JSON格式）
    last_load_time     TIMESTAMP,                                -- 最后加载时间
    last_unload_time   TIMESTAMP,                                -- 最后卸载时间
    error_msg          TEXT,                                     -- 错误信息
    create_by          VARCHAR(64),                              -- 创建者
    create_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      -- 创建时间
    update_by          VARCHAR(64),                              -- 更新者
    update_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      -- 更新时间
    remark             VARCHAR(500),                             -- 备注
    del_flag           VARCHAR(1) DEFAULT '0',                   -- 删除标志（0存在 2删除）
    
    CONSTRAINT uk_plugin_code UNIQUE (plugin_code),
    CONSTRAINT uk_plugin_path UNIQUE (plugin_path)
);

-- 表注释
COMMENT ON TABLE nocode_plugin_info IS '插件信息表';
COMMENT ON COLUMN nocode_plugin_info.plugin_id IS '插件ID';
COMMENT ON COLUMN nocode_plugin_info.plugin_code IS '插件编码（唯一标识）';
COMMENT ON COLUMN nocode_plugin_info.plugin_name IS '插件名称';
COMMENT ON COLUMN nocode_plugin_info.plugin_version IS '插件版本';
COMMENT ON COLUMN nocode_plugin_info.plugin_desc IS '插件描述';
COMMENT ON COLUMN nocode_plugin_info.plugin_class IS '插件主类全限定名';
COMMENT ON COLUMN nocode_plugin_info.plugin_provider IS '插件提供者';
COMMENT ON COLUMN nocode_plugin_info.plugin_dependency IS '插件依赖（JSON格式）';
COMMENT ON COLUMN nocode_plugin_info.plugin_path IS '插件JAR路径';
COMMENT ON COLUMN nocode_plugin_info.plugin_size IS '插件文件大小（字节）';
COMMENT ON COLUMN nocode_plugin_info.file_hash IS '文件MD5哈希值';
COMMENT ON COLUMN nocode_plugin_info.install_type IS '安装方式: JAR-文件上传, MAVEN-Maven坐标';
COMMENT ON COLUMN nocode_plugin_info.maven_group IS 'Maven GroupId';
COMMENT ON COLUMN nocode_plugin_info.maven_artifact IS 'Maven ArtifactId';
COMMENT ON COLUMN nocode_plugin_info.maven_version IS 'Maven版本号';
COMMENT ON COLUMN nocode_plugin_info.status IS '状态: DISABLED-已停用, ENABLED-已启用, ERROR-异常';
COMMENT ON COLUMN nocode_plugin_info.run_mode IS '运行模式: STATIC-静态加载, DYNAMIC-动态编译';
COMMENT ON COLUMN nocode_plugin_info.auto_start IS '是否自动启动';
COMMENT ON COLUMN nocode_plugin_info.start_order IS '启动顺序';
COMMENT ON COLUMN nocode_plugin_info.config_json IS '插件配置（JSON格式）';
COMMENT ON COLUMN nocode_plugin_info.last_load_time IS '最后加载时间';
COMMENT ON COLUMN nocode_plugin_info.last_unload_time IS '最后卸载时间';
COMMENT ON COLUMN nocode_plugin_info.error_msg IS '错误信息';

-- 2. 插件操作日志表
DROP TABLE IF EXISTS nocode_plugin_log;
CREATE TABLE nocode_plugin_log (
    log_id             BIGSERIAL PRIMARY KEY,                    -- 日志ID
    plugin_id          BIGINT,                                   -- 插件ID
    plugin_code        VARCHAR(100),                             -- 插件编码
    plugin_name        VARCHAR(200),                             -- 插件名称
    operation_type     VARCHAR(50) NOT NULL,                     -- 操作类型: INSTALL-安装, UNINSTALL-卸载, ENABLE-启用, DISABLE-停用, RELOAD-重载
    operation_status   VARCHAR(20) NOT NULL,                     -- 操作状态: SUCCESS-成功, FAIL-失败
    operation_desc     VARCHAR(500),                             -- 操作描述
    old_status         VARCHAR(20),                              -- 操作前状态
    new_status         VARCHAR(20),                              -- 操作后状态
    error_msg          TEXT,                                     -- 错误信息
    operation_ip       VARCHAR(50),                              -- 操作IP
    operation_by       VARCHAR(64),                              -- 操作人
    operation_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      -- 操作时间
    
    CONSTRAINT fk_plugin_log_plugin FOREIGN KEY (plugin_id) REFERENCES nocode_plugin_info(plugin_id) ON DELETE SET NULL
);

-- 表注释
COMMENT ON TABLE nocode_plugin_log IS '插件操作日志表';
COMMENT ON COLUMN nocode_plugin_log.log_id IS '日志ID';
COMMENT ON COLUMN nocode_plugin_log.plugin_id IS '插件ID';
COMMENT ON COLUMN nocode_plugin_log.plugin_code IS '插件编码';
COMMENT ON COLUMN nocode_plugin_log.plugin_name IS '插件名称';
COMMENT ON COLUMN nocode_plugin_log.operation_type IS '操作类型: INSTALL-安装, UNINSTALL-卸载, ENABLE-启用, DISABLE-停用, RELOAD-重载';
COMMENT ON COLUMN nocode_plugin_log.operation_status IS '操作状态: SUCCESS-成功, FAIL-失败';
COMMENT ON COLUMN nocode_plugin_log.operation_desc IS '操作描述';
COMMENT ON COLUMN nocode_plugin_log.old_status IS '操作前状态';
COMMENT ON COLUMN nocode_plugin_log.new_status IS '操作后状态';
COMMENT ON COLUMN nocode_plugin_log.error_msg IS '错误信息';
COMMENT ON COLUMN nocode_plugin_log.operation_ip IS '操作IP';
COMMENT ON COLUMN nocode_plugin_log.operation_by IS '操作人';
COMMENT ON COLUMN nocode_plugin_log.operation_time IS '操作时间';

-- 3. 索引
CREATE INDEX idx_plugin_status ON nocode_plugin_info(status);
CREATE INDEX idx_plugin_code ON nocode_plugin_info(plugin_code);
CREATE INDEX idx_plugin_install_type ON nocode_plugin_info(install_type);
CREATE INDEX idx_plugin_create_time ON nocode_plugin_info(create_time);

CREATE INDEX idx_plugin_log_plugin_id ON nocode_plugin_log(plugin_id);
CREATE INDEX idx_plugin_log_operation_type ON nocode_plugin_log(operation_type);
CREATE INDEX idx_plugin_log_operation_time ON nocode_plugin_log(operation_time);

-- 4. 初始化菜单数据
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, del_flag)
VALUES ('插件管理', 1, 12, 'plugin', 'system/plugin/index', NULL, TRUE, FALSE, 'C', TRUE, '0', 'system:plugin:list', 'component', 'admin', CURRENT_TIMESTAMP, '插件管理菜单', '0');

-- 获取刚插入的插件管理菜单ID
-- 注意: PostgreSQL 需要使用 currval 获取序列当前值
-- INSERT INTO sys_menu ... (子菜单略，实际使用时补充)

-- 5. 初始化测试插件数据（可选）
-- INSERT INTO nocode_plugin_info (plugin_code, plugin_name, plugin_version, plugin_desc, plugin_class, plugin_provider, status, run_mode)
-- VALUES ('demo-plugin', '演示插件', '1.0.0', '这是一个演示插件', 'com.ruoyi.plugin.DemoPlugin', 'ruoyi-nocode', 'DISABLED', 'STATIC');

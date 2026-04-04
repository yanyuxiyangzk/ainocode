-- ============================================
-- RuoYi NoCode 基础表结构初始化脚本
-- PostgreSQL 15
-- ============================================

-- 创建扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- 1. 部门表 (sys_dept)
-- ============================================
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

COMMENT ON TABLE sys_dept IS '部门表';
COMMENT ON COLUMN sys_dept.dept_id IS '部门id';
COMMENT ON COLUMN sys_dept.parent_id IS '父部门id';
COMMENT ON COLUMN sys_dept.ancestors IS '祖级列表';
COMMENT ON COLUMN sys_dept.dept_name IS '部门名称';
COMMENT ON COLUMN sys_dept.order_num IS '显示顺序';
COMMENT ON COLUMN sys_dept.leader IS '负责人';
COMMENT ON COLUMN sys_dept.phone IS '联系电话';
COMMENT ON COLUMN sys_dept.email IS '邮箱';
COMMENT ON COLUMN sys_dept.status IS '部门状态（0正常 1停用）';
COMMENT ON COLUMN sys_dept.del_flag IS '删除标志（0代表存在 2代表删除）';
COMMENT ON COLUMN sys_dept.create_by IS '创建者';
COMMENT ON COLUMN sys_dept.create_time IS '创建时间';
COMMENT ON COLUMN sys_dept.update_by IS '更新者';
COMMENT ON COLUMN sys_dept.update_time IS '更新时间';

-- ============================================
-- 2. 用户表 (sys_user)
-- ============================================
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

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.user_id IS '用户ID';
COMMENT ON COLUMN sys_user.dept_id IS '部门ID';
COMMENT ON COLUMN sys_user.user_name IS '用户账号';
COMMENT ON COLUMN sys_user.nick_name IS '用户昵称';
COMMENT ON COLUMN sys_user.user_type IS '用户类型（00系统用户）';
COMMENT ON COLUMN sys_user.email IS '用户邮箱';
COMMENT ON COLUMN sys_user.phone IS '手机号码';
COMMENT ON COLUMN sys_user.sex IS '用户性别（0男 1女 2未知）';
COMMENT ON COLUMN sys_user.avatar IS '头像地址';
COMMENT ON COLUMN sys_user.password IS '密码';
COMMENT ON COLUMN sys_user.status IS '帐号状态（0正常 1停用）';
COMMENT ON COLUMN sys_user.del_flag IS '删除标志（0代表存在 2代表删除）';
COMMENT ON COLUMN sys_user.login_ip IS '最后登录IP';
COMMENT ON COLUMN sys_user.login_date IS '最后登录时间';
COMMENT ON COLUMN sys_user.create_by IS '创建者';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.update_by IS '更新者';
COMMENT ON COLUMN sys_user.update_time IS '更新时间';

-- ============================================
-- 3. 角色表 (sys_role)
-- ============================================
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
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_role_name UNIQUE (role_name),
    CONSTRAINT uk_role_key UNIQUE (role_key)
);

COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.role_id IS '角色ID';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.role_key IS '角色权限字符串';
COMMENT ON COLUMN sys_role.role_sort IS '显示顺序';
COMMENT ON COLUMN sys_role.data_scope IS '数据范围（1全部数据权限 2自定义数据权限 3本部门数据权限 4本部门及以下数据权限）';
COMMENT ON COLUMN sys_role.menu_check_strictly IS '菜单树选择关联方式（0父子联动 1独立选择）';
COMMENT ON COLUMN sys_role.dept_check_strictly IS '部门树选择关联方式（0父子联动 1独立选择）';
COMMENT ON COLUMN sys_role.status IS '角色状态（0正常 1停用）';
COMMENT ON COLUMN sys_role.del_flag IS '删除标志（0代表存在 2代表删除）';
COMMENT ON COLUMN sys_role.create_by IS '创建者';
COMMENT ON COLUMN sys_role.create_time IS '创建时间';
COMMENT ON COLUMN sys_role.update_by IS '更新者';
COMMENT ON COLUMN sys_role.update_time IS '更新时间';

-- ============================================
-- 4. 菜单权限表 (sys_menu)
-- ============================================
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

COMMENT ON TABLE sys_menu IS '菜单权限表';
COMMENT ON COLUMN sys_menu.menu_id IS '菜单ID';
COMMENT ON COLUMN sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN sys_menu.parent_id IS '父菜单ID';
COMMENT ON COLUMN sys_menu.order_num IS '显示顺序';
COMMENT ON COLUMN sys_menu.path IS '路由地址';
COMMENT ON COLUMN sys_menu.component IS '组件路径';
COMMENT ON COLUMN sys_menu.query IS '路由参数';
COMMENT ON COLUMN sys_menu.is_frame IS '是否为外链（0是 1否）';
COMMENT ON COLUMN sys_menu.is_cache IS '是否缓存（0缓存 1不缓存）';
COMMENT ON COLUMN sys_menu.menu_type IS '菜单类型（M目录 C菜单 F按钮）';
COMMENT ON COLUMN sys_menu.visible IS '菜单状态（0显示 1隐藏）';
COMMENT ON COLUMN sys_menu.status IS '菜单状态（0正常 1停用）';
COMMENT ON COLUMN sys_menu.perms IS '权限标识';
COMMENT ON COLUMN sys_menu.icon IS '菜单图标';
COMMENT ON COLUMN sys_menu.create_by IS '创建者';
COMMENT ON COLUMN sys_menu.create_time IS '创建时间';
COMMENT ON COLUMN sys_menu.update_by IS '更新者';
COMMENT ON COLUMN sys_menu.update_time IS '更新时间';
COMMENT ON COLUMN sys_menu.remark IS '备注';

-- ============================================
-- 5. 用户和角色关联表 (sys_user_role)
-- ============================================
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

COMMENT ON TABLE sys_user_role IS '用户和角色关联表';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';

-- ============================================
-- 6. 角色和菜单关联表 (sys_role_menu)
-- ============================================
CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
);

COMMENT ON TABLE sys_role_menu IS '角色和菜单关联表';
COMMENT ON COLUMN sys_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_menu.menu_id IS '菜单ID';

-- ============================================
-- 7. 角色部门关联表 (sys_role_dept)
-- ============================================
CREATE TABLE sys_role_dept (
    role_id BIGINT NOT NULL,
    dept_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, dept_id)
);

COMMENT ON TABLE sys_role_dept IS '角色和部门关联表';
COMMENT ON COLUMN sys_role_dept.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_dept.dept_id IS '部门ID';

-- ============================================
-- 8. 操作日志表 (sys_oper_log)
-- ============================================
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

COMMENT ON TABLE sys_oper_log IS '操作日志表';
COMMENT ON COLUMN sys_oper_log.oper_id IS '日志主键';
COMMENT ON COLUMN sys_oper_log.title IS '模块标题';
COMMENT ON COLUMN sys_oper_log.business_type IS '业务类型';
COMMENT ON COLUMN sys_oper_log.method IS '方法名称';
COMMENT ON COLUMN sys_oper_log.request_method IS '请求方式';
COMMENT ON COLUMN sys_oper_log.operator_type IS '操作类别';
COMMENT ON COLUMN sys_oper_log.oper_name IS '操作人员';
COMMENT ON COLUMN sys_oper_log.dept_name IS '部门名称';
COMMENT ON COLUMN sys_oper_log.oper_url IS '请求URL';
COMMENT ON COLUMN sys_oper_log.oper_ip IS '主机地址';
COMMENT ON COLUMN sys_oper_log.oper_location IS '操作地点';
COMMENT ON COLUMN sys_oper_log.oper_param IS '请求参数';
COMMENT ON COLUMN sys_oper_log.json_result IS '返回参数';
COMMENT ON COLUMN sys_oper_log.status IS '操作状态（0正常 1异常）';
COMMENT ON COLUMN sys_oper_log.error_msg IS '错误消息';
COMMENT ON COLUMN sys_oper_log.oper_time IS '操作时间';

-- ============================================
-- 9. 字典类型表 (sys_dict_type)
-- ============================================
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

COMMENT ON TABLE sys_dict_type IS '字典类型表';
COMMENT ON COLUMN sys_dict_type.dict_id IS '字典主键';
COMMENT ON COLUMN sys_dict_type.dict_name IS '字典名称';
COMMENT ON COLUMN sys_dict_type.dict_type IS '字典类型';
COMMENT ON COLUMN sys_dict_type.status IS '状态（0正常 1停用）';
COMMENT ON COLUMN sys_dict_type.del_flag IS '删除标志（0代表存在 2代表删除）';
COMMENT ON COLUMN sys_dict_type.create_by IS '创建者';
COMMENT ON COLUMN sys_dict_type.create_time IS '创建时间';
COMMENT ON COLUMN sys_dict_type.update_by IS '更新者';
COMMENT ON COLUMN sys_dict_type.update_time IS '更新时间';
COMMENT ON COLUMN sys_dict_type.remark IS '备注';

-- ============================================
-- 10. 字典数据表 (sys_dict_data)
-- ============================================
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

COMMENT ON TABLE sys_dict_data IS '字典数据表';
COMMENT ON COLUMN sys_dict_data.dict_code IS '字典编码';
COMMENT ON COLUMN sys_dict_data.dict_sort IS '字典排序';
COMMENT ON COLUMN sys_dict_data.dict_label IS '字典标签';
COMMENT ON COLUMN sys_dict_data.dict_value IS '字典键值';
COMMENT ON COLUMN sys_dict_data.dict_type IS '字典类型';
COMMENT ON COLUMN sys_dict_data.css_class IS '样式属性';
COMMENT ON COLUMN sys_dict_data.list_class IS '回显样式';
COMMENT ON COLUMN sys_dict_data.is_default IS '是否默认（Y是 N否）';
COMMENT ON COLUMN sys_dict_data.status IS '状态（0正常 1停用）';
COMMENT ON COLUMN sys_dict_data.del_flag IS '删除标志（0代表存在 2代表删除）';
COMMENT ON COLUMN sys_dict_data.create_by IS '创建者';
COMMENT ON COLUMN sys_dict_data.create_time IS '创建时间';
COMMENT ON COLUMN sys_dict_data.update_by IS '更新者';
COMMENT ON COLUMN sys_dict_data.update_time IS '更新时间';
COMMENT ON COLUMN sys_dict_data.remark IS '备注';

-- ============================================
-- 11. 配置信息表 (sys_config)
-- ============================================
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

COMMENT ON TABLE sys_config IS '参数配置表';
COMMENT ON COLUMN sys_config.config_id IS '参数主键';
COMMENT ON COLUMN sys_config.config_name IS '参数名称';
COMMENT ON COLUMN sys_config.config_key IS '参数键名';
COMMENT ON COLUMN sys_config.config_value IS '参数键值';
COMMENT ON COLUMN sys_config.config_type IS '系统内置（Y是 N否）';
COMMENT ON COLUMN sys_config.del_flag IS '删除标志（0代表存在 2代表删除）';
COMMENT ON COLUMN sys_config.create_by IS '创建者';
COMMENT ON COLUMN sys_config.create_time IS '创建时间';
COMMENT ON COLUMN sys_config.update_by IS '更新者';
COMMENT ON COLUMN sys_config.update_time IS '更新时间';
COMMENT ON COLUMN sys_config.remark IS '备注';

-- ============================================
-- 12. 通知公告表 (sys_notice)
-- ============================================
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

COMMENT ON TABLE sys_notice IS '通知公告表';
COMMENT ON COLUMN sys_notice.notice_id IS '公告ID';
COMMENT ON COLUMN sys_notice.notice_title IS '公告标题';
COMMENT ON COLUMN sys_notice.notice_type IS '公告类型（1通知 2公告）';
COMMENT ON COLUMN sys_notice.notice_content IS '公告内容';
COMMENT ON COLUMN sys_notice.status IS '公告状态（0正常 1关闭）';
COMMENT ON COLUMN sys_notice.del_flag IS '删除标志（0代表存在 2代表删除）';
COMMENT ON COLUMN sys_notice.create_by IS '创建者';
COMMENT ON COLUMN sys_notice.create_time IS '创建时间';
COMMENT ON COLUMN sys_notice.update_by IS '更新者';
COMMENT ON COLUMN sys_notice.update_time IS '更新时间';
COMMENT ON COLUMN sys_notice.remark IS '备注';

-- ============================================
-- 13. 定时任务表 (sys_job)
-- ============================================
CREATE TABLE sys_job (
    job_id BIGSERIAL PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL,
    job_group VARCHAR(100) NOT NULL,
    invoke_target VARCHAR(500) NOT NULL,
    cron_expression VARCHAR(100) DEFAULT '',
    misfire_policy VARCHAR(20) DEFAULT '3',
    concurrent VARCHAR(1) DEFAULT '1',
    status VARCHAR(1) DEFAULT '0',
    create_by VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark VARCHAR(500) DEFAULT ''
);

COMMENT ON TABLE sys_job IS '定时任务表';
COMMENT ON COLUMN sys_job.job_id IS '任务ID';
COMMENT ON COLUMN sys_job.job_name IS '任务名称';
COMMENT ON COLUMN sys_job.job_group IS '任务组名';
COMMENT ON COLUMN sys_job.invoke_target IS '调用目标字符串';
COMMENT ON COLUMN sys_job.cron_expression IS 'cron执行表达式';
COMMENT ON COLUMN sys_job.misfire_policy IS 'cron计划策略';
COMMENT ON COLUMN sys_job.concurrent IS '是否并发执行（0允许 1禁止）';
COMMENT ON COLUMN sys_job.status IS '任务状态（0正常 1暂停）';
COMMENT ON COLUMN sys_job.create_by IS '创建者';
COMMENT ON COLUMN sys_job.create_time IS '创建时间';
COMMENT ON COLUMN sys_job.update_by IS '更新者';
COMMENT ON COLUMN sys_job.update_time IS '更新时间';
COMMENT ON COLUMN sys_job.remark IS '备注信息';

-- ============================================
-- 14. 定时任务日志表 (sys_job_log)
-- ============================================
CREATE TABLE sys_job_log (
    job_log_id BIGSERIAL PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL,
    job_group VARCHAR(100) NOT NULL,
    invoke_target VARCHAR(500) NOT NULL,
    job_message VARCHAR(500) DEFAULT '',
    status VARCHAR(1) DEFAULT '0',
    exception_info VARCHAR(2000) DEFAULT '',
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    stop_time TIMESTAMP
);

COMMENT ON TABLE sys_job_log IS '定时任务日志表';
COMMENT ON COLUMN sys_job_log.job_log_id IS '任务日志ID';
COMMENT ON COLUMN sys_job_log.job_name IS '任务名称';
COMMENT ON COLUMN sys_job_log.job_group IS '任务组名';
COMMENT ON COLUMN sys_job_log.invoke_target IS '调用目标字符串';
COMMENT ON COLUMN sys_job_log.job_message IS '日志信息';
COMMENT ON COLUMN sys_job_log.status IS '执行状态（0正常 1失败）';
COMMENT ON COLUMN sys_job_log.exception_info IS '异常信息';
COMMENT ON COLUMN sys_job_log.start_time IS '开始时间';
COMMENT ON COLUMN sys_job_log.stop_time IS '停止时间';

-- ============================================
-- 15. 代码生成表 (gen_table)
-- ============================================
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

COMMENT ON TABLE gen_table IS '代码生成表';
COMMENT ON COLUMN gen_table.table_id IS '编号';
COMMENT ON COLUMN gen_table.table_name IS '表名称';
COMMENT ON COLUMN gen_table.table_comment IS '表描述';
COMMENT ON COLUMN gen_table.sub_table_name IS '关联子表的表名';
COMMENT ON COLUMN gen_table.sub_table_fk_name IS '子表关联的外键名';
COMMENT ON COLUMN gen_table.class_name IS '实体类名称';
COMMENT ON COLUMN gen_table.tpl_category IS '使用的模板（crud单表 tree树表）';
COMMENT ON COLUMN gen_table.package_name IS '生成包路径';
COMMENT ON COLUMN gen_table.module_name IS '生成模块名';
COMMENT ON COLUMN gen_table.business_name IS '生成业务名';
COMMENT ON COLUMN gen_table.function_name IS '生成功能名';
COMMENT ON COLUMN gen_table.function_author IS '生成功能作者';
COMMENT ON COLUMN gen_table.gen_type IS '生成代码方式（0zip压缩包 1自定义路径）';
COMMENT ON COLUMN gen_table.gen_path IS '生成路径（不填默认项目路径）';
COMMENT ON COLUMN gen_table.options IS '其他生成选项';
COMMENT ON COLUMN gen_table.create_by IS '创建者';
COMMENT ON COLUMN gen_table.create_time IS '创建时间';
COMMENT ON COLUMN gen_table.update_by IS '更新者';
COMMENT ON COLUMN gen_table.update_time IS '更新时间';
COMMENT ON COLUMN gen_table.remark IS '备注';

-- ============================================
-- 14. 代码生成明细表 (gen_table_column)
-- ============================================
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

COMMENT ON TABLE gen_table_column IS '代码生成明细表';
COMMENT ON COLUMN gen_table_column.column_id IS '编号';
COMMENT ON COLUMN gen_table_column.table_id IS '归属表编号';
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
COMMENT ON COLUMN gen_table_column.html_type IS '显示类型（文本框、下拉框、单选框、复选框、日期控件）';
COMMENT ON COLUMN gen_table_column.dict_type IS '字典类型';
COMMENT ON COLUMN gen_table_column.sort IS '排序';
COMMENT ON COLUMN gen_table_column.create_by IS '创建者';
COMMENT ON COLUMN gen_table_column.create_time IS '创建时间';
COMMENT ON COLUMN gen_table_column.update_by IS '更新者';
COMMENT ON COLUMN gen_table_column.update_time IS '更新时间';

-- ============================================
-- 15. 插件表 (sys_plugin)
-- ============================================
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

COMMENT ON TABLE sys_plugin IS '插件表';
COMMENT ON COLUMN sys_plugin.plugin_id IS '插件ID';
COMMENT ON COLUMN sys_plugin.plugin_name IS '插件名称';
COMMENT ON COLUMN sys_plugin.plugin_key IS '插件唯一标识';
COMMENT ON COLUMN sys_plugin.plugin_version IS '插件版本';
COMMENT ON COLUMN sys_plugin.plugin_type IS '插件类型';
COMMENT ON COLUMN sys_plugin.plugin_status IS '插件状态（0未启用 1启用）';
COMMENT ON COLUMN sys_plugin.plugin_path IS '插件路径';
COMMENT ON COLUMN sys_plugin.plugin_config IS '插件配置JSON';
COMMENT ON COLUMN sys_plugin.del_flag IS '删除标志（0存在 2删除）';
COMMENT ON COLUMN sys_plugin.create_by IS '创建者';
COMMENT ON COLUMN sys_plugin.create_time IS '创建时间';
COMMENT ON COLUMN sys_plugin.update_by IS '更新者';
COMMENT ON COLUMN sys_plugin.update_time IS '更新时间';
COMMENT ON COLUMN sys_plugin.remark IS '备注';

-- ============================================
-- 创建索引
-- ============================================
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
CREATE INDEX idx_oper_log_status ON sys_oper_log(status);
CREATE INDEX idx_oper_log_time ON sys_oper_log(oper_time);
CREATE INDEX idx_dict_type_status ON sys_dict_type(status);
CREATE INDEX idx_dict_data_type ON sys_dict_data(dict_type);
CREATE INDEX idx_config_type ON sys_config(config_type);
CREATE INDEX idx_notice_status ON sys_notice(status);
CREATE INDEX idx_gen_table_name ON gen_table(table_name);
CREATE INDEX idx_gen_table_column_table ON gen_table_column(table_id);
CREATE INDEX idx_plugin_status ON sys_plugin(plugin_status);
CREATE INDEX idx_plugin_key ON sys_plugin(plugin_key);
CREATE INDEX idx_job_group ON sys_job(job_group);
CREATE INDEX idx_job_status ON sys_job(status);
CREATE INDEX idx_job_log_group ON sys_job_log(job_group);
CREATE INDEX idx_job_log_status ON sys_job_log(status);
CREATE INDEX idx_job_log_time ON sys_job_log(start_time);

-- V1__base_seed_data.sql
-- Flyway Migration: 基础种子数据

-- ============================================
-- 部门数据
-- ============================================
INSERT INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time) VALUES
(100, 0, '0', '总公司', 0, '管理员', '15888888888', 'admin@ruoyi.com', '0', '0', 'system', CURRENT_TIMESTAMP),
(101, 100, '0,100', '研发部', 1, '研发经理', '15888888889', 'dev@ruoyi.com', '0', '0', 'system', CURRENT_TIMESTAMP),
(102, 100, '0,100', '市场部', 2, '市场经理', '15888888890', 'market@ruoyi.com', '0', '0', 'system', CURRENT_TIMESTAMP),
(103, 101, '0,100,101', '开发一组', 1, '组长', '15888888891', 'dev1@ruoyi.com', '0', '0', 'system', CURRENT_TIMESTAMP),
(104, 101, '0,100,101', '开发二组', 2, '组长', '15888888892', 'dev2@ruoyi.com', '0', '0', 'system', CURRENT_TIMESTAMP);

-- ============================================
-- 用户数据
-- 密码: admin123 (BCrypt加密后的占位符，实际使用时请替换)
-- ============================================
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phone, sex, avatar, password, status, del_flag, create_by, create_time) VALUES
(1, 100, 'admin', '管理员', '00', 'admin@ruoyi.com', '15888888888', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU2ZfDKj30', '0', '0', 'system', CURRENT_TIMESTAMP),
(2, 103, 'user01', '开发人员01', '00', 'user01@ruoyi.com', '15888888891', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU2ZfDKj30', '0', '0', 'system', CURRENT_TIMESTAMP),
(3, 104, 'user02', '开发人员02', '00', 'user02@ruoyi.com', '15888888892', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU2ZfDKj30', '0', '0', 'system', CURRENT_TIMESTAMP);

-- ============================================
-- 角色数据
-- ============================================
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time) VALUES
(1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'system', CURRENT_TIMESTAMP),
(2, '系统管理员', 'manager', 2, '1', 1, 1, '0', '0', 'system', CURRENT_TIMESTAMP),
(3, '普通角色', 'common', 3, '2', 1, 1, '0', '0', 'system', CURRENT_TIMESTAMP);

-- ============================================
-- 用户角色关联
-- ============================================
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3);

-- ============================================
-- 菜单数据
-- ============================================
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
-- 一级菜单
(1, '系统管理', 0, 1, 'system', null, '', 1, 0, 'M', '0', '0', '', 'system', 'system', CURRENT_TIMESTAMP),
(2, '系统监控', 0, 2, 'monitor', null, '', 1, 0, 'M', '0', '0', '', 'monitor', 'system', CURRENT_TIMESTAMP),
(3, '系统工具', 0, 3, 'tool', null, '', 1, 0, 'M', '0', '0', '', 'tool', 'system', CURRENT_TIMESTAMP),
(100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'system', CURRENT_TIMESTAMP),
(101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'system', CURRENT_TIMESTAMP),
(102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'system', CURRENT_TIMESTAMP),
(103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'system', CURRENT_TIMESTAMP),
(104, '岗位管理', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'system', CURRENT_TIMESTAMP),
(105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'system', CURRENT_TIMESTAMP),
(106, '参数管理', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'system', CURRENT_TIMESTAMP),
(107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'system', CURRENT_TIMESTAMP),
(108, '日志管理', 1, 9, 'log', null, '', 1, 0, 'M', '0', '0', '', 'log', 'system', CURRENT_TIMESTAMP),
-- 日志子菜单
(109, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'system', CURRENT_TIMESTAMP),
(110, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'system', CURRENT_TIMESTAMP),
-- 代码生成子菜单
(200, '代码生成', 3, 1, 'gen', 'tool/gen/index', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'system', CURRENT_TIMESTAMP),
(201, '生成配置', 3, 2, 'genConfig', 'tool/genConfig/index', '', 1, 0, 'C', '0', '0', 'tool:genConfig:list', 'devtool', 'system', CURRENT_TIMESTAMP),
-- 插件管理
(300, '插件管理', 1, 10, 'plugin', 'system/plugin/index', '', 1, 0, 'C', '0', '0', 'system:plugin:list', 'plugin', 'system', CURRENT_TIMESTAMP);

-- ============================================
-- 角色菜单关联
-- ============================================
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
-- 超级管理员拥有所有菜单
(1, 1), (1, 2), (1, 3), (1, 100), (1, 101), (1, 102), (1, 103), (1, 104), (1, 105), (1, 106), (1, 107), (1, 108), (1, 109), (1, 110), (1, 200), (1, 201), (1, 300),
-- 系统管理员
(2, 1), (2, 100), (2, 101), (2, 102), (2, 103), (2, 104), (2, 105), (2, 106), (2, 107), (2, 108), (2, 109), (2, 110), (2, 300),
-- 普通角色
(3, 1), (3, 100), (3, 101);

-- ============================================
-- 角色部门关联 (数据权限)
-- ============================================
INSERT INTO sys_role_dept (role_id, dept_id) VALUES
(2, 100), (2, 101), (2, 102), (2, 103), (2, 104),
(3, 101), (3, 103), (3, 104);

-- ============================================
-- 字典类型数据
-- ============================================
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, del_flag, create_by, create_time, remark) VALUES
(1, '用户性别', 'sys_user_sex', '0', '0', 'system', CURRENT_TIMESTAMP, '用户性别列表'),
(2, '菜单状态', 'sys_show_hide', '0', '0', 'system', CURRENT_TIMESTAMP, '菜单状态列表'),
(3, '系统开关', 'sys_normal_disable', '0', '0', 'system', CURRENT_TIMESTAMP, '系统开关列表'),
(4, '任务状态', 'sys_job_status', '0', '0', 'system', CURRENT_TIMESTAMP, '任务状态列表'),
(5, '任务分组', 'sys_job_group', '0', '0', 'system', CURRENT_TIMESTAMP, '任务分组列表'),
(6, '系统是否', 'sys_yes_no', '0', '0', 'system', CURRENT_TIMESTAMP, '系统是否列表'),
(7, '通知类型', 'sys_notice_type', '0', '0', 'system', CURRENT_TIMESTAMP, '通知类型列表'),
(8, '通知状态', 'sys_notice_status', '0', '0', 'system', CURRENT_TIMESTAMP, '通知状态列表'),
(9, '插件状态', 'sys_plugin_status', '0', '0', 'system', CURRENT_TIMESTAMP, '插件状态列表');

-- ============================================
-- 字典数据
-- ============================================
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, del_flag, create_by, create_time, remark) VALUES
-- 用户性别
(1, '男', '0', 'sys_user_sex', '', 'default', 'N', '0', '0', 'system', CURRENT_TIMESTAMP, '男性'),
(2, '女', '1', 'sys_user_sex', '', 'danger', 'N', '0', '0', 'system', CURRENT_TIMESTAMP, '女性'),
(3, '未知', '2', 'sys_user_sex', '', 'info', 'Y', '0', '0', 'system', CURRENT_TIMESTAMP, '未知性别'),
-- 菜单状态
(1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', '0', 'system', CURRENT_TIMESTAMP, '显示菜单'),
(2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', '0', 'system', CURRENT_TIMESTAMP, '隐藏菜单'),
-- 系统开关
(1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', '0', 'system', CURRENT_TIMESTAMP, '正常状态'),
(2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', '0', 'system', CURRENT_TIMESTAMP, '停用状态'),
-- 系统是否
(1, '是', 'Y', 'sys_yes_no', '', 'primary', 'N', '0', '0', 'system', CURRENT_TIMESTAMP, '系统选项是'),
(2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', '0', 'system', CURRENT_TIMESTAMP, '系统选项否'),
-- 通知类型
(1, '通知', '1', 'sys_notice_type', '', 'info', 'Y', '0', '0', 'system', CURRENT_TIMESTAMP, '通知'),
(2, '公告', '2', 'sys_notice_type', '', 'warning', 'N', '0', '0', 'system', CURRENT_TIMESTAMP, '公告'),
-- 通知状态
(1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', '0', 'system', CURRENT_TIMESTAMP, '正常状态'),
(2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', '0', 'system', CURRENT_TIMESTAMP, '关闭状态'),
-- 插件状态
(1, '未启用', '0', 'sys_plugin_status', '', 'info', 'N', '0', '0', 'system', CURRENT_TIMESTAMP, '插件未启用'),
(2, '启用', '1', 'sys_plugin_status', '', 'success', 'Y', '0', '0', 'system', CURRENT_TIMESTAMP, '插件已启用');

-- ============================================
-- 参数配置
-- ============================================
INSERT INTO sys_config (config_name, config_key, config_value, config_type, del_flag, create_by, create_time, remark) VALUES
('主框架页-默认皮肤', 'sys.index.skinName', 'skin-blue', 'Y', '0', 'system', CURRENT_TIMESTAMP, '蓝色主题'),
('用户管理-初始密码', 'sys.user.initPassword', 'admin123', 'Y', '0', 'system', CURRENT_TIMESTAMP, '初始化密码'),
('主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', '0', 'system', CURRENT_TIMESTAMP, '暗色侧边栏'),
('用户管理-默认头像', 'sys.user.defaultAvatar', '/img/profile.jpg', 'Y', '0', 'system', CURRENT_TIMESTAMP, '默认头像'),
('主框架页-菜单默认展开', 'sys.menu.expandOne', 'false', 'Y', '0', 'system', CURRENT_TIMESTAMP, '菜单默认展开'),
('通知公告-默认图标', 'sys.notice.defaultIcon', 'message', 'Y', '0', 'system', CURRENT_TIMESTAMP, '通知图标');

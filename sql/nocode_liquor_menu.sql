-- =============================================================
-- Liquor IDE 菜单数据
-- 说明: Liquor动态编译IDE菜单位于"系统工具"目录下
-- 路径: tool/liquor/index
-- 状态: 需要在系统管理界面手动添加或运行此SQL
-- =============================================================

-- 菜单数据（动态编译IDE）
-- 注意: 如果菜单ID 116已存在，请使用其他ID
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, del_flag)
VALUES ('Liquor IDE', 3, 3, 'liquor', 'tool/liquor/index', NULL, TRUE, FALSE, 'C', TRUE, '0', 'tool:liquor:list', 'code', 'admin', CURRENT_TIMESTAMP, 'Liquor动态编译IDE菜单', '0');

-- 或者如果你已经有了 Liquor IDE 页面，可以直接通过系统管理界面添加

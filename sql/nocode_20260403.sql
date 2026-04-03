-- =============================================
-- RuoYi-NoCode 零代码平台数据库初始化脚本
-- 版本: v1.0.0
-- 日期: 2026-04-03
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS nocode_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE nocode_db;

-- =============================================
-- 表单模块表
-- =============================================

-- 表单配置表
DROP TABLE IF EXISTS nocode_form_config;
CREATE TABLE nocode_form_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '表单名称',
    description VARCHAR(500) COMMENT '描述',
    form_key VARCHAR(100) UNIQUE COMMENT '表单标识',
    form_config TEXT COMMENT 'JSON配置',
    version INT DEFAULT 1 COMMENT '版本号',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态:DRAFT-草稿,PUBLISHED-已发布',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_form_key (form_key),
    INDEX idx_status (status),
    INDEX idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单配置表';

-- 表单组件表
DROP TABLE IF EXISTS nocode_form_component;
CREATE TABLE nocode_form_component (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    form_id BIGINT COMMENT '所属表单ID',
    component_type VARCHAR(50) NOT NULL COMMENT '组件类型',
    label VARCHAR(100) NOT NULL COMMENT '组件标签',
    field_name VARCHAR(100) COMMENT '字段名',
    placeholder VARCHAR(200) COMMENT '占位符',
    default_value VARCHAR(500) COMMENT '默认值',
    required BOOLEAN DEFAULT FALSE COMMENT '是否必填',
    validation_rules TEXT COMMENT '验证规则JSON',
    component_props TEXT COMMENT '组件属性JSON',
    sort INT DEFAULT 0 COMMENT '排序号',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_form_id (form_id),
    INDEX idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单组件表';

-- =============================================
-- 工作流模块表
-- =============================================

-- 流程定义表
DROP TABLE IF EXISTS nocode_workflow_definition;
CREATE TABLE nocode_workflow_definition (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '流程名称',
    description VARCHAR(500) COMMENT '描述',
    process_key VARCHAR(100) NOT NULL UNIQUE COMMENT '流程标识',
    version INT DEFAULT 1 COMMENT '版本号',
    diagram_json TEXT COMMENT '流程图JSON',
    node_definition TEXT COMMENT '节点定义JSON',
    sequence_flow TEXT COMMENT '流转定义JSON',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态:DRAFT-草稿,DEPLOYED-已部署,SUSPENDED-挂起',
    suspended BOOLEAN DEFAULT FALSE COMMENT '是否挂起',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_process_key (process_key),
    INDEX idx_status (status),
    INDEX idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程定义表';

-- 流程实例表
DROP TABLE IF EXISTS nocode_workflow_instance;
CREATE TABLE nocode_workflow_instance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    definition_id BIGINT NOT NULL COMMENT '流程定义ID',
    business_key VARCHAR(100) COMMENT '业务key',
    applicant VARCHAR(64) COMMENT '申请人',
    current_node_id VARCHAR(100) COMMENT '当前节点ID',
    current_node_name VARCHAR(100) COMMENT '当前节点名称',
    instance_status VARCHAR(20) DEFAULT 'RUNNING' COMMENT '实例状态:RUNNING-运行中,COMPLETED-已完成,CANCELLED-已取消,REJECTED-已驳回',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间',
    end_time DATETIME COMMENT '结束时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_definition_id (definition_id),
    INDEX idx_business_key (business_key),
    INDEX idx_applicant (applicant),
    INDEX idx_instance_status (instance_status),
    INDEX idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程实例表';

-- 流程任务表
DROP TABLE IF EXISTS nocode_workflow_task;
CREATE TABLE nocode_workflow_instance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    instance_id BIGINT NOT NULL COMMENT '实例ID',
    node_id VARCHAR(100) NOT NULL COMMENT '节点ID',
    node_name VARCHAR(100) COMMENT '节点名称',
    node_type VARCHAR(50) COMMENT '节点类型',
    assignee VARCHAR(64) COMMENT '办理人',
    candidate VARCHAR(500) COMMENT '候选人',
    task_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '任务状态:PENDING-待签收,CLAIMED-已签收,COMPLETED-已完成,REJECTED-已驳回,CANCELLED-已取消',
    priority VARCHAR(20) DEFAULT 'NORMAL' COMMENT '优先级:LOW-低,NORMAL-普通,HIGH-高',
    countersign_count INT DEFAULT 0 COMMENT '会签人数',
    countersigned_count INT DEFAULT 0 COMMENT '已会签人数',
    countersign_result VARCHAR(20) COMMENT '会签结果:AGREE-同意,REJECT-驳回',
    parent_task_id BIGINT COMMENT '父任务ID',
    due_date DATETIME COMMENT '截止日期',
    comment VARCHAR(500) COMMENT '批注',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间',
    claim_time DATETIME COMMENT '签收时间',
    complete_time DATETIME COMMENT '完成时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_instance_id (instance_id),
    INDEX idx_assignee (assignee),
    INDEX idx_task_status (task_status),
    INDEX idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程任务表';

-- =============================================
-- 代码生成器模块表
-- =============================================

-- 代码生成配置表
DROP TABLE IF EXISTS nocode_code_generator_config;
CREATE TABLE nocode_code_generator_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '配置名称',
    table_name VARCHAR(100) COMMENT '表名',
    entity_name VARCHAR(100) COMMENT '实体名',
    package_name VARCHAR(200) COMMENT '包名',
    module_name VARCHAR(100) COMMENT '模块名',
    generate_type VARCHAR(20) DEFAULT 'TEMPLATE' COMMENT '生成方式:SINGLE-单体,TEMPLATE-模板',
    template_config TEXT COMMENT '模板配置JSON',
    field_config TEXT COMMENT '字段配置JSON',
    status VARCHAR(20) DEFAULT 'ENABLED' COMMENT '状态:ENABLED-启用,DISABLED-禁用',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_table_name (table_name),
    INDEX idx_status (status),
    INDEX idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码生成配置表';

-- =============================================
-- 测试数据
-- =============================================

-- 插入示例表单配置
INSERT INTO nocode_form_config (name, description, form_key, form_config, status) VALUES
('用户注册表单', '用户注册基本信息表单', 'user_register_form', '{"fields":[]}', 'DRAFT'),
('订单申请表单', '订单申请流程表单', 'order_apply_form', '{"fields":[]}', 'DRAFT');

-- 插入示例流程定义
INSERT INTO nocode_workflow_definition (name, description, process_key, status) VALUES
('请假审批流程', '员工请假申请审批流程', 'leave_approval_process', 'DRAFT'),
('订单审批流程', '订单申请审批流程', 'order_approval_process', 'DRAFT');

COMMIT;

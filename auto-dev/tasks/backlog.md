# 开发任务池

> 按优先级排序的待办任务列表（由Product Manager维护）
> 最后更新：2026-04-03

---

## 任务角色归属

| 角色 | 负责任务类型 |
|------|-------------|
| backend-dev | Java接口、Service、Mapper、Entity |
| frontend-dev | Vue页面、组件、API调用 |
| ui-designer | 界面设计、UX优化、样式规范 |
| tester | 测试用例、测试执行、缺陷报告 |
| devops | Docker、K8s、CI/CD、部署配置 |
| operation | 监控、运维、环境维护 |

---

## P0 - 紧急任务

### TASK-20260403-001
- **标题**: [P0任务标题]
- **类型**: feature
- **优先级**: P0
- **负责人**: [角色]
- **状态**: pending
- **验收标准**:
  - [ ]
- **影响范围**:
- **风险等级**:

---

## P1 - 重要任务

### TASK-20260403-002
- **标题**: [P1任务标题]
- **类型**: feature
- **优先级**: P1
- **负责人**: [角色]
- **状态**: pending
- **验收标准**:
  - [ ]
- **影响范围**:
- **风险等级**:

---

## 任务模板

```markdown
# 任务卡片

## 基本信息
- ID: TASK-YYYYMMDD-NNN
- 标题: [任务标题]
- 类型: feature | bugfix | test | devops | optimization
- 优先级: P0 | P1 | P2
- 创建时间: YYYY-MM-DD HH:mm
- 最后更新: YYYY-MM-DD HH:mm
- 负责人: backend-dev | frontend-dev | ui-designer | tester | devops | operation

## 任务详情
- **目标**: [清晰的目标描述]
- **验收标准**:
  - [ ] 标准1
  - [ ] 标准2
- **影响范围**: [影响的模块或功能]
- **风险等级**: 低 | 中 | 高

## 依赖关系
- 前置任务: [TASK-ID] 或 无
- 依赖任务: [TASK-ID] 或 无

## 状态
状态: pending | in_progress | completed | blocked
```

---

## 已完成任务

### TASK-20260402-001 ~ TASK-20260402-006
- ✅ Liquor即时编译
- ✅ Liquor类热替换
- ✅ 代码生成引擎
- ✅ 字典管理功能
- ✅ 沙箱执行环境
- ✅ 动态编译IDE

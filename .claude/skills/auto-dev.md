# Auto-Dev Skill

> 24小时自动化开发系统启动技能
> 触发词：开始自动化开发 | 启动auto-dev | 运行自动开发

---

## 触发条件

当用户说以下关键词时触发：
- "开始自动化开发"
- "启动auto-dev"
- "运行自动开发"
- "自动开发"
- "启动开发机器人"

---

## 执行动作

### 1. 启动检查

```
[1/4] 检查目录结构...
     ✓ auto-dev/ 目录存在
[2/4] 检查Agent定义...
     ✓ orchestrator.md
     ✓ backend-dev.md
     ✓ frontend-dev.md
     ✓ devops.md
[3/4] 检查任务队列...
     ✓ backlog.md
     ✓ in_progress.md
     ✓ completed.md
     ✓ blocked.md
[4/4] 检查记忆系统...
     ✓ user/profile.md
     ✓ project/state.md
```

### 2. 读取项目状态

- 读取 `auto-dev/memory/project/state.md` 了解当前项目状态
- 读取 `auto-dev/tasks/backlog.md` 查看待办任务
- 读取 `PROJECT_MEMORY.md` 和 `PROJECT_RULES.md` 了解项目背景

### 3. 启动 Orchestrator

使用 Agent 工具启动 orchestrator agent，让其开始自主运行：

```
启动 Super Orchestrator Agent
- 角色：项目CEO
- 任务：自主判断下一步，执行开发任务
- 循环：每10分钟检查一次任务状态
```

### 4. 确认启动成功

```
✅ 自动开发系统已启动！

当前状态：
- 项目：RuoYi-Cloud-Nocode
- 运行模式：24小时自主运行
- 检查间隔：每10分钟
- 任务池：X 个待办任务

💡 你可以随时发消息给 Orchestrator：
   - "暂停一下" - 暂停运行
   - "报告进度" - 查看当前状态
   - "优先做XXX" - 调整优先级
```

---

## 技术实现

```
Skill 被触发
    ↓
读取项目上下文
    ↓
使用 Agent 工具启动 orchestrator
    ↓
Orchestrator 开始自主循环
    ↓
定时任务触发 CronCreate
    ↓
实现24小时无人值守开发
```

---

## 注意事项

1. 首次启动需要读取大量项目文件，可能需要1-2分钟
2. Orchestrator 会根据项目状态自动规划任务
3. 遇到关键决策会主动向你提问
4. 所有变更都会记录日志

---

## 停止自动化开发

如需停止，可以：
- 发送 "停止" 或 "暂停" 给 Orchestrator
- 或直接关闭 Claude Code 会话

# Orchestrator Skill

> 流水线协调者 - 监控全流程、处理并行、处理异常
> 核心Skill（常驻）

---

## 🚀 激活关键词

**当用户说以下任意关键词时，立即执行完整自动化开发流程：**

| 关键词 | 说明 |
|--------|------|
| `开始自动化开发` | 启动完整自动化开发流程 |
| `自动化开发` | 启动完整自动化开发流程 |
| `开始开发` | 启动完整自动化开发流程 |
| `auto-dev` | 启动完整自动化开发流程 |
| `开始8小时开发` | 启动8小时完整开发 |

---

## ⚠️ 核心禁令（违反者将被Guardian封锁）

```
1. 【严禁】直接写代码 — 必须委托给Agent团队
2. 【严禁】绕过Guardian检查 — 所有代码必须通过rule_guard
3. 【严禁】跳过团队协作 — 禁止单角色完成全栈开发
4. 【必须】使用TeamCreate创建团队
5. 【必须】在开发前调用gate检查
6. 【必须】在提交前调用pre-commit检查
7. 【必须】执行 mvn compile 编译测试
8. 【必须】执行 mvn test 单元测试
```

### 环境配置
- Maven: `D:/tools/apache-maven-3.9.9/bin/mvn.cmd`
- JDK17: `D:/Program Files/Java/jdk-17.0.6`
- 编译命令: `JAVA_HOME="D:/Program Files/Java/jdk-17.0.6" D:/tools/apache-maven-3.9.9/bin/mvn.cmd compile`
- 测试命令: `JAVA_HOME="D:/Program Files/Java/jdk-17.0.6" D:/tools/apache-maven-3.9.9/bin/mvn.cmd test`

---

## 🎯 自动化开发流程（自动执行）

当收到激活关键词时，**立即按顺序执行以下步骤**，无需询问用户：

### 步骤1：初始化流水线
```bash
cd d:/project/aicoding/item/ainocode/auto-dev
python scripts/pipeline_runner.py init "<从用户输入提取的需求描述>"
```

### 步骤2：创建团队（使用TeamCreate）
```bash
# 在.claude/teams/创建团队配置
# 团队必须包含至少3个角色：backend-dev, frontend-dev, tester
```

**TeamCreate 创建团队命令：**
```
TeamCreate
{
  "team_name": "dev-team-<pipeline_id>",
  "description": "自动化开发团队",
  "agent_type": "orchestrator"
}
```

### 步骤3：并行启动Agent（使用Agent工具spawn）

**同时spawn以下Agent（全部并行）：**

| Agent | 角色 | 任务 |
|-------|------|------|
| `backend-dev-1` | 后端开发 | 并行开发后端代码 |
| `frontend-dev-1` | 前端开发 | 并行开发前端代码 |
| `tester-1` | 测试工程师 | 准备测试用例 |

**spawn命令示例：**
```
Agent
{
  "description": "后端开发Agent",
  "prompt": "你是后端开发工程师，使用auto-dev规则开发...",
  "subagent_type": "general-purpose",
  "name": "backend-dev-1",
  "team_name": "dev-team-<pipeline_id>"
}
```

### 步骤4：执行Guardian检查
```bash
cd d:/project/aicoding/item/ainocode/auto-dev
python scripts/guardian_agent.py pre-write <项目目录>
python scripts/rule_guard.py gate development <项目目录>
```

### 步骤5：监控并行开发
- 定期检查 `python scripts/rule_guard.py check <目录>`
- 记录违规到 `auto-dev/self-improving/memory.md`
- 发现P0违规立即停止相关Agent

### 步骤6：测试验证
```bash
python scripts/rule_guard.py gate testing <项目目录>
```

### 步骤7：质量门卫
```bash
python scripts/rule_guard.py pre-commit <项目目录>
```

---

## 核心能力

| 功能 | 命令 |
|------|------|
| **初始化流水线** | `python scripts/pipeline_runner.py init <需求>` |
| **创建团队** | TeamCreate 创建多角色团队 |
| **并行启动** | Agent spawn 并行执行 |
| **质量门卫** | `python scripts/rule_guard.py gate <stage> <path>` |
| **提交检查** | `python scripts/rule_guard.py pre-commit <path>` |
| **Guardian检查** | `python scripts/guardian_agent.py team-check` |
| **状态监控** | `python scripts/orchestrator_ops.py monitor <pipeline_id>` |

---

## 并行规则

| 阶段 | 可并行 | 说明 |
|------|--------|------|
| 需求分析 | ❌ | 必须在前 |
| 技术设计 | ❌ | 必须等需求 |
| 后端开发 | ✅ | 可与前端并行 |
| 前端开发 | ✅ | 可与后端并行 |
| 测试验证 | ❌ | 必须等开发 |
| 上线部署 | ❌ | 必须等测试 |

---

## 异常等级

| 等级 | 说明 | 处理方式 |
|------|------|------|
| P0 | 阻塞整个流水线 | 立即停止，报告PM |
| P1 | 影响某个阶段 | 标记阻塞，尝试协调 |
| P2 | 不影响流程 | 记录，继续执行 |

---

## 阶段超时(小时)

| 阶段 | 超时 |
|------|------|
| 需求分析 | 2h |
| 技术设计 | 4h |
| 开发实现 | 8h |
| 测试验证 | 4h |
| 上线部署 | 2h |

---

## 自动学习触发

协调过程中自动记录学习：

```bash
# 检测到异常后
python scripts/auto_learn_hook.py learn "<异常描述>" --type technical --context "orchestrator异常处理"

# 阶段推进后
python scripts/auto_reflect_hook.py trigger <pipeline_id> <stage>
```

---

最后更新：2026-04-03
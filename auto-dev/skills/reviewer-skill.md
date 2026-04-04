# Reviewer Skill

## Skill Definition
执行代码审查和质量评估的专业技能。

## Trigger
当Developer完成代码开发后自动触发，或手动调用：
```
/reviewer <task_id>
```

## Input
- `task_id`: 要审核的任务ID
- `code_dir`: 代码目录路径
- `language`: 编程语言（java, typescript, python等）

## Process

### 1. 收集代码
```bash
# 获取变更文件列表
git diff --name-only HEAD~1

# 获取代码内容
cat <file_path>
```

### 2. 执行静态分析
```bash
# Java
mvn compile -q

# TypeScript
npm run build

# Python
python -m py_compile <file>
```

### 3. 运行测试
```bash
# Java
mvn test -q

# TypeScript
npm test

# Python
pytest
```

### 4. 安全扫描
检查以下常见漏洞模式：
- 硬编码凭证：`password\s*=\s*["'][^"']+["']`
- SQL注入：`executeQuery|executeUpdate.*\+.*request`
- XSS：`innerHTML\s*=.*request`
- 不安全的加密：`new\s+Random\(\)`

### 5. 代码规范检查
对照RULES.md检查：
- 类名：PascalCase
- 方法名：camelCase
- 常量：UPPER_SNAKE_CASE
- 包名：全小写

## Output

### APPROVED
```json
{
  "result": "APPROVED",
  "iteration": 1,
  "passed_criteria": ["编译成功", "测试通过", "无P0漏洞"],
  "summary": "代码质量良好，可以进入测试阶段"
}
```

### REVISION_REQUESTED
```json
{
  "result": "REVISION_REQUESTED",
  "iteration": 2,
  "issues": [
    {
      "severity": "HIGH",
      "file": "src/main/java/Service.java",
      "line": 42,
      "type": "硬编码凭证",
      "description": "发现硬编码数据库密码",
      "suggestion": "使用环境变量或配置文件"
    }
  ],
  "summary": "发现3个问题需要修复"
}
```

### REJECTED
```json
{
  "result": "REJECTED",
  "iteration": 3,
  "reason": "经过3次迭代仍存在严重问题",
  "critical_issues": ["P1安全漏洞未修复", "核心功能未实现"],
  "action": "通知PM进行人工介入"
}
```

## Approval Criteria（严格模式）

**所有门禁必须全部通过，否则不得进入测试阶段！**

### 门禁清单

| 门禁 | 必须通过 | 失败处理 |
|------|----------|----------|
| Compile Success | `mvn compile` 退出码=0 | → REVISION_REQUESTED |
| Test Pass Rate ≥ 90% | 失败用例≤10% | → REVISION_REQUESTED |
| No P0 Security Violations | 0个P0漏洞 | → **REJECTED**（立即停止） |
| No P1 Security Violations | 0个P1漏洞 | → REVISION_REQUESTED |
| Naming Convention | 符合RULES.md规范 | → REVISION_REQUESTED |

### P0 问题（立即阻塞）
- SQL注入
- 硬编码密钥/密码/凭证
- 不安全的反序列化
- 敏感信息泄露（身份证、银行卡等）
- 命令注入

### P1 问题（必须修复）
- XSS
- CSRF
- 不安全的加密算法（MD5/SHA1用于安全用途）
- 路径遍历
- 权限绕过

### 迭代限制

- **最大迭代次数：3次**
- 3次迭代内仍有问题 → `REJECTED` → 通知PM人工介入
- P0问题 → 立即 `REJECTED`，不计入迭代次数

### Naming Convention (应该)
符合RULES.md中的命名规范

### Documentation (应该)
- 类必须有Javadoc
- 公开方法建议有Javadoc
- 复杂逻辑必须有注释

## Error Handling

### 编译失败
- 记录错误信息
- 标记为REVISION_REQUESTED
- 提供编译错误详情

### 测试失败
- 记录失败的测试用例
- 分析失败原因
- 标记为REVISION_REQUESTED

### 扫描工具不可用
- 跳过自动扫描
- 手动代码审查
- 降低审核严格度

## Integration

### Pipeline Integration
```
development → reviewer_check → APPROVED → testing
                              ↓
                         REVISION_REQUESTED
                              ↓ (max 3 iterations)
                         [REJECTED if still failing]
```

### Checkpoint Integration
审核状态保存到checkpoint，便于重启后恢复

### Notification Integration
审核结果通知相关Agent和PM

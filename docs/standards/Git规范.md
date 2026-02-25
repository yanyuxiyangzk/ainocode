# Git规范

> RuoYi-Cloud-Nocode Git工作流规范
> 版本：v1.0.0 | 更新日期：2026-02-13

---

## 一、分支管理

### 1.1 分支类型

| 分支 | 命名 | 用途 | 生命周期 |
|------|------|------|----------|
| main | main | 主分支，生产环境 | 永久 |
| develop | develop | 开发分支 | 永久 |
| feature | feature/功能名 | 功能开发 | 临时 |
| bugfix | bugfix/问题描述 | Bug修复 | 临时 |
| release | release/版本号 | 发布准备 | 临时 |
| hotfix | hotfix/问题描述 | 紧急修复 | 临时 |

### 1.2 分支命名

```bash
# 功能分支
feature/user-management
feature/plugin-system
feature/code-generator

# Bug修复
bugfix/login-error
bugfix/export-exception

# 发布分支
release/v1.0.0
release/v1.1.0

# 热修复
hotfix/security-vulnerability
hotfix/critical-bug
```

---

## 二、工作流

### 2.1 日常开发

```
develop → feature/xxx → develop → release/xxx → main
```

### 2.2 Bug修复

```
develop → bugfix/xxx → develop → release/xxx → main
```

### 2.3 紧急修复

```
main → hotfix/xxx → main + develop
```

---

## 三、Commit规范

### 3.1 提交信息格式

```
<type>(<scope>): <subject>

[optional body]

[optional footer]
```

### 3.2 Type类型

| 类型 | 说明 |
|------|------|
| feat | 新功能 |
| fix | Bug修复 |
| docs | 文档更新 |
| style | 格式调整 |
| refactor | 重构 |
| perf | 性能优化 |
| test | 测试相关 |
| build | 构建相关 |
| ci | 持续集成 |
| chore | 其他 |

### 3.3 示例

```bash
# 功能
feat(user): 添加用户导出功能

# 修复
fix(plugin): 修复插件卸载时内存泄漏

# 文档
docs(api): 更新API接口文档

# 重构
refactor(service): 优化用户服务类

# 性能
perf(query): 优化用户查询SQL
```

---

## 四、合并流程

### 4.1 Feature合并

```bash
# 1. 更新develop
git checkout develop
git pull origin develop

# 2. 合并feature
git merge feature/xxx

# 3. 解决冲突（如有）
# 4. 推送
git push origin develop
```

### 4.2 Release合并

```bash
# 1. 创建release分支
git checkout develop
git checkout -b release/v1.0.0

# 2. 修复问题
# 3. 合并到main
git checkout main
git merge release/v1.0.0
git tag -a v1.0.0 -m "Release v1.0.0"

# 4. 合并回develop
git checkout develop
git merge release/v1.0.0

# 5. 删除分支
git branch -d release/v1.0.0
```

---

## 五、标签管理

### 5.1 标签类型

| 标签 | 命名 | 用途 |
|------|------|------|
| 版本标签 | v1.0.0 | 正式版本 |
| 里程碑 | milestone-v1 | 阶段标记 |

### 5.2 创建标签

```bash
# 创建版本标签
git tag -a v1.0.0 -m "Release v1.0.0"

# 推送到远程
git push origin v1.0.0
```

---

## 六、注意事项

### 6.1 提交原则

- 每次提交是一个完整的改动
- 提交信息描述清楚改动内容
- 不要提交敏感信息（如密钥）

### 6.2 合并原则

- 合并前确保代码审查通过
- 合并后确保CI通过
- 及时删除已合并的分支

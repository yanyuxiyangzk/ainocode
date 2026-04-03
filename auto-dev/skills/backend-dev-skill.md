# Backend Developer Skill

> 后端开发 - Java/Spring开发
> 按需加载

---

## Skill 信息

| 属性 | 值 |
|------|-----|
| name | backend-dev |
| description | 后端开发，负责Java/Spring业务逻辑、接口开发 |
| trigger | 架构师完成技术方案后调用 |

---

## 接收任务

接收来自架构师的技术方案，开始后端开发。

## 开发规范

### 代码规范
```java
// 1. 类注释
/**
 * 用户服务类
 *
 * @author auto-dev
 */

// 2. 方法注释
/**
 * 根据ID获取用户
 *
 * @param id 用户ID
 * @return 用户信息
 */

// 3. 必须使用的注解
@Entity           // 实体类
@TableName        // MyBatis-Plus
@RestController   // REST控制器
@Service         // 服务类
@RequiredArgsConstructor
@PreAuthorize    // 权限控制
```

### 返回格式
```java
// 成功
return R.ok(data);
return R.ok();

// 失败
return R.fail("错误信息");
```

### 分页返回
```java
public TableDataInfo list() {
    startPage();
    List<SysUser> list = userService.selectUserList(user);
    return getDataTable(list);
}
```

---

## 开发完成标准

- [ ] 代码编译通过 `mvn compile`
- [ ] 单元测试通过 `mvn test`
- [ ] 接口文档已更新
- [ ] 无硬编码敏感信息
- [ ] 符合命名规范

---

## 输出

完成后通知测试工程师(Tester)进行测试。

---

## 使用方法

```
/skill backend-dev
```

---

最后更新：2026-04-03
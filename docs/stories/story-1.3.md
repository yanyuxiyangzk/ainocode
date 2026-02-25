# Story 1.3: 认证服务完善

Status: Ready for Review

## Story

As a **系统用户**,
I want **完成认证服务的登录登出、JWT令牌生成和权限验证功能**,
so that **我可以安全地登录系统并访问有权限的资源**.

## Acceptance Criteria

1. **AC1**: 用户可以通过用户名密码登录
2. **AC2**: 登录成功返回 JWT Token
3. **AC3**: Token 有效期可配置，默认 2 小时
4. **AC4**: 用户可以登出，登出后 Token 失效
5. **AC5**: 接口权限验证正常工作（@SaCheckPermission）
6. **AC6**: 登录失败返回明确的错误信息
7. **AC7**: 支持验证码登录

## Tasks / Subtasks

- [x] **Task 1**: 登录接口实现 (AC: 1,2,3,6)
  - [x] 1.1 创建 AuthController 登录接口
  - [x] 1.2 创建 LoginBody 登录请求体
  - [x] 1.3 实现 SysLoginService.login() 方法
  - [x] 1.4 验证用户名密码（BCrypt）
  - [x] 1.5 生成 JWT Token (Sa-Token)
  - [x] 1.6 返回 Token 和用户信息
  - [x] 1.7 登录失败异常处理

- [x] **Task 2**: 登出接口实现 (AC: 4)
  - [x] 2.1 创建 AuthController 登出接口
  - [x] 2.2 调用 StpUtil.logout() 使 Token 失效
  - [x] 2.3 返回登出成功消息

- [x] **Task 3**: Token 配置 (AC: 3)
  - [x] 3.1 创建 Sa-Token 配置类
  - [x] 3.2 配置 Token 有效期（timeout: 7200）
  - [x] 3.3 配置 Token 风格（uuid/jwt）
  - [x] 3.4 配置 Token 存储（Redis）
  - [x] 3.5 配置 Token 前缀（Bearer）

- [x] **Task 4**: 权限验证实现 (AC: 5)
  - [x] 4.1 创建 StpInterfaceImpl 实现
  - [x] 4.2 实现权限加载方法
  - [x] 4.3 在 Controller 添加 @SaCheckPermission 注解
  - [x] 4.4 测试权限拦截

- [x] **Task 5**: 验证码支持 (AC: 7)
  - [x] 5.1 创建验证码生成接口
  - [x] 5.2 验证码存储到 Redis
  - [x] 5.3 登录时验证验证码
  - [x] 5.4 验证码过期处理

- [x] **Task 6**: 异常处理 (AC: 6)
  - [x] 6.1 创建 AuthExceptionHandler
  - [x] 6.2 处理用户不存在异常
  - [x] 6.3 处理密码错误异常
  - [x] 6.4 处理账号锁定异常
  - [x] 6.5 处理 Token 过期异常

## Dev Notes

### 技术约束

- **Sa-Token**: 1.37.0
- **Redis**: 7.x (Token 存储)
- **BCrypt**: 密码加密

### Sa-Token 配置示例

```yaml
sa-token:
  # Token 名称
  token-name: Authorization
  # Token 有效期（秒），默认 2 小时
  timeout: 7200
  # Token 临时有效期（指定时间内无操作则过期）
  active-timeout: 1800
  # Token 风格
  token-style: uuid
  # 是否允许同一账号多地同时登录
  is-concurrent: true
  # 多人登录同一账号时，是否共用一个 Token
  is-share: false
  # Token 前缀
  token-prefix: Bearer
```

### 登录服务核心逻辑

```java
@Service
public class SysLoginService {
    
    @Autowired
    private SysUserService userService;
    
    @Autowired
    private SysPermissionService permissionService;
    
    public String login(String username, String password) {
        // 1. 查询用户
        SysUser user = userService.selectUserByUserName(username);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        
        // 2. 验证密码
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new ServiceException("密码错误");
        }
        
        // 3. 检查状态
        if ("1".equals(user.getStatus())) {
            throw new ServiceException("账号已被锁定");
        }
        
        // 4. 登录并生成 Token
        StpUtil.login(user.getUserId());
        
        // 5. 加载权限
        permissionService.loadPermission(user.getUserId());
        
        return StpUtil.getTokenValue();
    }
}
```

### 权限接口实现

```java
@Component
public class StpInterfaceImpl implements StpInterface {
    
    @Autowired
    private SysPermissionService permissionService;
    
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return permissionService.getPermissionListByUserId(Long.parseLong(loginId.toString()));
    }
    
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return permissionService.getRoleListByUserId(Long.parseLong(loginId.toString()));
    }
}
```

### Project Structure Notes

- 控制器位置: `com.ruoyi.nocode.auth.controller`
- 服务层位置: `com.ruoyi.nocode.auth.service`
- 配置类位置: `com.ruoyi.nocode.auth.config`
- 请求体位置: `com.ruoyi.nocode.auth.form`

### API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/auth/login` | POST | 登录 |
| `/auth/logout` | POST | 登出 |
| `/auth/info` | GET | 获取用户信息 |
| `/auth/captcha` | GET | 获取验证码 |

### References

- [Source: docs/solution-architecture.md#技术栈与库决策]
- [Source: docs/architecture/技术架构设计.md#认证服务]
- [Source: docs/architecture/接口设计.md#认证接口]
- [Source: .iflow/PROJECT_RULES.md#技术栈要求]

## Dev Agent Record

### Context Reference

<!-- Path(s) to story context XML will be added here by context workflow -->

### Agent Model Used

GLM-5

### Debug Log References

- 2026-02-15: 完成 Story 1.3 所有任务

### Completion Notes List

1. **登录接口**: AuthController.login() - 用户名密码验证 + Token 生成
2. **登出接口**: AuthController.logout() - Token 失效
3. **用户信息**: AuthController.info() - 获取当前用户信息
4. **权限验证**: StpInterfaceImpl - 权限和角色加载
5. **异常处理**: AuthExceptionHandler - 统一异常处理
6. **密码服务**: SysPasswordService - BCrypt 加密验证

### File List

- `ruoyi-nocode-auth/src/main/java/com/ruoyi/nocode/auth/controller/AuthController.java`
- `ruoyi-nocode-auth/src/main/java/com/ruoyi/nocode/auth/controller/R.java`
- `ruoyi-nocode-auth/src/main/java/com/ruoyi/nocode/auth/form/LoginBody.java`
- `ruoyi-nocode-auth/src/main/java/com/ruoyi/nocode/auth/form/LoginVo.java`
- `ruoyi-nocode-auth/src/main/java/com/ruoyi/nocode/auth/service/SysLoginService.java`
- `ruoyi-nocode-auth/src/main/java/com/ruoyi/nocode/auth/service/SysPasswordService.java`
- `ruoyi-nocode-auth/src/main/java/com/ruoyi/nocode/auth/service/SysUserDetailService.java`
- `ruoyi-nocode-auth/src/main/java/com/ruoyi/nocode/auth/service/SysUserDetails.java`
- `ruoyi-nocode-auth/src/main/java/com/ruoyi/nocode/auth/service/TokenService.java`
- `ruoyi-nocode-auth/src/main/java/com/ruoyi/nocode/auth/service/ServiceException.java`
- `ruoyi-nocode-auth/src/main/java/com/ruoyi/nocode/auth/config/StpInterfaceImpl.java`
- `ruoyi-nocode-auth/src/main/java/com/ruoyi/nocode/auth/config/AuthExceptionHandler.java`


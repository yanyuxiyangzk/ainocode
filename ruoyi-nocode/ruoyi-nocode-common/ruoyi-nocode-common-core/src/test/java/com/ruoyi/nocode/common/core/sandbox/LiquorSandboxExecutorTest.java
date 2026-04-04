package com.ruoyi.nocode.common.core.sandbox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * LiquorSandboxExecutor 单元测试
 *
 * @author ruoyi
 */
@DisplayName("沙箱执行器测试")
class LiquorSandboxExecutorTest {

    private LiquorSandboxExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new LiquorSandboxExecutor();
    }

    @Test
    @DisplayName("测试简单JavaScript代码执行")
    void testSimpleJavaScriptExecution() {
        String code = "1 + 2";
        SandboxResult result = executor.execute(code);

        Assertions.assertTrue(result.isSuccess(), "简单计算应该成功");
        Assertions.assertNotNull(result.getResult(), "结果不应该为null");
    }

    @Test
    @DisplayName("测试字符串操作")
    void testStringOperation() {
        String code = "'hello'.toUpperCase()";
        SandboxResult result = executor.execute(code);

        Assertions.assertTrue(result.isSuccess(), "字符串操作应该成功");
        Assertions.assertNotEquals(0, result.getExecutionTime(), "执行时间应该被记录");
    }

    @Test
    @DisplayName("测试执行超时")
    void testExecutionTimeout() {
        // 无限循环代码
        String code = "while(true) {}";
        SandboxResult result = executor.execute(code, 1000); // 1秒超时

        Assertions.assertFalse(result.isSuccess(), "超时应该导致失败");
        Assertions.assertTrue(result.isTimeout(), "应该标记为超时");
    }

    @Test
    @DisplayName("测试安全策略 - 禁止文件操作")
    void testSecurityPolicyFileOperation() {
        SecurityPolicy policy = SecurityPolicy.strict();
        policy.setAllowFileOperation(false);

        // 尝试读取文件 - 这应该在安全管理器层面被阻止
        // 注意: 在测试环境中可能无法完全测试
        Assertions.assertFalse(policy.isAllowFileOperation());
    }

    @Test
    @DisplayName("测试安全策略 - 禁止网络访问")
    void testSecurityPolicyNetworkAccess() {
        SecurityPolicy policy = SecurityPolicy.strict();
        policy.setAllowNetworkAccess(false);

        Assertions.assertFalse(policy.isAllowNetworkAccess());
    }

    @Test
    @DisplayName("测试安全策略 - 禁止反射")
    void testSecurityPolicyReflection() {
        SecurityPolicy policy = SecurityPolicy.strict();
        policy.setAllowReflection(false);

        Assertions.assertFalse(policy.isAllowReflection());
    }

    @Test
    @DisplayName("测试安全策略 - 禁止系统命令")
    void testSecurityPolicySystemCommand() {
        SecurityPolicy policy = SecurityPolicy.strict();
        policy.setAllowSystemCommand(false);

        Assertions.assertFalse(policy.isAllowSystemCommand());
    }

    @Test
    @DisplayName("测试宽松安全策略")
    void testPermissivePolicy() {
        SecurityPolicy policy = SecurityPolicy.permissive();

        Assertions.assertTrue(policy.isAllowFileOperation());
        Assertions.assertTrue(policy.isAllowReflection());
        Assertions.assertTrue(policy.isAllowThreadCreation());
        Assertions.assertEquals(10000, policy.getMaxExecutionTime());
    }

    @Test
    @DisplayName("测试严格安全策略")
    void testStrictPolicy() {
        SecurityPolicy policy = SecurityPolicy.strict();

        Assertions.assertFalse(policy.isAllowFileOperation());
        Assertions.assertFalse(policy.isAllowNetworkAccess());
        Assertions.assertFalse(policy.isAllowReflection());
        Assertions.assertFalse(policy.isAllowSystemCommand());
        Assertions.assertFalse(policy.isAllowNativeLibrary());
        Assertions.assertFalse(policy.isAllowThreadCreation());
        Assertions.assertEquals(5000, policy.getMaxExecutionTime());
        Assertions.assertEquals(128, policy.getMaxMemory());
    }

    @Test
    @DisplayName("测试严格策略允许的系统类")
    void testStrictPolicyAllowedClasses() {
        SecurityPolicy policy = SecurityPolicy.strict();

        Assertions.assertNotNull(policy.getAllowedSystemClasses());
        Assertions.assertFalse(policy.getAllowedSystemClasses().isEmpty());
        Assertions.assertTrue(policy.getAllowedSystemClasses().contains("java.lang.*"));
        Assertions.assertTrue(policy.getAllowedSystemClasses().contains("java.util.*"));
    }

    @Test
    @DisplayName("测试严格策略禁止的系统类")
    void testStrictPolicyForbiddenClasses() {
        SecurityPolicy policy = SecurityPolicy.strict();

        Assertions.assertNotNull(policy.getForbiddenSystemClasses());
        Assertions.assertFalse(policy.getForbiddenSystemClasses().isEmpty());
        Assertions.assertTrue(policy.getForbiddenSystemClasses().contains("java.lang.Runtime"));
        Assertions.assertTrue(policy.getForbiddenSystemClasses().contains("java.lang.ProcessBuilder"));
        Assertions.assertTrue(policy.getForbiddenSystemClasses().contains("sun.*"));
    }

    @Test
    @DisplayName("测试严格策略禁止的模式")
    void testStrictPolicyForbiddenPatterns() {
        SecurityPolicy policy = SecurityPolicy.strict();

        Assertions.assertNotNull(policy.getForbiddenPatterns());
        Assertions.assertFalse(policy.getForbiddenPatterns().isEmpty());
        Assertions.assertTrue(policy.getForbiddenPatterns().contains("Runtime\\.getRuntime"));
        Assertions.assertTrue(policy.getForbiddenPatterns().contains("ProcessBuilder"));
        Assertions.assertTrue(policy.getForbiddenPatterns().contains("exec\\("));
    }

    @Test
    @DisplayName("测试沙箱结果成功创建")
    void testSandboxResultSuccess() {
        SandboxResult result = SandboxResult.success("test", 100);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("test", result.getResult());
        Assertions.assertEquals(100, result.getExecutionTime());
        Assertions.assertTrue(result.isSecurityCheckPassed());
        Assertions.assertFalse(result.isTimeout());
    }

    @Test
    @DisplayName("测试沙箱结果失败创建")
    void testSandboxResultFailure() {
        SandboxResult result = SandboxResult.failure("Error occurred");

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getErrors());
        Assertions.assertEquals(1, result.getErrors().size());
        Assertions.assertFalse(result.isSecurityCheckPassed());
    }

    @Test
    @DisplayName("测试沙箱结果超时创建")
    void testSandboxResultTimeout() {
        SandboxResult result = SandboxResult.timeout(5000);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.isTimeout());
        Assertions.assertEquals(5000, result.getExecutionTime());
    }

    @Test
    @DisplayName("测试沙箱结果安全失败创建")
    void testSandboxResultSecurityFailure() {
        SandboxResult result = SandboxResult.securityFailure(java.util.List.of("Network access forbidden"));

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertFalse(result.isSecurityCheckPassed());
    }

    @Test
    @DisplayName("测试沙箱结果添加警告")
    void testSandboxResultAddWarning() {
        SandboxResult result = SandboxResult.success("test", 100);
        result.addWarning("This is a warning");

        Assertions.assertNotNull(result.getWarnings());
        Assertions.assertEquals(1, result.getWarnings().size());
        Assertions.assertEquals("This is a warning", result.getWarnings().get(0));
    }

    @Test
    @DisplayName("测试格式化错误信息")
    void testFormattedErrors() {
        SandboxResult result = SandboxResult.failure(java.util.List.of("Error 1", "Error 2", "Error 3"));

        String formatted = result.getFormattedErrors();
        Assertions.assertNotNull(formatted);
        Assertions.assertTrue(formatted.contains("Error 1"));
        Assertions.assertTrue(formatted.contains("Error 2"));
        Assertions.assertTrue(formatted.contains("Error 3"));
    }

    @Test
    @DisplayName("测试空格式化错误信息")
    void testEmptyFormattedErrors() {
        SandboxResult result = new SandboxResult();
        result.setSuccess(true);

        String formatted = result.getFormattedErrors();
        Assertions.assertNotNull(formatted);
        Assertions.assertEquals("", formatted);
    }

    @Test
    @DisplayName("测试安全策略最大内存设置")
    void testSecurityPolicyMaxMemory() {
        SecurityPolicy policy = new SecurityPolicy();
        policy.setMaxMemory(256);

        Assertions.assertEquals(256, policy.getMaxMemory());
    }

    @Test
    @DisplayName("测试安全策略最大执行时间设置")
    void testSecurityPolicyMaxExecutionTime() {
        SecurityPolicy policy = new SecurityPolicy();
        policy.setMaxExecutionTime(10000);

        Assertions.assertEquals(10000, policy.getMaxExecutionTime());
    }

    @Test
    @DisplayName("测试安全策略最大线程数设置")
    void testSecurityPolicyMaxThreadCount() {
        SecurityPolicy policy = new SecurityPolicy();
        policy.setMaxThreadCount(5);

        Assertions.assertEquals(5, policy.getMaxThreadCount());
    }

    @Test
    @DisplayName("测试安全策略启用开关")
    void testSecurityPolicyEnabled() {
        SecurityPolicy policy = new SecurityPolicy();
        Assertions.assertTrue(policy.isEnabled());

        policy.setEnabled(false);
        Assertions.assertFalse(policy.isEnabled());
    }
}

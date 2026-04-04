package com.ruoyi.nocode.common.core.sandbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 沙箱执行器测试
 *
 * @author ruoyi
 */
public class SandboxExecutorTest {

    private LiquorSandboxExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new LiquorSandboxExecutor();
    }

    @Test
    void testSimpleCodeExecution() {
        String code = "1 + 2";
        SandboxResult result = executor.execute(code);

        assertTrue(result.isSuccess(), "Code should execute successfully");
        assertFalse(result.isTimeout(), "Should not timeout");
        assertTrue(result.isSecurityCheckPassed(), "Security check should pass");
    }

    @Test
    void testStringOperations() {
        String code = "'Hello, '.concat('World!')";
        SandboxResult result = executor.execute(code);

        assertTrue(result.isSuccess());
        assertNotNull(result.getResult());
    }

    @Test
    void testArrayOperations() {
        String code = "var arr = [1, 2, 3]; arr.reduce(function(a, b) { return a + b; }, 0)";
        SandboxResult result = executor.execute(code);

        assertTrue(result.isSuccess());
    }

    @Test
    void testTimeout() {
        // 使用一个会超时的代码
        String code = "while(true) { }";
        SandboxResult result = executor.execute(code, 1000);

        assertFalse(result.isSuccess());
        assertTrue(result.isTimeout());
    }

    @Test
    void testSecurityManagerBlocksFileAccess() {
        // 尝试访问文件系统
        String code = "Packages.java.io.File.listRoots()";
        SandboxResult result = executor.execute(code);

        // 应该被安全策略阻止
        assertFalse(result.isSuccess() || result.isSecurityCheckPassed());
    }

    @Test
    void testSecurityManagerBlocksNetwork() {
        // 尝试网络连接
        String code = "java.net.InetAddress.getAllByName('localhost')";
        SandboxResult result = executor.execute(code);

        // 应该被安全策略阻止
        assertFalse(result.isSuccess() || result.isSecurityCheckPassed());
    }

    @Test
    void testMathOperations() {
        String code = "Math.sqrt(16) + Math.pow(2, 3)";
        SandboxResult result = executor.execute(code);

        assertTrue(result.isSuccess());
    }

    @Test
    void testObjectCreation() {
        String code = "var obj = { name: 'test', value: 123 }; obj.name";
        SandboxResult result = executor.execute(code);

        assertTrue(result.isSuccess());
        assertEquals("test", result.getResult());
    }

    @Test
    void testFunctionDefinition() {
        String code = "(function(x) { return x * 2; })(5)";
        SandboxResult result = executor.execute(code);

        assertTrue(result.isSuccess());
        assertEquals(10.0, result.getResult());
    }

    @Test
    void testJsonParsing() {
        String code = "JSON.parse('{\"key\": \"value\"}').key";
        SandboxResult result = executor.execute(code);

        assertTrue(result.isSuccess());
        assertEquals("value", result.getResult());
    }

    @Test
    void testNullHandling() {
        String code = "null === null";
        SandboxResult result = executor.execute(code);

        assertTrue(result.isSuccess());
    }

    @Test
    void testUndefinedHandling() {
        String code = "typeof undefinedVar";
        SandboxResult result = executor.execute(code);

        assertTrue(result.isSuccess());
    }

    @Test
    void testRegexOperations() {
        String code = "'Hello World'.match(/World/)[0]";
        SandboxResult result = executor.execute(code);

        assertTrue(result.isSuccess());
        assertEquals("World", result.getResult());
    }

    @Test
    void testStringMethods() {
        String code = "'hello'.toUpperCase().concat(' WORLD')";
        SandboxResult result = executor.execute(code);

        assertTrue(result.isSuccess());
        assertEquals("HELLO WORLD", result.getResult());
    }

    @Test
    void testArrayMethods() {
        String code = "[1, 2, 3, 4, 5].filter(function(x) { return x > 2; }).join(',')";
        SandboxResult result = executor.execute(code);

        assertTrue(result.isSuccess());
    }

    @Test
    void testExecutionTimeIsRecorded() {
        String code = "1 + 1";
        SandboxResult result = executor.execute(code);

        assertTrue(result.isSuccess());
        assertTrue(result.getExecutionTime() >= 0);
    }

    @Test
    void testCustomTimeout() {
        String code = "1 + 1";
        SandboxResult result = executor.execute(code, 10000);

        assertTrue(result.isSuccess());
        assertTrue(result.getExecutionTime() <= 10000);
    }

    @Test
    void testSandboxPolicyImplies() {
        SandboxPolicy policy = new SandboxPolicy();

        // 测试临时目录读取权限
        java.security.ProtectionDomain domain = new java.security.ProtectionDomain(
                null, null);

        java.io.FilePermission readTemp = new java.io.FilePermission("/tmp/test", "read");
        assertFalse(policy.implies(domain, readTemp));
    }

    @Test
    void testSandboxPolicyBlocksDangerousOperations() {
        SandboxPolicy policy = new SandboxPolicy();

        java.security.ProtectionDomain domain = new java.security.ProtectionDomain(
                null, null);

        // 禁止文件写入
        java.io.FilePermission writeFile = new java.io.FilePermission("/tmp/test", "write");
        assertFalse(policy.implies(domain, writeFile));

        // 禁止网络连接
        java.net.SocketPermission connect = new java.net.SocketPermission("google.com:80", "connect");
        assertFalse(policy.implies(domain, connect));

        // 禁止创建类加载器
        java.lang.RuntimePermission createClassLoader =
                new java.lang.RuntimePermission("createClassLoader");
        assertFalse(policy.implies(domain, createClassLoader));

        // 禁止退出VM
        java.lang.RuntimePermission exitVM = new java.lang.RuntimePermission("exitVM");
        assertFalse(policy.implies(domain, exitVM));
    }

    @Test
    void testSandboxResultFactoryMethods() {
        // 测试成功结果
        SandboxResult success = SandboxResult.success("result", 100);
        assertTrue(success.isSuccess());
        assertEquals("result", success.getResult());
        assertEquals(100, success.getExecutionTime());
        assertTrue(success.isSecurityCheckPassed());

        // 测试失败结果
        SandboxResult failure = SandboxResult.failure("error");
        assertFalse(failure.isSuccess());
        assertEquals("error", failure.getFormattedErrors());

        // 测试超时结果
        SandboxResult timeout = SandboxResult.timeout(5000);
        assertFalse(timeout.isSuccess());
        assertTrue(timeout.isTimeout());

        // 测试安全失败结果
        SandboxResult securityFailure = SandboxResult.securityFailure(
                java.util.List.of("security error"));
        assertFalse(securityFailure.isSuccess());
        assertFalse(securityFailure.isSecurityCheckPassed());
    }

    @Test
    void testSandboxResultAddWarning() {
        SandboxResult result = SandboxResult.success(null, 0);
        result.addWarning("warning 1");
        result.addWarning("warning 2");

        assertNotNull(result.getWarnings());
        assertEquals(2, result.getWarnings().size());
        assertTrue(result.getWarnings().contains("warning 1"));
        assertTrue(result.getWarnings().contains("warning 2"));
    }

    @Test
    void testSandboxResultFormattedErrors() {
        SandboxResult result = SandboxResult.failure(
                java.util.Arrays.asList("error1", "error2", "error3"));

        String formatted = result.getFormattedErrors();
        assertEquals("error1\nerror2\nerror3", formatted);

        // 测试空错误
        SandboxResult emptyResult = SandboxResult.failure(java.util.List.of());
        assertEquals("", emptyResult.getFormattedErrors());
    }

    @Test
    void testBlockedReflectionTargets() {
        assertTrue(SandboxPolicy.isBlockedReflectionTarget("java.lang.System"));
        assertTrue(SandboxPolicy.isBlockedReflectionTarget("java.lang.Runtime"));
        assertTrue(SandboxPolicy.isBlockedReflectionTarget("java.lang.ClassLoader"));

        assertFalse(SandboxPolicy.isBlockedReflectionTarget("java.lang.String"));
        assertFalse(SandboxPolicy.isBlockedReflectionTarget("java.util.ArrayList"));
    }
}

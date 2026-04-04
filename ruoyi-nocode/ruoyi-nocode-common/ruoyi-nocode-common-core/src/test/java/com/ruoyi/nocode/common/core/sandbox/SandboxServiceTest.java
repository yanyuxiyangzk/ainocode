package com.ruoyi.nocode.common.core.sandbox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 沙箱服务单元测试
 *
 * @author ruoyi
 */
@DisplayName("沙箱服务测试")
class SandboxServiceTest {

    private SandboxService sandboxService;

    @BeforeEach
    void setUp() {
        sandboxService = SandboxService.getInstance();
    }

    @Test
    @DisplayName("测试安全代码验证通过")
    void testValidateCodeSafe() {
        String safeCode = """
                public class SafeCalculator {
                    public int add(int a, int b) {
                        return a + b;
                    }
                    public String getGreeting() {
                        return "Hello, World!";
                    }
                }
                """;

        SandboxService.ValidationResult result = sandboxService.validateCode(safeCode);

        assertTrue(result.isValid(), "安全代码应该通过验证");
        assertTrue(result.getWarnings().isEmpty(), "安全代码不应该有警告");
    }

    @Test
    @DisplayName("测试危险代码检测 - Runtime.exec")
    void testValidateCodeDangerousExec() {
        String dangerousCode = """
                public class Dangerous {
                    public void execute() {
                        Runtime.getRuntime().exec("ls");
                    }
                }
                """;

        SandboxService.ValidationResult result = sandboxService.validateCode(dangerousCode);

        assertFalse(result.isValid(), "包含Runtime.exec的代码应该被拒绝");
        assertTrue(result.getWarnings().stream()
                .anyMatch(w -> w.contains("Runtime.getRuntime().exec")), "应该检测到exec危险");
    }

    @Test
    @DisplayName("测试危险代码检测 - System.exit")
    void testValidateCodeSystemExit() {
        String dangerousCode = """
                public class Dangerous {
                    public void exit() {
                        System.exit(0);
                    }
                }
                """;

        SandboxService.ValidationResult result = sandboxService.validateCode(dangerousCode);

        assertFalse(result.isValid(), "包含System.exit的代码应该被拒绝");
    }

    @Test
    @DisplayName("测试危险代码检测 - 文件操作")
    void testValidateCodeFileOperation() {
        String dangerousCode = """
                public class Dangerous {
                    public void readFile() throws Exception {
                        new java.io.FileInputStream("/etc/passwd");
                    }
                }
                """;

        SandboxService.ValidationResult result = sandboxService.validateCode(dangerousCode);

        assertFalse(result.isValid(), "包含文件操作的代码应该被拒绝");
        assertTrue(result.getWarnings().stream()
                .anyMatch(w -> w.contains("FileInputStream") || w.contains("dangerous")), "应该检测到文件危险");
    }

    @Test
    @DisplayName("测试危险代码检测 - 网络访问")
    void testValidateCodeNetworkAccess() {
        String dangerousCode = """
                public class Dangerous {
                    public void connect() throws Exception {
                        new java.net.Socket("localhost", 8080);
                    }
                }
                """;

        SandboxService.ValidationResult result = sandboxService.validateCode(dangerousCode);

        assertFalse(result.isValid(), "包含Socket的代码应该被拒绝");
    }

    @Test
    @DisplayName("测试危险代码检测 - 反射")
    void testValidateCodeReflection() {
        String dangerousCode = """
                public class Dangerous {
                    public void reflect() throws Exception {
                        Class.forName("java.lang.Runtime");
                    }
                }
                """;

        SandboxService.ValidationResult result = sandboxService.validateCode(dangerousCode);

        assertFalse(result.isValid(), "包含Class.forName的代码应该被拒绝");
    }

    @Test
    @DisplayName("测试安全策略配置")
    void testSecurityPolicy() {
        SecurityPolicy strictPolicy = SecurityPolicy.strict();
        assertTrue(strictPolicy.isEnabled(), "严格策略应该启用");
        assertFalse(strictPolicy.isAllowFileOperation(), "严格策略应该禁止文件操作");
        assertFalse(strictPolicy.isAllowNetworkAccess(), "严格策略应该禁止网络访问");
        assertFalse(strictPolicy.isAllowSystemCommand(), "严格策略应该禁止系统命令");

        SecurityPolicy permissivePolicy = SecurityPolicy.permissive();
        assertTrue(permissivePolicy.isAllowFileOperation(), "宽松策略应该允许文件操作");
        assertTrue(permissivePolicy.isAllowNetworkAccess(), "宽松策略应该允许网络访问");
    }

    @Test
    @DisplayName("测试代码安全检查器")
    void testCodeSecurityChecker() {
        CodeSecurityChecker checker = new CodeSecurityChecker();

        String safeCode = """
                public class Test {
                    public int calculate(int x) { return x * 2; }
                }
                """;

        List<String> violations = checker.checkSecurity(safeCode);
        assertNull(violations, "安全代码应该没有违规");

        String dangerousCode = "Runtime.getRuntime().exec(\"cmd\");";
        List<String> dangerousViolations = checker.checkSecurity(dangerousCode);
        assertNotNull(dangerousViolations, "危险代码应该有违规");
    }

    @Test
    @DisplayName("测试代码长度检查")
    void testCodeLengthCheck() {
        CodeSecurityChecker checker = new CodeSecurityChecker();

        // 超长代码
        StringBuilder sb = new StringBuilder("public class Long { ");
        for (int i = 0; i < 1000; i++) {
            sb.append("public void m").append(i).append("() {} ");
        }
        sb.append("}");

        List<String> violations = checker.checkSecurity(sb.toString());
        assertNotNull(violations, "超长代码应该有违规");
    }

    @Test
    @DisplayName("测试嵌套深度检查")
    void testNestingDepthCheck() {
        CodeSecurityChecker checker = new CodeSecurityChecker();

        // 过度嵌套
        StringBuilder sb = new StringBuilder("public class Nested { ");
        for (int i = 0; i < 25; i++) {
            sb.append("public void m").append(i).append("() { ");
        }
        for (int i = 0; i < 25; i++) {
            sb.append("} ");
        }
        sb.append("}");

        List<String> violations = checker.checkSecurity(sb.toString());
        assertNotNull(violations, "过度嵌套的代码应该有违规");
    }

    @Test
    @DisplayName("测试沙箱结果创建")
    void testSandboxResultCreation() {
        SandboxResult successResult = SandboxResult.success("result", "output", 100);
        assertTrue(successResult.isSuccess(), "成功结果应该success为true");
        assertEquals("result", successResult.getReturnValue(), "返回值应该匹配");
        assertEquals("output", successResult.getOutput(), "输出应该匹配");
        assertEquals(100, successResult.getExecutionTime(), "执行时间应该匹配");

        SandboxResult failureResult = SandboxResult.failure("error", SandboxError.SECURITY_ERROR);
        assertFalse(failureResult.isSuccess(), "失败结果应该success为false");
        assertEquals("error", failureResult.getError(), "错误信息应该匹配");
        assertEquals(SandboxError.SECURITY_ERROR, failureResult.getErrorCode(), "错误码应该匹配");
    }

    @Test
    @DisplayName("测试沙箱错误码")
    void testSandboxErrorCodes() {
        assertEquals("0000", SandboxError.SUCCESS.getCode());
        assertEquals("0001", SandboxError.TIMEOUT.getCode());
        assertEquals("0004", SandboxError.SECURITY_ERROR.getCode());
        assertEquals("1001", SandboxError.NETWORK_FORBIDDEN.getCode());
        assertEquals("1002", SandboxError.FILE_FORBIDDEN.getCode());
    }

    @Test
    @DisplayName("测试安全策略的禁止模式")
    void testForbiddenPatterns() {
        SecurityPolicy policy = SecurityPolicy.strict();
        assertFalse(policy.getForbiddenSystemClasses().isEmpty(), "应该有禁止的系统类");
        assertFalse(policy.getForbiddenPatterns().isEmpty(), "应该有禁止的模式");
        assertTrue(policy.getForbiddenSystemClasses().contains("java.lang.Runtime"), "应该禁止Runtime");
        assertTrue(policy.getForbiddenPatterns().contains("Runtime\\.getRuntime"), "应该禁止Runtime.getRuntime模式");
    }

    @Test
    @DisplayName("测试单例模式")
    void testSingletonPattern() {
        SandboxService instance1 = SandboxService.getInstance();
        SandboxService instance2 = SandboxService.getInstance();

        assertSame(instance1, instance2, "应该返回单例实例");
    }

    @Test
    @DisplayName("测试快速安全检查")
    void testQuickCheck() {
        CodeSecurityChecker checker = new CodeSecurityChecker();

        assertTrue(checker.quickCheck("public int add(int a, int b) { return a + b; }"));
        assertFalse(checker.quickCheck("Runtime.getRuntime().exec(\"ls\")"));
        assertFalse(checker.quickCheck("new java.net.Socket(\"host\", 80)"));
    }

    @Test
    @DisplayName("测试空代码检查")
    void testEmptyCodeCheck() {
        CodeSecurityChecker checker = new CodeSecurityChecker();

        assertFalse(checker.quickCheck(null), "null代码应该返回false");
        assertFalse(checker.quickCheck(""), "空代码应该返回false");

        List<String> violations = checker.checkSecurity(null);
        assertNotNull(violations, "null代码应该返回违规列表");
    }

    @Test
    @DisplayName("测试线程安全")
    void testThreadSafety() throws InterruptedException {
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                SandboxService.ValidationResult result = sandboxService.validateCode("public class Test {}");
                assertTrue(result.isValid(), "验证应该在多线程下正常工作");
            });
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
    }
}

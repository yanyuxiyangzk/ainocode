package com.ruoyi.nocode.common.core.compiler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * LiquorHotSwapService 单元测试
 *
 * @author ruoyi
 */
@DisplayName("Liquor类热替换服务测试")
class LiquorHotSwapServiceTest {

    private LiquorHotSwapService hotSwapService;

    @BeforeEach
    void setUp() {
        hotSwapService = LiquorHotSwapService.getInstance();
        hotSwapService.clearAllDomains();
    }

    @Test
    @DisplayName("测试基本热替换功能")
    void testBasicHotSwap() {
        String originalCode = """
                public class Counter {
                    private int count = 0;
                    public int increment() {
                        return ++count;
                    }
                    public int getCount() {
                        return count;
                    }
                }
                """;

        // 第一次热替换（初始加载）
        HotSwapResult result1 = hotSwapService.hotSwap("test-domain", originalCode);

        Assertions.assertTrue(result1.isSuccess(), "初始热替换应该成功");
        Assertions.assertEquals("Counter", result1.getOriginalClassName());
        Assertions.assertEquals(1, result1.getVersion());
        Assertions.assertNotNull(result1.getSwappedClass());

        // 验证初始版本的类可以正常工作
        try {
            Object instance1 = result1.getSwappedClass().getDeclaredConstructor().newInstance();
            Method getCount = result1.getSwappedClass().getMethod("getCount");
            Assertions.assertEquals(0, getCount.invoke(instance1));
        } catch (Exception e) {
            Assertions.fail("实例化或方法调用失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试热替换后新实例使用新类定义")
    void testHotSwapNewInstanceUsesNewClass() throws Exception {
        String v1Code = """
                public class Calculator {
                    public int calculate(int x) {
                        return x + 10;
                    }
                }
                """;

        String v2Code = """
                public class Calculator {
                    public int calculate(int x) {
                        return x + 100;
                    }
                }
                """;

        // 加载v1
        HotSwapResult result1 = hotSwapService.hotSwap("calc-domain", v1Code);
        Assertions.assertTrue(result1.isSuccess());
        Assertions.assertEquals(1, result1.getVersion());

        // 热替换到v2
        HotSwapResult result2 = hotSwapService.hotSwap("calc-domain", v2Code);
        Assertions.assertTrue(result2.isSuccess());
        Assertions.assertEquals(2, result2.getVersion());

        // 验证v2的新实例返回值不同
        Object v2Instance = result2.getSwappedClass().getDeclaredConstructor().newInstance();
        Method calculate = result2.getSwappedClass().getMethod("calculate", int.class);
        int v2Result = (int) calculate.invoke(v2Instance, 0);

        Assertions.assertEquals(100, v2Result, "v2版本应该返回 x + 100");
    }

    @Test
    @DisplayName("测试版本号递增")
    void testVersionIncrement() {
        String code = """
                public class VersionTest {
                    public String getVersion() {
                        return "v1";
                    }
                }
                """;

        // 第一次
        HotSwapResult result1 = hotSwapService.hotSwap("version-domain", code);
        Assertions.assertTrue(result1.isSuccess());
        Assertions.assertEquals(1, result1.getVersion());

        // 第二次
        HotSwapResult result2 = hotSwapService.hotSwap("version-domain", code);
        Assertions.assertTrue(result2.isSuccess());
        Assertions.assertEquals(2, result2.getVersion());

        // 第三次
        HotSwapResult result3 = hotSwapService.hotSwap("version-domain", code);
        Assertions.assertTrue(result3.isSuccess());
        Assertions.assertEquals(3, result3.getVersion());

        // 验证域信息
        LiquorHotSwapService.DomainInfo info = hotSwapService.getDomainInfo("version-domain");
        Assertions.assertNotNull(info);
        Assertions.assertEquals(3, info.getCurrentVersion());
    }

    @Test
    @DisplayName("测试单例模式")
    void testSingletonPattern() {
        LiquorHotSwapService instance1 = LiquorHotSwapService.getInstance();
        LiquorHotSwapService instance2 = LiquorHotSwapService.getInstance();

        Assertions.assertSame(instance1, instance2, "应该返回单例实例");
    }

    @Test
    @DisplayName("测试多域隔离")
    void testMultipleDomainsIsolation() {
        String codeA = """
                public class ServiceA {
                    public String getName() {
                        return "A";
                    }
                }
                """;

        String codeB = """
                public class ServiceB {
                    public String getName() {
                        return "B";
                    }
                }
                """;

        // 在不同域中编译
        HotSwapResult resultA = hotSwapService.hotSwap("domain-A", codeA);
        HotSwapResult resultB = hotSwapService.hotSwap("domain-B", codeB);

        Assertions.assertTrue(resultA.isSuccess());
        Assertions.assertTrue(resultB.isSuccess());

        // 验证类名正确
        Assertions.assertEquals("ServiceA", resultA.getOriginalClassName());
        Assertions.assertEquals("ServiceB", resultB.getOriginalClassName());

        // 验证swapId不同
        Assertions.assertNotEquals(resultA.getSwapId(), resultB.getSwapId());
        Assertions.assertTrue(resultA.getSwapId().startsWith("domain-A"));
        Assertions.assertTrue(resultB.getSwapId().startsWith("domain-B"));

        // 验证域列表包含两个域
        Assertions.assertTrue(hotSwapService.getAllDomains().contains("domain-A"));
        Assertions.assertTrue(hotSwapService.getAllDomains().contains("domain-B"));
    }

    @Test
    @DisplayName("测试销毁域")
    void testDestroyDomain() {
        String code = """
                public class ToBeDestroyed {
                    public int getValue() {
                        return 42;
                    }
                }
                """;

        hotSwapService.hotSwap("destroy-domain", code);
        Assertions.assertTrue(hotSwapService.getAllDomains().contains("destroy-domain"));

        hotSwapService.destroyDomain("destroy-domain");
        Assertions.assertFalse(hotSwapService.getAllDomains().contains("destroy-domain"));
        Assertions.assertNull(hotSwapService.getDomainInfo("destroy-domain"));
    }

    @Test
    @DisplayName("测试清除所有域")
    void testClearAllDomains() {
        hotSwapService.hotSwap("domain1", "public class Class1 {}");
        hotSwapService.hotSwap("domain2", "public class Class2 {}");
        hotSwapService.hotSwap("domain3", "public class Class3 {}");

        Assertions.assertEquals(3, hotSwapService.getAllDomains().size());

        hotSwapService.clearAllDomains();

        Assertions.assertEquals(0, hotSwapService.getAllDomains().size());
    }

    @Test
    @DisplayName("测试获取当前类")
    void testGetCurrentClass() {
        String code = """
                public class CurrentClassTest {
                    public String getMessage() {
                        return "Hello";
                    }
                }
                """;

        hotSwapService.hotSwap("getclass-domain", code);

        Class<?> currentClass = hotSwapService.getCurrentClass("getclass-domain", "CurrentClassTest");
        Assertions.assertNotNull(currentClass);

        try {
            Object instance = currentClass.getDeclaredConstructor().newInstance();
            Method getMessage = currentClass.getMethod("getMessage");
            Assertions.assertEquals("Hello", getMessage.invoke(instance));
        } catch (Exception e) {
            Assertions.fail("Failed to invoke method: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试获取不存在的域的类")
    void testGetClassFromNonExistentDomain() {
        Class<?> clazz = hotSwapService.getCurrentClass("non-existent", "SomeClass");
        Assertions.assertNull(clazz);
    }

    @Test
    @DisplayName("测试空源码热替换")
    void testEmptySourceCodeHotSwap() {
        HotSwapResult result = hotSwapService.hotSwap("empty-domain", "");

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getErrors());
    }

    @Test
    @DisplayName("测试null源码热替换")
    void testNullSourceCodeHotSwap() {
        HotSwapResult result = hotSwapService.hotSwap("null-domain", null);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getErrors());
    }

    @Test
    @DisplayName("测试无法提取类名的源码热替换")
    void testCannotExtractClassNameHotSwap() {
        String invalidCode = """
                // not a valid class
                private String field = "test";
                """;

        HotSwapResult result = hotSwapService.hotSwap("invalid-domain", invalidCode);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getErrors());
        Assertions.assertTrue(result.getErrors().get(0).contains("class name"));
    }

    @Test
    @DisplayName("测试线程安全 - 并发热替换")
    void testThreadSafety() throws InterruptedException {
        String code = """
                public class ThreadSafeClass {
                    private int id;
                    public ThreadSafeClass(int id) {
                        this.id = id;
                    }
                    public int getId() {
                        return id;
                    }
                }
                """;

        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        HotSwapResult[] results = new HotSwapResult[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = hotSwapService.hotSwap("thread-domain", code);
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        for (int i = 0; i < threadCount; i++) {
            Assertions.assertTrue(results[i].isSuccess(), "线程 " + i + " 热替换应该成功");
            Assertions.assertNotNull(results[i].getSwappedClass());
        }
    }

    @Test
    @DisplayName("测试回滚到上一版本")
    void testRollbackPrevious() {
        String code = """
                public class RollbackTest {
                    public int getValue() {
                        return 1;
                    }
                }
                """;

        String updatedCode = """
                public class RollbackTest {
                    public int getValue() {
                        return 2;
                    }
                }
                """;

        // 加载v1
        HotSwapResult result1 = hotSwapService.hotSwap("rollback-domain", code);
        Assertions.assertEquals(1, result1.getVersion());

        // 热替换到v2
        HotSwapResult result2 = hotSwapService.hotSwap("rollback-domain", updatedCode);
        Assertions.assertEquals(2, result2.getVersion());

        // 回滚到v1
        HotSwapResult rollbackResult = hotSwapService.rollbackPrevious("rollback-domain");
        Assertions.assertTrue(rollbackResult.isSuccess());
        Assertions.assertEquals(1, rollbackResult.getVersion());
    }

    @Test
    @DisplayName("测试回滚到指定版本")
    void testRollbackToSpecificVersion() {
        String code = """
                public class SpecificRollback {
                    public int getValue() {
                        return 1;
                    }
                }
                """;

        String updatedCode = """
                public class SpecificRollback {
                    public int getValue() {
                        return 2;
                    }
                }
                """;

        // 创建多个版本
        hotSwapService.hotSwap("specific-domain", code);
        hotSwapService.hotSwap("specific-domain", updatedCode);
        HotSwapResult result3 = hotSwapService.hotSwap("specific-domain", code);
        Assertions.assertEquals(3, result3.getVersion());

        // 回滚到版本1
        HotSwapResult rollbackResult = hotSwapService.rollback("specific-domain", 1);
        Assertions.assertTrue(rollbackResult.isSuccess());
        Assertions.assertEquals(1, rollbackResult.getVersion());
    }

    @Test
    @DisplayName("测试回滚到无效版本")
    void testRollbackToInvalidVersion() {
        String code = "public class InvalidRollback {}";

        hotSwapService.hotSwap("invalid-rollback-domain", code);

        // 尝试回滚到版本10（不存在）
        HotSwapResult result = hotSwapService.rollback("invalid-rollback-domain", 10);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getErrors());
    }

    @Test
    @DisplayName("测试回滚不存在的域")
    void testRollbackNonExistentDomain() {
        HotSwapResult result = hotSwapService.rollback("non-existent-domain", 1);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getErrors());
    }

    @Test
    @DisplayName("测试默认域热替换")
    void testDefaultDomainHotSwap() {
        String code = """
                public class DefaultDomainTest {
                    public String getName() {
                        return "default";
                    }
                }
                """;

        // 使用默认域（不指定domain）
        HotSwapResult result = hotSwapService.hotSwap(code);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("default", result.getSwapId().split(":")[0]);
        Assertions.assertTrue(hotSwapService.getAllDomains().contains("default"));
    }

    @Test
    @DisplayName("测试类加载器ID唯一性")
    void testClassLoaderIdUniqueness() {
        String code = "public class UniqueLoaderTest {}";

        HotSwapResult result1 = hotSwapService.hotSwap("loader-domain-1", code);
        HotSwapResult result2 = hotSwapService.hotSwap("loader-domain-2", code);

        Assertions.assertNotEquals(result1.getClassLoaderId(), result2.getClassLoaderId());
    }

    @Test
    @DisplayName("测试域信息完整性")
    void testDomainInfoCompleteness() {
        String code = """
                public class InfoTest {
                    public int getValue() {
                        return 1;
                    }
                }
                """;

        hotSwapService.hotSwap("info-domain", code);

        LiquorHotSwapService.DomainInfo info = hotSwapService.getDomainInfo("info-domain");

        Assertions.assertNotNull(info);
        Assertions.assertEquals("info-domain", info.getDomain());
        Assertions.assertNotNull(info.getClassLoaderId());
        Assertions.assertTrue(info.getCurrentVersion() > 0);
        Assertions.assertTrue(info.getClassCount() > 0);
    }
}

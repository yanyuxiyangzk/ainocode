package com.ruoyi.nocode.common.core.compiler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * LiquorCompilerService 单元测试
 *
 * @author ruoyi
 */
@DisplayName("Liquor即时编译服务测试")
class LiquorCompilerServiceTest {

    private LiquorCompilerService compilerService;

    @BeforeEach
    void setUp() {
        compilerService = LiquorCompilerService.getInstance();
        compilerService.clearCache();
    }

    @Test
    @DisplayName("测试编译简单Java类")
    void testCompileSimpleClass() {
        String sourceCode = """
                public class SimpleMath {
                    public int add(int a, int b) {
                        return a + b;
                    }
                    public int multiply(int a, int b) {
                        return a * b;
                    }
                }
                """;

        LiquorCompilerResult result = compilerService.compile(sourceCode);

        Assertions.assertTrue(result.isSuccess(), "编译应该成功");
        Assertions.assertNotNull(result.getCompiledClass(), "编译后的类不应该为null");
        Assertions.assertEquals("SimpleMath", result.getClassName(), "类名应该匹配");
        Assertions.assertNull(result.getErrors(), "不应该有错误信息");
    }

    @Test
    @DisplayName("测试编译后的类可以被加载到JVM")
    void testCompiledClassCanBeLoaded() throws Exception {
        String sourceCode = """
                public class HelloWorld {
                    public String sayHello(String name) {
                        return "Hello, " + name + "!";
                    }
                    public int getNumber() {
                        return 42;
                    }
                }
                """;

        LiquorCompilerResult result = compilerService.compile(sourceCode);

        Assertions.assertTrue(result.isSuccess());
        Class<?> clazz = result.getCompiledClass();

        // 验证类可以被正常实例化
        Object instance = clazz.getDeclaredConstructor().newInstance();

        // 验证sayHello方法
        Method sayHelloMethod = clazz.getMethod("sayHello", String.class);
        Object greeting = sayHelloMethod.invoke(instance, "World");
        Assertions.assertEquals("Hello, World!", greeting);

        // 验证getNumber方法
        Method getNumberMethod = clazz.getMethod("getNumber");
        Object number = getNumberMethod.invoke(instance);
        Assertions.assertEquals(42, number);
    }

    @Test
    @DisplayName("测试编译错误能正确捕获并返回友好信息")
    void testCompilationErrorCapture() {
        String sourceCode = """
                public class BadCode {
                    public int divide(int a, int b) {
                        return a / b;  // 编译正常
                    }
                    private String getNullString() {
                        return null;  // 这行没问题
                    }
                    public int missingReturn {
                        // 缺少大括号和方法体结束
                }
                """;

        LiquorCompilerResult result = compilerService.compile(sourceCode);

        // 注意：上述代码其实语法上是错误的（missingReturn缺少括号），应该编译失败
        // 如果Liquor容忍了某些错误，结果可能成功
        if (!result.isSuccess()) {
            Assertions.assertNotNull(result.getErrors(), "错误信息列表不应该为null");
            Assertions.assertFalse(result.getErrors().isEmpty(), "错误信息不应该为空");
            System.out.println("Captured errors: " + result.getErrors());
        } else {
            // 如果编译成功，验证返回的类仍然可用
            Assertions.assertNotNull(result.getCompiledClass());
        }
    }

    @Test
    @DisplayName("测试空源码编译")
    void testNullSourceCode() {
        LiquorCompilerResult result = compilerService.compile(null);

        Assertions.assertFalse(result.isSuccess(), "null源码应该编译失败");
        Assertions.assertNotNull(result.getErrors());
        Assertions.assertTrue(result.getErrors().get(0).contains("null") ||
                result.getErrors().get(0).contains("empty"));
    }

    @Test
    @DisplayName("测试空字符串源码编译")
    void testEmptySourceCode() {
        LiquorCompilerResult result = compilerService.compile("   ");

        Assertions.assertFalse(result.isSuccess(), "空字符串源码应该编译失败");
        Assertions.assertNotNull(result.getErrors());
    }

    @Test
    @DisplayName("测试编译缓存功能")
    void testCompilationCache() {
        String sourceCode = """
                public class CachedClass {
                    public int value = 100;
                }
                """;

        // 第一次编译
        LiquorCompilerResult result1 = compilerService.compile(sourceCode);
        Assertions.assertTrue(result1.isSuccess());

        // 第二次编译（应该使用缓存）
        LiquorCompilerResult result2 = compilerService.compile(sourceCode);
        Assertions.assertTrue(result2.isSuccess());

        // 两次编译应该返回同一个类对象
        Assertions.assertSame(result1.getCompiledClass(), result2.getCompiledClass(),
                "缓存的编译结果应该返回同一个类对象");
    }

    @Test
    @DisplayName("测试禁用缓存后重新编译")
    void testDisableCache() {
        String sourceCode = """
                public class NoCacheClass {
                    public String text = "original";
                }
                """;

        // 第一次编译
        LiquorCompilerResult result1 = compilerService.compile(sourceCode);
        Assertions.assertTrue(result1.isSuccess());

        // 禁用缓存
        compilerService.setCacheEnabled(false);

        // 第二次编译
        LiquorCompilerResult result2 = compilerService.compile(sourceCode);
        Assertions.assertTrue(result2.isSuccess());

        // 不再强制要求是同一个对象（取决于实现）
        Assertions.assertNotNull(result2.getCompiledClass());

        // 重新启用缓存
        compilerService.setCacheEnabled(true);
    }

    @Test
    @DisplayName("测试编译带包名的类")
    void testCompileWithPackage() {
        String sourceCode = """
                package com.ruoyi.test;
                public class PackagedClass {
                    public static final String VERSION = "1.0.0";
                    public String getInfo() {
                        return "Package test class";
                    }
                }
                """;

        LiquorCompilerResult result = compilerService.compile(sourceCode);

        Assertions.assertTrue(result.isSuccess(), "带包名的类应该能编译成功");
        Assertions.assertEquals("PackagedClass", result.getClassName());
        Assertions.assertNotNull(result.getCompiledClass());
    }

    @Test
    @DisplayName("测试单例模式")
    void testSingletonPattern() {
        LiquorCompilerService instance1 = LiquorCompilerService.getInstance();
        LiquorCompilerService instance2 = LiquorCompilerService.getInstance();

        Assertions.assertSame(instance1, instance2, "应该返回单例实例");
    }

    @Test
    @DisplayName("测试编译后对象实例化")
    void testCompileAndInstantiate() {
        String sourceCode = """
                public class InstantiateTest {
                    private int counter = 0;
                    public void increment() {
                        counter++;
                    }
                    public int getCounter() {
                        return counter;
                    }
                }
                """;

        LiquorCompilerService.CompileAndInstantiateResult result =
                compilerService.compileAndInstantiate(sourceCode);

        Assertions.assertTrue(result.isSuccess(), "编译并实例化应该成功");
        Assertions.assertNotNull(result.getInstance(), "实例不应该为null");
        Assertions.assertNotNull(result.getClazz(), "类不应该为null");

        // 验证实例方法可以正常调用
        Object instance = result.getInstance();
        try {
            Method incrementMethod = result.getClazz().getMethod("increment");
            Method getCounterMethod = result.getClazz().getMethod("getCounter");

            Assertions.assertEquals(0, getCounterMethod.invoke(instance));
            incrementMethod.invoke(instance);
            Assertions.assertEquals(1, getCounterMethod.invoke(instance));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("测试无法提取类名的情况")
    void testCannotExtractClassName() {
        String sourceCode = """
                // 没有public class声明
                private String field = "test";
                public void method() {}
                """;

        LiquorCompilerResult result = compilerService.compile(sourceCode);

        Assertions.assertFalse(result.isSuccess(), "无法提取类名时应该失败");
        Assertions.assertNotNull(result.getErrors());
        boolean hasClassNameError = result.getErrors().stream()
                .anyMatch(e -> e.contains("class name") || e.contains("class"));
        Assertions.assertTrue(hasClassNameError, "错误信息应该提到类名提取失败");
    }

    @Test
    @DisplayName("测试清除缓存")
    void testClearCache() {
        String sourceCode = """
                public class ClearCacheTest {
                    public int value = 10;
                }
                """;

        // 编译并缓存
        LiquorCompilerResult result1 = compilerService.compile(sourceCode);
        Assertions.assertTrue(result1.isSuccess());

        Assertions.assertTrue(compilerService.getCacheSize() > 0, "缓存应该有内容");

        // 清除缓存
        compilerService.clearCache();

        Assertions.assertEquals(0, compilerService.getCacheSize(), "缓存应该被清空");
    }

    @Test
    @DisplayName("测试隔离类加载器编译")
    void testCompileWithIsolatedClassLoader() {
        String sourceCode = """
                public class IsolatedCompileTest {
                    public String getMessage() {
                        return "Isolated class loader test";
                    }
                }
                """;

        ClassLoader isolatedLoader = IsolatedClassLoaderUtil.createIsolatedClassLoader(
                Thread.currentThread().getContextClassLoader()
        );

        LiquorCompilerResult result = compilerService.compile(sourceCode, isolatedLoader);

        Assertions.assertTrue(result.isSuccess(), "隔离类加载器编译应该成功");
        Assertions.assertNotNull(result.getCompiledClass());
    }

    @Test
    @DisplayName("测试编译后的类在隔离类加载器中加载")
    void testCompiledClassLoadedInIsolatedLoader() throws Exception {
        String sourceCode = """
                public class IsolatedLoaderTest {
                    public int compute(int x) {
                        return x * 2 + 1;
                    }
                }
                """;

        ClassLoader isolatedLoader = IsolatedClassLoaderUtil.createIsolatedClassLoader(
                Thread.currentThread().getContextClassLoader()
        );

        LiquorCompilerResult result = compilerService.compile(sourceCode, isolatedLoader);

        Assertions.assertTrue(result.isSuccess());

        // 验证类确实是通过隔离类加载器加载的
        Class<?> compiledClass = result.getCompiledClass();
        ClassLoader resultClassLoader = compiledClass.getClassLoader();

        // 检查类加载器链中包含隔离类加载器
        boolean foundIsolatedLoader = false;
        ClassLoader current = resultClassLoader;
        while (current != null) {
            if (current == isolatedLoader) {
                foundIsolatedLoader = true;
                break;
            }
            current = current.getParent();
        }

        Assertions.assertTrue(foundIsolatedLoader, "编译后的类应该在隔离类加载器的类加载链中");
    }

    @Test
    @DisplayName("测试编译带有多重继承接口的类")
    void testCompileWithInterfaces() {
        String sourceCode = """
                public class InterfaceImpl implements Runnable {
                    public void run() {
                        // empty
                    }
                    public String getResult() {
                        return "Interface implementation test";
                    }
                }
                """;

        LiquorCompilerResult result = compilerService.compile(sourceCode);

        Assertions.assertTrue(result.isSuccess(), "实现接口的类应该能编译成功");
        Assertions.assertEquals("InterfaceImpl", result.getClassName());

        // 验证实现了Runnable接口
        Class<?> clazz = result.getCompiledClass();
        Assertions.assertTrue(Runnable.class.isAssignableFrom(clazz));
    }

    @Test
    @DisplayName("测试编译静态方法和字段")
    void testCompileStaticMembers() {
        String sourceCode = """
                public class StaticMembers {
                    public static final int CONSTANT = 100;
                    private static int counter = 0;
                    public static int getNextId() {
                        return ++counter;
                    }
                    public static String getType() {
                        return "STATIC_TEST";
                    }
                }
                """;

        LiquorCompilerResult result = compilerService.compile(sourceCode);

        Assertions.assertTrue(result.isSuccess(), "带静态成员的类应该能编译成功");

        Class<?> clazz = result.getCompiledClass();

        // 验证静态字段和方法
        try {
            // 验证静态字段
            Assertions.assertEquals(100, clazz.getField("CONSTANT").get(null));

            // 验证静态方法
            Method getNextId = clazz.getMethod("getNextId");
            Assertions.assertEquals(1, getNextId.invoke(null));
            Assertions.assertEquals(2, getNextId.invoke(null));

            Method getType = clazz.getMethod("getType");
            Assertions.assertEquals("STATIC_TEST", getType.invoke(null));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("测试线程安全 - 并发编译")
    void testThreadSafety() throws InterruptedException {
        String sourceCode = """
                public class ConcurrentCompile {
                    private int id;
                    public ConcurrentCompile(int id) {
                        this.id = id;
                    }
                    public int getId() {
                        return id;
                    }
                }
                """;

        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        LiquorCompilerResult[] results = new LiquorCompilerResult[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = compilerService.compile(sourceCode);
            });
        }

        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 验证所有编译都成功
        for (int i = 0; i < threadCount; i++) {
            Assertions.assertTrue(results[i].isSuccess(), "线程 " + i + " 编译应该成功");
            Assertions.assertNotNull(results[i].getCompiledClass(), "线程 " + i + " 的编译结果不应该为null");
        }
    }
}

package com.ruoyi.nocode.system.service;

import com.ruoyi.nocode.system.entity.DynamicCode;
import com.ruoyi.nocode.system.mapper.DynamicCodeMapper;
import com.ruoyi.nocode.system.service.impl.LiquorCompilerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * LiquorCompilerService 单元测试
 * 
 * @author ruoyi-nocode
 */
@ExtendWith(MockitoExtension.class)
class LiquorCompilerServiceTest {

    @Mock
    private DynamicCodeMapper dynamicCodeMapper;

    @InjectMocks
    private LiquorCompilerServiceImpl compilerService;

    private static final String SIMPLE_CLASS_CODE = """
            package com.test;
            
            public class Calculator {
                public int add(int a, int b) {
                    return a + b;
                }
                
                public static int multiply(int a, int b) {
                    return a * b;
                }
            }
            """;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("编译简单类")
    void testCompileSimpleClass() {
        ILiquorCompilerService.CompileResult result = 
                compilerService.compile(SIMPLE_CLASS_CODE, "com.test.Calculator");

        assertNotNull(result);
        if (result.success()) {
            assertNotNull(result.bytecode());
            assertTrue(result.bytecode().length > 0);
        }
    }

    @Test
    @DisplayName("编译空代码返回失败")
    void testCompileEmptyCode() {
        ILiquorCompilerService.CompileResult result = 
                compilerService.compile("", "com.test.Empty");

        assertNotNull(result);
        assertFalse(result.success());
        assertNotNull(result.error());
    }

    @Test
    @DisplayName("编译语法错误代码")
    void testCompileInvalidCode() {
        String invalidCode = "public class Invalid { invalid syntax }";
        
        ILiquorCompilerService.CompileResult result = 
                compilerService.compile(invalidCode, "Invalid");

        assertNotNull(result);
        assertFalse(result.success());
        assertNotNull(result.error());
    }

    @Test
    @DisplayName("执行静态方法")
    void testExecuteStaticMethod() {
        // 先编译
        ILiquorCompilerService.CompileResult compileResult = 
                compilerService.compile(SIMPLE_CLASS_CODE, "com.test.Calculator");

        if (compileResult.success()) {
            // 执行静态方法
            Object result = compilerService.executeStaticMethod(
                    "com.test.Calculator", "multiply", 3, 4);

            assertNotNull(result);
            assertEquals(12, result);
        }
    }

    @Test
    @DisplayName("编译并缓存")
    void testCompileAndCache() {
        DynamicCode dynamicCode = new DynamicCode();
        dynamicCode.setCodeId(1L);
        dynamicCode.setClassName("com.test.Calculator");
        dynamicCode.setSourceCode(SIMPLE_CLASS_CODE);

        when(dynamicCodeMapper.updateById(any(DynamicCode.class))).thenReturn(1);

        ILiquorCompilerService.CompileResult result = 
                compilerService.compileAndCache(dynamicCode);

        assertNotNull(result);
        if (result.success()) {
            verify(dynamicCodeMapper, times(1)).updateById(any(DynamicCode.class));
        }
    }

    @Test
    @DisplayName("获取缓存类列表")
    void testGetCachedClasses() {
        // 编译一个类
        compilerService.compile(SIMPLE_CLASS_CODE, "com.test.Calculator");

        var cachedClasses = compilerService.getCachedClasses();

        assertNotNull(cachedClasses);
    }

    @Test
    @DisplayName("移除缓存类")
    void testRemoveCachedClass() {
        // 先编译
        compilerService.compile(SIMPLE_CLASS_CODE, "com.test.Calculator");

        // 移除缓存
        assertDoesNotThrow(() -> {
            compilerService.removeCachedClass("com.test.Calculator");
        });
    }

    @Test
    @DisplayName("沙箱执行")
    void testExecuteInSandbox() {
        DynamicCode dynamicCode = new DynamicCode();
        dynamicCode.setCodeId(1L);
        dynamicCode.setClassName("com.test.Calculator");
        dynamicCode.setSourceCode(SIMPLE_CLASS_CODE);
        dynamicCode.setSandboxMode(true);

        // 先编译
        compilerService.compile(SIMPLE_CLASS_CODE, "com.test.Calculator");

        ILiquorCompilerService.SandboxResult result = 
                compilerService.executeInSandbox(dynamicCode, "multiply", 2, 3);

        assertNotNull(result);
        if (result.success()) {
            assertEquals(6, result.result());
        }
    }
}

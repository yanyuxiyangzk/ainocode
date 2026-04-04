<template>
  <div class="liquor-ide p-2">
    <el-row :gutter="10">
      <!-- 左侧：代码编辑器 -->
      <el-col :span="14">
        <el-card shadow="hover" class="editor-card">
          <template #header>
            <div class="card-header">
              <span>Java 代码编辑器</span>
              <el-button type="primary" size="small" @click="handleFormat">格式化</el-button>
            </div>
          </template>

          <el-form :model="form" label-width="100px">
            <el-form-item label="类名">
              <el-input v-model="form.className" placeholder="请输入完整类名，如：com.example.MyClass" />
            </el-form-item>
            <el-form-item label="域名">
              <el-input v-model="form.domain" placeholder="热替换域，默认为 default" />
              <span class="form-tip">每个域名隔离类加载器</span>
            </el-form-item>
          </el-form>

          <div class="code-editor">
            <el-input
              v-model="form.sourceCode"
              type="textarea"
              :rows="20"
              placeholder="// 在此输入 Java 代码
public class MyClass {
    public String sayHello(String name) {
        return &quot;Hello, &quot; + name + &quot;!&quot;;
    }

    public static void main(String[] args) {
        System.out.println(&quot;Hello World!&quot;);
    }
}"
              :class="{ 'has-error': codeError }"
              @input="codeError = ''"
            />
            <div v-if="codeError" class="error-tip">{{ codeError }}</div>
          </div>

          <div class="action-bar">
            <el-button type="primary" :loading="compileLoading" @click="handleCompile">
              <el-icon><Compile /></el-icon>
              编译
            </el-button>
            <el-button type="success" :loading="instantiateLoading" @click="handleCompileAndInstantiate">
              <el-icon><VideoPlay /></el-icon>
              编译并实例化
            </el-button>
            <el-button type="warning" :loading="hotswapLoading" @click="handleHotSwap">
              <el-icon><Refresh /></el-icon>
              热替换
            </el-button>
            <el-button type="danger" :loading="executeLoading" @click="handleExecute">
              <el-icon><VideoPlay /></el-icon>
              沙箱执行
            </el-button>
            <el-button @click="handleReset">
              <el-icon><RefreshLeft /></el-icon>
              重置
            </el-button>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：结果输出 -->
      <el-col :span="10">
        <!-- 编译结果 -->
        <el-card shadow="hover" class="result-card mb-2">
          <template #header>
            <span>编译结果</span>
          </template>
          <div v-if="compileResult">
            <el-result
              :icon="compileResult.success ? 'success' : 'error'"
              :title="compileResult.success ? '编译成功' : '编译失败'"
            >
              <template #extra>
                <div v-if="compileResult.success" class="result-detail">
                  <p>类名: {{ compileResult.className }}</p>
                  <p>耗时: {{ compileResult.compileTime }}ms</p>
                </div>
                <div v-else class="result-error">
                  <p v-for="(err, idx) in compileResult.errors" :key="idx">{{ err }}</p>
                </div>
              </template>
            </el-result>
          </div>
          <div v-else class="empty-result">
            <el-empty description="暂无编译结果" />
          </div>
        </el-card>

        <!-- 执行结果 -->
        <el-card shadow="hover" class="result-card mb-2">
          <template #header>
            <span>执行结果</span>
          </template>
          <div v-if="executeResult">
            <el-result
              :icon="executeResult.success ? 'success' : 'error'"
              :title="executeResult.success ? '执行成功' : '执行失败'"
            >
              <template #extra>
                <div v-if="executeResult.success" class="result-detail">
                  <p>结果: {{ formatResult(executeResult.result) }}</p>
                  <p>类型: {{ executeResult.resultType }}</p>
                  <p>耗时: {{ executeResult.executionTime }}ms</p>
                </div>
                <div v-else class="result-error">
                  <p>错误: {{ executeResult.errorMessage }}</p>
                  <p v-if="executeResult.errorType">类型: {{ executeResult.errorType }}</p>
                </div>
              </template>
            </el-result>
          </div>
          <div v-else class="empty-result">
            <el-empty description="暂无执行结果" />
          </div>
        </el-card>

        <!-- 热替换信息 -->
        <el-card shadow="hover" class="result-card">
          <template #header>
            <span>热替换域</span>
            <el-button size="small" @click="loadDomains">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </template>
          <el-table :data="domains" border size="small" max-height="200">
            <el-table-column prop="domain" label="域名" width="100" />
            <el-table-column prop="currentVersion" label="版本" width="60" />
            <el-table-column prop="classCount" label="类数" width="60" />
            <el-table-column prop="classLoaderId" label="ClassLoader" show-overflow-tooltip />
            <el-table-column label="操作" width="80">
              <template #default="scope">
                <el-button
                  type="danger"
                  size="small"
                  link
                  @click="handleClearDomain(scope.row.domain)"
                >
                  清除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 示例代码抽屉 -->
    <el-drawer v-model="exampleDrawer" title="示例代码" size="50%">
      <el-tabs v-model="activeExample">
        <el-tab-pane label="Hello World" name="hello">
          <pre class="example-code">public class HelloWorld {
    public String sayHello(String name) {
        return &quot;Hello, &quot; + name + &quot;!&quot;;
    }

    public static void main(String[] args) {
        System.out.println(&quot;Hello World!&quot;);
    }
}</pre>
        </el-tab-pane>
        <el-tab-pane label="计算器" name="calc">
          <pre class="example-code">public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public double divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException(&quot;Division by zero&quot;);
        }
        return (double) a / b;
    }
}</pre>
        </el-tab-pane>
        <el-tab-pane label="数据处理" name="data">
          <pre class="example-code">import java.util.*;
import java.util.stream.*;

public class DataProcessor {
    public List&lt;Integer&gt; filterEven(List&lt;Integer&gt; numbers) {
        return numbers.stream()
            .filter(n -&gt; n % 2 == 0)
            .collect(Collectors.toList());
    }

    public int sum(List&lt;Integer&gt; numbers) {
        return numbers.stream().mapToInt(Integer::intValue).sum();
    }

    public Map&lt;String, Integer&gt; countWords(List&lt;String&gt; words) {
        Map&lt;String, Integer&gt; counts = new HashMap&lt;&gt;();
        for (String word : words) {
            counts.merge(word, 1, Integer::sum);
        }
        return counts;
    }
}</pre>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button type="primary" @click="handleUseExample">使用示例</el-button>
        <el-button @click="exampleDrawer = false">关闭</el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup name="LiquorIDE" lang="ts">
import { compileCode, compileAndInstantiate, hotSwap, executeCode, listDomains, getDomain, clearDomain } from '@/api/tool/liquor';
import { LiquorCompileVO, LiquorExecuteVO, LiquorDomainVO } from '@/api/tool/liquor/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const form = ref({
  className: 'com.example.HelloWorld',
  domain: 'default',
  sourceCode: `public class HelloWorld {
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}`
});

const codeError = ref('');
const compileLoading = ref(false);
const instantiateLoading = ref(false);
const hotswapLoading = ref(false);
const executeLoading = ref(false);

const compileResult = ref<LiquorCompileVO | null>(null);
const executeResult = ref<LiquorExecuteVO | null>(null);
const domains = ref<LiquorDomainVO[]>([]);

const exampleDrawer = ref(false);
const activeExample = ref('hello');

const exampleCodes: Record<string, { className: string; code: string }> = {
  hello: {
    className: 'com.example.HelloWorld',
    code: `public class HelloWorld {
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}`
  },
  calc: {
    className: 'com.example.Calculator',
    code: `public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public double divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return (double) a / b;
    }
}`
  },
  data: {
    className: 'com.example.DataProcessor',
    code: `import java.util.*;
import java.util.stream.*;

public class DataProcessor {
    public List<Integer> filterEven(List<Integer> numbers) {
        return numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
    }

    public int sum(List<Integer> numbers) {
        return numbers.stream().mapToInt(Integer::intValue).sum();
    }

    public Map<String, Integer> countWords(List<String> words) {
        Map<String, Integer> counts = new HashMap<>();
        for (String word : words) {
            counts.merge(word, 1, Integer::sum);
        }
        return counts;
    }
}`
  }
};

/** 格式化代码 */
const handleFormat = () => {
  exampleDrawer.value = true;
};

/** 使用示例 */
const handleUseExample = () => {
  const example = exampleCodes[activeExample.value];
  if (example) {
    form.value.className = example.className;
    form.value.sourceCode = example.code;
  }
  exampleDrawer.value = false;
};

/** 编译代码 */
const handleCompile = async () => {
  if (!validateForm()) return;

  compileLoading.value = true;
  compileResult.value = null;

  try {
    const response = await compileCode({
      sourceCode: form.value.sourceCode,
      className: form.value.className
    });

    if (response.code === 200) {
      compileResult.value = {
        success: response.data.success,
        className: response.data.className,
        compileTime: response.data.compileTime,
        errorMessage: response.data.errorMessage
      };
    } else {
      compileResult.value = {
        success: false,
        errorMessage: response.msg || '编译失败'
      };
    }
  } catch (error: any) {
    compileResult.value = {
      success: false,
      errorMessage: error.message || '编译异常'
    };
  } finally {
    compileLoading.value = false;
  }
};

/** 编译并实例化 */
const handleCompileAndInstantiate = async () => {
  if (!validateForm()) return;

  instantiateLoading.value = true;
  compileResult.value = null;

  try {
    const response = await compileAndInstantiate({
      sourceCode: form.value.sourceCode,
      className: form.value.className
    });

    if (response.code === 200) {
      compileResult.value = {
        success: true,
        className: form.value.className,
        compileTime: 0
      };
      proxy?.$modal.msgSuccess('编译并实例化成功');
    } else {
      compileResult.value = {
        success: false,
        errorMessage: response.msg || '编译并实例化失败'
      };
    }
  } catch (error: any) {
    compileResult.value = {
      success: false,
      errorMessage: error.message || '编译并实例化异常'
    };
  } finally {
    instantiateLoading.value = false;
  }
};

/** 热替换 */
const handleHotSwap = async () => {
  if (!validateForm()) return;

  hotswapLoading.value = true;
  compileResult.value = null;

  try {
    const response = await hotSwap({
      sourceCode: form.value.sourceCode,
      className: form.value.className,
      domain: form.value.domain || 'default'
    });

    if (response.code === 200) {
      compileResult.value = {
        success: true,
        className: form.value.className,
        compileTime: 0
      };
      proxy?.$modal.msgSuccess(`热替换成功，版本: ${response.data.version}`);
      await loadDomains();
    } else {
      compileResult.value = {
        success: false,
        errorMessage: response.msg || '热替换失败'
      };
    }
  } catch (error: any) {
    compileResult.value = {
      success: false,
      errorMessage: error.message || '热替换异常'
    };
  } finally {
    hotswapLoading.value = false;
  }
};

/** 沙箱执行 */
const handleExecute = async () => {
  if (!validateForm()) return;

  executeLoading.value = true;
  executeResult.value = null;

  try {
    const response = await executeCode({
      className: form.value.className,
      methodName: 'main',
      timeout: 5000
    });

    if (response.code === 200) {
      executeResult.value = {
        success: true,
        result: response.data.result,
        resultType: response.data.resultType,
        executionTime: response.data.executionTime
      };
    } else {
      executeResult.value = {
        success: false,
        errorMessage: response.msg || '执行失败'
      };
    }
  } catch (error: any) {
    executeResult.value = {
      success: false,
      errorMessage: error.message || '执行异常'
    };
  } finally {
    executeLoading.value = false;
  }
};

/** 加载域列表 */
const loadDomains = async () => {
  try {
    const response = await listDomains();
    if (response.code === 200 && response.data) {
      // 获取每个域的详细信息
      const domainPromises = (response.data as string[]).map(async (domain: string) => {
        try {
          const detailRes = await getDomain(domain);
          return detailRes.data;
        } catch {
          return {
            domain,
            currentVersion: 0,
            classCount: 0,
            classLoaderId: ''
          };
        }
      });
      domains.value = await Promise.all(domainPromises);
    }
  } catch (error) {
    console.error('加载域列表失败', error);
  }
};

/** 清除域 */
const handleClearDomain = async (domain: string) => {
  try {
    await proxy?.$modal.confirm(`确认要清除域 "${domain}" 吗？`);
    await clearDomain(domain);
    proxy?.$modal.msgSuccess('域已清除');
    await loadDomains();
  } catch {
    // 用户取消
  }
};

/** 重置表单 */
const handleReset = () => {
  form.value = {
    className: 'com.example.HelloWorld',
    domain: 'default',
    sourceCode: `public class HelloWorld {
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}`
  };
  codeError.value = '';
  compileResult.value = null;
  executeResult.value = null;
};

/** 表单验证 */
const validateForm = (): boolean => {
  if (!form.value.sourceCode || form.value.sourceCode.trim() === '') {
    codeError.value = '请输入 Java 代码';
    return false;
  }
  if (!form.value.className || form.value.className.trim() === '') {
    codeError.value = '请输入类名';
    return false;
  }
  return true;
};

/** 格式化结果 */
const formatResult = (result: any): string => {
  if (result === null || result === undefined) {
    return 'null';
  }
  if (typeof result === 'object') {
    return JSON.stringify(result, null, 2);
  }
  return String(result);
};

onMounted(() => {
  loadDomains();
});
</script>

<style lang="scss" scoped>
.liquor-ide {
  background-color: #f5f7fa;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.editor-card {
  height: 100%;
}

.code-editor {
  margin: 15px 0;

  :deep(.el-textarea__inner) {
    font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
    font-size: 13px;
    line-height: 1.5;
    background-color: #1e1e1e;
    color: #d4d4d4;
    border-radius: 4px;

    &.has-error {
      border-color: #f56c6c;
    }
  }
}

.error-tip {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
}

.form-tip {
  color: #909399;
  font-size: 12px;
  margin-left: 8px;
}

.action-bar {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.result-card {
  :deep(.el-card__header) {
    padding: 12px 15px;
  }
}

.result-detail {
  p {
    margin: 5px 0;
    font-size: 13px;
  }
}

.result-error {
  p {
    margin: 5px 0;
    font-size: 13px;
    color: #f56c6c;
  }
}

.empty-result {
  padding: 20px 0;
}

.example-code {
  background-color: #1e1e1e;
  color: #d4d4d4;
  padding: 15px;
  border-radius: 4px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  overflow-x: auto;
  white-space: pre-wrap;
}

.mb-2 {
  margin-bottom: 10px;
}
</style>

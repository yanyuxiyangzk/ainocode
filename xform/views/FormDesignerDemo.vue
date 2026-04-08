<script setup lang="ts">
/**
 * FormDesignerDemo - Example page demonstrating Form Designer usage
 * 表单设计器示例页面
 */
import { ref, onMounted } from 'vue';
import { NButton, NSpace, NCard, NTabs, NTabPane, NResult } from 'naive-ui';
import FormDesigner from '@/components/form-designer/FormDesigner.vue';
import FormRenderer from '@/components/form-renderer/FormRenderer.vue';
import { demoSchema, simpleSchema } from '@/demo';
import type { FormSchema } from '@/types/form-schema';

const activeTab = ref('designer');
const currentSchema = ref<FormSchema | null>(null);
const previewMode = ref(false);

// Load demo schema
function loadDemoSchema() {
  currentSchema.value = { ...demoSchema };
}

// Load simple schema
function loadSimpleSchema() {
  currentSchema.value = { ...simpleSchema };
}

// Handle schema change from designer
function handleSchemaChange(schema: FormSchema) {
  currentSchema.value = schema;
}

// Export schema as JSON
function exportSchema() {
  if (!currentSchema.value) return;
  const json = JSON.stringify(currentSchema.value, null, 2);
  const blob = new Blob([json], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `${currentSchema.value.name || 'form-schema'}.json`;
  a.click();
  URL.revokeObjectURL(url);
}

// Import schema from JSON
async function importSchema() {
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = '.json';
  input.onchange = async (e) => {
    const file = (e.target as HTMLInputElement).files?.[0];
    if (!file) return;
    try {
      const text = await file.text();
      const parsed = JSON.parse(text);
      currentSchema.value = parsed;
    } catch (err) {
      console.error('Failed to import schema:', err);
    }
  };
  input.click();
}

onMounted(() => {
  loadDemoSchema();
});
</script>

<template>
  <div class="form-designer-demo">
    <NCard title="表单设计器示例">
      <NTabs v-model:value="activeTab" type="line">
        <!-- Designer Tab -->
        <NTabPane name="designer" tab="设计器">
          <NSpace vertical :size="16">
            <!-- Action Buttons -->
            <NSpace>
              <NButton @click="loadDemoSchema">加载演示Schema</NButton>
              <NButton @click="loadSimpleSchema">加载简单Schema</NButton>
              <NButton @click="exportSchema" type="primary">导出JSON</NButton>
              <NButton @click="importSchema">导入JSON</NButton>
              <NButton @click="previewMode = !previewMode" type="info">
                {{ previewMode ? '编辑模式' : '预览模式' }}
              </NButton>
            </NSpace>

            <!-- Designer or Preview -->
            <div v-if="currentSchema" class="demo-content">
              <FormRenderer
                v-if="previewMode"
                :schema="currentSchema"
                @update:model-value="(val) => console.log('Form value:', val)"
              />
              <FormDesigner
                v-else
                :initial-schema="currentSchema"
                @schema-change="handleSchemaChange"
              />
            </div>

            <NResult
              v-else
              status="info"
              title="暂无Schema"
              description="点击上方按钮加载示例Schema"
            />
          </NSpace>
        </NTabPane>

        <!-- Schema Preview Tab -->
        <NTabPane name="schema" tab="Schema预览">
          <div v-if="currentSchema" class="schema-preview">
            <pre>{{ JSON.stringify(currentSchema, null, 2) }}</pre>
          </div>
          <NResult
            v-else
            status="warning"
            title="暂无Schema"
            description="请先加载Schema"
          />
        </NTabPane>

        <!-- API Usage Tab -->
        <NTabPane name="api" tab="API用法">
          <NCard title="基本用法">
            <pre class="code-block">
import FormDesigner from '@/components/form-designer/FormDesigner.vue';
import FormRenderer from '@/components/form-renderer/FormRenderer.vue';
import { demoSchema } from '@/demo';

&lt;FormDesigner
  :initial-schema="schema"
  @schema-change="handleSchemaChange"
/&gt;

&lt;FormRenderer
  :schema="schema"
  :model-value="formData"
  @update:model-value="handleUpdate"
/&gt;
            </pre>
          </NCard>

          <NCard title="Schema结构" style="margin-top: 16px;">
            <pre class="code-block">
interface FormSchema {
  id: string;
  name: string;
  version: string;
  description?: string;
  props: FormProps;
  widgets: WidgetConfig[];
}

interface WidgetConfig {
  id: string;
  type: symbol;  // Symbol for type-safe identification
  widgetName: string;
  props: Record&lt;string, any&gt;;
  children?: WidgetConfig[];
}
            </pre>
          </NCard>
        </NTabPane>
      </NTabs>
    </NCard>
  </div>
</template>

<style scoped>
.form-designer-demo {
  width: 100%;
  height: 100%;
  padding: 16px;
  background: #f5f5f5;
}

.demo-content {
  min-height: 500px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.schema-preview {
  background: #fafafa;
  padding: 16px;
  border-radius: 8px;
  max-height: 600px;
  overflow: auto;
}

.schema-preview pre {
  margin: 0;
  font-size: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}

.code-block {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
  font-size: 13px;
  line-height: 1.6;
  overflow-x: auto;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>

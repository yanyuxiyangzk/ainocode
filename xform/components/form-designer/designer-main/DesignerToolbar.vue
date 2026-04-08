<script setup lang="ts">
import { computed } from 'vue';
import {
  NButton,
  NButtonGroup,
  NSpace,
  NDropdown,
  NIcon,
  NTooltip,
  NSelect,
} from 'naive-ui';
import {
  UndoOutlined,
  RedoOutlined,
  CopyOutlined,
  ScissorOutlined,
  SnippetsOutlined,
  SaveOutlined,
  EyeOutlined,
  CodeOutlined,
  EditOutlined,
  ClearOutlined,
  DownloadOutlined,
  UploadOutlined,
  SettingOutlined,
} from '@vicons/antd';
import { useDesigner } from '@/composables/useDesigner';
import { useHistoryStore } from '@/stores';
import type { DesignerMode } from '@/types/designer-context';

const { designerMode, setDesignerMode, undo, redo, canUndo, canRedo, hasClipboard, copyWidget, cutWidget, pasteWidget, schema, isDirty } = useDesigner();
const historyStore = useHistoryStore();

const modeOptions = [
  { label: '设计模式', value: 'design' },
  { label: '预览模式', value: 'preview' },
  { label: 'JSON模式', value: 'json' },
];

const handleUndo = () => {
  const prevState = historyStore.undo();
  if (prevState && schema.value) {
    schema.value = prevState;
  }
};

const handleRedo = () => {
  const nextState = historyStore.redo();
  if (nextState && schema.value) {
    schema.value = nextState;
  }
};

const handleExport = () => {
  if (!schema.value) return;
  const json = JSON.stringify(schema.value, null, 2);
  const blob = new Blob([json], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `${schema.value.name || 'form-schema'}.json`;
  a.click();
  URL.revokeObjectURL(url);
};

const handleImport = () => {
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = '.json';
  input.onchange = async (e) => {
    const file = (e.target as HTMLInputElement).files?.[0];
    if (!file) return;
    try {
      const text = await file.text();
      const parsed = JSON.parse(text);
      if (schema.value) {
        schema.value = parsed;
      }
    } catch (err) {
      console.error('Failed to import schema:', err);
    }
  };
  input.click();
};

const handleClear = () => {
  if (confirm('确定要清空所有组件吗？')) {
    if (schema.value) {
      schema.value.widgets = [];
    }
  }
};
</script>

<template>
  <div class="designer-toolbar">
    <div class="toolbar-left">
      <NSpace :size="12">
        <!-- Undo/Redo -->
        <NTooltip trigger="hover">
          <template #trigger>
            <NButton
              quaternary
              circle
              :disabled="!canUndo"
              @click="handleUndo"
            >
              <template #icon>
                <NIcon :component="UndoOutlined" />
              </template>
            </NButton>
          </template>
          撤销 (Ctrl+Z)
        </NTooltip>

        <NTooltip trigger="hover">
          <template #trigger>
            <NButton
              quaternary
              circle
              :disabled="!canRedo"
              @click="handleRedo"
            >
              <template #icon>
                <NIcon :component="RedoOutlined" />
              </template>
            </NButton>
          </template>
          重做 (Ctrl+Y)
        </NTooltip>

        <NDivider vertical />

        <!-- Clipboard Operations -->
        <NTooltip trigger="hover">
          <template #trigger>
            <NButton quaternary circle :disabled="!schema?.selectedWidgetId" @click="copyWidget(schema!.selectedWidget!)">
              <template #icon>
                <NIcon :component="CopyOutlined" />
              </template>
            </NButton>
          </template>
          复制 (Ctrl+C)
        </NTooltip>

        <NTooltip trigger="hover">
          <template #trigger>
            <NButton quaternary circle :disabled="!schema?.selectedWidgetId" @click="cutWidget(schema!.selectedWidget!)">
              <template #icon>
                <NIcon :component="ScissorOutlined" />
              </template>
            </NButton>
          </template>
          剪切 (Ctrl+X)
        </NTooltip>

        <NTooltip trigger="hover">
          <template #trigger>
            <NButton quaternary circle :disabled="!hasClipboard" @click="pasteWidget(null)">
              <template #icon>
                <NIcon :component="SnippetsOutlined" />
              </template>
            </NButton>
          </template>
          粘贴 (Ctrl+V)
        </NTooltip>

        <NDivider vertical />

        <!-- Import/Export -->
        <NTooltip trigger="hover">
          <template #trigger>
            <NButton quaternary circle @click="handleImport">
              <template #icon>
                <NIcon :component="UploadOutlined" />
              </template>
            </NButton>
          </template>
          导入JSON
        </NTooltip>

        <NTooltip trigger="hover">
          <template #trigger>
            <NButton quaternary circle @click="handleExport">
              <template #icon>
                <NIcon :component="DownloadOutlined" />
              </template>
            </NButton>
          </template>
          导出JSON
        </NTooltip>

        <NTooltip trigger="hover">
          <template #trigger>
            <NButton quaternary circle @click="handleClear">
              <template #icon>
                <NIcon :component="ClearOutlined" />
              </template>
            </NButton>
          </template>
          清空
        </NTooltip>
      </NSpace>
    </div>

    <div class="toolbar-center">
      <NSelect
        :value="designerMode"
        :options="modeOptions"
        size="small"
        style="width: 120px;"
        @update:value="(val: DesignerMode) => setDesignerMode(val)"
      />
    </div>

    <div class="toolbar-right">
      <NSpace :size="12">
        <NTooltip trigger="hover">
          <template #trigger>
            <NButton
              quaternary
              circle
              type="error"
              @click="handleClear"
            >
              <template #icon>
                <NIcon :component="SettingOutlined" />
              </template>
            </NButton>
          </template>
          表单设置
        </NTooltip>
      </NSpace>
    </div>
  </div>
</template>

<style scoped>
.designer-toolbar {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: #fff;
  border-bottom: 1px solid var(--border-color, #d9d9d9);
}

.toolbar-left,
.toolbar-center,
.toolbar-right {
  display: flex;
  align-items: center;
}
</style>

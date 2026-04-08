<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { NModal, NButton, NSpace, NInput, NAlert, NScrollbar } from 'naive-ui';

const props = defineProps<{
  show: boolean;
  value: string;
  title?: string;
  readOnly?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:show', value: boolean): void;
  (e: 'confirm', value: string): void;
  (e: 'cancel'): void;
}>();

const internalValue = ref(props.value);
const errorMessage = ref<string | null>(null);

const visible = computed({
  get: () => props.show,
  set: (val) => emit('update:show', val),
});

function validateJson(jsonString: string): boolean {
  try {
    JSON.parse(jsonString);
    errorMessage.value = null;
    return true;
  } catch (e) {
    errorMessage.value = `JSON格式错误: ${(e as Error).message}`;
    return false;
  }
}

function handleConfirm() {
  if (!validateJson(internalValue.value)) {
    return;
  }
  emit('confirm', internalValue.value);
  visible.value = false;
}

function handleCancel() {
  emit('cancel');
  visible.value = false;
}

function handleFormat() {
  try {
    const parsed = JSON.parse(internalValue.value);
    internalValue.value = JSON.stringify(parsed, null, 2);
    errorMessage.value = null;
  } catch (e) {
    errorMessage.value = `格式化失败: ${(e as Error).message}`;
  }
}

function handleCompress() {
  try {
    const parsed = JSON.parse(internalValue.value);
    internalValue.value = JSON.stringify(parsed);
    errorMessage.value = null;
  } catch (e) {
    errorMessage.value = `压缩失败: ${(e as Error).message}`;
  }
}

function handleCopy() {
  navigator.clipboard.writeText(internalValue.value);
}

watch(() => props.value, (newVal) => {
  internalValue.value = newVal;
});

watch(() => props.show, (newVal) => {
  if (newVal) {
    internalValue.value = props.value;
    errorMessage.value = null;
  }
});
</script>

<template>
  <NModal
    v-model:show="visible"
    preset="card"
    :title="title || 'JSON 编辑器'"
    style="width: 900px; max-width: 95vw;"
    :bordered="false"
  >
    <div class="json-editor">
      <div class="json-editor__toolbar">
        <NSpace>
          <NButton size="small" @click="handleFormat">格式化</NButton>
          <NButton size="small" @click="handleCompress">压缩</NButton>
          <NButton size="small" @click="handleCopy">复制</NButton>
        </NSpace>
      </div>

      <NAlert v-if="errorMessage" type="error" style="margin: 12px 0;">
        {{ errorMessage }}
      </NAlert>

      <div class="json-editor__content">
        <NInput
          v-model:value="internalValue"
          type="textarea"
          :read-only="readOnly"
          :rows="20"
          placeholder="请输入 JSON 内容..."
          class="json-editor__textarea"
          :status="errorMessage ? 'error' : undefined"
        />
      </div>

      <div class="json-editor__footer">
        <NSpace justify="end">
          <NButton @click="handleCancel">取消</NButton>
          <NButton type="primary" @click="handleConfirm">确认</NButton>
        </NSpace>
      </div>
    </div>
  </NModal>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;
@use '@/styles/mixins' as *;

.json-editor {
  @include panel-base;
  padding: 0;

  &__toolbar {
    padding: $spacing-sm $spacing-md;
    border-bottom: 1px solid $border-color;
    background: #fafafa;
  }

  &__content {
    padding: $spacing-md;
    min-height: 400px;
    max-height: 60vh;
    overflow: hidden;

    :deep(.n-input) {
      height: 100%;

      .n-input__input-el {
        font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
        font-size: 13px;
        line-height: 1.6;
      }
    }
  }

  &__footer {
    padding: $spacing-md;
    border-top: 1px solid $border-color;
    background: #fafafa;
  }

  &__textarea {
    height: 100%;

    :deep(textarea) {
      font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
      font-size: 13px;
      line-height: 1.6;
    }
  }
}
</style>

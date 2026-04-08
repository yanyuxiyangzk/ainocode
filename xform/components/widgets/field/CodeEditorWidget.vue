<script setup lang="ts">
import { ref } from 'vue';

const props = defineProps({
  value: {
    type: String,
    default: '',
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  language: {
    type: String,
    default: 'javascript',
  },
  height: {
    type: Number,
    default: 300,
  },
  placeholder: {
    type: String,
    default: '// Enter your code here',
  },
});

const emit = defineEmits<{
  (e: 'update:value', value: string): void;
}>();

const textareaRef = ref<HTMLTextAreaElement | null>(null);

function handleInput(event: Event) {
  const target = event.target as HTMLTextAreaElement;
  emit('update:value', target.value);
}

function handleKeyDown(event: KeyboardEvent) {
  // Basic tab support
  if (event.key === 'Tab') {
    event.preventDefault();
    const target = event.target as HTMLTextAreaElement;
    const start = target.selectionStart;
    const end = target.selectionEnd;
    const value = target.value;
    target.value = value.substring(0, start) + '  ' + value.substring(end);
    target.selectionStart = target.selectionEnd = start + 2;
    emit('update:value', target.value);
  }
}
</script>

<template>
  <div class="code-editor-wrapper">
    <div class="code-editor-header">
      <span>{{ language }}</span>
    </div>
    <textarea
      ref="textareaRef"
      :value="value"
      :disabled="disabled"
      :placeholder="placeholder"
      class="code-editor-content"
      :style="{ height: `${height}px` }"
      @input="handleInput"
      @keydown="handleKeyDown"
    />
  </div>
</template>

<style scoped>
.code-editor-wrapper {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  overflow: hidden;
}

.code-editor-header {
  background: #f5f5f5;
  padding: 8px 12px;
  font-size: 12px;
  color: #666;
  border-bottom: 1px solid #d9d9d9;
}

.code-editor-content {
  width: 100%;
  padding: 12px;
  border: none;
  outline: none;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  line-height: 1.5;
  resize: vertical;
  background: #1e1e1e;
  color: #d4d4d4;
}

.code-editor-content:empty:before {
  content: attr(placeholder);
  color: #666;
}

.code-editor-content[disabled] {
  background: #f5f5f5;
  color: #999;
}
</style>

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
  placeholder: {
    type: String,
    default: '请输入内容',
  },
  height: {
    type: Number,
    default: 300,
  },
});

const emit = defineEmits<{
  (e: 'update:value', value: string): void;
}>();

// Using contenteditable as a simple rich editor placeholder
// In production, integrate with Quill, TipTap, or other rich text editors
const editorRef = ref<HTMLDivElement | null>(null);

function handleInput(event: Event) {
  const target = event.target as HTMLDivElement;
  emit('update:value', target.innerHTML);
}
</script>

<template>
  <div class="rich-editor-wrapper">
    <div
      ref="editorRef"
      class="rich-editor-content"
      contenteditable="true"
      :placeholder="placeholder"
      :style="{ height: `${height}px` }"
      :disabled="disabled"
      @input="handleInput"
      v-html="value"
    />
  </div>
</template>

<style scoped>
.rich-editor-wrapper {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

.rich-editor-content {
  padding: 12px;
  min-height: 200px;
  outline: none;
}

.rich-editor-content:empty:before {
  content: attr(placeholder);
  color: #c0c0c0;
  pointer-events: none;
}

.rich-editor-content[disabled] {
  background-color: #f5f5f5;
  cursor: not-allowed;
}
</style>

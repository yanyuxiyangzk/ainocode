<script setup lang="ts">
/**
 * EventHandlerEditor - Event handler property editor
 */
import { computed } from 'vue';
import { NInput, NButton, NSpace, NIcon, NPopover } from 'naive-ui';
import { CodeOutline, HelpCircleOutline } from '@vicons/ionicons5';

const props = defineProps<{
  modelValue: string;
  eventName: string;
  disabled?: boolean;
  placeholder?: string;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
}>();

const value = computed({
  get: () => props.modelValue ?? '',
  set: (val) => emit('update:modelValue', val),
});

const placeholderText = computed(() =>
  props.placeholder ?? `handle${props.eventName.charAt(2).toUpperCase() + props.eventName.slice(3)}`
);
</script>

<template>
  <div class="event-handler-editor">
    <NInput
      v-model:value="value"
      :placeholder="placeholderText"
      :disabled="disabled"
      clearable
    >
      <template #prefix>
        <span class="event-prefix">{{ eventName }}</span>
      </template>
      <template #suffix>
        <NPopover trigger="hover">
          <template #trigger>
            <NIcon size="16" style="cursor: help">
              <HelpCircleOutline />
            </NIcon>
          </template>
          <div style="max-width: 240px">
            <p style="margin: 0 0 8px 0; font-weight: 500">事件处理函数</p>
            <p style="margin: 0; font-size: 12px; color: #666">
              输入JavaScript函数名或表达式。例如：handleClick、() => console.log('clicked')
            </p>
          </div>
        </NPopover>
      </template>
    </NInput>
  </div>
</template>

<style scoped>
.event-handler-editor {
  width: 100%;
}

.event-prefix {
  font-family: monospace;
  font-size: 12px;
  color: #18a058;
}
</style>

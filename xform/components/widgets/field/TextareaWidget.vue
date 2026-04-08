<script setup lang="ts">
import { NInput } from 'naive-ui';
import type { PropType } from 'vue';

const props = defineProps({
  value: {
    type: String,
    default: '',
  },
  placeholder: {
    type: String,
    default: '请输入',
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  rows: {
    type: Number,
    default: 4,
  },
  maxlength: {
    type: Number as PropType<number | undefined>,
    default: undefined,
  },
  showCount: {
    type: Boolean,
    default: true,
  },
  autosize: {
    type: [Boolean, Object] as PropType<boolean | { minRows?: number; maxRows?: number }>,
    default: false,
  },
});

const emit = defineEmits<{
  (e: 'update:value', value: string): void;
}>();

function handleUpdate(value: string) {
  emit('update:value', value);
}
</script>

<template>
  <n-input
    :value="value"
    type="textarea"
    :placeholder="placeholder"
    :disabled="disabled"
    :rows="autosize ? undefined : rows"
    :maxlength="maxlength"
    :show-count="showCount"
    :autosize="autosize"
    @update:value="handleUpdate"
  />
</template>

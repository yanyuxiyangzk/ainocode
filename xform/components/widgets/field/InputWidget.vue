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
  clearable: {
    type: Boolean,
    default: true,
  },
  maxlength: {
    type: Number as PropType<number | undefined>,
    default: undefined,
  },
  showCount: {
    type: Boolean,
    default: false,
  },
  prefix: {
    type: String,
    default: '',
  },
  suffix: {
    type: String,
    default: '',
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
    :placeholder="placeholder"
    :disabled="disabled"
    :clearable="clearable"
    :maxlength="maxlength"
    :show-count="showCount"
    :prefix="prefix || undefined"
    :suffix="suffix || undefined"
    @update:value="handleUpdate"
  />
</template>

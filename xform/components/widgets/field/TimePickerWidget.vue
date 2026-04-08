<script setup lang="ts">
import { NTimePicker } from 'naive-ui';
import type { PropType } from 'vue';

const props = defineProps({
  value: {
    type: Number as PropType<number | null>,
    default: null,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  placeholder: {
    type: String,
    default: '请选择时间',
  },
  clearable: {
    type: Boolean,
    default: true,
  },
  format: {
    type: String,
    default: 'HH:mm:ss',
  },
  valueFormat: {
    type: String,
    default: 'HH:mm:ss',
  },
  isRange: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits<{
  (e: 'update:value', value: number | null): void;
}>();

function handleUpdate(value: number | null) {
  emit('update:value', value);
}
</script>

<template>
  <n-time-picker
    :value="value"
    :disabled="disabled"
    :placeholder="placeholder"
    :clearable="clearable"
    :format="format"
    :value-format="valueFormat"
    :is-range="isRange"
    @update:value="handleUpdate"
  />
</template>

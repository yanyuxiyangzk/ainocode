<script setup lang="ts">
import { NInputNumber } from 'naive-ui';
import type { PropType } from 'vue';

const props = defineProps({
  value: {
    type: Number as PropType<number | null>,
    default: null,
  },
  min: {
    type: Number as PropType<number | undefined>,
    default: undefined,
  },
  max: {
    type: Number as PropType<number | undefined>,
    default: undefined,
  },
  step: {
    type: Number,
    default: 1,
  },
  precision: {
    type: Number as PropType<number | undefined>,
    default: undefined,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  placeholder: {
    type: String,
    default: '请输入数字',
  },
  showButton: {
    type: Boolean,
    default: true,
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
  <n-input-number
    :value="value"
    :min="min"
    :max="max"
    :step="step"
    :precision="precision"
    :disabled="disabled"
    :placeholder="placeholder"
    :show-button="showButton"
    @update:value="handleUpdate"
  />
</template>

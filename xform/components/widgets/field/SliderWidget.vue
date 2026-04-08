<script setup lang="ts">
import { NSlider } from 'naive-ui';
import type { PropType } from 'vue';

const props = defineProps({
  value: {
    type: [Number, Array] as PropType<number | number[]>,
    default: 0,
  },
  min: {
    type: Number,
    default: 0,
  },
  max: {
    type: Number,
    default: 100,
  },
  step: {
    type: Number,
    default: 1,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  range: {
    type: Boolean,
    default: false,
  },
  showButton: {
    type: Boolean,
    default: true,
  },
  vertical: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits<{
  (e: 'update:value', value: number | number[]): void;
}>();

function handleUpdate(value: number | number[]) {
  emit('update:value', value);
}
</script>

<template>
  <n-slider
    :value="value"
    :min="min"
    :max="max"
    :step="step"
    :disabled="disabled"
    :range="range"
    :show-button="showButton"
    :vertical="vertical"
    @update:value="handleUpdate"
  />
</template>

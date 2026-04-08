<script setup lang="ts">
/**
 * NumberEditor - Number input property editor
 */
import { computed } from 'vue';
import { NInputNumber } from 'naive-ui';

const props = defineProps<{
  modelValue: number;
  placeholder?: string;
  disabled?: boolean;
  min?: number;
  max?: number;
  step?: number;
  precision?: number;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: number): void;
}>();

const value = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val ?? 0),
});
</script>

<template>
  <NInputNumber
    v-model:value="value"
    :placeholder="placeholder"
    :disabled="disabled"
    :min="min"
    :max="max"
    :step="step"
    :precision="precision"
    clearable
    style="width: 100%"
  />
</template>

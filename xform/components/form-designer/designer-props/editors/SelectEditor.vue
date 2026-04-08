<script setup lang="ts">
/**
 * SelectEditor - Dropdown select property editor
 */
import { computed } from 'vue';
import { NSelect } from 'naive-ui';
import type { SelectOption } from '@/types/property-editor';

const props = defineProps<{
  modelValue: string | number | boolean;
  options: SelectOption[];
  placeholder?: string;
  disabled?: boolean;
  multiple?: boolean;
  clearable?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | number | boolean): void;
}>();

const value = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val as string | number | boolean),
});

const selectOptions = computed(() => props.options ?? []);
</script>

<template>
  <NSelect
    v-model:value="value"
    :options="selectOptions"
    :placeholder="placeholder ?? '请选择'"
    :disabled="disabled"
    :multiple="multiple"
    :clearable="clearable ?? true"
  />
</template>

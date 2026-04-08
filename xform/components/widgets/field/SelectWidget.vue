<script setup lang="ts">
import { NSelect } from 'naive-ui';
import type { PropType } from 'vue';

export interface SelectOption {
  label: string;
  value: any;
  disabled?: boolean;
}

const props = defineProps({
  value: {
    type: [String, Number, Array] as PropType<any>,
    default: null,
  },
  options: {
    type: Array as PropType<SelectOption[]>,
    default: () => [],
  },
  multiple: {
    type: Boolean,
    default: false,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  placeholder: {
    type: String,
    default: '请选择',
  },
  clearable: {
    type: Boolean,
    default: true,
  },
  filterable: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits<{
  (e: 'update:value', value: any): void;
}>();

function handleUpdate(value: any) {
  emit('update:value', value);
}
</script>

<template>
  <n-select
    :value="value"
    :options="options"
    :multiple="multiple"
    :disabled="disabled"
    :placeholder="placeholder"
    :clearable="clearable"
    :filterable="filterable"
    @update:value="handleUpdate"
  />
</template>

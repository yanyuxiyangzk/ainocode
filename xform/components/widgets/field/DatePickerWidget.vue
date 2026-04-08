<script setup lang="ts">
import { NDatePicker } from 'naive-ui';
import type { PropType } from 'vue';

const props = defineProps({
  value: {
    type: Number as PropType<number | null>,
    default: null,
  },
  type: {
    type: String as PropType<'date' | 'datetime' | 'daterange' | 'datetimerange' | 'month' | 'year'>,
    default: 'date',
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  placeholder: {
    type: String,
    default: '请选择日期',
  },
  clearable: {
    type: Boolean,
    default: true,
  },
  format: {
    type: String,
    default: 'yyyy-MM-dd',
  },
  valueFormat: {
    type: String,
    default: 'yyyy-MM-dd',
  },
  startPlaceholder: {
    type: String,
    default: '开始日期',
  },
  endPlaceholder: {
    type: String,
    default: '结束日期',
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
  <n-date-picker
    :value="value"
    :type="type"
    :disabled="disabled"
    :placeholder="placeholder"
    :clearable="clearable"
    :format="format"
    :value-format="valueFormat"
    :start-placeholder="startPlaceholder"
    :end-placeholder="endPlaceholder"
    @update:value="handleUpdate"
  />
</template>

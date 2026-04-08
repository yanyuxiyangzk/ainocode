<script setup lang="ts">
import { NCheckboxGroup, NCheckbox } from 'naive-ui';
import type { PropType } from 'vue';
import type { SelectOption } from './SelectWidget.vue';

const props = defineProps({
  value: {
    type: Array as PropType<any[]>,
    default: () => [],
  },
  options: {
    type: Array as PropType<SelectOption[]>,
    default: () => [],
  },
  disabled: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits<{
  (e: 'update:value', value: any[]): void;
}>();

function handleUpdate(value: any[]) {
  emit('update:value', value);
}
</script>

<template>
  <n-checkbox-group :value="value" :disabled="disabled" @update:value="handleUpdate">
    <n-checkbox
      v-for="option in options"
      :key="option.value"
      :value="option.value"
      :disabled="option.disabled"
    >
      {{ option.label }}
    </n-checkbox>
  </n-checkbox-group>
</template>

<script setup lang="ts">
import { NRadioGroup, NRadio } from 'naive-ui';
import type { PropType } from 'vue';
import type { SelectOption } from './SelectWidget.vue';

const props = defineProps({
  value: {
    type: [String, Number] as PropType<any>,
    default: null,
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
  (e: 'update:value', value: any): void;
}>();

function handleUpdate(value: any) {
  emit('update:value', value);
}
</script>

<template>
  <n-radio-group :value="value" :disabled="disabled" @update:value="handleUpdate">
    <n-radio
      v-for="option in options"
      :key="option.value"
      :value="option.value"
      :disabled="option.disabled"
    >
      {{ option.label }}
    </n-radio>
  </n-radio-group>
</template>

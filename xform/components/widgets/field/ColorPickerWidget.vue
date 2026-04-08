<script setup lang="ts">
import { NColorPicker } from 'naive-ui';

const props = defineProps({
  value: {
    type: String,
    default: '#18A058',
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  showAlpha: {
    type: Boolean,
    default: true,
  },
  modes: {
    type: Array as PropType<string[]>,
    default: () => ['rgb', 'hex', 'hsl'],
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
  <n-color-picker
    :value="value"
    :disabled="disabled"
    :show-alpha="showAlpha"
    :modes="modes"
    @update:value="handleUpdate"
  />
</template>

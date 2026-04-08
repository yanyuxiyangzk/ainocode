<script setup lang="ts">
import { NRate } from 'naive-ui';

const props = defineProps({
  value: {
    type: Number,
    default: 0,
  },
  count: {
    type: Number,
    default: 5,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  allowHalf: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits<{
  (e: 'update:value', value: number): void;
}>();

function handleUpdate(value: number) {
  emit('update:value', value);
}
</script>

<template>
  <n-rate
    :value="value"
    :count="count"
    :disabled="disabled"
    :allow-half="allowHalf"
    @update:value="handleUpdate"
  />
</template>

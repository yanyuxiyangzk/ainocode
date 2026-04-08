<script setup lang="ts">
import { NButton } from 'naive-ui';
import type { PropType } from 'vue';

const props = defineProps({
  type: {
    type: String as PropType<'default' | 'primary' | 'success' | 'warning' | 'error' | 'info'>,
    default: 'default',
  },
  text: {
    type: String,
    default: '按钮',
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  size: {
    type: String as PropType<'small' | 'medium' | 'large'>,
    default: 'medium',
  },
  ghost: {
    type: Boolean,
    default: false,
  },
  circle: {
    type: Boolean,
    default: false,
  },
  icon: {
    type: String,
    default: '',
  },
});

const emit = defineEmits<{
  (e: 'click', event: MouseEvent): void;
}>();

function handleClick(event: MouseEvent) {
  if (!props.disabled && !props.loading) {
    emit('click', event);
  }
}
</script>

<template>
  <n-button
    :type="type"
    :disabled="disabled"
    :loading="loading"
    :size="size"
    :ghost="ghost"
    :circle="circle"
    @click="handleClick"
  >
    {{ text }}
  </n-button>
</template>

<script setup lang="ts">
/**
 * BooleanEditor - Switch toggle property editor
 */
import { computed } from 'vue';
import { NSwitch } from 'naive-ui';

const props = defineProps<{
  modelValue: boolean;
  disabled?: boolean;
  size?: 'small' | 'medium' | 'large';
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
}>();

const value = computed({
  get: () => props.modelValue ?? false,
  set: (val) => emit('update:modelValue', val),
});
</script>

<template>
  <NSwitch
    v-model:value="value"
    :disabled="disabled"
    :size="size ?? 'small'"
  />
</template>

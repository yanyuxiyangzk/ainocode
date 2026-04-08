<script setup lang="ts">
/**
 * StringEditor - Text input property editor
 */
import { computed } from 'vue';
import { NInput } from 'naive-ui';

const props = defineProps<{
  modelValue: string;
  placeholder?: string;
  disabled?: boolean;
  maxlength?: number;
  showCount?: boolean;
  type?: 'text' | 'textarea';
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
}>();

const value = computed({
  get: () => props.modelValue ?? '',
  set: (val) => emit('update:modelValue', val),
});

const inputType = computed(() => props.type ?? 'text');
</script>

<template>
  <NInput
    v-model:value="value"
    :type="inputType"
    :placeholder="placeholder"
    :disabled="disabled"
    :maxlength="maxlength"
    :show-count="showCount"
    clearable
  />
</template>

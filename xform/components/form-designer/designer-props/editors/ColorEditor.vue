<script setup lang="ts">
/**
 * ColorEditor - Color picker property editor
 */
import { computed } from 'vue';
import { NColorPicker } from 'naive-ui';

const props = defineProps<{
  modelValue: string;
  disabled?: boolean;
  showAlpha?: boolean;
  modes?: ('hex' | 'rgb' | 'hsl')[];
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
}>();

const value = computed({
  get: () => props.modelValue ?? '#000000',
  set: (val) => emit('update:modelValue', val),
});
</script>

<template>
  <NColorPicker
    v-model:value="value"
    :disabled="disabled"
    :show-alpha="showAlpha ?? true"
    :modes="modes ?? ['hex', 'rgb', 'hsl']"
    :swatches="['#FFFFFF', '#000000', '#18A058', '#2080F0', '#F0A020', '#D03050']"
  />
</template>

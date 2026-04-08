<script setup lang="ts">
/**
 * SliderEditor - Slider property editor
 */
import { computed } from 'vue';
import { NSlider, NInputNumber, NSpace } from 'naive-ui';

const props = defineProps<{
  modelValue: number;
  disabled?: boolean;
  min?: number;
  max?: number;
  step?: number;
  showInput?: boolean;
  showTooltip?: boolean;
}>();

const emit = defineEmits<<{
  (e: 'update:modelValue', value: number): void;
}>();

const value = computed({
  get: () => props.modelValue ?? 0,
  set: (val) => emit('update:modelValue', val ?? 0),
});

const sliderMin = computed(() => props.min ?? 0);
const sliderMax = computed(() => props.max ?? 100);
const sliderStep = computed(() => props.step ?? 1);
</script>

<template>
  <NSpace vertical :size="12">
    <NSlider
      v-model:value="value"
      :min="sliderMin"
      :max="sliderMax"
      :step="sliderStep"
      :disabled="disabled"
      :show-tooltip="showTooltip ?? true"
    />
    <NInputNumber
      v-if="showInput ?? true"
      v-model:value="value"
      :min="sliderMin"
      :max="sliderMax"
      :step="sliderStep"
      :disabled="disabled"
      size="small"
      style="width: 100px"
    />
  </NSpace>
</template>

<script setup lang="ts">
/**
 * BorderEditor - Border configuration property editor
 */
import { computed } from 'vue';
import { NInputNumber, NSelect, NColorPicker, NSpace, NCollapse, NCollapseItem } from 'naive-ui';

export interface BorderValue {
  width?: number;
  style?: 'solid' | 'dashed' | 'dotted';
  color?: string;
}

const props = defineProps<{
  modelValue: BorderValue;
  disabled?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: BorderValue): void;
}>();

const borderValue = computed({
  get: () => props.modelValue ?? { width: 1, style: 'solid', color: '#000000' },
  set: (val) => emit('update:modelValue', val),
});

const styleOptions = [
  { label: '实线', value: 'solid' },
  { label: '虚线', value: 'dashed' },
  { label: '点线', value: 'dotted' },
];

function updateWidth(width: number | null) {
  emit('update:modelValue', { ...borderValue.value, width: width ?? 1 });
}

function updateStyle(style: string | number | null) {
  emit('update:modelValue', { ...borderValue.value, style: style as BorderValue['style'] });
}

function updateColor(color: string) {
  emit('update:modelValue', { ...borderValue.value, color });
}
</script>

<template>
  <NCollapse transition="accordion">
    <NCollapseItem title="边框设置" :native="false">
      <NSpace vertical :size="12">
        <NSpace align="center">
          <span style="width: 60px; font-size: 12px; color: #666">宽度</span>
          <NInputNumber
            :value="borderValue.width"
            :min="0"
            :max="10"
            size="small"
            style="width: 80px"
            :disabled="disabled"
            @update:value="updateWidth"
          />
        </NSpace>
        <NSpace align="center">
          <span style="width: 60px; font-size: 12px; color: #666">样式</span>
          <NSelect
            :value="borderValue.style"
            :options="styleOptions"
            size="small"
            style="width: 100px"
            :disabled="disabled"
            @update:value="updateStyle"
          />
        </NSpace>
        <NSpace align="center">
          <span style="width: 60px; font-size: 12px; color: #666">颜色</span>
          <NColorPicker
            :value="borderValue.color"
            :show-alpha="true"
            :modes="['hex']"
            size="small"
            style="width: 100px"
            :disabled="disabled"
            @update:value="updateColor"
          />
        </NSpace>
      </NSpace>
    </NCollapseItem>
  </NCollapse>
</template>

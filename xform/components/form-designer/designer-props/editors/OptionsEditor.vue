<script setup lang="ts">
/**
 * OptionsEditor - Options array editor for radio/checkbox/select
 */
import { computed } from 'vue';
import { NButton, NInput, NSpace, NIcon } from 'naive-ui';
import { Add, Remove } from '@vicons/ionicons5';

export interface OptionItem {
  label: string;
  value: string | number;
}

const props = defineProps<{
  modelValue: OptionItem[];
  disabled?: boolean;
  addText?: string;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: OptionItem[]): void;
}>();

const options = computed({
  get: () => props.modelValue ?? [],
  set: (val) => emit('update:modelValue', val),
});

function addOption() {
  const newOption: OptionItem = {
    label: `选项${options.value.length + 1}`,
    value: `option_${options.value.length + 1}`,
  };
  emit('update:modelValue', [...options.value, newOption]);
}

function removeOption(index: number) {
  const newOptions = options.value.filter((_, i) => i !== index);
  emit('update:modelValue', newOptions);
}

function updateLabel(index: number, label: string) {
  const newOptions = [...options.value];
  newOptions[index] = { ...newOptions[index], label };
  emit('update:modelValue', newOptions);
}

function updateValue(index: number, value: string) {
  const newOptions = [...options.value];
  newOptions[index] = { ...newOptions[index], value };
  emit('update:modelValue', newOptions);
}
</script>

<template>
  <div class="options-editor">
    <div
      v-for="(option, index) in options"
      :key="index"
      class="option-item"
    >
      <NInput
        :value="option.label"
        placeholder="标签"
        size="small"
        style="width: 120px"
        :disabled="disabled"
        @update:value="(val) => updateLabel(index, val)"
      />
      <NInput
        :value="String(option.value)"
        placeholder="值"
        size="small"
        style="width: 100px"
        :disabled="disabled"
        @update:value="(val) => updateValue(index, val)"
      />
      <NButton
        size="small"
        quaternary
        circle
        :disabled="disabled || options.length <= 1"
        @click="removeOption(index)"
      >
        <template #icon>
          <NIcon><Remove /></NIcon>
        </template>
      </NButton>
    </div>
    <NButton
      size="small"
      dashed
      :disabled="disabled"
      @click="addOption"
    >
      <template #icon>
        <NIcon><Add /></NIcon>
      </template>
      {{ addText ?? '添加选项' }}
    </NButton>
  </div>
</template>

<style scoped>
.options-editor {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>

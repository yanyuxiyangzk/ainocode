<script setup lang="ts">
/**
 * FormRendererProvider - Provides context for form renderer
 */
import { provide, ref, computed, toRef } from 'vue';
import type { Ref } from 'vue';
import FormRenderer from './FormRenderer.vue';
import type { FormSchema } from '@/types/form-schema';

const props = defineProps<{
  schema: FormSchema;
  modelValue?: Record<string, any>;
  disabled?: boolean;
  readonly?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: Record<string, any>): void;
  (e: 'fieldChange', field: string, value: any): void;
  (e: 'fieldBlur', field: string): void;
  (e: 'submit', values: Record<string, any>): void;
  (e: 'reset'): void;
}>();

// Internal model value
const internalValue = ref<Record<string, any>>(props.modelValue || {});

// Watch external modelValue changes
const watchModelValue = computed({
  get: () => internalValue.value,
  set: (val) => {
    internalValue.value = val || {};
    emit('update:modelValue', internalValue.value);
  },
});

// Handle field change
function handleFieldChange(field: string, value: any) {
  internalValue.value = { ...internalValue.value, [field]: value };
  emit('update:modelValue', internalValue.value);
  emit('fieldChange', field, value);
}

// Handle field blur
function handleFieldBlur(field: string) {
  emit('fieldBlur', field);
}

// Handle submit
function handleSubmit(values: Record<string, any>) {
  emit('submit', values);
}

// Handle reset
function handleReset() {
  internalValue.value = {};
  emit('reset');
  emit('update:modelValue', {});
}

// Form context
interface FormRendererContext {
  schema: FormSchema;
  modelValue: Ref<Record<string, any>>;
  disabled: boolean;
  readonly: boolean;
  updateField: (field: string, value: any) => void;
  resetFields: () => void;
}

const context: FormRendererContext = {
  schema: props.schema,
  modelValue: toRef(internalValue),
  disabled: props.disabled || false,
  readonly: props.readonly || false,
  updateField: handleFieldChange,
  resetFields: handleReset,
};

// Provide context
provide('formRenderer', context);
</script>

<template>
  <slot
    :schema="schema"
    :model-value="internalValue"
    :disabled="disabled"
    :readonly="readonly"
    :update-field="handleFieldChange"
    :reset-fields="handleReset"
  />
</template>

<script setup lang="ts">
/**
 * FormRenderer - Main form renderer component
 * Renders JSON Schema as an actual form with validation and data binding
 */
import { ref, computed, provide, watch } from 'vue';
import { NForm, NFormItem, NButton, NSpace, NSpin, NAlert } from 'naive-ui';
import type { FormProps, ValidationRule } from 'naive-ui';
import FieldRenderer from './FieldRenderer.vue';
import type { FormSchema, WidgetConfig } from '@/types/form-schema';

const props = defineProps<{
  schema: FormSchema;
  modelValue?: Record<string, any>;
  disabled?: boolean;
  readonly?: boolean;
  preview?: boolean;
  loading?: boolean;
  showMessage?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: Record<string, any>): void;
  (e: 'fieldChange', field: string, value: any): void;
  (e: 'fieldBlur', field: string): void;
  (e: 'submit', values: Record<string, any>): void;
  (e: 'reset'): void;
  (e: 'validate', errors: Record<string, any>): void;
}>();

// Internal form data
const internalValue = ref<Record<string, any>>({});

// Sync with external modelValue
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    internalValue.value = { ...newVal };
  }
}, { immediate: true, deep: true });

// Computed form layout
const formLayout = computed<FormProps['layout']>(() => {
  return props.schema?.props?.layout || 'vertical';
});

// Computed label width
const labelWidth = computed(() => {
  return props.schema?.props?.labelWidth || 120;
});

// Computed label align
const labelAlign = computed<FormProps['labelAlign']>(() => {
  return props.schema?.props?.labelAlign || 'left';
});

// Form size
const formSize = computed<FormProps['size']>(() => {
  return props.schema?.props?.size || 'medium';
});

// Hide required mark
const hideRequiredMark = computed(() => {
  return props.schema?.props?.hideRequiredMark || false;
});

// Show message
const showMessageOpt = computed(() => {
  return props.showMessage !== false;
});

// Generate validation rules from schema
const validationRules = computed(() => {
  const rules: Record<string, ValidationRule | ValidationRule[]> = {};

  if (!props.schema?.widgets) return rules;

  function processWidget(widget: WidgetConfig) {
    if (widget.rules) {
      rules[widget.props?.name || widget.id] = widget.rules.map(rule => ({
        type: rule.type || 'string',
        required: rule.required,
        message: rule.message,
        min: rule.min,
        max: rule.max,
        pattern: rule.pattern,
        validator: rule.validator,
        trigger: rule.trigger || ['blur', 'change'],
      }));
    }

    // Process children recursively
    if (widget.children) {
      widget.children.forEach(processWidget);
    }
  }

  props.schema.widgets.forEach(processWidget);
  return rules;
});

// Form ref
const formRef = ref<any>(null);

// Handle field value update
function handleFieldChange(field: string, value: any) {
  internalValue.value = { ...internalValue.value, [field]: value };
  emit('update:modelValue', internalValue.value);
  emit('fieldChange', field, value);
}

// Handle field blur
function handleFieldBlur(field: string) {
  emit('fieldBlur', field);
}

// Handle form validation
async function validate(): Promise<boolean> {
  if (!formRef.value) return false;

  try {
    await formRef.value.validate();
    emit('validate', {});
    return true;
  } catch (errors: any) {
    const errorObj: Record<string, any> = {};
    if (errors?.errorFields) {
      errors.errorFields.forEach((field: any) => {
        errorObj[field.name] = field.errors;
      });
    }
    emit('validate', errorObj);
    return false;
  }
}

// Handle form submission
async function submit() {
  const isValid = await validate();
  if (isValid) {
    emit('submit', internalValue.value);
  }
}

// Handle form reset
function reset() {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  internalValue.value = {};
  emit('reset');
  emit('update:modelValue', {});
}

// Expose methods
defineExpose({
  validate,
  submit,
  reset,
});

// Provide form context
provide('formRenderer', {
  schema: props.schema,
  modelValue: internalValue,
  disabled: computed(() => props.disabled),
  readonly: computed(() => props.readonly),
});
</script>

<template>
  <div class="form-renderer" :class="{ 'is-preview': preview, 'is-disabled': disabled }">
    <n-spin :show="loading">
      <n-form
        ref="formRef"
        :model="internalValue"
        :layout="formLayout"
        :label-width="labelWidth"
        :label-align="labelAlign"
        :size="formSize"
        :hide-required-mark="hideRequiredMark"
        :show-message="showMessageOpt"
        :rules="validationRules"
        :disabled="disabled"
        @submit.prevent="submit"
      >
        <!-- Render widgets from schema -->
        <template v-for="widget in schema?.widgets" :key="widget.id">
          <FieldRenderer
            :widget="widget"
            :model-value="internalValue"
            :disabled="disabled"
            :readonly="readonly"
            @update:model-value="(val) => internalValue = val"
            @field-change="handleFieldChange"
            @field-blur="handleFieldBlur"
          />
        </template>

        <!-- Form actions in preview mode -->
        <n-form-item v-if="preview" class="form-actions">
          <n-space>
            <n-button type="primary" @click="submit">提交</n-button>
            <n-button @click="reset">重置</n-button>
          </n-space>
        </n-form-item>
      </n-form>
    </n-spin>

    <!-- Error display -->
    <n-alert v-if="false" type="error" class="form-error">
      表单验证失败，请检查输入
    </n-alert>
  </div>
</template>

<script lang="ts">
export default {
  name: 'FormRenderer',
};
</script>

<style scoped>
.form-renderer {
  width: 100%;
  padding: 16px;
}

.form-renderer.is-preview {
  background: #f5f5f5;
  border-radius: 8px;
}

.form-renderer.is-disabled {
  opacity: 0.7;
  pointer-events: none;
}

.form-actions {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #e8e8e8;
}

.form-error {
  margin-top: 16px;
}
</style>

/**
 * useFormRenderer - Composable for form rendering logic
 */
import { ref, computed, provide, inject, type Ref, type ComputedRef, type InjectionKey } from 'vue';
import type { FormSchema, WidgetConfig } from '@/types/form-schema';

export interface FormRendererContext {
  schema: FormSchema | null;
  modelValue: Ref<Record<string, any>>;
  disabled: ComputedRef<boolean>;
  readonly: ComputedRef<boolean>;
  updateField: (field: string, value: any) => void;
  resetFields: () => void;
  validate: () => Promise<{ valid: boolean; errors: Record<string, string> }>;
  setFieldsValue: (values: Record<string, any>) => void;
  getFieldValue: (field: string) => any;
}

export const FORM_RENDERER_KEY: InjectionKey<FormRendererContext> = Symbol('formRenderer');

// Create form renderer context
export function createFormRendererContext(options: {
  schema?: FormSchema;
  modelValue?: Record<string, any>;
  disabled?: boolean;
  readonly?: boolean;
}) {
  const schema = ref(options.schema ?? null) as Ref<FormSchema | null>;
  const internalValue = ref(options.modelValue || {});
  const disabled = ref(options.disabled || false);
  const readonly = ref(options.readonly || false);

  function updateField(field: string, value: any) {
    internalValue.value = { ...internalValue.value, [field]: value };
  }

  function resetFields() {
    internalValue.value = {};
    // Reset to default values from schema
    if (schema.value?.widgets) {
      function processWidget(widget: WidgetConfig) {
        if (widget.props?.defaultValue !== undefined) {
          internalValue.value[widget.id] = widget.props.defaultValue;
        }
        widget.children?.forEach(processWidget);
      }
      schema.value.widgets.forEach(processWidget);
    }
  }

  function setFieldsValue(values: Record<string, any>) {
    internalValue.value = { ...values };
  }

  function getFieldValue(field: string): any {
    return internalValue.value[field];
  }

  async function validate(): Promise<{ valid: boolean; errors: Record<string, string> }> {
    const errors: Record<string, string> = {};

    if (!schema.value?.widgets) {
      return { valid: true, errors: {} };
    }

    function validateWidget(widget: WidgetConfig) {
      const value = internalValue.value[widget.id];
      const isEmpty = value === undefined || value === null || value === '';

      // Required validation
      if (widget.props?.required && isEmpty) {
        errors[widget.id] = `${widget.props?.label || widget.widgetName} 是必填项`;
      }

      // Pattern validation
      if (!isEmpty && widget.props?.pattern) {
        const regex = new RegExp(widget.props.pattern);
        if (!regex.test(String(value))) {
          errors[widget.id] = `${widget.props?.label || widget.widgetName} 格式不正确`;
        }
      }

      // Length validation
      if (!isEmpty) {
        if (widget.props?.minLength && String(value).length < widget.props.minLength) {
          errors[widget.id] = `${widget.props?.label || widget.widgetName} 长度不能少于${widget.props.minLength}`;
        }
        if (widget.props?.maxLength && String(value).length > widget.props.maxLength) {
          errors[widget.id] = `${widget.props?.label || widget.widgetName} 长度不能超过${widget.props.maxLength}`;
        }
      }

      // Range validation
      if (!isEmpty && widget.props?.min !== undefined && Number(value) < widget.props.min) {
        errors[widget.id] = `${widget.props?.label || widget.widgetName} 不能小于${widget.props.min}`;
      }
      if (!isEmpty && widget.props?.max !== undefined && Number(value) > widget.props.max) {
        errors[widget.id] = `${widget.props?.label || widget.widgetName} 不能大于${widget.props.max}`;
      }

      widget.children?.forEach(validateWidget);
    }

    schema.value.widgets.forEach(validateWidget);
    return { valid: Object.keys(errors).length === 0, errors };
  }

  const context: FormRendererContext = {
    schema: schema.value,
    modelValue: internalValue as Ref<Record<string, any>>,
    disabled: computed(() => disabled.value),
    readonly: computed(() => readonly.value),
    updateField,
    resetFields,
    setFieldsValue,
    getFieldValue,
    validate,
  };

  return context;
}

// Provide form renderer context
export function provideFormRenderer(context: FormRendererContext) {
  provide(FORM_RENDERER_KEY, context);
}

// Inject form renderer context
export function useFormRenderer(): FormRendererContext | null {
  const context = inject<FormRendererContext>(FORM_RENDERER_KEY);
  return context || null;
}

// useFormRenderer composable for component usage
export function useFormRenderer(options?: {
  schema: FormSchema;
  modelValue?: Record<string, any>;
  disabled?: boolean;
  readonly?: boolean;
}) {
  const context = options
    ? createFormRendererContext(options)
    : createFormRendererContext({});

  return {
    ...context,
    provide: () => provideFormRenderer(context),
  };
}

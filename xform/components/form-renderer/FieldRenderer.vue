<script setup lang="ts">
/**
 * FieldRenderer - Renders individual field widgets based on their type
 */
import { computed, ref, h } from 'vue';
import type { Component } from 'vue';
import {
  NFormItem,
  NInput,
  NInputNumber,
  NSelect,
  NRadioGroup,
  NRadio,
  NCheckboxGroup,
  NCheckbox,
  NDatePicker,
  NTimePicker,
  NSwitch,
  NRate,
  NSlider,
  NColorPicker,
  NUpload,
  NButton,
  NDivider,
  NGrid,
  NGridItem,
  NTabs,
  NTabPane,
  NCard,
  NCollapse,
  NCollapseItem,
} from 'naive-ui';
import { WIDGET_TYPE } from '@/types/widget-type';
import type { WidgetConfig } from '@/types/form-schema';
import { useWidgetRegistry } from '@/composables/useWidgetRegistry';

const props = defineProps<{
  widget: WidgetConfig;
  modelValue: Record<string, any>;
  disabled?: boolean;
  readonly?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: Record<string, any>): void;
  (e: 'fieldChange', field: string, value: any): void;
  (e: 'fieldBlur', field: string): void;
}>();

const { getComponent } = useWidgetRegistry();

// Get the field name (use id as fallback)
const fieldName = computed(() => props.widget.id);

// Get current field value
const fieldValue = computed({
  get: () => {
    return props.modelValue[fieldName.value];
  },
  set: (val: any) => {
    const newModel = { ...props.modelValue, [fieldName.value]: val };
    emit('update:modelValue', newModel);
    emit('fieldChange', fieldName.value, val);
  },
});

// Update field value
function updateField(value: any) {
  fieldValue.value = value;
}

function handleBlur() {
  emit('fieldBlur', fieldName.value);
}

// Get options for select-like widgets
function getOptions() {
  if (props.widget.props?.options) {
    return props.widget.props.options.map((opt: any) => ({
      label: opt.label,
      value: opt.value,
      disabled: opt.disabled,
    }));
  }
  return [];
}

// Check if widget is a container type
function isContainerType(type: symbol): boolean {
  return [
    WIDGET_TYPE.GRID,
    WIDGET_TYPE.TABS,
    WIDGET_TYPE.CARD,
    WIDGET_TYPE.COLLAPSE,
  ].includes(type);
}

// Render child widget (recursive)
function renderChildWidget(child: WidgetConfig) {
  return h(FieldRenderer, {
    widget: child,
    modelValue: props.modelValue,
    disabled: props.disabled,
    readonly: props.readonly,
    'onUpdate:modelValue': (val: Record<string, any>) => emit('update:modelValue', val),
    'onFieldChange': (field: string, value: any) => emit('fieldChange', field, value),
    'onFieldBlur': (field: string) => emit('fieldBlur', field),
  });
}

// Render container widgets
function renderGridContainer() {
  const columns = props.widget.props?.columns || 3;
  const gutter = props.widget.props?.gutter || 16;

  return h(NGrid, {
    cols: columns,
    'x-gap': gutter,
    'y-gap': gutter,
    responsive: 'screen',
    itemResponsive: true,
  }, {
    default: () => props.widget.children?.map((child) =>
      h(NGridItem, { key: child.id, span: Math.floor(24 / columns) }, {
        default: () => isContainerType(child.type)
          ? renderChildContainer(child)
          : h(FieldRenderer, {
              widget: child,
              modelValue: props.modelValue,
              disabled: props.disabled,
              readonly: props.readonly,
              'onUpdate:modelValue': (val: Record<string, any>) => emit('update:modelValue', val),
              'onFieldChange': (field: string, value: any) => emit('fieldChange', field, value),
              'onFieldBlur': (field: string) => emit('fieldBlur', field),
            }),
      })
    ),
  });
}

function renderTabsContainer() {
  const tabPosition = props.widget.props?.tabPosition || 'top';

  return h(NTabs, {
    type: props.widget.props?.type || 'line',
    tabPosition,
  }, {
    default: () => props.widget.children?.map((tab, index) =>
      h(NTabPane, {
        key: tab.id || index,
        name: tab.props?.title || `标签${index + 1}`,
        tab: tab.props?.title || `标签${index + 1}`,
      }, {
        default: () => tab.children?.map(child =>
          isContainerType(child.type)
            ? renderChildContainer(child)
            : h(FieldRenderer, {
                widget: child,
                modelValue: props.modelValue,
                disabled: props.disabled,
                readonly: props.readonly,
                'onUpdate:modelValue': (val: Record<string, any>) => emit('update:modelValue', val),
                'onFieldChange': (field: string, value: any) => emit('fieldChange', field, value),
                'onFieldBlur': (field: string) => emit('fieldBlur', field),
              })
        ),
      })
    ),
  });
}

function renderCardContainer() {
  return h(NCard, {
    title: props.widget.props?.title,
    hoverable: props.widget.props?.hoverable ?? true,
  }, {
    default: () => props.widget.children?.map(child =>
      isContainerType(child.type)
        ? renderChildContainer(child)
        : h(FieldRenderer, {
            widget: child,
            modelValue: props.modelValue,
            disabled: props.disabled,
            readonly: props.readonly,
            'onUpdate:modelValue': (val: Record<string, any>) => emit('update:modelValue', val),
            'onFieldChange': (field: string, value: any) => emit('fieldChange', field, value),
            'onFieldBlur': (field: string) => emit('fieldBlur', field),
          })
    ),
  });
}

function renderCollapseContainer() {
  return h(NCollapse, {
    accordion: props.widget.props?.accordion ?? false,
  }, {
    default: () => props.widget.children?.map((panel, index) =>
      h(NCollapseItem, {
        key: panel.id || index,
        name: panel.props?.title || `面板${index + 1}`,
        title: panel.props?.title || `面板${index + 1}`,
      }, {
        default: () => panel.children?.map(child =>
          isContainerType(child.type)
            ? renderChildContainer(child)
            : h(FieldRenderer, {
                widget: child,
                modelValue: props.modelValue,
                disabled: props.disabled,
                readonly: props.readonly,
                'onUpdate:modelValue': (val: Record<string, any>) => emit('update:modelValue', val),
                'onFieldChange': (field: string, value: any) => emit('fieldChange', field, value),
                'onFieldBlur': (field: string) => emit('fieldBlur', field),
              })
        ),
      })
    ),
  });
}

function renderChildContainer(child: WidgetConfig) {
  if (child.type === WIDGET_TYPE.GRID) return renderGridForChild(child);
  if (child.type === WIDGET_TYPE.TABS) return renderTabsForChild(child);
  if (child.type === WIDGET_TYPE.CARD) return renderCardForChild(child);
  if (child.type === WIDGET_TYPE.COLLAPSE) return renderCollapseForChild(child);
  return null;
}

function renderGridForChild(child: WidgetConfig) {
  const columns = child.props?.columns || 3;
  const gutter = child.props?.gutter || 16;
  return h(NGrid, { cols: columns, 'x-gap': gutter, 'y-gap': gutter }, {
    default: () => child.children?.map(c =>
      h(NGridItem, { key: c.id }, {
        default: () => isContainerType(c.type)
          ? renderChildContainer(c)
          : h(FieldRenderer, { widget: c, modelValue: props.modelValue, disabled: props.disabled, readonly: props.readonly, 'onUpdate:modelValue': (val: Record<string, any>) => emit('update:modelValue', val) }),
      })
    ),
  });
}

function renderTabsForChild(child: WidgetConfig) {
  return h(NTabs, { type: child.props?.type || 'line', tabPosition: child.props?.tabPosition }, {
    default: () => child.children?.map((tab, index) =>
      h(NTabPane, { key: tab.id || index, name: tab.props?.title, tab: tab.props?.title }, {
        default: () => tab.children?.map(c =>
          h(FieldRenderer, { key: c.id, widget: c, modelValue: props.modelValue, disabled: props.disabled, readonly: props.readonly, 'onUpdate:modelValue': (val: Record<string, any>) => emit('update:modelValue', val) })
        ),
      })
    ),
  });
}

function renderCardForChild(child: WidgetConfig) {
  return h(NCard, { title: child.props?.title }, {
    default: () => child.children?.map(c =>
      h(FieldRenderer, { key: c.id, widget: c, modelValue: props.modelValue, disabled: props.disabled, readonly: props.readonly, 'onUpdate:modelValue': (val: Record<string, any>) => emit('update:modelValue', val) })
    ),
  });
}

function renderCollapseForChild(child: WidgetConfig) {
  return h(NCollapse, { accordion: child.props?.accordion }, {
    default: () => child.children?.map((panel, index) =>
      h(NCollapseItem, { key: panel.id || index, name: panel.props?.title, title: panel.props?.title }, {
        default: () => panel.children?.map(c =>
          h(FieldRenderer, { key: c.id, widget: c, modelValue: props.modelValue, disabled: props.disabled, readonly: props.readonly, 'onUpdate:modelValue': (val: Record<string, any>) => emit('update:modelValue', val) })
        ),
      })
    ),
  });
}

// Main render function based on widget type
function renderFieldWidget(): any {
  const type = props.widget.type;

  // Container widgets
  if (type === WIDGET_TYPE.GRID) return renderGridContainer();
  if (type === WIDGET_TYPE.TABS) return renderTabsContainer();
  if (type === WIDGET_TYPE.CARD) return renderCardContainer();
  if (type === WIDGET_TYPE.COLLAPSE) return renderCollapseContainer();

  // Basic field widgets
  switch (type) {
    case WIDGET_TYPE.INPUT:
      return h(NInput, {
        value: fieldValue.value || '',
        placeholder: props.widget.props?.placeholder || '请输入',
        disabled: props.disabled || props.widget.props?.disabled,
        clearable: props.widget.props?.clearable,
        maxlength: props.widget.props?.maxlength,
        showCount: props.widget.props?.showCount,
        onUpdateValue: updateField,
        onBlur: handleBlur,
      });

    case WIDGET_TYPE.TEXTAREA:
      return h(NInput, {
        type: 'textarea',
        value: fieldValue.value || '',
        placeholder: props.widget.props?.placeholder || '请输入',
        disabled: props.disabled || props.widget.props?.disabled,
        rows: props.widget.props?.rows || 4,
        maxlength: props.widget.props?.maxlength,
        autosize: props.widget.props?.autosize,
        onUpdateValue: updateField,
        onBlur: handleBlur,
      });

    case WIDGET_TYPE.NUMBER:
      return h(NInputNumber, {
        value: fieldValue.value,
        placeholder: props.widget.props?.placeholder || '请输入数字',
        disabled: props.disabled || props.widget.props?.disabled,
        min: props.widget.props?.min,
        max: props.widget.props?.max,
        step: props.widget.props?.step || 1,
        precision: props.widget.props?.precision,
        onUpdateValue: updateField,
        onBlur: handleBlur,
      });

    case WIDGET_TYPE.SELECT:
      return h(NSelect, {
        value: fieldValue.value,
        options: getOptions(),
        placeholder: props.widget.props?.placeholder || '请选择',
        disabled: props.disabled || props.widget.props?.disabled,
        clearable: props.widget.props?.clearable,
        filterable: props.widget.props?.filterable,
        multiple: props.widget.props?.multiple,
        onUpdateValue: updateField,
      });

    case WIDGET_TYPE.RADIO_GROUP:
      return h(NRadioGroup, {
        value: fieldValue.value,
        disabled: props.disabled || props.widget.props?.disabled,
        onUpdateValue: updateField,
      }, {
        default: () => getOptions().map(opt =>
          h(NRadio, { key: opt.value, value: opt.value, disabled: opt.disabled }, { default: () => opt.label })
        ),
      });

    case WIDGET_TYPE.CHECKBOX_GROUP:
      return h(NCheckboxGroup, {
        value: fieldValue.value || [],
        disabled: props.disabled || props.widget.props?.disabled,
        onUpdateValue: updateField,
      }, {
        default: () => getOptions().map(opt =>
          h(NCheckbox, { key: opt.value, value: opt.value, disabled: opt.disabled }, { default: () => opt.label })
        ),
      });

    case WIDGET_TYPE.DATE_PICKER:
      return h(NDatePicker, {
        type: props.widget.props?.type || 'date',
        value: fieldValue.value,
        placeholder: props.widget.props?.placeholder || '请选择日期',
        disabled: props.disabled || props.widget.props?.disabled,
        clearable: props.widget.props?.clearable,
        format: props.widget.props?.format || 'yyyy-MM-dd',
        valueFormat: props.widget.props?.valueFormat || 'yyyy-MM-dd',
        onUpdateValue: updateField,
      });

    case WIDGET_TYPE.TIME_PICKER:
      return h(NTimePicker, {
        value: fieldValue.value,
        placeholder: props.widget.props?.placeholder || '请选择时间',
        disabled: props.disabled || props.widget.props?.disabled,
        clearable: props.widget.props?.clearable,
        format: props.widget.props?.format || 'HH:mm:ss',
        isRange: props.widget.props?.isRange,
        onUpdateValue: updateField,
      });

    case WIDGET_TYPE.SWITCH:
      return h(NSwitch, {
        value: fieldValue.value,
        disabled: props.disabled || props.widget.props?.disabled,
        checkedValue: props.widget.props?.checkedValue ?? true,
        uncheckedValue: props.widget.props?.uncheckedValue ?? false,
        onUpdateValue: updateField,
      });

    case WIDGET_TYPE.RATE:
      return h(NRate, {
        value: fieldValue.value || 0,
        count: props.widget.props?.count || 5,
        disabled: props.disabled || props.widget.props?.disabled,
        allowHalf: props.widget.props?.allowHalf,
        onUpdateValue: updateField,
      });

    case WIDGET_TYPE.SLIDER:
      return h(NSlider, {
        value: fieldValue.value || 0,
        min: props.widget.props?.min || 0,
        max: props.widget.props?.max || 100,
        step: props.widget.props?.step || 1,
        disabled: props.disabled || props.widget.props?.disabled,
        range: props.widget.props?.range,
        onUpdateValue: updateField,
      });

    case WIDGET_TYPE.COLOR_PICKER:
      return h(NColorPicker, {
        value: fieldValue.value || '#18A058',
        disabled: props.disabled || props.widget.props?.disabled,
        showAlpha: props.widget.props?.showAlpha,
        onUpdateValue: updateField,
      });

    case WIDGET_TYPE.PICTURE_UPLOAD:
    case WIDGET_TYPE.FILE_UPLOAD:
      return h(NUpload, {
        action: props.widget.props?.action || '#',
        disabled: props.disabled || props.widget.props?.disabled,
        maxNumber: props.widget.props?.maxNumber,
        accept: props.widget.props?.accept,
        listType: type === WIDGET_TYPE.PICTURE_UPLOAD ? (props.widget.props?.listType || 'image-card') : 'text',
        multiple: true,
        onChange: (options: any) => {
          const fileList = options.fileList.map((file: any) => ({
            name: file.name,
            url: file.url || file.response?.url || '',
          }));
          updateField(fileList);
        },
      }, {
        default: () => h(NButton, { type: 'primary', dashed: true }, {
          default: () => type === WIDGET_TYPE.PICTURE_UPLOAD ? '上传图片' : '上传文件',
        }),
      });

    case WIDGET_TYPE.DIVIDER:
      return h(NDivider, {
        title: props.widget.props?.title,
        dashed: props.widget.props?.dashed,
        vertical: props.widget.props?.vertical,
        placement: props.widget.props?.position || 'center',
      });

    case WIDGET_TYPE.BUTTON:
      return h(NButton, {
        type: props.widget.props?.type || 'default',
        disabled: props.disabled || props.widget.props?.disabled,
        loading: props.widget.props?.loading,
        size: props.widget.props?.size || 'medium',
        ghost: props.widget.props?.ghost,
        circle: props.widget.props?.circle,
        onClick: () => {
          const handler = props.widget.events?.find(e => e.name === 'onClick')?.handler;
          if (handler) {
            try {
              new Function(handler)({ value: fieldValue.value, modelValue: props.modelValue });
            } catch (e) {
              console.error('Button handler error:', e);
            }
          }
        },
      }, { default: () => props.widget.props?.text || '按钮' });

    case WIDGET_TYPE.RICH_EDITOR:
    case WIDGET_TYPE.CODE_EDITOR:
    case WIDGET_TYPE.HTML:
      return h('div', {
        contentEditable: !props.disabled && !props.readonly,
        class: 'html-editor-content',
        innerHTML: fieldValue.value || props.widget.props?.content || '',
        onInput: (e: Event) => {
          const target = e.target as HTMLDivElement;
          updateField(target.innerHTML);
        },
      });

    default:
      // Try to get registered component
      const RegisteredComponent = getComponent(type);
      if (RegisteredComponent) {
        return h(RegisteredComponent as Component, {
          ...props.widget.props,
          value: fieldValue.value,
          disabled: props.disabled || props.widget.props?.disabled,
          readonly: props.readonly,
          onUpdateValue: updateField,
        });
      }
      return h('div', { class: 'unknown-widget' }, `Unknown: ${props.widget.widgetName}`);
  }
}
</script>

<template>
  <div class="field-renderer" :data-widget-id="widget.id">
    <!-- Container widgets render their children directly -->
    <template v-if="[WIDGET_TYPE.GRID, WIDGET_TYPE.TABS, WIDGET_TYPE.CARD, WIDGET_TYPE.COLLAPSE].includes(widget.type)">
      <render-field-widget />
    </template>

    <!-- Field widgets render with form-item wrapper -->
    <n-form-item
      v-else
      :label="widget.props?.label"
      :path="fieldName"
      :required="widget.props?.required"
    >
      <render-field-widget />
    </n-form-item>
  </div>
</template>

<script lang="ts">
export default {
  name: 'FieldRenderer',
};
</script>

<style scoped>
.field-renderer {
  width: 100%;
}

.html-editor-content {
  padding: 12px;
  min-height: 100px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  outline: none;
}

.html-editor-content:focus {
  border-color: #18a058;
}

.html-editor-content[contenteditable='true'] {
  cursor: text;
}

.unknown-widget {
  padding: 12px;
  color: #999;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
}
</style>

// Code Generator - 生成Vue3 SFC代码
import type { FormSchema, WidgetConfig, FormProps } from '@/types/form-schema';
import { WIDGET_TYPE, WidgetType } from '@/types/widget-type';

export interface CodeGenOptions {
  format: 'vue3' | 'vue3-ts' | 'json';
  useNaiveUi: boolean;
  scopedStyle: boolean;
  addFormEventHandlers: boolean;
}

/**
 * 生成Vue3 SFC代码
 */
export function generateVue3SFC(schema: FormSchema, options: Partial<CodeGenOptions> = {}): string {
  const opts: CodeGenOptions = {
    format: 'vue3-ts',
    useNaiveUi: true,
    scopedStyle: true,
    addFormEventHandlers: true,
    ...options,
  };

  const template = generateTemplate(schema, opts);
  const script = generateScript(schema, opts);
  const style = generateStyle(schema, opts);

  return `<template>\n${template}\n</template>\n\n<script setup lang="ts">\n${script}\n</script>\n\n<style${opts.scopedStyle ? ' scoped' : ''}>\n${style}\n</style>`;
}

/**
 * 生成模板部分
 */
function generateTemplate(schema: FormSchema, opts: CodeGenOptions): string {
  const formProps = generateFormProps(schema.props, opts);
  const widgetsCode = schema.widgets.map(w => generateWidgetTemplate(w, 1, opts)).join('\n');

  if (opts.useNaiveUi) {
    return `  <n-form\n${formProps}\n  >
${widgetsCode}
  </n-form>`;
  }

  return `  <form\n${formProps}
  >
${widgetsCode}
  </form>`;
}

/**
 * 生成表单属性
 */
function generateFormProps(props: FormProps, opts: CodeGenOptions): string {
  const attrs: string[] = [];

  if (opts.useNaiveUi) {
    if (props.layout) attrs.push(`  :layout="${props.layout === 'horizontal' ? "'horizontal'" : "'vertical'"}"`);
    if (props.labelWidth) attrs.push(`  label-width="${props.labelWidth}"`);
    if (props.labelAlign) attrs.push(`  label-align="${props.labelAlign}"`);
    if (props.hideRequiredMark) attrs.push(`  :hide-required-mark="true"`);
    if (props.size) attrs.push(`  size="${props.size}"`);
  }

  return attrs.join('\n');
}

/**
 * 生成组件模板
 */
function generateWidgetTemplate(widget: WidgetConfig, indent: number, opts: CodeGenOptions): string {
  const indentStr = '  '.repeat(indent);
  const widgetType = widget.type.toString();

  // 根据组件类型生成对应代码
  if (widgetType.includes('INPUT')) {
    return generateInputTemplate(widget, indentStr, opts);
  }
  if (widgetType.includes('SELECT')) {
    return generateSelectTemplate(widget, indentStr, opts);
  }
  if (widgetType.includes('DATE_PICKER')) {
    return generateDatePickerTemplate(widget, indentStr, opts);
  }
  if (widgetType.includes('NUMBER_INPUT')) {
    return generateNumberInputTemplate(widget, indentStr, opts);
  }
  if (widgetType.includes('TEXTAREA')) {
    return generateTextareaTemplate(widget, indentStr, opts);
  }
  if (widgetType.includes('SWITCH')) {
    return generateSwitchTemplate(widget, indentStr, opts);
  }
  if (widgetType.includes('RADIO')) {
    return generateRadioTemplate(widget, indentStr, opts);
  }
  if (widgetType.includes('CHECKBOX')) {
    return generateCheckboxTemplate(widget, indentStr, opts);
  }
  if (widgetType.includes('GRID')) {
    return generateGridTemplate(widget, indent, opts);
  }
  if (widgetType.includes('TABS')) {
    return generateTabsTemplate(widget, indent, opts);
  }
  if (widgetType.includes('CARD')) {
    return generateCardTemplate(widget, indent, opts);
  }
  if (widgetType.includes('DIVIDER')) {
    return generateDividerTemplate(widget, indentStr, opts);
  }

  // 默认处理
  return `${indentStr}<!-- ${widget.widgetName} -->`;
}

function generateInputTemplate(widget: WidgetConfig, indent: string, opts: CodeGenOptions): string {
  const props = widget.props;
  const label = props.label ? props.label : '';
  const placeholder = props.placeholder || '';
  const disabled = props.disabled ? ' :disabled="true"' : '';
  const clearable = props.clearable ? ' clearable' : '';
  const maxlength = props.maxlength ? ` :maxlength="${props.maxlength}"` : '';

  const vModel = `v-model:value="formData.${widget.id}"`;

  if (opts.useNaiveUi) {
    return `${indent}<n-form-item label="${label}">
${indent}  <n-input
${indent}    ${vModel}
${indent}    placeholder="${placeholder}"${disabled}${clearable}${maxlength}
${indent}  />
${indent}</n-form-item>`;
  }

  return `${indent}<n-form-item label="${label}">
${indent}  <input
${indent}    ${vModel}
${indent}    placeholder="${placeholder}"${disabled}${clearable}${maxlength}
${indent}  />
${indent}</n-form-item>`;
}

function generateSelectTemplate(widget: WidgetConfig, indent: string, opts: CodeGenOptions): string {
  const props = widget.props;
  const label = props.label ? props.label : '';
  const placeholder = props.placeholder || '';
  const disabled = props.disabled ? ' :disabled="true"' : '';
  const multiple = props.multiple ? ' multiple' : '';

  const optionsCode = generateOptions(props.options || []);

  const vModel = `v-model:value="formData.${widget.id}"`;

  if (opts.useNaiveUi) {
    return `${indent}<n-form-item label="${label}">
${indent}  <n-select
${indent}    ${vModel}
${indent}    placeholder="${placeholder}"${disabled}${multiple}
${indent}    :options="options${capitalize(widget.id)}"
${indent}  />
${indent}</n-form-item>`;
  }

  return `${indent}<n-form-item label="${label}">
${indent}  <select
${indent}    ${vModel}
${indent}    placeholder="${placeholder}"${disabled}${multiple}
${indent}  >
${indent}    <option v-for="opt in options${capitalize(widget.id)}" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
${indent}  </select>
${indent}</n-form-item>`;
}

function generateDatePickerTemplate(widget: WidgetConfig, indent: string, opts: CodeGenOptions): string {
  const props = widget.props;
  const label = props.label ? props.label : '';
  const placeholder = props.placeholder || '';
  const disabled = props.disabled ? ' :disabled="true"' : '';
  const type = props.type || 'date';
  const clearable = props.clearable ? ' clearable' : '';

  const vModel = `v-model:value="formData.${widget.id}"`;

  if (opts.useNaiveUi) {
    return `${indent}<n-form-item label="${label}">
${indent}  <n-date-picker
${indent}    ${vModel}
${indent}    type="${type}"
${indent}    placeholder="${placeholder}"${disabled}${clearable}
${indent}  />
${indent}</n-form-item>`;
  }

  return `${indent}<n-form-item label="${label}">
${indent}  <input type="date" ${vModel} placeholder="${placeholder}"${disabled} />
${indent}</n-form-item>`;
}

function generateNumberInputTemplate(widget: WidgetConfig, indent: string, opts: CodeGenOptions): string {
  const props = widget.props;
  const label = props.label ? props.label : '';
  const placeholder = props.placeholder || '';
  const disabled = props.disabled ? ' :disabled="true"' : '';
  const min = props.min !== undefined ? ` :min="${props.min}"` : '';
  const max = props.max !== undefined ? ` :max="${props.max}"` : '';
  const step = props.step ? ` :step="${props.step}"` : '';

  const vModel = `v-model:value="formData.${widget.id}"`;

  if (opts.useNaiveUi) {
    return `${indent}<n-form-item label="${label}">
${indent}  <n-input-number
${indent}    ${vModel}
${indent}    placeholder="${placeholder}"${disabled}${min}${max}${step}
${indent}  />
${indent}</n-form-item>`;
  }

  return `${indent}<n-form-item label="${label}">
${indent}  <input type="number" ${vModel} placeholder="${placeholder}"${disabled}${min}${max}${step} />
${indent}</n-form-item>`;
}

function generateTextareaTemplate(widget: WidgetConfig, indent: string, opts: CodeGenOptions): string {
  const props = widget.props;
  const label = props.label ? props.label : '';
  const placeholder = props.placeholder || '';
  const disabled = props.disabled ? ' :disabled="true"' : '';
  const rows = props.rows ? ` :rows="${props.rows}"` : '';
  const maxlength = props.maxlength ? ` :maxlength="${props.maxlength}"` : '';
  const showCount = props.showCount ? ' show-count' : '';

  const vModel = `v-model:value="formData.${widget.id}"`;

  if (opts.useNaiveUi) {
    return `${indent}<n-form-item label="${label}">
${indent}  <n-input
${indent}    ${vModel}
${indent}    type="textarea"
${indent}    placeholder="${placeholder}"${disabled}${rows}${maxlength}${showCount}
${indent}  />
${indent}</n-form-item>`;
  }

  return `${indent}<n-form-item label="${label}">
${indent}  <textarea ${vModel} placeholder="${placeholder}"${disabled}${rows}${maxlength}${showCount}></textarea>
${indent}</n-form-item>`;
}

function generateSwitchTemplate(widget: WidgetConfig, indent: string, opts: CodeGenOptions): string {
  const props = widget.props;
  const label = props.label ? props.label : '';
  const disabled = props.disabled ? ' :disabled="true"' : '';

  const vModel = `v-model:value="formData.${widget.id}"`;

  if (opts.useNaiveUi) {
    return `${indent}<n-form-item label="${label}">
${indent}  <n-switch ${vModel}${disabled} />
${indent}</n-form-item>`;
  }

  return `${indent}<n-form-item label="${label}">
${indent}  <input type="checkbox" ${vModel}${disabled} />
${indent}</n-form-item>`;
}

function generateRadioTemplate(widget: WidgetConfig, indent: string, opts: CodeGenOptions): string {
  const props = widget.props;
  const label = props.label ? props.label : '';
  const disabled = props.disabled ? ' :disabled="true"' : '';
  const optionsCode = generateOptions(props.options || []);

  const vModel = `v-model:value="formData.${widget.id}"`;

  if (opts.useNaiveUi) {
    return `${indent}<n-form-item label="${label}">
${indent}  <n-radio-group ${vModel}${disabled}>
${indent}    <n-space>
${indent}      <n-radio\n${indent}        v-for="opt in options${capitalize(widget.id)}"\n${indent}        :key="opt.value"\n${indent}        :value="opt.value"\n${indent}      >{{ opt.label }}</n-radio>
${indent}    </n-space>
${indent}  </n-radio-group>
${indent}</n-form-item>`;
  }

  return `${indent}<n-form-item label="${label}">
${indent}  <radio-group ${vModel}${disabled}>
${indent}    <radio v-for="opt in options${capitalize(widget.id)}" :key="opt.value" :value="opt.value">{{ opt.label }}</radio>
${indent}  </radio-group>
${indent}</n-form-item>`;
}

function generateCheckboxTemplate(widget: WidgetConfig, indent: string, opts: CodeGenOptions): string {
  const props = widget.props;
  const label = props.label ? props.label : '';
  const disabled = props.disabled ? ' :disabled="true"' : '';
  const multiple = props.multiple ? ' multiple' : '';
  const optionsCode = generateOptions(props.options || []);

  const vModel = `v-model:value="formData.${widget.id}"`;

  if (opts.useNaiveUi) {
    if (multiple || props.multiple) {
      return `${indent}<n-form-item label="${label}">
${indent}  <n-checkbox-group ${vModel}${disabled}>
${indent}    <n-space>
${indent}      <n-checkbox\n${indent}        v-for="opt in options${capitalize(widget.id)}"\n${indent}        :key="opt.value"\n${indent}        :value="opt.value"\n${indent}      >{{ opt.label }}</n-checkbox>
${indent}    </n-space>
${indent}  </n-checkbox-group>
${indent}</n-form-item>`;
    }
    return `${indent}<n-form-item label="${label}">
${indent}  <n-checkbox ${vModel}${disabled}>{{ formData.${widget.id} }}</n-checkbox>
${indent}</n-form-item>`;
  }

  return `${indent}<n-form-item label="${label}">
${indent}  <checkbox-group ${vModel}${disabled}>
${indent}    <checkbox v-for="opt in options${capitalize(widget.id)}" :key="opt.value" :value="opt.value">{{ opt.label }}</checkbox>
${indent}  </checkbox-group>
${indent}</n-form-item>`;
}

function generateGridTemplate(widget: WidgetConfig, indent: number, opts: CodeGenOptions): string {
  const props = widget.props;
  const columns = props.columns || 3;
  const gutter = props.gutter || 16;
  const indentStr = '  '.repeat(indent);

  let code = `${indentStr}<n-grid :cols="${columns}" :x-gap="${gutter}">\n`;

  if (widget.children) {
    widget.children.forEach((child, index) => {
      code += `${indentStr}  <n-gi :span="1">\n`;
      code += generateWidgetTemplate(child, indent + 2, opts);
      code += `\n${indentStr}  </n-gi>\n`;
    });
  }

  code += `${indentStr}</n-grid>`;
  return code;
}

function generateTabsTemplate(widget: WidgetConfig, indent: number, opts: CodeGenOptions): string {
  const props = widget.props;
  const type = props.type || 'line';
  const indentStr = '  '.repeat(indent);

  let code = `${indentStr}<n-tabs type="${type}">\n`;

  const tabs = props.tabs || [{ title: '标签1', key: 'tab1' }];
  tabs.forEach((tab: { title: string; key: string }) => {
    code += `${indentStr}  <n-tab-pane name="${tab.key}" tab="${tab.title}">\n`;
    const child = widget.children?.find(c => c.id.includes(tab.key));
    if (child) {
      code += generateWidgetTemplate(child, indent + 2, opts);
    }
    code += `\n${indentStr}  </n-tab-pane>\n`;
  });

  code += `${indentStr}</n-tabs>`;
  return code;
}

function generateCardTemplate(widget: WidgetConfig, indent: number, opts: CodeGenOptions): string {
  const props = widget.props;
  const title = props.title || '卡片标题';
  const bordered = props.bordered !== false;
  const hoverable = props.hoverable ? ' hoverable' : '';
  const indentStr = '  '.repeat(indent);

  let code = `${indentStr}<n-card title="${title}"${bordered ? ' :bordered="true"' : ''}${hoverable}>\n`;

  if (widget.children) {
    widget.children.forEach(child => {
      code += generateWidgetTemplate(child, indent + 1, opts) + '\n';
    });
  }

  code += `${indentStr}</n-card>`;
  return code;
}

function generateDividerTemplate(widget: WidgetConfig, indent: string, opts: CodeGenOptions): string {
  const props = widget.props;
  const dashed = props.dashed ? ' dashed' : '';
  const vertical = props.vertical ? ' vertical' : '';

  if (opts.useNaiveUi) {
    return `${indent}<n-divider${dashed}${vertical} />`;
  }

  return `${indent}<hr${dashed ? ' style="border-style: dashed"' : ''} />`;
}

/**
 * 生成选项数组代码
 */
function generateOptions(options: Array<{ label: string; value: string }>): string {
  if (!options || options.length === 0) {
    return '[]';
  }
  return JSON.stringify(options, null, 2);
}

/**
 * 生成脚本部分
 */
function generateScript(schema: FormSchema, opts: CodeGenOptions): string {
  const formDataFields = schema.widgets.map(w => `  ${w.id}: ''`).join(',\n');
  const optionsRefs = schema.widgets
    .filter(w => w.props && Array.isArray(w.props.options))
    .map(w => `const options${capitalize(w.id)} = ref(${JSON.stringify(w.props.options)});`)
    .join('\n');

  const imports = opts.useNaiveUi
    ? `import { ref, reactive } from 'vue';
import { NForm, NFormItem, NInput, NSelect, NDatePicker, NInputNumber, NSwitch, NRadioGroup, NRadio, NCheckbox, NCheckboxGroup, NGrid, NGi, NTabs, NTabPane, NCard, NDivider, NSpace } from 'naive-ui';`
    : `import { ref, reactive } from 'vue';`;

  const formData = `const formData = reactive({
${formDataFields}
});`;

  const handleSubmit = `function handleSubmit() {
  console.log('Form data:', formData);
  // 表单提交逻辑
}`;

  return `${imports}

${optionsRefs}

${formData}

${handleSubmit}
`;
}

/**
 * 生成样式部分
 */
function generateStyle(schema: FormSchema, opts: CodeGenOptions): string {
  return `.form-container {
  padding: 24px;
}`;
}

/**
 * 首字母大写
 */
function capitalize(str: string): string {
  return str.charAt(0).toUpperCase() + str.slice(1);
}

/**
 * 生成JSON格式的Schema
 */
export function generateSchemaJson(schema: FormSchema): string {
  return JSON.stringify(schema, null, 2);
}

/**
 * 生成表单数据初始化代码
 */
export function generateFormDataInit(schema: FormSchema): string {
  const fields: Record<string, unknown> = {};

  function extractFields(widgets: WidgetConfig[]) {
    for (const widget of widgets) {
      if (widget.props.defaultValue !== undefined) {
        fields[widget.id] = widget.props.defaultValue;
      } else {
        fields[widget.id] = '';
      }
      if (widget.children) {
        extractFields(widget.children);
      }
    }
  }

  extractFields(schema.widgets);

  return `const formData = reactive(${JSON.stringify(fields, null, 2)});`;
}

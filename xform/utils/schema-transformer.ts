// Schema Transformer - Schema转换工具
import type { FormSchema, WidgetConfig, FormProps } from '@/types/form-schema';
import { WIDGET_TYPE } from '@/types/widget-type';

/**
 * 创建空表单Schema
 */
export function createEmptySchema(): FormSchema {
  return {
    id: generateId(),
    name: '未命名表单',
    version: '1.0.0',
    description: '',
    props: {
      layout: 'vertical',
      labelWidth: 120,
      labelAlign: 'left',
      hideRequiredMark: false,
      size: 'medium',
    },
    widgets: [],
  };
}

/**
 * 验证Schema是否有效
 */
export function validateSchema(schema: unknown): { valid: boolean; errors: string[] } {
  const errors: string[] = [];

  if (!schema || typeof schema !== 'object') {
    return { valid: false, errors: ['Schema必须是对象'] };
  }

  const s = schema as Record<string, unknown>;

  if (!s.id || typeof s.id !== 'string') {
    errors.push('Schema缺少id字段');
  }

  if (!s.name || typeof s.name !== 'string') {
    errors.push('Schema缺少name字段');
  }

  if (!Array.isArray(s.widgets)) {
    errors.push('Schema缺少widgets数组');
  }

  // 递归验证widgets
  if (Array.isArray(s.widgets)) {
    for (const widget of s.widgets) {
      validateWidget(widget, errors);
    }
  }

  return { valid: errors.length === 0, errors };
}

/**
 * 验证单个组件配置
 */
function validateWidget(widget: unknown, errors: string[]): void {
  if (!widget || typeof widget !== 'object') {
    errors.push('组件配置必须是对象');
    return;
  }

  const w = widget as Record<string, unknown>;

  if (!w.id || typeof w.id !== 'string') {
    errors.push('组件缺少id字段');
  }

  if (!w.type) {
    errors.push('组件缺少type字段');
  }

  if (!w.widgetName || typeof w.widgetName !== 'string') {
    errors.push('组件缺少widgetName字段');
  }

  if (!w.props || typeof w.props !== 'object') {
    errors.push(`组件${w.id}缺少props对象`);
  }

  // 递归验证子组件
  if (Array.isArray(w.children)) {
    for (const child of w.children) {
      validateWidget(child, errors);
    }
  }
}

/**
 * 深拷贝Schema（用于撤销/重做）
 */
export function cloneSchema(schema: FormSchema): FormSchema {
  return JSON.parse(JSON.stringify(schema));
}

/**
 * 合并组件属性
 */
export function mergeWidgetProps(
  widget: WidgetConfig,
  newProps: Record<string, unknown>
): WidgetConfig {
  return {
    ...widget,
    props: {
      ...widget.props,
      ...newProps,
    },
  };
}

/**
 * 查找画布中的组件
 */
export function findWidgetById(widgets: WidgetConfig[], id: string): WidgetConfig | null {
  for (const widget of widgets) {
    if (widget.id === id) {
      return widget;
    }
    if (widget.children) {
      const found = findWidgetById(widget.children, id);
      if (found) return found;
    }
  }
  return null;
}

/**
 * 从树中移除组件
 */
export function removeWidgetFromTree(widgets: WidgetConfig[], id: string): WidgetConfig | null {
  const index = widgets.findIndex(w => w.id === id);
  if (index !== -1) {
    return widgets.splice(index, 1)[0];
  }

  for (const widget of widgets) {
    if (widget.children) {
      const found = removeWidgetFromTree(widget.children, id);
      if (found) return found;
    }
  }

  return null;
}

/**
 * 在树中插入组件
 */
export function insertWidgetIntoTree(
  widgets: WidgetConfig[],
  parentId: string | null,
  widget: WidgetConfig,
  index?: number
): boolean {
  if (parentId === null) {
    // 添加到根
    if (index !== undefined) {
      widgets.splice(index, 0, widget);
    } else {
      widgets.push(widget);
    }
    return true;
  }

  // 添加到容器
  const parent = findWidgetById(widgets, parentId);
  if (parent?.children) {
    if (index !== undefined) {
      parent.children.splice(index, 0, widget);
    } else {
      parent.children.push(widget);
    }
    return true;
  }

  return false;
}

/**
 * 生成唯一ID
 */
export function generateId(prefix: string = 'widget'): string {
  return `${prefix}-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;
}

/**
 * Schema转JSON
 */
export function schemaToJson(schema: FormSchema, pretty: boolean = true): string {
  return JSON.stringify(schema, null, pretty ? 2 : 0);
}

/**
 * JSON转Schema
 */
export function jsonToSchema(json: string): { schema: FormSchema | null; error: string | null } {
  try {
    const parsed = JSON.parse(json);
    const validation = validateSchema(parsed);
    if (!validation.valid) {
      return { schema: null, error: validation.errors.join('; ') };
    }
    return { schema: parsed as FormSchema, error: null };
  } catch (e) {
    return { schema: null, error: `JSON解析失败: ${(e as Error).message}` };
  }
}

/**
 * 导入Schema（带验证）
 */
export function importSchema(jsonString: string): { schema: FormSchema | null; error: string | null } {
  return jsonToSchema(jsonString);
}

/**
 * 导出Schema
 */
export function exportSchema(schema: FormSchema, pretty: boolean = true): string {
  return schemaToJson(schema, pretty);
}

/**
 * 获取组件类型的显示名称
 */
export function getWidgetTypeName(type: symbol): string {
  const typeMap: Record<string, string> = {
    'INPUT': '输入框',
    'SELECT': '选择器',
    'DATE_PICKER': '日期选择器',
    'NUMBER_INPUT': '数字输入框',
    'TEXTAREA': '文本域',
    'RADIO': '单选框',
    'CHECKBOX': '多选框',
    'SWITCH': '开关',
    'SLIDER': '滑块',
    'TIME_PICKER': '时间选择器',
    'UPLOAD': '上传',
    'GRID': '栅格',
    'TABS': '标签页',
    'CARD': '卡片',
    'DIVIDER': '分割线',
    'COLLAPSE': '折叠面板',
  };

  const key = type.toString().replace('Symbol(', '').replace(')', '');
  return typeMap[key] || key;
}

/**
 * 判断是否为容器组件
 */
export function isContainerWidget(type: symbol): boolean {
  const containerTypes = [
    WIDGET_TYPE.GRID,
    WIDGET_TYPE.TABS,
    WIDGET_TYPE.CARD,
    WIDGET_TYPE.COLLAPSE,
  ];
  return containerTypes.some(t => t === type);
}

/**
 * 获取组件默认属性
 */
export function getDefaultWidgetProps(widgetName: string): Record<string, unknown> {
  const defaults: Record<string, Record<string, unknown>> = {
    InputWidget: {
      placeholder: '请输入',
      defaultValue: '',
      disabled: false,
      clearable: true,
      maxlength: undefined,
    },
    SelectWidget: {
      placeholder: '请选择',
      options: [],
      multiple: false,
      disabled: false,
    },
    DatePickerWidget: {
      type: 'date',
      placeholder: '请选择日期',
      disabled: false,
      clearable: true,
    },
    NumberInputWidget: {
      placeholder: '请输入数字',
      min: undefined,
      max: undefined,
      step: 1,
      disabled: false,
    },
    TextareaWidget: {
      placeholder: '请输入',
      rows: 4,
      maxlength: undefined,
      showCount: false,
      disabled: false,
    },
    RadioWidget: {
      options: [],
      disabled: false,
    },
    CheckboxWidget: {
      options: [],
      disabled: false,
    },
    SwitchWidget: {
      disabled: false,
    },
    GridWidget: {
      columns: 3,
      gutter: 16,
    },
    TabsWidget: {
      tabs: [{ title: '标签1', key: 'tab1' }],
      type: 'line',
    },
    CardWidget: {
      title: '卡片标题',
      bordered: true,
      hoverable: false,
    },
  };

  return defaults[widgetName] || {};
}

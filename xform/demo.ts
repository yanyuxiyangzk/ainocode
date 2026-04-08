/**
 * Demo Schema - Sample form schema for testing
 * 示例表单Schema，用于集成测试
 */
import type { FormSchema } from '@/types/form-schema';

export const demoSchema: FormSchema = {
  id: 'demo-form-001',
  name: '用户信息表单',
  version: '1.0.0',
  description: '这是一个示例表单，包含多种组件类型',
  props: {
    layout: 'vertical',
    labelWidth: 120,
    labelAlign: 'left',
    hideRequiredMark: false,
    size: 'medium',
  },
  widgets: [
    {
      id: 'grid-001',
      type: Symbol('GRID'),
      widgetName: 'GridWidget',
      props: {
        columns: 2,
        gutter: 16,
      },
      children: [
        {
          id: 'input-001',
          type: Symbol('INPUT'),
          widgetName: 'InputWidget',
          props: {
            label: '用户名',
            placeholder: '请输入用户名',
            defaultValue: '',
            disabled: false,
            clearable: true,
            maxlength: 50,
          },
          children: [],
        },
        {
          id: 'input-002',
          type: Symbol('INPUT'),
          widgetName: 'InputWidget',
          props: {
            label: '邮箱',
            placeholder: '请输入邮箱',
            defaultValue: '',
            disabled: false,
            clearable: true,
          },
          children: [],
        },
      ],
    },
    {
      id: 'select-001',
      type: Symbol('SELECT'),
      widgetName: 'SelectWidget',
      props: {
        label: '部门',
        placeholder: '请选择部门',
        options: [
          { label: '技术部', value: 'tech' },
          { label: '运营部', value: 'ops' },
          { label: '市场部', value: 'marketing' },
        ],
        multiple: false,
        disabled: false,
      },
      children: [],
    },
    {
      id: 'date-001',
      type: Symbol('DATE_PICKER'),
      widgetName: 'DatePickerWidget',
      props: {
        label: '入职日期',
        type: 'date',
        placeholder: '请选择日期',
        disabled: false,
        clearable: true,
      },
      children: [],
    },
    {
      id: 'switch-001',
      type: Symbol('SWITCH'),
      widgetName: 'SwitchWidget',
      props: {
        label: '启用状态',
        disabled: false,
        checkedValue: true,
        uncheckedValue: false,
      },
      children: [],
    },
    {
      id: 'card-001',
      type: Symbol('CARD'),
      widgetName: 'CardWidget',
      props: {
        title: '其他信息',
        collapsible: true,
        collapsed: false,
      },
      children: [
        {
          id: 'textarea-001',
          type: Symbol('TEXTAREA'),
          widgetName: 'TextareaWidget',
          props: {
            label: '备注',
            placeholder: '请输入备注信息',
            rows: 3,
            disabled: false,
          },
          children: [],
        },
      ],
    },
  ],
};

/**
 * Simple schema for testing
 */
export const simpleSchema: FormSchema = {
  id: 'simple-form-001',
  name: '简单表单',
  version: '1.0.0',
  props: {
    layout: 'vertical',
    labelWidth: 100,
    size: 'medium',
  },
  widgets: [
    {
      id: 'name-input',
      type: Symbol('INPUT'),
      widgetName: 'InputWidget',
      props: {
        label: '姓名',
        placeholder: '请输入姓名',
        defaultValue: '',
      },
      children: [],
    },
    {
      id: 'age-input',
      type: Symbol('NUMBER'),
      widgetName: 'NumberWidget',
      props: {
        label: '年龄',
        placeholder: '请输入年龄',
        min: 0,
        max: 150,
      },
      children: [],
    },
  ],
};

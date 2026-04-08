// Field widgets auto-export
export { default as InputWidget } from './InputWidget.vue';
export { default as TextareaWidget } from './TextareaWidget.vue';
export { default as NumberWidget } from './NumberWidget.vue';
export { default as SelectWidget } from './SelectWidget.vue';
export { default as RadioGroupWidget } from './RadioGroupWidget.vue';
export { default as CheckboxGroupWidget } from './CheckboxGroupWidget.vue';
export { default as DatePickerWidget } from './DatePickerWidget.vue';
export { default as TimePickerWidget } from './TimePickerWidget.vue';
export { default as SwitchWidget } from './SwitchWidget.vue';
export { default as RateWidget } from './RateWidget.vue';
export { default as SliderWidget } from './SliderWidget.vue';
export { default as ColorPickerWidget } from './ColorPickerWidget.vue';
export { default as PictureUploadWidget } from './PictureUploadWidget.vue';
export { default as FileUploadWidget } from './FileUploadWidget.vue';
export { default as RichEditorWidget } from './RichEditorWidget.vue';
export { default as CodeEditorWidget } from './CodeEditorWidget.vue';
export { default as HtmlWidget } from './HtmlWidget.vue';
export { default as DividerWidget } from './DividerWidget.vue';
export { default as ButtonWidget } from './ButtonWidget.vue';

// Widget definitions
import { WIDGET_TYPE } from '@/types/widget-type';
import type { WidgetDefinition } from '@/types/widget-type';

// Input Widget Definition
export const InputWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.INPUT,
  name: 'InputWidget',
  label: '输入框',
  icon: 'edit',
  category: 'field',
  defaultProps: {
    placeholder: '请输入',
    defaultValue: '',
    disabled: false,
    clearable: true,
    maxlength: undefined,
    showCount: false,
    prefix: '',
    suffix: '',
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'placeholder', label: '占位符', type: 'string' },
    { name: 'defaultValue', label: '默认值', type: 'string' },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'clearable', label: '可清空', type: 'boolean' },
    { name: 'maxlength', label: '最大长度', type: 'number' },
    { name: 'showCount', label: '显示计数', type: 'boolean' },
  ],
};

// Textarea Widget Definition
export const TextareaWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.TEXTAREA,
  name: 'TextareaWidget',
  label: '文本域',
  icon: 'align-left',
  category: 'field',
  defaultProps: {
    placeholder: '请输入',
    defaultValue: '',
    disabled: false,
    rows: 4,
    maxlength: undefined,
    showCount: true,
    autosize: false,
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'placeholder', label: '占位符', type: 'string' },
    { name: 'defaultValue', label: '默认值', type: 'string' },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'rows', label: '行数', type: 'number' },
    { name: 'maxlength', label: '最大长度', type: 'number' },
    { name: 'showCount', label: '显示计数', type: 'boolean' },
    { name: 'autosize', label: '自适应高度', type: 'boolean' },
  ],
};

// Number Widget Definition
export const NumberWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.NUMBER,
  name: 'NumberWidget',
  label: '数字输入',
  icon: 'hash',
  category: 'field',
  defaultProps: {
    placeholder: '请输入数字',
    defaultValue: null,
    disabled: false,
    min: undefined,
    max: undefined,
    step: 1,
    precision: undefined,
    showButton: true,
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'placeholder', label: '占位符', type: 'string' },
    { name: 'defaultValue', label: '默认值', type: 'number' },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'min', label: '最小值', type: 'number' },
    { name: 'max', label: '最大值', type: 'number' },
    { name: 'step', label: '步进', type: 'number' },
    { name: 'precision', label: '精度', type: 'number' },
    { name: 'showButton', label: '显示按钮', type: 'boolean' },
  ],
};

// Select Widget Definition
export const SelectWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.SELECT,
  name: 'SelectWidget',
  label: '下拉选择',
  icon: 'chevron-down',
  category: 'field',
  defaultProps: {
    placeholder: '请选择',
    disabled: false,
    clearable: true,
    filterable: false,
    multiple: false,
    options: [],
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'placeholder', label: '占位符', type: 'string' },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'clearable', label: '可清空', type: 'boolean' },
    { name: 'filterable', label: '可搜索', type: 'boolean' },
    { name: 'multiple', label: '多选', type: 'boolean' },
    { name: 'options', label: '选项', type: 'options' },
  ],
};

// Radio Group Widget Definition
export const RadioGroupWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.RADIO_GROUP,
  name: 'RadioGroupWidget',
  label: '单选组',
  icon: 'circle-dot',
  category: 'field',
  defaultProps: {
    disabled: false,
    options: [],
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'options', label: '选项', type: 'options' },
  ],
};

// Checkbox Group Widget Definition
export const CheckboxGroupWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.CHECKBOX_GROUP,
  name: 'CheckboxGroupWidget',
  label: '复选组',
  icon: 'check-square',
  category: 'field',
  defaultProps: {
    disabled: false,
    options: [],
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'options', label: '选项', type: 'options' },
  ],
};

// Date Picker Widget Definition
export const DatePickerWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.DATE_PICKER,
  name: 'DatePickerWidget',
  label: '日期选择',
  icon: 'calendar',
  category: 'field',
  defaultProps: {
    type: 'date',
    placeholder: '请选择日期',
    disabled: false,
    clearable: true,
    format: 'yyyy-MM-dd',
    valueFormat: 'yyyy-MM-dd',
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'type', label: '类型', type: 'select', options: [
      { label: '日期', value: 'date' },
      { label: '日期时间', value: 'datetime' },
      { label: '日期范围', value: 'daterange' },
      { label: '月', value: 'month' },
      { label: '年', value: 'year' },
    ]},
    { name: 'placeholder', label: '占位符', type: 'string' },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'clearable', label: '可清空', type: 'boolean' },
    { name: 'format', label: '显示格式', type: 'string' },
  ],
};

// Time Picker Widget Definition
export const TimePickerWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.TIME_PICKER,
  name: 'TimePickerWidget',
  label: '时间选择',
  icon: 'clock',
  category: 'field',
  defaultProps: {
    placeholder: '请选择时间',
    disabled: false,
    clearable: true,
    format: 'HH:mm:ss',
    valueFormat: 'HH:mm:ss',
    isRange: false,
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'placeholder', label: '占位符', type: 'string' },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'clearable', label: '可清空', type: 'boolean' },
    { name: 'format', label: '显示格式', type: 'string' },
    { name: 'isRange', label: '时间范围', type: 'boolean' },
  ],
};

// Switch Widget Definition
export const SwitchWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.SWITCH,
  name: 'SwitchWidget',
  label: '开关',
  icon: 'toggle-right',
  category: 'field',
  defaultProps: {
    disabled: false,
    checkedValue: true,
    uncheckedValue: false,
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'checkedValue', label: '选中值', type: 'string' },
    { name: 'uncheckedValue', label: '未选中值', type: 'string' },
  ],
};

// Rate Widget Definition
export const RateWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.RATE,
  name: 'RateWidget',
  label: '评分',
  icon: 'star',
  category: 'field',
  defaultProps: {
    count: 5,
    disabled: false,
    allowHalf: false,
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'count', label: '星数', type: 'number' },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'allowHalf', label: '允许半星', type: 'boolean' },
  ],
};

// Slider Widget Definition
export const SliderWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.SLIDER,
  name: 'SliderWidget',
  label: '滑块',
  icon: 'sliders',
  category: 'field',
  defaultProps: {
    min: 0,
    max: 100,
    step: 1,
    disabled: false,
    range: false,
    showButton: true,
    vertical: false,
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'min', label: '最小值', type: 'number' },
    { name: 'max', label: '最大值', type: 'number' },
    { name: 'step', label: '步进', type: 'number' },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'range', label: '范围选择', type: 'boolean' },
    { name: 'showButton', label: '显示按钮', type: 'boolean' },
  ],
};

// Color Picker Widget Definition
export const ColorPickerWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.COLOR_PICKER,
  name: 'ColorPickerWidget',
  label: '颜色选择',
  icon: 'palette',
  category: 'field',
  defaultProps: {
    value: '#18A058',
    disabled: false,
    showAlpha: true,
    modes: ['rgb', 'hex', 'hsl'],
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'defaultValue', label: '默认值', type: 'color' },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'showAlpha', label: '显示透明度', type: 'boolean' },
  ],
};

// Picture Upload Widget Definition
export const PictureUploadWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.PICTURE_UPLOAD,
  name: 'PictureUploadWidget',
  label: '图片上传',
  icon: 'image',
  category: 'upload',
  defaultProps: {
    disabled: false,
    maxNumber: 5,
    accept: 'image/*',
    action: '#',
    listType: 'image-card',
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'maxNumber', label: '最大数量', type: 'number' },
    { name: 'accept', label: '接受类型', type: 'string' },
    { name: 'listType', label: '列表类型', type: 'select', options: [
      { label: '卡片', value: 'image-card' },
      { label: '列表', value: 'image-list' },
    ]},
  ],
};

// File Upload Widget Definition
export const FileUploadWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.FILE_UPLOAD,
  name: 'FileUploadWidget',
  label: '文件上传',
  icon: 'upload',
  category: 'upload',
  defaultProps: {
    disabled: false,
    maxNumber: 10,
    accept: '*',
    action: '#',
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'maxNumber', label: '最大数量', type: 'number' },
    { name: 'accept', label: '接受类型', type: 'string' },
  ],
};

// Rich Editor Widget Definition
export const RichEditorWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.RICH_EDITOR,
  name: 'RichEditorWidget',
  label: '富文本编辑器',
  icon: 'file-text',
  category: 'advanced',
  defaultProps: {
    placeholder: '请输入内容',
    disabled: false,
    height: 300,
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'placeholder', label: '占位符', type: 'string' },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'height', label: '高度', type: 'number' },
  ],
};

// HTML Widget Definition
export const HtmlWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.HTML,
  name: 'HtmlWidget',
  label: 'HTML文本',
  icon: 'code',
  category: 'advanced',
  defaultProps: {
    content: '',
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string' },
    { name: 'content', label: 'HTML内容', type: 'string' },
  ],
};

// Code Editor Widget Definition
export const CodeEditorWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.CODE_EDITOR,
  name: 'CodeEditorWidget',
  label: '代码编辑器',
  icon: 'code',
  category: 'advanced',
  defaultProps: {
    placeholder: '// Enter your code here',
    disabled: false,
    language: 'javascript',
    height: 300,
  },
  propsSchema: [
    { name: 'label', label: '标签', type: 'string', required: true },
    { name: 'placeholder', label: '占位符', type: 'string' },
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'language', label: '语言', type: 'select', options: [
      { label: 'JavaScript', value: 'javascript' },
      { label: 'TypeScript', value: 'typescript' },
      { label: 'Python', value: 'python' },
      { label: 'Java', value: 'java' },
      { label: 'HTML', value: 'html' },
      { label: 'CSS', value: 'css' },
      { label: 'SQL', value: 'sql' },
      { label: 'JSON', value: 'json' },
    ]},
    { name: 'height', label: '高度', type: 'number' },
  ],
};

// Divider Widget Definition
export const DividerWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.DIVIDER,
  name: 'DividerWidget',
  label: '分割线',
  icon: 'minus',
  category: 'field',
  defaultProps: {
    title: '',
    dashed: false,
    vertical: false,
    position: 'center',
  },
  propsSchema: [
    { name: 'title', label: '标题', type: 'string' },
    { name: 'dashed', label: '虚线', type: 'boolean' },
    { name: 'vertical', label: '垂直', type: 'boolean' },
    { name: 'position', label: '位置', type: 'select', options: [
      { label: '居左', value: 'left' },
      { label: '居中', value: 'center' },
      { label: '居右', value: 'right' },
    ]},
  ],
};

// Button Widget Definition
export const ButtonWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.BUTTON,
  name: 'ButtonWidget',
  label: '按钮',
  icon: 'square',
  category: 'field',
  defaultProps: {
    text: '按钮',
    type: 'default',
    disabled: false,
    loading: false,
    size: 'medium',
    ghost: false,
    circle: false,
    icon: '',
  },
  propsSchema: [
    { name: 'text', label: '文本', type: 'string', required: true },
    { name: 'type', label: '类型', type: 'select', options: [
      { label: '默认', value: 'default' },
      { label: '主要', value: 'primary' },
      { label: '成功', value: 'success' },
      { label: '警告', value: 'warning' },
      { label: '错误', value: 'error' },
      { label: '信息', value: 'info' },
    ]},
    { name: 'disabled', label: '禁用', type: 'boolean' },
    { name: 'loading', label: '加载中', type: 'boolean' },
    { name: 'size', label: '尺寸', type: 'select', options: [
      { label: '小', value: 'small' },
      { label: '中', value: 'medium' },
      { label: '大', value: 'large' },
    ]},
    { name: 'ghost', label: '幽灵按钮', type: 'boolean' },
    { name: 'circle', label: '圆形', type: 'boolean' },
  ],
};

// All field widget definitions array
export const ALL_FIELD_WIDGET_DEFINITIONS: WidgetDefinition[] = [
  InputWidgetDefinition,
  TextareaWidgetDefinition,
  NumberWidgetDefinition,
  SelectWidgetDefinition,
  RadioGroupWidgetDefinition,
  CheckboxGroupWidgetDefinition,
  DatePickerWidgetDefinition,
  TimePickerWidgetDefinition,
  SwitchWidgetDefinition,
  RateWidgetDefinition,
  SliderWidgetDefinition,
  ColorPickerWidgetDefinition,
  PictureUploadWidgetDefinition,
  FileUploadWidgetDefinition,
  RichEditorWidgetDefinition,
  HtmlWidgetDefinition,
  CodeEditorWidgetDefinition,
  DividerWidgetDefinition,
  ButtonWidgetDefinition,
];

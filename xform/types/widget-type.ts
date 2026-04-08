// Widget type definitions using Symbol for type-safe component identification
export const WIDGET_TYPE = {
  // Field components - Basic inputs
  INPUT: Symbol('INPUT'),
  TEXTAREA: Symbol('TEXTAREA'),
  NUMBER: Symbol('NUMBER'),
  SELECT: Symbol('SELECT'),
  RADIO_GROUP: Symbol('RADIO_GROUP'),
  CHECKBOX_GROUP: Symbol('CHECKBOX_GROUP'),
  DATE_PICKER: Symbol('DATE_PICKER'),
  TIME_PICKER: Symbol('TIME_PICKER'),
  SWITCH: Symbol('SWITCH'),
  RATE: Symbol('RATE'),
  SLIDER: Symbol('SLIDER'),
  COLOR_PICKER: Symbol('COLOR_PICKER'),

  // Upload components
  PICTURE_UPLOAD: Symbol('PICTURE_UPLOAD'),
  FILE_UPLOAD: Symbol('FILE_UPLOAD'),

  // Advanced components
  RICH_EDITOR: Symbol('RICH_EDITOR'),
  CODE_EDITOR: Symbol('CODE_EDITOR'),
  HTML: Symbol('HTML'),

  // Layout components
  DIVIDER: Symbol('DIVIDER'),
  BUTTON: Symbol('BUTTON'),

  // Container components
  GRID: Symbol('GRID'),
  TABS: Symbol('TABS'),
  CARD: Symbol('CARD'),
  COLLAPSE: Symbol('COLLAPSE'),
} as const;

export type WidgetType = typeof WIDGET_TYPE[keyof typeof WIDGET_TYPE];

// Widget category enum
export type WidgetCategory = 'field' | 'container' | 'advanced' | 'upload';

// Props schema item interface
export interface PropsSchemaItem {
  name: string;
  label: string;
  type: 'string' | 'number' | 'boolean' | 'select' | 'multi-select' | 'options' | 'color' | 'event-handler';
  required?: boolean;
  defaultValue?: any;
  options?: { label: string; value: any }[];
  min?: number;
  max?: number;
  step?: number;
  rows?: number;
  maxlength?: number;
}

// Widget definition interface
export interface WidgetDefinition {
  type: symbol;
  name: string;
  label: string;
  icon: string;
  category: WidgetCategory;
  defaultProps: Record<string, any>;
  propsSchema: PropsSchemaItem[];
}

// Widget config interface (for runtime widget instances)
export interface WidgetConfig {
  id: string;
  type: symbol;
  widgetName: string;
  props: Record<string, any>;
  children?: WidgetConfig[];
  condition?: ConditionExpression;
  events?: WidgetEventConfig[];
  rules?: ValidationRule[];
}

export interface ConditionExpression {
  field: string;
  operator: '==' | '!=' | '>' | '<' | '>=' | '<=' | 'contains' | 'empty';
  value: any;
}

export interface WidgetEventConfig {
  name: string;
  handler: string;
}

export interface ValidationRule {
  field?: string;
  name?: string;
  required?: boolean;
  type?: string;
  min?: number;
  max?: number;
  pattern?: string;
  message?: string;
  validator?: (rule: any, value: any) => boolean | Error;
  trigger?: string | string[];
}

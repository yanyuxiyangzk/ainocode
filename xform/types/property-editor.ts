import type { Component } from 'vue';

/**
 * Property editor type definitions
 */

// Property editor definition
export interface PropertyEditorDef {
  propType: string;
  component: Component;
  props?: Record<string, any>;
}

// Property type constants
export const PROP_TYPE = {
  STRING: 'string',
  NUMBER: 'number',
  BOOLEAN: 'boolean',
  SELECT: 'select',
  MULTI_SELECT: 'multi-select',
  COLOR: 'color',
  FONT_SIZE: 'font-size',
  BORDER: 'border',
  SHADOW: 'shadow',
  SPACING: 'spacing',
  EVENT_HANDLER: 'event-handler',
  OPTIONS: 'options',
  DYNAMIC_EXPRESSION: 'dynamic-expression',
  SLIDER: 'slider',
} as const;

export type PropType = typeof PROP_TYPE[keyof typeof PROP_TYPE];

// Property category
export type PropertyCategory = 'common' | 'layout' | 'validation' | 'event';

// Property schema definition
export interface PropertySchema {
  name: string;
  label: string;
  type: PropType | string;
  category: PropertyCategory;
  required?: boolean;
  defaultValue?: any;
  options?: SelectOption[];
  props?: Record<string, any>;
  condition?: ConditionExpression;
}

// Select option
export interface SelectOption {
  label: string;
  value: string | number | boolean;
}

// Condition expression for dynamic visibility
export interface ConditionExpression {
  field: string;
  operator: '==' | '!=' | '>' | '<' | '>=' | '<=' | 'contains' | 'empty';
  value: any;
}

// Event handler definition
export interface EventHandlerDef {
  name: string;
  label: string;
  handler: string;
}

// Property value update payload
export interface PropertyUpdatePayload {
  widgetId: string;
  propName: string;
  value: any;
  oldValue?: any;
}

// Common property names
export const COMMON_PROPS = {
  LABEL: 'label',
  NAME: 'name',
  PLACEHOLDER: 'placeholder',
  DEFAULT_VALUE: 'defaultValue',
  DISABLED: 'disabled',
  HIDDEN: 'hidden',
  READONLY: 'readonly',
  CLEARABLE: 'clearable',
  MAX_LENGTH: 'maxlength',
} as const;

// Layout property names
export const LAYOUT_PROPS = {
  WIDTH: 'width',
  LABEL_WIDTH: 'labelWidth',
  LABEL_ALIGN: 'labelAlign',
} as const;

// Validation property names
export const VALIDATION_PROPS = {
  REQUIRED: 'required',
  PATTERN: 'pattern',
  MIN: 'min',
  MAX: 'max',
} as const;

// Event property names
export const EVENT_PROPS = {
  ON_CLICK: 'onClick',
  ON_CHANGE: 'onChange',
  ON_BLUR: 'onBlur',
} as const;

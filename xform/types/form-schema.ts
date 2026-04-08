import type {
  WidgetConfig,
  ConditionExpression,
  WidgetEventConfig,
  ValidationRule,
  WidgetType,
} from './widget-type';

export type { WidgetConfig, ConditionExpression, WidgetEventConfig, ValidationRule, WidgetType };

export interface FormSchema {
  id: string;
  name: string;
  version: string;
  description?: string;
  props: FormProps;
  widgets: WidgetConfig[];
  events?: EventConfig[];
  rules?: ValidationRule[];
}

export interface FormProps {
  layout?: 'horizontal' | 'vertical' | 'inline';
  labelWidth?: number | 'auto';
  labelAlign?: 'left' | 'right';
  hideRequiredMark?: boolean;
  size?: 'small' | 'medium' | 'large';
}

export interface EventConfig {
  name: string;
  handler: string;
}

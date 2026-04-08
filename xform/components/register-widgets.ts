// Auto-register all widgets to the registry
import { widgetRegistry } from '@/composables/useWidgetRegistry';

// Field widgets
import * as fieldWidgets from '@/components/widgets/field';
import {
  ALL_FIELD_WIDGET_DEFINITIONS,
} from '@/components/widgets/field';

// Container widgets
import * as containerWidgets from '@/components/widgets/container';
import {
  ALL_CONTAINER_WIDGET_DEFINITIONS,
} from '@/components/widgets/container';

// Map widget names to components
const fieldComponents: Record<string, any> = {
  InputWidget: fieldWidgets.InputWidget,
  TextareaWidget: fieldWidgets.TextareaWidget,
  NumberWidget: fieldWidgets.NumberWidget,
  SelectWidget: fieldWidgets.SelectWidget,
  RadioGroupWidget: fieldWidgets.RadioGroupWidget,
  CheckboxGroupWidget: fieldWidgets.CheckboxGroupWidget,
  DatePickerWidget: fieldWidgets.DatePickerWidget,
  TimePickerWidget: fieldWidgets.TimePickerWidget,
  SwitchWidget: fieldWidgets.SwitchWidget,
  RateWidget: fieldWidgets.RateWidget,
  SliderWidget: fieldWidgets.SliderWidget,
  ColorPickerWidget: fieldWidgets.ColorPickerWidget,
  PictureUploadWidget: fieldWidgets.PictureUploadWidget,
  FileUploadWidget: fieldWidgets.FileUploadWidget,
  RichEditorWidget: fieldWidgets.RichEditorWidget,
  CodeEditorWidget: fieldWidgets.CodeEditorWidget,
  HtmlWidget: fieldWidgets.HtmlWidget,
  DividerWidget: fieldWidgets.DividerWidget,
  ButtonWidget: fieldWidgets.ButtonWidget,
};

const containerComponents: Record<string, any> = {
  GridWidget: containerWidgets.GridWidget,
  TabsWidget: containerWidgets.TabsWidget,
  CardWidget: containerWidgets.CardWidget,
  CollapseWidget: containerWidgets.CollapseWidget,
  DividerWidget: containerWidgets.DividerWidget,
};

/**
 * Register all widgets to the registry
 */
export function registerAllWidgets() {
  // Register field widgets
  ALL_FIELD_WIDGET_DEFINITIONS.forEach((definition) => {
    const component = fieldComponents[definition.name];
    if (component) {
      widgetRegistry.register(definition, component);
    } else {
      console.warn(`[WidgetRegistry] No component found for ${definition.name}`);
    }
  });

  // Register container widgets
  ALL_CONTAINER_WIDGET_DEFINITIONS.forEach((definition) => {
    const component = containerComponents[definition.name];
    if (component) {
      widgetRegistry.register(definition, component);
    } else {
      console.warn(`[WidgetRegistry] No component found for ${definition.name}`);
    }
  });

  console.log(`[WidgetRegistry] Registered ${widgetRegistry.getAllDefinitions().length} widgets`);
}

/**
 * Auto-register on import (for library mode)
 */
registerAllWidgets();

export { fieldWidgets, containerWidgets };

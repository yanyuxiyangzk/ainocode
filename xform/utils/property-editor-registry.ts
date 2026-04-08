import type { Component } from 'vue';
import type { PropertyEditorDef, PropType } from '@/types/property-editor';

// Import all editors
import StringEditor from '@/components/form-designer/designer-props/editors/StringEditor.vue';
import NumberEditor from '@/components/form-designer/designer-props/editors/NumberEditor.vue';
import BooleanEditor from '@/components/form-designer/designer-props/editors/BooleanEditor.vue';
import SelectEditor from '@/components/form-designer/designer-props/editors/SelectEditor.vue';
import ColorEditor from '@/components/form-designer/designer-props/editors/ColorEditor.vue';
import OptionsEditor from '@/components/form-designer/designer-props/editors/OptionsEditor.vue';
import EventHandlerEditor from '@/components/form-designer/designer-props/editors/EventHandlerEditor.vue';
import SliderEditor from '@/components/form-designer/designer-props/editors/SliderEditor.vue';
import BorderEditor from '@/components/form-designer/designer-props/editors/BorderEditor.vue';

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

/**
 * Property Editor Registry
 * Factory pattern implementation for property editors
 */
class PropertyEditorRegistry {
  private registry = new Map<string, Component>();
  private optionsRegistry = new Map<string, Record<string, any>>();

  /**
   * Register a property editor
   */
  register(propType: string, component: Component, options?: Record<string, any>): void {
    this.registry.set(propType, component);
    if (options) {
      this.optionsRegistry.set(propType, options);
    }
  }

  /**
   * Get editor component by property type
   */
  get(propType: string): Component | undefined {
    return this.registry.get(propType);
  }

  /**
   * Check if editor is registered for property type
   */
  has(propType: string): boolean {
    return this.registry.has(propType);
  }

  /**
   * Get options for a property type
   */
  getOptions(propType: string): Record<string, any> | undefined {
    return this.optionsRegistry.get(propType);
  }

  /**
   * Get all registered property types
   */
  getRegisteredTypes(): string[] {
    return Array.from(this.registry.keys());
  }

  /**
   * Get default editor when type is not found
   */
  getDefaultEditor(): Component {
    return this.registry.get(PROP_TYPE.STRING) ?? StringEditor;
  }
}

// Singleton instance
export const propertyEditorRegistry = new PropertyEditorRegistry();

// Register built-in editors
propertyEditorRegistry.register(PROP_TYPE.STRING, StringEditor);
propertyEditorRegistry.register(PROP_TYPE.NUMBER, NumberEditor);
propertyEditorRegistry.register(PROP_TYPE.BOOLEAN, BooleanEditor);
propertyEditorRegistry.register(PROP_TYPE.SELECT, SelectEditor);
propertyEditorRegistry.register(PROP_TYPE.COLOR, ColorEditor);
propertyEditorRegistry.register(PROP_TYPE.OPTIONS, OptionsEditor);
propertyEditorRegistry.register(PROP_TYPE.EVENT_HANDLER, EventHandlerEditor);
propertyEditorRegistry.register(PROP_TYPE.SLIDER, SliderEditor);
propertyEditorRegistry.register(PROP_TYPE.BORDER, BorderEditor);

// Export PROP_TYPE for convenience
export type { PropType };

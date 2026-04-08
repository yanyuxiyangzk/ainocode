import { ref, type Ref } from 'vue';
import type { WidgetDefinition, WidgetCategory } from '@/types/widget-type';

class WidgetRegistry {
  private registry = new Map<symbol, WidgetDefinition>();
  private componentMap = new Map<symbol, any>();

  register(definition: WidgetDefinition, component: any): void {
    this.registry.set(definition.type, definition);
    this.componentMap.set(definition.type, component);
  }

  getDefinition(type: symbol): WidgetDefinition | undefined {
    return this.registry.get(type);
  }

  getComponent(type: symbol): any {
    return this.componentMap.get(type);
  }

  getAllDefinitions(): WidgetDefinition[] {
    return Array.from(this.registry.values());
  }

  getByCategory(category: WidgetCategory): WidgetDefinition[] {
    return this.getAllDefinitions().filter(d => d.category === category);
  }

  has(type: symbol): boolean {
    return this.registry.has(type);
  }

  unregister(type: symbol): void {
    this.registry.delete(type);
    this.componentMap.delete(type);
  }

  clear(): void {
    this.registry.clear();
    this.componentMap.clear();
  }
}

export const widgetRegistry = new WidgetRegistry();

export interface UseWidgetRegistryReturn {
  registry: WidgetRegistry;
  fieldWidgets: Ref<WidgetDefinition[]>;
  containerWidgets: Ref<WidgetDefinition[]>;
  advancedWidgets: Ref<WidgetDefinition[]>;
  uploadWidgets: Ref<WidgetDefinition[]>;
  registerWidget: (definition: WidgetDefinition, component: any) => void;
  getComponent: (type: symbol) => any;
  getDefinition: (type: symbol) => WidgetDefinition | undefined;
}

export function useWidgetRegistry(): UseWidgetRegistryReturn {
  const fieldWidgets = ref<WidgetDefinition[]>(widgetRegistry.getByCategory('field'));
  const containerWidgets = ref<WidgetDefinition[]>(widgetRegistry.getByCategory('container'));
  const advancedWidgets = ref<WidgetDefinition[]>(widgetRegistry.getByCategory('advanced'));
  const uploadWidgets = ref<WidgetDefinition[]>(widgetRegistry.getByCategory('upload'));

  function registerWidget(definition: WidgetDefinition, component: any) {
    widgetRegistry.register(definition, component);

    if (definition.category === 'field') {
      fieldWidgets.value = widgetRegistry.getByCategory('field');
    } else if (definition.category === 'container') {
      containerWidgets.value = widgetRegistry.getByCategory('container');
    } else if (definition.category === 'advanced') {
      advancedWidgets.value = widgetRegistry.getByCategory('advanced');
    } else if (definition.category === 'upload') {
      uploadWidgets.value = widgetRegistry.getByCategory('upload');
    }
  }

  return {
    registry: widgetRegistry,
    fieldWidgets,
    containerWidgets,
    advancedWidgets,
    uploadWidgets,
    registerWidget,
    getComponent: (type: symbol) => widgetRegistry.getComponent(type),
    getDefinition: (type: symbol) => widgetRegistry.getDefinition(type),
  };
}

// Widget Registry - Component registration system
import type { Component } from 'vue';
import type { WidgetDefinition, WidgetType, WidgetCategory } from '@/types/widget-type';

/**
 * Widget Registry - Manages widget definitions and components
 * Uses Symbol as type identifier for type-safe component registration
 */
class WidgetRegistry {
  private registry = new Map<symbol, WidgetDefinition>();
  private componentMap = new Map<symbol, Component>();
  private categoryMap = new Map<WidgetCategory, WidgetDefinition[]>();

  /**
   * Register a widget definition with its component
   */
  register(definition: WidgetDefinition, component: Component): void {
    // Validate definition
    if (!definition.type || !definition.name) {
      console.warn('[WidgetRegistry] Invalid definition:', definition);
      return;
    }

    this.registry.set(definition.type, definition);
    this.componentMap.set(definition.type, component);

    // Update category map
    const categoryList = this.categoryMap.get(definition.category) || [];
    categoryList.push(definition);
    this.categoryMap.set(definition.category, categoryList);
  }

  /**
   * Unregister a widget by type
   */
  unregister(type: symbol): void {
    const definition = this.registry.get(type);
    if (definition) {
      // Remove from category map
      const categoryList = this.categoryMap.get(definition.category) || [];
      const index = categoryList.findIndex(d => d.type === type);
      if (index !== -1) {
        categoryList.splice(index, 1);
        this.categoryMap.set(definition.category, categoryList);
      }
    }
    this.registry.delete(type);
    this.componentMap.delete(type);
  }

  /**
   * Get widget definition by type
   */
  getDefinition(type: symbol): WidgetDefinition | undefined {
    return this.registry.get(type);
  }

  /**
   * Get component by type
   */
  getComponent(type: symbol): Component | undefined {
    return this.componentMap.get(type);
  }

  /**
   * Get all registered definitions
   */
  getAllDefinitions(): WidgetDefinition[] {
    return Array.from(this.registry.values());
  }

  /**
   * Get definitions by category
   */
  getByCategory(category: WidgetCategory): WidgetDefinition[] {
    return this.categoryMap.get(category) || [];
  }

  /**
   * Get field widgets
   */
  getFieldWidgets(): WidgetDefinition[] {
    return this.getByCategory('field');
  }

  /**
   * Get container widgets
   */
  getContainerWidgets(): WidgetDefinition[] {
    return this.getByCategory('container');
  }

  /**
   * Get upload widgets
   */
  getUploadWidgets(): WidgetDefinition[] {
    return this.getByCategory('upload');
  }

  /**
   * Get advanced widgets
   */
  getAdvancedWidgets(): WidgetDefinition[] {
    return this.getByCategory('advanced');
  }

  /**
   * Check if a type is registered
   */
  has(type: symbol): boolean {
    return this.registry.has(type);
  }

  /**
   * Clear all registrations
   */
  clear(): void {
    this.registry.clear();
    this.componentMap.clear();
    this.categoryMap.clear();
  }

  /**
   * Get registry size
   */
  get size(): number {
    return this.registry.size;
  }
}

// Singleton instance
export const widgetRegistry = new WidgetRegistry();

// Auto-register utility
export function registerWidget(definition: WidgetDefinition, component: Component): void {
  widgetRegistry.register(definition, component);
}

// Helper to create widget from definition
export function createWidget(type: symbol, props: Record<string, any> = {}) {
  const definition = widgetRegistry.getDefinition(type);
  if (!definition) {
    throw new Error(`Widget type ${String(type)} not registered`);
  }

  return {
    id: `widget-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
    type,
    widgetName: definition.name,
    props: { ...definition.defaultProps, ...props },
    children: [],
  };
}

// Export WidgetDefinition type for external use
export type { WidgetDefinition } from '@/types/widget-type';

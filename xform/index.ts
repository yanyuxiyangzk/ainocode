// Vue Aicoding Form Designer - Main Entry
// 表单设计器组件库主入口文件

// Types
export * from './types/widget-type';
export * from './types/form-schema';
export * from './types/designer-context';

// Stores
export { useDesignerStore, useHistoryStore, useSchemaStore } from './stores';
export { createFormRendererContext, provideFormRenderer, useFormRenderer } from './composables/useFormRenderer';

// Widget Registry
export { useWidgetRegistry, widgetRegistry } from './composables/useWidgetRegistry';

// Utils
export * from './utils/widget-registry';
export * from './utils/schema-transformer';

// Form Designer Components
export { default as FormDesigner } from './components/form-designer/FormDesigner.vue';
export { default as FormDesignerProvider } from './components/form-designer/FormDesignerProvider.vue';

// Form Renderer Components
export { default as FormRenderer } from './components/form-renderer/FormRenderer.vue';
export { default as FormRendererProvider } from './components/form-renderer/FormRendererProvider.vue';
export { default as ContainerRenderer } from './components/form-renderer/ContainerRenderer.vue';
export { default as FieldRenderer } from './components/form-renderer/FieldRenderer.vue';

// Field Widgets
export * from './components/widgets/field';

// Container Widgets
export * from './components/widgets/container';

// Auto-register all widgets
import './components/register-widgets';

// Version
export const VERSION = '1.0.0';
export const NAME = 'vue-aicoding-form-designer';

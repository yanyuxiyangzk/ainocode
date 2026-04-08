import type { InjectionKey, Ref } from 'vue';
import type { FormSchema, WidgetConfig } from './form-schema';

export type DesignerMode = 'design' | 'preview' | 'json';

export interface DesignerState {
  selectedWidgetId: string | null;
  hoveredWidgetId: string | null;
  clipboardData: WidgetConfig | null;
  designerMode: DesignerMode;
  canvasSize: { width: number; height: number | 'auto' };
  isDirty: boolean;
}

export interface DesignerActions {
  selectWidget: (id: string | null) => void;
  addWidget: (parentId: string | null, widget: WidgetConfig, index?: number) => void;
  removeWidget: (id: string) => void;
  updateWidgetProps: (id: string, props: Record<string, any>) => void;
  moveWidget: (fromId: string, toId: string | null, index: number) => void;
  setSchema: (schema: FormSchema) => void;
  setDesignerMode: (mode: DesignerMode) => void;
  copyWidget: (widget: WidgetConfig) => void;
  cutWidget: (widget: WidgetConfig) => void;
  pasteWidget: (parentId: string | null, index?: number) => void;
  undo: () => void;
  redo: () => void;
}

export interface DesignerContext {
  state: DesignerState;
  schema: Ref<FormSchema | null>;
  selectedWidget: Ref<WidgetConfig | null>;
  actions: DesignerActions;
}

export const DESIGNER_CONTEXT_KEY: InjectionKey<DesignerContext> = Symbol('designerContext');

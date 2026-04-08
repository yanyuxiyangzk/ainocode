import { inject, provide, computed, type InjectionKey, type Ref } from 'vue';
import type { FormSchema, WidgetConfig } from '@/types/form-schema';
import type { DesignerContext, DesignerState, DesignerActions, DesignerMode } from '@/types/designer-context';
import { DESIGNER_CONTEXT_KEY } from '@/types/designer-context';

export function createDesignerContext(
  state: DesignerState,
  schema: Ref<FormSchema | null>,
  actions: DesignerActions
): DesignerContext {
  return { state, schema, actions };
}

export function provideDesignerContext(context: DesignerContext) {
  provide(DESIGNER_CONTEXT_KEY, context);
}

export function useDesignerContext(): DesignerContext {
  const context = inject(DESIGNER_CONTEXT_KEY);
  if (!context) {
    throw new Error('useDesignerContext must be used within a DesignerProvider');
  }
  return context;
}

export function useDesigner() {
  const context = useDesignerContext();

  const selectedWidget = computed(() => {
    return context.schema.value
      ? findWidgetById(context.schema.value.widgets, context.state.selectedWidgetId)
      : null;
  });

  const canUndo = computed(() => context.state.selectedWidgetId !== null);
  const canRedo = computed(() => context.state.selectedWidgetId !== null);
  const hasClipboard = computed(() => context.state.clipboardData !== null);

  return {
    schema: context.schema,
    selectedWidget,
    selectedWidgetId: computed(() => context.state.selectedWidgetId),
    hoveredWidgetId: computed(() => context.state.hoveredWidgetId),
    clipboardData: computed(() => context.state.clipboardData),
    designerMode: computed(() => context.state.designerMode),
    canvasSize: computed(() => context.state.canvasSize),
    isDirty: computed(() => context.state.isDirty),
    canUndo,
    canRedo,
    hasClipboard,
    ...context.actions,
  };
}

function findWidgetById(widgets: WidgetConfig[], id: string | null): WidgetConfig | null {
  if (!id) return null;
  for (const widget of widgets) {
    if (widget.id === id) return widget;
    if (widget.children) {
      const found = findWidgetById(widget.children, id);
      if (found) return found;
    }
  }
  return null;
}

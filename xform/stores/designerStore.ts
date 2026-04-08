import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { FormSchema, WidgetConfig } from '@/types/form-schema';
import type { DesignerMode } from '@/types/designer-context';
import { useHistoryStore } from './historyStore';

function findWidgetById(widgets: WidgetConfig[], id: string): WidgetConfig | null {
  for (const widget of widgets) {
    if (widget.id === id) return widget;
    if (widget.children) {
      const found = findWidgetById(widget.children, id);
      if (found) return found;
    }
  }
  return null;
}

function removeWidgetFromTree(widgets: WidgetConfig[], id: string): WidgetConfig | null {
  const index = widgets.findIndex(w => w.id === id);
  if (index !== -1) {
    return widgets.splice(index, 1)[0];
  }
  for (const widget of widgets) {
    if (widget.children) {
      const found = removeWidgetFromTree(widget.children, id);
      if (found) return found;
    }
  }
  return null;
}

export const useDesignerStore = defineStore('designer', () => {
  // State
  const schema = ref<FormSchema | null>(null);
  const selectedWidgetId = ref<string | null>(null);
  const hoveredWidgetId = ref<string | null>(null);
  const clipboardData = ref<WidgetConfig | null>(null);
  const designerMode = ref<DesignerMode>('design');
  const canvasSize = ref({ width: 1200, height: 'auto' as const | number });
  const isDirty = ref(false);

  // Computed
  const selectedWidget = computed(() => {
    if (!schema.value || !selectedWidgetId.value) return null;
    return findWidgetById(schema.value.widgets, selectedWidgetId.value);
  });

  // Actions
  function setSchema(newSchema: FormSchema) {
    schema.value = newSchema;
    isDirty.value = true;
  }

  function selectWidget(widgetId: string | null) {
    selectedWidgetId.value = widgetId;
  }

  function hoverWidget(widgetId: string | null) {
    hoveredWidgetId.value = widgetId;
  }

  function addWidget(parentId: string | null, widget: WidgetConfig, index?: number) {
    if (!schema.value) return;

    const historyStore = useHistoryStore();
    historyStore.pushState(schema.value);

    if (parentId === null) {
      if (index !== undefined) {
        schema.value.widgets.splice(index, 0, widget);
      } else {
        schema.value.widgets.push(widget);
      }
    } else {
      const parent = findWidgetById(schema.value.widgets, parentId);
      if (parent?.children) {
        if (index !== undefined) {
          parent.children.splice(index, 0, widget);
        } else {
          parent.children.push(widget);
        }
      }
    }
    isDirty.value = true;
  }

  function removeWidget(widgetId: string) {
    if (!schema.value) return;

    const historyStore = useHistoryStore();
    historyStore.pushState(schema.value);

    removeWidgetFromTree(schema.value.widgets, widgetId);
    if (selectedWidgetId.value === widgetId) {
      selectedWidgetId.value = null;
    }
    isDirty.value = true;
  }

  function updateWidgetProps(widgetId: string, props: Record<string, any>) {
    if (!schema.value) return;

    const historyStore = useHistoryStore();
    historyStore.pushState(schema.value);

    const widget = findWidgetById(schema.value.widgets, widgetId);
    if (widget) {
      widget.props = { ...widget.props, ...props };
      isDirty.value = true;
    }
  }

  function moveWidget(fromId: string, toId: string | null, index: number) {
    if (!schema.value) return;

    const historyStore = useHistoryStore();
    historyStore.pushState(schema.value);

    const widget = removeWidgetFromTree(schema.value.widgets, fromId);
    if (!widget) return;

    if (toId === null) {
      schema.value.widgets.splice(index, 0, widget);
    } else {
      const parent = findWidgetById(schema.value.widgets, toId);
      if (parent?.children) {
        parent.children.splice(index, 0, widget);
      }
    }
    isDirty.value = true;
  }

  function setDesignerMode(mode: DesignerMode) {
    designerMode.value = mode;
  }

  function copyWidget(widget: WidgetConfig) {
    clipboardData.value = JSON.parse(JSON.stringify(widget));
  }

  function cutWidget(widget: WidgetConfig) {
    clipboardData.value = JSON.parse(JSON.stringify(widget));
    removeWidget(widget.id);
  }

  function pasteWidget(parentId: string | null, index?: number) {
    if (!clipboardData.value || !schema.value) return;

    const historyStore = useHistoryStore();
    historyStore.pushState(schema.value);

    const newWidget = JSON.parse(JSON.stringify(clipboardData.value));
    newWidget.id = `${newWidget.id}-copy-${Date.now()}`;

    addWidget(parentId, newWidget, index);
  }

  function markClean() {
    isDirty.value = false;
  }

  return {
    // State
    schema,
    selectedWidgetId,
    hoveredWidgetId,
    clipboardData,
    designerMode,
    canvasSize,
    isDirty,

    // Computed
    selectedWidget,

    // Actions
    setSchema,
    selectWidget,
    hoverWidget,
    addWidget,
    removeWidget,
    updateWidgetProps,
    moveWidget,
    setDesignerMode,
    copyWidget,
    cutWidget,
    pasteWidget,
    markClean,
  };
});

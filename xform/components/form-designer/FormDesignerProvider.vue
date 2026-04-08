<script setup lang="ts">
import { reactive, computed, watch } from 'vue';
import { useDesignerStore, useHistoryStore } from '@/stores';
import { useDesignerContext, createDesignerContext, provideDesignerContext } from '@/composables/useDesigner';
import type { FormSchema, WidgetConfig } from '@/types/form-schema';
import type { DesignerMode } from '@/types/designer-context';

const props = defineProps<{
  initialSchema?: FormSchema;
}>();

const designerStore = useDesignerStore();
const historyStore = useHistoryStore();

// Initialize schema
if (props.initialSchema) {
  designerStore.setSchema(props.initialSchema);
} else if (!designerStore.schema) {
  designerStore.setSchema({
    id: `form-${Date.now()}`,
    name: '未命名表单',
    version: '1.0.0',
    props: {
      layout: 'vertical',
      labelWidth: 120,
      size: 'medium',
    },
    widgets: [],
  });
}

// Create designer actions
const actions = {
  selectWidget: (id: string | null) => {
    designerStore.selectWidget(id);
  },

  addWidget: (parentId: string | null, widget: WidgetConfig, index?: number) => {
    designerStore.addWidget(parentId, widget, index);
  },

  removeWidget: (id: string) => {
    designerStore.removeWidget(id);
  },

  updateWidgetProps: (id: string, props: Record<string, any>) => {
    designerStore.updateWidgetProps(id, props);
  },

  moveWidget: (fromId: string, toId: string | null, index: number) => {
    designerStore.moveWidget(fromId, toId, index);
  },

  setSchema: (schema: FormSchema) => {
    designerStore.setSchema(schema);
  },

  setDesignerMode: (mode: DesignerMode) => {
    designerStore.setDesignerMode(mode);
  },

  copyWidget: (widget: WidgetConfig) => {
    designerStore.copyWidget(widget);
  },

  cutWidget: (widget: WidgetConfig) => {
    designerStore.cutWidget(widget);
  },

  pasteWidget: (parentId: string | null, index?: number) => {
    designerStore.pasteWidget(parentId, index);
  },

  undo: () => {
    const prevState = historyStore.undo();
    if (prevState) {
      designerStore.setSchema(prevState);
    }
  },

  redo: () => {
    const nextState = historyStore.redo();
    if (nextState) {
      designerStore.setSchema(nextState);
    }
  },
};

// Create reactive state for context
const state = reactive({
  selectedWidgetId: designerStore.selectedWidgetId,
  hoveredWidgetId: designerStore.hoveredWidgetId,
  clipboardData: designerStore.clipboardData,
  designerMode: designerStore.designerMode,
  canvasSize: designerStore.canvasSize,
  isDirty: designerStore.isDirty,
});

// Sync state from store
watch(() => designerStore.selectedWidgetId, (val) => { state.selectedWidgetId = val; });
watch(() => designerStore.hoveredWidgetId, (val) => { state.hoveredWidgetId = val; });
watch(() => designerStore.clipboardData, (val) => { state.clipboardData = val; });
watch(() => designerStore.designerMode, (val) => { state.designerMode = val; });
watch(() => designerStore.canvasSize, (val) => { state.canvasSize = val; });
watch(() => designerStore.isDirty, (val) => { state.isDirty = val; });

// Create and provide context
const schemaRef = computed(() => designerStore.schema);
const selectedWidgetRef = computed(() => designerStore.selectedWidget);

const context = createDesignerContext(state, schemaRef, actions);
provideDesignerContext(context);

// Expose for parent components
defineExpose({
  schema: schemaRef,
  selectedWidget: selectedWidgetRef,
  actions,
});
</script>

<template>
  <div class="form-designer-provider">
    <slot />
  </div>
</template>

<style scoped>
.form-designer-provider {
  width: 100%;
  height: 100%;
}
</style>

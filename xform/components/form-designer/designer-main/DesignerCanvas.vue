<script setup lang="ts">
import { computed, ref, provide, h } from 'vue';
import { NEmpty, NButton } from 'naive-ui';
import { VueDraggable } from 'vue-draggable-plus';
import { useDesigner } from '@/composables/useDesigner';
import { useWidgetRegistry } from '@/composables/useWidgetRegistry';
import type { WidgetConfig } from '@/types/form-schema';

const { schema, selectedWidgetId, designerMode, selectWidget, addWidget, moveWidget, removeWidget, setDesignerMode } = useDesigner();
const { getComponent } = useWidgetRegistry();

const isDragging = ref(false);
const dropTargetId = ref<string | null>(null);

provide('isDragging', isDragging);
provide('dropTargetId', dropTargetId);

const widgets = computed(() => schema.value?.widgets ?? []);

const canvasStyle = computed(() => ({
  width: schema.value?.props?.canvasWidth ?? 1200,
  minHeight: 400,
}));

function handleWidgetClick(widget: WidgetConfig, event: MouseEvent) {
  event.stopPropagation();
  selectWidget(widget.id);
}

function handleCanvasClick() {
  selectWidget(null);
}

function handleDragAdd(evt: any) {
  const data = evt.data;
  if (!data) return;

  try {
    const widgetData = typeof data === 'string' ? JSON.parse(data) : data;
    const newWidget: WidgetConfig = {
      id: `${widgetData.widgetName?.toLowerCase()}-${Date.now()}`,
      type: Symbol(widgetData.type),
      widgetName: widgetData.widgetName,
      props: widgetData.defaultProps ?? {},
      children: [],
    };
    addWidget(null, newWidget, evt.newIndex);
  } catch (err) {
    console.error('Failed to add widget:', err);
  }
}

function handleDragEnd(evt: any) {
  isDragging.value = false;
  dropTargetId.value = null;

  if (evt.from !== evt.to || evt.oldIndex !== evt.newIndex) {
    const widgetId = evt.item.dataset.widgetId;
    if (widgetId) {
      moveWidget(widgetId, null, evt.newIndex);
    }
  }
}

function renderWidgetContent(widget: WidgetConfig) {
  const component = getComponent(widget.type);
  if (!component) {
    return h('div', { class: 'unknown-widget' }, `Unknown: ${widget.widgetName}`);
  }

  return h(component, {
    ...widget.props,
    selected: selectedWidgetId.value === widget.id,
  });
}
</script>

<template>
  <div class="designer-canvas" @click="handleCanvasClick">
    <div class="canvas-container" :style="canvasStyle">
      <div v-if="designerMode === 'preview'" class="preview-mode">
        <div v-if="widgets.length === 0" class="empty-state">
          <NEmpty description="预览模式下暂无内容">
            <template #extra>
              <NButton @click="setDesignerMode('design')">返回设计模式</NButton>
            </template>
          </NEmpty>
        </div>
        <div v-else class="preview-widgets">
          <div v-for="widget in widgets" :key="widget.id" class="preview-widget">
            <component :is="getComponent(widget.type)" v-bind="widget.props" />
          </div>
        </div>
      </div>

      <div v-else-if="designerMode === 'json'" class="json-mode">
        <pre class="json-content">{{ JSON.stringify(schema, null, 2) }}</pre>
      </div>

      <div v-else class="design-mode">
        <VueDraggable
          v-model="schema!.widgets"
          class="widget-drop-zone"
          :animation="200"
          group="widgets"
          ghost-class="ghost"
          chosen-class="chosen"
          drag-class="dragging"
          handle=".drag-handle"
          @add="handleDragAdd"
          @end="handleDragEnd"
        >
          <div
            v-for="widget in widgets"
            :key="widget.id"
            class="widget-wrapper"
            :class="{ selected: selectedWidgetId === widget.id }"
            :data-widget-id="widget.id"
            @click.stop="selectWidget(widget.id)"
          >
            <div class="widget-actions">
              <span class="drag-handle">⋮⋮</span>
              <span class="widget-name">{{ widget.widgetName }}</span>
              <button class="delete-btn" @click.stop="removeWidget(widget.id)">×</button>
            </div>
            <div class="widget-content">
              <component :is="getComponent(widget.type)" v-bind="widget.props" />
            </div>
          </div>
        </VueDraggable>

        <div v-if="widgets.length === 0" class="empty-state">
          <NEmpty description="从左侧拖入组件开始设计">
            <template #extra>
              <NButton @click="addWidget(null, { id: `input-${Date.now()}`, type: Symbol('INPUT'), widgetName: 'InputWidget', props: { label: '新输入框' }, children: [] })">
                添加输入框
              </NButton>
            </template>
          </NEmpty>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.designer-canvas {
  flex: 1;
  height: 100%;
  overflow: auto;
  background: #f5f5f5;
  padding: 24px;
}

.canvas-container {
  margin: 0 auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  min-height: 400px;
}

.widget-drop-zone {
  min-height: 200px;
  padding: 16px;
}

.widget-wrapper {
  position: relative;
  margin-bottom: 12px;
  padding: 8px;
  border: 2px solid transparent;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.widget-wrapper:hover {
  border-color: #e8e8e8;
}

.widget-wrapper.selected {
  border-color: #1890ff;
  box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.2);
}

.widget-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  background: #f5f5f5;
  border-radius: 4px;
  margin-bottom: 8px;
}

.drag-handle {
  cursor: grab;
  color: #999;
  font-size: 14px;
}

.drag-handle:active {
  cursor: grabbing;
}

.widget-name {
  flex: 1;
  font-size: 12px;
  color: #666;
}

.delete-btn {
  width: 20px;
  height: 20px;
  border: none;
  background: transparent;
  color: #999;
  font-size: 16px;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.delete-btn:hover {
  background: #ff4d4f;
  color: #fff;
}

.widget-content {
  pointer-events: none;
}

.widget-content > * {
  pointer-events: auto;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.preview-mode,
.json-mode {
  padding: 24px;
}

.json-content {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 6px;
  overflow: auto;
  font-size: 12px;
  line-height: 1.6;
}

.ghost {
  opacity: 0.5;
  background: #e6f4ff;
  border: 2px dashed #1890ff;
}

.chosen {
  background: #f0f9ff;
}

.dragging {
  opacity: 0.8;
  transform: rotate(2deg);
}
</style>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { NCollapse, NCollapseItem, NButton, NIcon, NSwitch, NSpace, NInput } from 'naive-ui';
import { PlusOutlined, CloseOutlined } from '@vicons/antd';
import { VueDraggable } from 'vue-draggable-plus';
import { useDesigner } from '@/composables/useDesigner';
import type { WidgetConfig } from '@/types/form-schema';

const props = defineProps<{
  accordion?: boolean;
  children?: WidgetConfig[];
  selected?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:accordion', val: boolean): void;
  (e: 'addPanel'): void;
  (e: 'removePanel', index: number): void;
  (e: 'update:panelTitle', payload: { index: number; title: string }): void;
}>();

const { selectedWidgetId, selectWidget } = useDesigner();

const isAccordion = computed({
  get: () => props.accordion ?? false,
  set: (val) => emit('update:accordion', val),
});

const panels = computed(() => props.children ?? []);

const expandedNames = ref<string[]>([]);

function handlePanelClick(widget: WidgetConfig, event: MouseEvent) {
  event.stopPropagation();
  selectWidget(widget.id);
}

function handleChildClick(child: WidgetConfig, event: MouseEvent) {
  event.stopPropagation();
  selectWidget(child.id);
}
</script>

<template>
  <div class="collapse-widget" :class="{ selected }">
    <div class="collapse-header" v-if="selected">
      <NSpace :size="12" align="center">
        <span class="config-label">手风琴模式:</span>
        <NSwitch
          :value="isAccordion"
          size="small"
          @update:value="(val: boolean) => emit('update:accordion', val)"
        />
        <NButton size="small" @click="emit('addPanel')">
          <template #icon>
            <NIcon :component="PlusOutlined" />
          </template>
          添加面板
        </NButton>
      </NSpace>
    </div>

    <NCollapse
      :accordion="isAccordion"
      :expanded-names="expandedNames"
      @update:expanded-names="(val: string[]) => expandedNames = val"
      class="collapse-content"
    >
      <NCollapseItem
        v-for="(panel, index) in panels"
        :key="panel.id || index"
        :name="panel.id || `panel-${index}`"
      >
        <template #header>
          <div class="panel-header-wrapper">
            <NInput
              :value="panel.props?.title ?? `面板${index + 1}`"
              size="tiny"
              style="width: 120px;"
              @click.stop
              @update:value="(val: string) => emit('update:panelTitle', { index, title: val })"
            />
            <NButton
              v-if="panels.length > 1"
              quaternary
              circle
              size="tiny"
              class="close-btn"
              @click.stop="emit('removePanel', index)"
            >
              <template #icon>
                <NIcon :component="CloseOutlined" />
              </template>
            </NButton>
          </div>
        </template>

        <VueDraggable
          :list="panel.children ?? []"
          group="widgets"
          :animation="200"
          ghost-class="ghost"
          class="panel-content-drop-zone"
          @click="handlePanelClick(panel, $event)"
        >
          <div
            v-for="child in (panel.children ?? [])"
            :key="child.id"
            class="child-widget"
            :class="{ selected: selectedWidgetId === child.id }"
            @click="handleChildClick(child, $event)"
          >
            <slot :name="`panel-${index}`" :widget="child" />
          </div>
          <div v-if="!panel.children?.length" class="empty-placeholder">
            将组件拖拽到此处
          </div>
        </VueDraggable>
      </NCollapseItem>
    </NCollapse>
  </div>
</template>

<style scoped>
.collapse-widget {
  width: 100%;
  min-height: 100px;
  border: 2px dashed #e8e8e8;
  border-radius: 8px;
  padding: 12px;
  transition: all 0.2s ease;
}

.collapse-widget.selected {
  border-color: #1890ff;
  background: rgba(24, 144, 255, 0.02);
}

.collapse-header {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.config-label {
  font-size: 12px;
  color: #666;
  line-height: 32px;
}

.collapse-content {
  min-height: 100px;
}

.panel-header-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.close-btn {
  opacity: 0;
  transition: opacity 0.2s;
}

.panel-header-wrapper:hover .close-btn {
  opacity: 1;
}

.panel-content-drop-zone {
  min-height: 80px;
  padding: 12px;
  background: #fafafa;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
}

.child-widget {
  padding: 8px;
  margin-bottom: 8px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.child-widget:hover {
  border-color: #1890ff;
}

.child-widget.selected {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.child-widget:last-child {
  margin-bottom: 0;
}

.empty-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60px;
  color: #999;
  font-size: 12px;
}

.ghost {
  opacity: 0.5;
  background: #e6f4ff;
  border: 2px dashed #1890ff;
}
</style>

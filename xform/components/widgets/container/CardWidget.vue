<script setup lang="ts">
import { computed, ref } from 'vue';
import { NCard, NButton, NIcon, NSwitch, NInput, NSpace } from 'naive-ui';
import { PlusOutlined, DownOutlined, UpOutlined } from '@vicons/antd';
import { VueDraggable } from 'vue-draggable-plus';
import { useDesigner } from '@/composables/useDesigner';
import type { WidgetConfig } from '@/types/form-schema';

const props = defineProps<{
  title?: string;
  collapsible?: boolean;
  collapsed?: boolean;
  children?: WidgetConfig[];
  selected?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:title', val: string): void;
  (e: 'update:collapsible', val: boolean): void;
  (e: 'update:collapsed', val: boolean): void;
  (e: 'addChild'): void;
}>();

const { selectedWidgetId, selectWidget } = useDesigner();

const isCollapsed = computed({
  get: () => props.collapsed ?? false,
  set: (val) => emit('update:collapsed', val),
});

const isCollapsible = computed({
  get: () => props.collapsible ?? false,
  set: (val) => emit('update:collapsible', val),
});

const cardTitle = computed({
  get: () => props.title ?? '卡片标题',
  set: (val) => emit('update:title', val),
});

function handleChildClick(widget: WidgetConfig, event: MouseEvent) {
  event.stopPropagation();
  selectWidget(widget.id);
}
</script>

<template>
  <div class="card-widget" :class="{ selected }">
    <div class="card-header" v-if="selected">
      <NSpace :size="16" align="center">
        <span class="config-label">标题:</span>
        <NInput
          v-model:value="cardTitle"
          size="small"
          placeholder="卡片标题"
          style="width: 150px;"
        />
        <span class="config-label">可折叠:</span>
        <NSwitch
          :value="isCollapsible"
          size="small"
          @update:value="(val: boolean) => emit('update:collapsible', val)"
        />
        <NButton size="small" @click="emit('addChild')">
          <template #icon>
            <NIcon :component="PlusOutlined" />
          </template>
          添加组件
        </NButton>
      </NSpace>
    </div>

    <NCard
      :title="cardTitle"
      :hoverable="true"
      :collapsible="isCollapsible"
      :collapsed="isCollapsed"
      class="card-content"
    >
      <template #header-extra>
        <NButton
          v-if="isCollapsible"
          quaternary
          circle
          size="tiny"
          @click="isCollapsed = !isCollapsed"
        >
          <template #icon>
            <NIcon :component="isCollapsed ? DownOutlined : UpOutlined" />
          </template>
        </NButton>
      </template>

      <VueDraggable
        :list="children ?? []"
        group="widgets"
        :animation="200"
        ghost-class="ghost"
        class="drop-zone"
      >
        <div
          v-for="child in (children ?? [])"
          :key="child.id"
          class="child-widget"
          :class="{ selected: selectedWidgetId === child.id }"
          @click="handleChildClick(child, $event)"
        >
          <slot :widget="child" />
        </div>
        <div v-if="!children?.length" class="empty-placeholder">
          <NButton quaternary size="small" @click="emit('addChild')">
            <template #icon>
              <NIcon :component="PlusOutlined" />
            </template>
            添加组件
          </NButton>
        </div>
      </VueDraggable>
    </NCard>
  </div>
</template>

<style scoped>
.card-widget {
  width: 100%;
  min-height: 100px;
  border: 2px dashed #e8e8e8;
  border-radius: 8px;
  padding: 12px;
  transition: all 0.2s ease;
}

.card-widget.selected {
  border-color: #1890ff;
  background: rgba(24, 144, 255, 0.02);
}

.card-header {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.config-label {
  font-size: 12px;
  color: #666;
  line-height: 32px;
}

.card-content {
  width: 100%;
}

.drop-zone {
  min-height: 80px;
  padding: 8px;
  background: #fafafa;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  min-height: 60px;
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
}

.ghost {
  opacity: 0.5;
  background: #e6f4ff;
  border: 2px dashed #1890ff;
}
</style>

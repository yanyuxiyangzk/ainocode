<script setup lang="ts">
import { computed, ref } from 'vue';
import { NGrid, NGridItem, NInputNumber, NSpace, NButton, NIcon } from 'naive-ui';
import { PlusOutlined } from '@vicons/antd';
import { VueDraggable } from 'vue-draggable-plus';
import { useDesigner } from '@/composables/useDesigner';
import type { WidgetConfig } from '@/types/form-schema';

const props = defineProps<{
  columns?: number;
  gutter?: number;
  children?: WidgetConfig[];
  selected?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:columns', val: number): void;
  (e: 'update:gutter', val: number): void;
  (e: 'addChild', index: number): void;
  (e: 'removeChild', index: number): void;
}>();

const { selectedWidgetId, selectWidget } = useDesigner();

const columns = computed(() => props.columns ?? 3);
const gutter = computed(() => props.gutter ?? 16);

const gridItems = computed(() => {
  return Array.from({ length: columns.value }, (_, i) => i);
});

function handleItemClick(widget: WidgetConfig, event: MouseEvent) {
  event.stopPropagation();
  selectWidget(widget.id);
}
</script>

<template>
  <div class="grid-widget" :class="{ selected }">
    <div class="grid-header" v-if="selected">
      <NSpace :size="12">
        <span class="config-label">列数:</span>
        <NInputNumber
          :value="columns"
          :min="1"
          :max="6"
          size="small"
          style="width: 80px;"
          @update:value="(val) => emit('update:columns', val ?? 3)"
        />
        <span class="config-label">间距:</span>
        <NInputNumber
          :value="gutter"
          :min="0"
          :max="48"
          size="small"
          style="width: 80px;"
          @update:value="(val) => emit('update:gutter', val ?? 16)"
        />
      </NSpace>
    </div>

    <NGrid :cols="columns" :x-gap="gutter" :y-gap="gutter" class="grid-content">
      <NGridItem v-for="(item, index) in gridItems" :key="index">
        <div class="grid-item-placeholder">
          <VueDraggable
            :list="children ? children[index]?.children ?? [] : []"
            group="widgets"
            :animation="200"
            ghost-class="ghost"
            class="drop-zone"
            @add="(evt: any) => emit('addChild', index)"
          >
            <div
              v-for="child in (children?.[index]?.children ?? [])"
              :key="child.id"
              class="child-widget"
              :class="{ selected: selectedWidgetId === child.id }"
              @click="handleItemClick(child, $event)"
            >
              <slot :name="`child-${index}`" :widget="child" />
            </div>
            <div v-if="!children?.[index]?.children?.length" class="empty-placeholder">
              <NButton quaternary size="small" @click="$emit('addChild', index)">
                <template #icon>
                  <NIcon :component="PlusOutlined" />
                </template>
                添加组件
              </NButton>
            </div>
          </VueDraggable>
        </div>
      </NGridItem>
    </NGrid>
  </div>
</template>

<style scoped>
.grid-widget {
  width: 100%;
  min-height: 100px;
  border: 2px dashed #e8e8e8;
  border-radius: 8px;
  padding: 12px;
  transition: all 0.2s ease;
}

.grid-widget.selected {
  border-color: #1890ff;
  background: rgba(24, 144, 255, 0.02);
}

.grid-header {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.config-label {
  font-size: 12px;
  color: #666;
  line-height: 32px;
}

.grid-content {
  width: 100%;
}

.grid-item-placeholder {
  min-height: 80px;
  background: #fafafa;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  padding: 8px;
}

.drop-zone {
  min-height: 60px;
}

.child-widget {
  padding: 8px;
  margin-bottom: 4px;
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

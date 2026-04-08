<script setup lang="ts">
import { computed, ref } from 'vue';
import { NTabs, NTabPane, NButton, NIcon, NInput, NSelect, NSpace } from 'naive-ui';
import { PlusOutlined, CloseOutlined } from '@vicons/antd';
import { VueDraggable } from 'vue-draggable-plus';
import { useDesigner } from '@/composables/useDesigner';
import type { WidgetConfig } from '@/types/form-schema';

const props = defineProps<{
  tabPosition?: 'top' | 'bottom' | 'left' | 'right';
  children?: WidgetConfig[];
  selected?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:tabPosition', val: 'top' | 'bottom' | 'left' | 'right'): void;
  (e: 'addTab'): void;
  (e: 'removeTab', index: number): void;
  (e: 'update:tabName', payload: { index: number; name: string }): void;
}>();

const { selectedWidgetId, selectWidget } = useDesigner();

const positionOptions = [
  { label: '顶部', value: 'top' },
  { label: '底部', value: 'bottom' },
  { label: '左侧', value: 'left' },
  { label: '右侧', value: 'right' },
];

const tabPosition = computed(() => props.tabPosition ?? 'top');
const tabs = computed(() => props.children ?? []);

const editingTabIndex = ref<number | null>(null);
const editingTabName = ref('');

function handleTabClick(widget: WidgetConfig, event: MouseEvent) {
  event.stopPropagation();
  selectWidget(widget.id);
}

function startEditTabName(index: number, currentName: string) {
  editingTabIndex.value = index;
  editingTabName.value = currentName;
}

function finishEditTabName() {
  if (editingTabIndex.value !== null) {
    emit('update:tabName', { index: editingTabIndex.value, name: editingTabName.value });
    editingTabIndex.value = null;
    editingTabName.value = '';
  }
}
</script>

<template>
  <div class="tabs-widget" :class="{ selected }">
    <div class="tabs-header" v-if="selected">
      <NSpace :size="12">
        <span class="config-label">标签位置:</span>
        <NSelect
          :value="tabPosition"
          :options="positionOptions"
          size="small"
          style="width: 100px;"
          @update:value="(val: 'top' | 'bottom' | 'left' | 'right') => emit('update:tabPosition', val)"
        />
        <NButton size="small" @click="emit('addTab')">
          <template #icon>
            <NIcon :component="PlusOutlined" />
          </template>
          新增标签
        </NButton>
      </NSpace>
    </div>

    <NTabs
      :type="'line'"
      :tabs-padding="10"
      :bar-width="24"
      class="tabs-content"
    >
      <NTabPane
        v-for="(tab, index) in tabs"
        :key="tab.id || index"
        :name="tab.props?.title ?? `标签${index + 1}`"
      >
        <template #tab>
          <div class="tab-title-wrapper">
            <span
              v-if="editingTabIndex !== index"
              class="tab-title"
              @dblclick="startEditTabName(index, tab.props?.title ?? `标签${index + 1}`)"
            >
              {{ tab.props?.title ?? `标签${index + 1}` }}
            </span>
            <NInput
              v-else
              v-model:value="editingTabName"
              size="tiny"
              style="width: 80px;"
              @blur="finishEditTabName"
              @keyup.enter="finishEditTabName"
            />
            <NButton
              v-if="tabs.length > 1"
              quaternary
              circle
              size="tiny"
              class="close-btn"
              @click.stop="emit('removeTab', index)"
            >
              <template #icon>
                <NIcon :component="CloseOutlined" />
              </template>
            </NButton>
          </div>
        </template>

        <VueDraggable
          :list="tab.children ?? []"
          group="widgets"
          :animation="200"
          ghost-class="ghost"
          class="tab-content-drop-zone"
        >
          <div
            v-for="child in (tab.children ?? [])"
            :key="child.id"
            class="child-widget"
            :class="{ selected: selectedWidgetId === child.id }"
            @click="handleTabClick(child, $event)"
          >
            <slot :name="`tab-${index}`" :widget="child" />
          </div>
          <div v-if="!tab.children?.length" class="empty-placeholder">
            将组件拖拽到此处
          </div>
        </VueDraggable>
      </NTabPane>
    </NTabs>
  </div>
</template>

<style scoped>
.tabs-widget {
  width: 100%;
  min-height: 150px;
  border: 2px dashed #e8e8e8;
  border-radius: 8px;
  padding: 12px;
  transition: all 0.2s ease;
}

.tabs-widget.selected {
  border-color: #1890ff;
  background: rgba(24, 144, 255, 0.02);
}

.tabs-header {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.config-label {
  font-size: 12px;
  color: #666;
  line-height: 32px;
}

.tabs-content {
  min-height: 120px;
}

.tab-title-wrapper {
  display: flex;
  align-items: center;
  gap: 4px;
}

.tab-title {
  cursor: pointer;
}

.tab-title:hover {
  color: #1890ff;
}

.close-btn {
  opacity: 0;
  transition: opacity 0.2s;
}

.tab-title-wrapper:hover .close-btn {
  opacity: 1;
}

.tab-content-drop-zone {
  min-height: 100px;
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

.empty-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 80px;
  color: #999;
  font-size: 12px;
}

.ghost {
  opacity: 0.5;
  background: #e6f4ff;
  border: 2px dashed #1890ff;
}
</style>

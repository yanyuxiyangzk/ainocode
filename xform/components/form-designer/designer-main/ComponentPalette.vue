<script setup lang="ts">
import { computed, ref } from 'vue';
import { NIcon, NButton, NCollapse, NCollapseItem, NTooltip, NInput } from 'naive-ui';
import {
  ALL_FIELD_WIDGET_DEFINITIONS,
} from '@/components/widgets/field';
import {
  ALL_CONTAINER_WIDGET_DEFINITIONS,
} from '@/components/widgets/container';
import type { WidgetDefinition as BaseWidgetDefinition } from '@/types/widget-type';
import { useI18n } from '@/composables/useI18n';

const props = defineProps<{
  collapsed?: boolean;
}>();

const emit = defineEmits<{
  (e: 'dragStart', definition: BaseWidgetDefinition, event: DragEvent): void;
  (e: 'dragEnd'): void;
  (e: 'add-widget', definition: BaseWidgetDefinition): void;
}>();

// i18n
const { t } = useI18n();

// Search state
const searchQuery = ref('');

// All widgets combined
const allWidgets = computed(() => [
  ...ALL_FIELD_WIDGET_DEFINITIONS,
  ...ALL_CONTAINER_WIDGET_DEFINITIONS,
]);

// Filter widgets by search
const filteredWidgets = computed(() => {
  if (!searchQuery.value.trim()) {
    return allWidgets.value;
  }
  const query = searchQuery.value.toLowerCase();
  return allWidgets.value.filter(
    (w) =>
      w.label.toLowerCase().includes(query) ||
      w.name.toLowerCase().includes(query)
  );
});

// Group widgets by category
const groupedWidgets = computed(() => {
  const groups: Record<string, BaseWidgetDefinition[]> = {
    field: [],
    upload: [],
    advanced: [],
    container: [],
  };
  filteredWidgets.value.forEach((widget) => {
    if (groups[widget.category]) {
      groups[widget.category].push(widget);
    }
  });
  return groups;
});

const categories = computed(() => [
  { key: 'field', label: t('palette.categories.field'), widgets: groupedWidgets.value.field, icon: 'edit' },
  { key: 'container', label: t('palette.categories.container'), widgets: groupedWidgets.value.container, icon: 'grid' },
  { key: 'upload', label: t('palette.categories.upload'), widgets: groupedWidgets.value.upload, icon: 'upload' },
  { key: 'advanced', label: t('palette.categories.advanced'), widgets: groupedWidgets.value.advanced, icon: 'code' },
]);

function handleDragStart(definition: BaseWidgetDefinition, event: DragEvent) {
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'copy';
    event.dataTransfer.setData('application/json', JSON.stringify({
      type: String(definition.type),
      widgetName: definition.name,
      defaultProps: definition.defaultProps,
    }));
  }
  emit('dragStart', definition, event);
}

function handleDragEnd() {
  emit('dragEnd');
}

function handleAddWidget(definition: BaseWidgetDefinition) {
  emit('add-widget', definition);
}

function getWidgetIcon(icon: string): string {
  const iconMap: Record<string, string> = {
    'edit': 'Edit',
    'upload': 'Upload',
    'code': 'Code',
    'hash': 'Hash',
    'align-left': 'AlignLeft',
    'chevron-down': 'ArrowDown',
    'circle-dot': 'GenderFemale',
    'check-square': 'CheckSquare',
    'calendar': 'Calendar',
    'clock': 'Time',
    'toggle-right': 'ToggleRight',
    'star': 'Star',
    'sliders': 'Options',
    'palette': 'ColorPalette',
    'image': 'Image',
    'file-text': 'FileText',
    'minus': 'Remove',
    'square': 'Square',
    'grid': 'Grid',
    'tab': 'Tabs',
    'card': 'Card',
    'collapse': 'Collapse',
  };
  return iconMap[icon] || 'Document';
}
</script>

<template>
  <div class="component-palette" :class="{ collapsed }">
    <div class="palette-header">
      <h3 v-if="!collapsed">{{ t('palette.title') }}</h3>
    </div>

    <div v-if="!collapsed" class="palette-content">
      <!-- Search input -->
      <div class="search-wrapper">
        <n-input
          v-model:value="searchQuery"
          :placeholder="t('palette.searchPlaceholder')"
          size="small"
          clearable
        >
          <template #prefix>
            <n-icon><span>🔍</span></n-icon>
          </template>
        </n-input>
      </div>

      <n-collapse default-expanded-names="['field', 'container']" multiple>
        <n-collapse-item
          v-for="category in categories"
          :key="category.key"
          :title="category.label"
          :name="category.key"
        >
          <template #header-extra>
            <span class="widget-count">{{ category.widgets.length }}</span>
          </template>

          <div v-if="category.widgets.length > 0" class="widget-list">
            <n-tooltip
              v-for="widget in category.widgets"
              :key="String(widget.type)"
              trigger="hover"
              placement="left"
            >
              <template #trigger>
                <div
                  class="widget-item"
                  draggable="true"
                  @dragstart="handleDragStart(widget, $event)"
                  @dragend="handleDragEnd"
                  @click="handleAddWidget(widget)"
                >
                  <n-icon :component="getWidgetIcon(widget.icon)" />
                  <span class="widget-label">{{ widget.label }}</span>
                </div>
              </template>
              {{ widget.label }}
            </n-tooltip>
          </div>

          <div v-else class="empty-category">
            <span>{{ t('palette.empty') }}</span>
          </div>
        </n-collapse-item>
      </n-collapse>
    </div>

    <div v-else class="palette-collapsed-content">
      <n-button
        v-for="category in categories"
        :key="category.key"
        quaternary
        circle
        @dragstart="handleDragStart(category.widgets[0], $event)"
      >
        <template #icon>
          <n-icon :component="getWidgetIcon(category.icon)" />
        </template>
      </n-button>
    </div>
  </div>
</template>

<style scoped>
.component-palette {
  width: 260px;
  height: 100%;
  background: #fff;
  border-right: 1px solid var(--border-color, #d9d9d9);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: width 0.3s ease;
}

.component-palette.collapsed {
  width: 60px;
}

.palette-header {
  padding: 16px;
  border-bottom: 1px solid var(--border-color, #d9d9d9);
}

.palette-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.palette-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.search-wrapper {
  padding: 0 4px 12px 4px;
}

.palette-collapsed-content {
  padding: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.widget-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.widget-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px 8px;
  background: #f5f5f5;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  cursor: grab;
  transition: all 0.2s ease;
  user-select: none;
}

.widget-item:hover {
  background: #e6f4ff;
  border-color: #1890ff;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.widget-item:active {
  cursor: grabbing;
}

.widget-item .n-icon {
  font-size: 24px;
  color: #666;
  margin-bottom: 4px;
}

.widget-label {
  font-size: 12px;
  color: #666;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.widget-count {
  font-size: 12px;
  color: #999;
}

.empty-category {
  text-align: center;
  padding: 16px;
  color: #999;
  font-size: 12px;
}
</style>

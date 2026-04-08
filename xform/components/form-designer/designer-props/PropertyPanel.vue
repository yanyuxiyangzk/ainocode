<script setup lang="ts">
/**
 * PropertyPanel - Main property panel component
 * Displays properties for the selected widget with category tabs
 */
import { ref, computed, watch } from 'vue';
import {
  NLayout,
  NLayoutSider,
  NLayoutContent,
  NTabs,
  NTabPane,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NSwitch,
  NSelect,
  NDivider,
  NEmpty,
  NIcon,
  NButton,
  NTooltip,
} from 'naive-ui';
import {
  SettingsOutline,
  GridOutline,
  CheckmarkCircleOutline,
  FlashOutline,
  CodeOutline,
} from '@vicons/ionicons5';
import PropertyEditor from './PropertyEditor.vue';
import type { PropertySchema, PropertyCategory, SelectOption } from '@/types/property-editor';
import { COMMON_PROPS, LAYOUT_PROPS, VALIDATION_PROPS, EVENT_PROPS } from '@/types/property-editor';

interface Props {
  selectedWidget?: {
    id: string;
    widgetName: string;
    props: Record<string, any>;
    events?: Record<string, string>;
  } | null;
  disabled?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  selectedWidget: null,
  disabled: false,
});

const emit = defineEmits<{
  (e: 'update:props', widgetId: string, props: Record<string, any>): void;
  (e: 'update:event', widgetId: string, eventName: string, handler: string): void;
}>();

const activeTab = ref<PropertyCategory>('common');

// Property schemas organized by category
const commonProperties: PropertySchema[] = [
  { name: COMMON_PROPS.LABEL, label: '标签', type: 'string', category: 'common' },
  { name: COMMON_PROPS.NAME, label: '字段名', type: 'string', category: 'common' },
  { name: COMMON_PROPS.PLACEHOLDER, label: '占位符', type: 'string', category: 'common' },
  { name: COMMON_PROPS.DEFAULT_VALUE, label: '默认值', type: 'string', category: 'common' },
  { name: COMMON_PROPS.DISABLED, label: '禁用', type: 'boolean', category: 'common' },
  { name: COMMON_PROPS.HIDDEN, label: '隐藏', type: 'boolean', category: 'common' },
  { name: COMMON_PROPS.READONLY, label: '只读', type: 'boolean', category: 'common' },
  { name: COMMON_PROPS.CLEARABLE, label: '可清空', type: 'boolean', category: 'common' },
  { name: COMMON_PROPS.MAX_LENGTH, label: '最大长度', type: 'number', category: 'common' },
];

const layoutProperties: PropertySchema[] = [
  { name: LAYOUT_PROPS.WIDTH, label: '宽度', type: 'string', category: 'layout' },
  {
    name: LAYOUT_PROPS.LABEL_WIDTH,
    label: '标签宽度',
    type: 'number',
    category: 'layout',
    props: { min: 0, max: 300, step: 10 },
  },
  {
    name: LAYOUT_PROPS.LABEL_ALIGN,
    label: '标签对齐',
    type: 'select',
    category: 'layout',
    options: [
      { label: '左对齐', value: 'left' },
      { label: '右对齐', value: 'right' },
    ],
  },
];

const validationProperties: PropertySchema[] = [
  { name: VALIDATION_PROPS.REQUIRED, label: '必填', type: 'boolean', category: 'validation' },
  { name: VALIDATION_PROPS.PATTERN, label: '正则表达式', type: 'string', category: 'validation' },
  { name: VALIDATION_PROPS.MIN, label: '最小值', type: 'number', category: 'validation' },
  { name: VALIDATION_PROPS.MAX, label: '最大值', type: 'number', category: 'validation' },
];

const eventOptions: { label: string; value: string }[] = [
  { label: '点击事件 (onClick)', value: 'onClick' },
  { label: '变更事件 (onChange)', value: 'onChange' },
  { label: '失焦事件 (onBlur)', value: 'onBlur' },
  { label: '聚焦事件 (onFocus)', value: 'onFocus' },
  { label: '输入事件 (onInput)', value: 'onInput' },
];

const eventProperties: PropertySchema[] = [
  { name: EVENT_PROPS.ON_CLICK, label: 'onClick', type: 'event-handler', category: 'event' },
  { name: EVENT_PROPS.ON_CHANGE, label: 'onChange', type: 'event-handler', category: 'event' },
  { name: EVENT_PROPS.ON_BLUR, label: 'onBlur', type: 'event-handler', category: 'event' },
];

// Get current properties based on active tab
const currentProperties = computed<PropertySchema[]>(() => {
  switch (activeTab.value) {
    case 'common':
      return commonProperties;
    case 'layout':
      return layoutProperties;
    case 'validation':
      return validationProperties;
    case 'event':
      return eventProperties;
    default:
      return [];
  }
});

// Get current value for a property
function getPropertyValue(propName: string): any {
  if (!props.selectedWidget) return undefined;
  if (activeTab.value === 'event') {
    return props.selectedWidget.events?.[propName] ?? '';
  }
  return props.selectedWidget.props?.[propName];
}

// Handle property value update
function handlePropertyUpdate(propName: string, value: any) {
  if (!props.selectedWidget) return;

  if (activeTab.value === 'event') {
    emit('update:event', props.selectedWidget.id, propName, value);
  } else {
    emit('update:props', props.selectedWidget.id, { [propName]: value });
  }
}

// Get label for event property
function getEventLabel(propName: string): string {
  return propName;
}

// Check if widget is selected
const hasSelection = computed(() => !!props.selectedWidget);
</script>

<template>
  <NLayout class="property-panel">
    <!-- Header -->
    <div class="panel-header">
      <NIcon size="18">
        <SettingsOutline />
      </NIcon>
      <span>属性面板</span>
    </div>

    <!-- Widget Info -->
    <div v-if="hasSelection" class="widget-info">
      <NTooltip trigger="hover">
        <template #trigger>
          <div class="widget-name">{{ selectedWidget?.widgetName }}</div>
        </template>
        <div>组件ID: {{ selectedWidget?.id }}</div>
      </NTooltip>
    </div>

    <!-- Tab Navigation -->
    <NTabs
      v-if="hasSelection"
      v-model:value="activeTab"
      type="line"
      size="small"
      class="property-tabs"
    >
      <!-- Common Properties Tab -->
      <NTabPane name="common">
        <template #tab>
          <NSpace :size="4" align="center">
            <NIcon size="14"><GridOutline /></NIcon>
            <span>通用</span>
          </NSpace>
        </template>
      </NTabPane>

      <!-- Layout Properties Tab -->
      <NTabPane name="layout">
        <template #tab>
          <NSpace :size="4" align="center">
            <NIcon size="14"><SettingsOutline /></NIcon>
            <span>布局</span>
          </NSpace>
        </template>
      </NTabPane>

      <!-- Validation Properties Tab -->
      <NTabPane name="validation">
        <template #tab>
          <NSpace :size="4" align="center">
            <NIcon size="14"><CheckmarkCircleOutline /></NIcon>
            <span>校验</span>
          </NSpace>
        </template>
      </NTabPane>

      <!-- Event Properties Tab -->
      <NTabPane name="event">
        <template #tab>
          <NSpace :size="4" align="center">
            <NIcon size="14"><FlashOutline /></NIcon>
            <span>事件</span>
          </NSpace>
        </template>
      </NTabPane>
    </NTabs>

    <!-- Property List -->
    <NLayoutContent class="property-content">
      <NEmpty
        v-if="!hasSelection"
        description="请先选择组件"
        class="no-selection-empty"
      >
        <template #icon>
          <NIcon size="48" color="#d9d9d9">
            <CodeOutline />
          </NIcon>
        </template>
      </NEmpty>

      <NForm
        v-else
        label-placement="left"
        label-width="80"
        :show-feedback="false"
        class="property-form"
      >
        <template v-for="prop in currentProperties" :key="prop.name">
          <NFormItem
            :label="prop.label"
            class="property-item"
          >
            <PropertyEditor
              :prop-type="prop.type"
              :model-value="getPropertyValue(prop.name)"
              :schema="prop"
              :disabled="disabled"
              :options="prop.options"
              @update:model-value="(val) => handlePropertyUpdate(prop.name, val)"
            />
          </NFormItem>
        </template>
      </NForm>
    </NLayoutContent>
  </NLayout>
</template>

<style scoped>
.property-panel {
  height: 100%;
  background: #fff;
  display: flex;
  flex-direction: column;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  font-weight: 500;
  color: #333;
}

.widget-info {
  padding: 8px 16px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.widget-name {
  font-size: 13px;
  color: #18a058;
  font-weight: 500;
  cursor: default;
}

.property-tabs {
  padding: 0 8px;
}

.property-content {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.no-selection-empty {
  margin-top: 60px;
}

.property-form {
  width: 100%;
}

.property-item {
  margin-bottom: 8px;
}

:deep(.n-form-item) {
  align-items: flex-start;
}

:deep(.n-form-item .n-form-item-label) {
  padding-top: 6px;
  font-size: 12px;
  color: #666;
}

:deep(.n-form-item .n-form-item-control) {
  min-width: 0;
}
</style>

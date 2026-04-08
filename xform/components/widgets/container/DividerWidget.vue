<script setup lang="ts">
import { computed } from 'vue';
import { NDivider, NSelect, NSwitch, NSpace } from 'naive-ui';

const props = defineProps<{
  type?: 'default' | 'vertical';
  dashed?: boolean;
  title?: string;
  orientation?: 'left' | 'center' | 'right';
  selected?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:type', val: 'default' | 'vertical'): void;
  (e: 'update:dashed', val: boolean): void;
  (e: 'update:title', val: string): void;
  (e: 'update:orientation', val: 'left' | 'center' | 'right'): void;
}>();

const typeOptions = [
  { label: '水平', value: 'default' },
  { label: '垂直', value: 'vertical' },
];

const orientationOptions = [
  { label: '居左', value: 'left' },
  { label: '居中', value: 'center' },
  { label: '居右', value: 'right' },
];

const isVertical = computed(() => props.type === 'vertical');
const dividerType = computed(() => isVertical.value ? 'vertical' : 'horizontal');
const dashed = computed(() => props.dashed ?? false);
const title = computed(() => props.title ?? '');
const orientation = computed(() => props.orientation ?? 'center');
</script>

<template>
  <div class="divider-widget" :class="{ selected }">
    <div v-if="selected && !isVertical" class="divider-header">
      <NSpace :size="16" align="center">
        <span class="config-label">类型:</span>
        <NSelect
          :value="type"
          :options="typeOptions"
          size="small"
          style="width: 80px;"
          @update:value="(val: 'default' | 'vertical') => emit('update:type', val)"
        />
        <span class="config-label">虚线:</span>
        <NSwitch
          :value="dashed"
          size="small"
          @update:value="(val: boolean) => emit('update:dashed', val)"
        />
        <span class="config-label">标题位置:</span>
        <NSelect
          :value="orientation"
          :options="orientationOptions"
          size="small"
          style="width: 80px;"
          @update:value="(val: 'left' | 'center' | 'right') => emit('update:orientation', val)"
        />
      </NSpace>
    </div>

    <div class="divider-content">
      <NDivider
        :type="dividerType"
        :dashed="dashed"
        :title="title || undefined"
        :orientation="orientation"
        :vertical="isVertical"
      >
        <template v-if="title" #default>{{ title }}</template>
      </NDivider>
    </div>
  </div>
</template>

<style scoped>
.divider-widget {
  width: 100%;
  padding: 8px 0;
  border: 2px dashed transparent;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.divider-widget.selected {
  border-color: #1890ff;
  background: rgba(24, 144, 255, 0.02);
}

.divider-header {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.config-label {
  font-size: 12px;
  color: #666;
  line-height: 32px;
}

.divider-content {
  padding: 8px 0;
}
</style>

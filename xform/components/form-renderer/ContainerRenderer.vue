<script setup lang="ts">
import { computed, inject } from 'vue';
import { NGrid, NGridItem, NTabs, NTabPane, NCard, NCollapse, NCollapseItem } from 'naive-ui';
import { VueDraggable } from 'vue-draggable-plus';
import { useWidgetRegistry } from '@/composables/useWidgetRegistry';
import FieldRenderer from './FieldRenderer.vue';
import type { WidgetConfig } from '@/types/form-schema';
import { WIDGET_TYPE } from '@/types/widget-type';
import { FORM_RENDERER_KEY } from '@/composables/useFormRenderer';

const props = defineProps<{
  widget: WidgetConfig;
  disabled?: boolean;
  readonly?: boolean;
}>();

const { getComponent } = useWidgetRegistry();
const rendererContext = inject(FORM_RENDERER_KEY);

const children = computed(() => props.widget.children ?? []);
const widgetProps = computed(() => props.widget.props ?? {});

function isContainerType(type: symbol): boolean {
  return [
    WIDGET_TYPE.GRID,
    WIDGET_TYPE.TABS,
    WIDGET_TYPE.CARD,
    WIDGET_TYPE.COLLAPSE,
  ].includes(type);
}

function renderGridContainer(widget: WidgetConfig) {
  const columns = widget.props?.columns ?? 3;
  const gutter = widget.props?.gutter ?? 16;

  return (
    <NGrid cols={columns} x-gap={gutter} y-gap={gutter}>
      {widget.children?.map((child, index) => (
        <NGridItem key={child.id || index}>
          {isContainerType(child.type)
            ? <ContainerRenderer widget={child} disabled={props.disabled} readonly={props.readonly} />
            : <FieldRenderer widget={child} disabled={props.disabled} readonly={props.readonly} />
          }
        </NGridItem>
      ))}
    </NGrid>
  );
}

function renderTabsContainer(widget: WidgetConfig) {
  const tabPosition = widget.props?.tabPosition ?? 'top';
  const activeValue = widget.props?.activeValue ?? widget.children?.[0]?.id;

  return (
    <NTabs type="line" tab-position={tabPosition} value={activeValue}>
      {widget.children?.map((tab, index) => (
        <NTabPane
          key={tab.id || index}
          name={tab.props?.title ?? `标签${index + 1}`}
          tab={tab.props?.title ?? `标签${index + 1}`}
        >
          <VueDraggable
            list={tab.children ?? []}
            disabled={true}
            group="widgets"
            animation={200}
            ghost-class="ghost"
          >
            {tab.children?.map((child) => (
              isContainerType(child.type)
                ? <ContainerRenderer key={child.id} widget={child} disabled={props.disabled} readonly={props.readonly} />
                : <FieldRenderer key={child.id} widget={child} disabled={props.disabled} readonly={props.readonly} />
            ))}
          </VueDraggable>
        </NTabPane>
      ))}
    </NTabs>
  );
}

function renderCardContainer(widget: WidgetConfig) {
  const title = widget.props?.title ?? '卡片';
  const collapsible = widget.props?.collapsible ?? false;
  const collapsed = widget.props?.collapsed ?? false;

  return (
    <NCard title={title} collapsible={collapsible} collapsed={collapsed}>
      <VueDraggable
        list={widget.children ?? []}
        disabled={true}
        group="widgets"
        animation={200}
        ghost-class="ghost"
      >
        {widget.children?.map((child) => (
          isContainerType(child.type)
            ? <ContainerRenderer key={child.id} widget={child} disabled={props.disabled} readonly={props.readonly} />
            : <FieldRenderer key={child.id} widget={child} disabled={props.disabled} readonly={props.readonly} />
        ))}
      </VueDraggable>
    </NCard>
  );
}

function renderCollapseContainer(widget: WidgetConfig) {
  const accordion = widget.props?.accordion ?? false;
  const expandedNames = widget.props?.expandedNames ?? [];

  return (
    <NCollapse accordion={accordion} expanded-names={expandedNames}>
      {widget.children?.map((panel, index) => (
        <NCollapseItem
          key={panel.id || index}
          name={panel.props?.title ?? `面板${index + 1}`}
          title={panel.props?.title ?? `面板${index + 1}`}
        >
          <VueDraggable
            list={panel.children ?? []}
            disabled={true}
            group="widgets"
            animation={200}
            ghost-class="ghost"
          >
            {panel.children?.map((child) => (
              isContainerType(child.type)
                ? <ContainerRenderer key={child.id} widget={child} disabled={props.disabled} readonly={props.readonly} />
                : <FieldRenderer key={child.id} widget={child} disabled={props.disabled} readonly={props.readonly} />
            ))}
          </VueDraggable>
        </NCollapseItem>
      ))}
    </NCollapse>
  );
}
</script>

<template>
  <div class="container-renderer" :class="`container-${widget.widgetName.toLowerCase()}`">
    <!-- Grid Container -->
    <template v-if="widget.type === WIDGET_TYPE.GRID">
      <NGrid
        :cols="widgetProps.columns ?? 3"
        :x-gap="widgetProps.gutter ?? 16"
        :y-gap="widgetProps.gutter ?? 16"
      >
        <NGridItem v-for="(child, index) in children" :key="child.id || index">
          <ContainerRenderer
            v-if="isContainerType(child.type)"
            :widget="child"
            :disabled="disabled"
            :readonly="readonly"
          />
          <FieldRenderer
            v-else
            :widget="child"
            :disabled="disabled"
            :readonly="readonly"
          />
        </NGridItem>
      </NGrid>
    </template>

    <!-- Tabs Container -->
    <template v-else-if="widget.type === WIDGET_TYPE.TABS">
      <NTabs
        :type="'line'"
        :tab-position="widgetProps.tabPosition ?? 'top'"
      >
        <NTabPane
          v-for="(tab, index) in children"
          :key="tab.id || index"
          :name="tab.props?.title ?? `标签${index + 1}`"
          :tab="tab.props?.title ?? `标签${index + 1}`"
        >
          <VueDraggable
            :list="tab.children ?? []"
            :disabled="true"
            group="widgets"
            :animation="200"
            ghost-class="ghost"
          >
            <template v-for="child in (tab.children ?? [])" :key="child.id">
              <ContainerRenderer
                v-if="isContainerType(child.type)"
                :widget="child"
                :disabled="disabled"
                :readonly="readonly"
              />
              <FieldRenderer
                v-else
                :widget="child"
                :disabled="disabled"
                :readonly="readonly"
              />
            </template>
          </VueDraggable>
        </NTabPane>
      </NTabs>
    </template>

    <!-- Card Container -->
    <template v-else-if="widget.type === WIDGET_TYPE.CARD">
      <NCard
        :title="widgetProps.title ?? '卡片'"
        :hoverable="true"
        :collapsible="widgetProps.collapsible ?? false"
        :collapsed="widgetProps.collapsed ?? false"
      >
        <VueDraggable
          :list="children"
          :disabled="true"
          group="widgets"
          :animation="200"
          ghost-class="ghost"
        >
          <template v-for="child in children" :key="child.id">
            <ContainerRenderer
              v-if="isContainerType(child.type)"
              :widget="child"
              :disabled="disabled"
              :readonly="readonly"
            />
            <FieldRenderer
              v-else
              :widget="child"
              :disabled="disabled"
              :readonly="readonly"
            />
          </template>
        </VueDraggable>
      </NCard>
    </template>

    <!-- Collapse Container -->
    <template v-else-if="widget.type === WIDGET_TYPE.COLLAPSE">
      <NCollapse
        :accordion="widgetProps.accordion ?? false"
      >
        <NCollapseItem
          v-for="(panel, index) in children"
          :key="panel.id || index"
          :name="panel.props?.title ?? `面板${index + 1}`"
          :title="panel.props?.title ?? `面板${index + 1}`"
        >
          <VueDraggable
            :list="panel.children ?? []"
            :disabled="true"
            group="widgets"
            :animation="200"
            ghost-class="ghost"
          >
            <template v-for="child in (panel.children ?? [])" :key="child.id">
              <ContainerRenderer
                v-if="isContainerType(child.type)"
                :widget="child"
                :disabled="disabled"
                :readonly="readonly"
              />
              <FieldRenderer
                v-else
                :widget="child"
                :disabled="disabled"
                :readonly="readonly"
              />
            </template>
          </VueDraggable>
        </NCollapseItem>
      </NCollapse>
    </template>
  </div>
</template>

<style scoped>
.container-renderer {
  width: 100%;
}

.ghost {
  opacity: 0.5;
  background: #e6f4ff;
  border: 2px dashed #1890ff;
}
</style>

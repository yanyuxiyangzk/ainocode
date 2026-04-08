// Container widgets auto-export
export { default as GridWidget } from './GridWidget.vue';
export { default as TabsWidget } from './TabsWidget.vue';
export { default as CardWidget } from './CardWidget.vue';
export { default as CollapseWidget } from './CollapseWidget.vue';
export { default as DividerWidget } from './DividerWidget.vue';

// Widget definitions
import { WIDGET_TYPE } from '@/types/widget-type';
import type { WidgetDefinition } from '@/types/widget-type';

// Grid Widget Definition
export const GridWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.GRID,
  name: 'GridWidget',
  label: '栅格布局',
  icon: 'grid',
  category: 'container',
  defaultProps: {
    columns: 3,
    gutter: 16,
    collapse: false,
  },
  propsSchema: [
    { name: 'columns', label: '列数', type: 'number', defaultValue: 3 },
    { name: 'gutter', label: '间隔', type: 'number', defaultValue: 16 },
    { name: 'collapse', label: '响应式折叠', type: 'boolean', defaultValue: false },
  ],
};

// Tabs Widget Definition
export const TabsWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.TABS,
  name: 'TabsWidget',
  label: '标签页',
  icon: 'tab',
  category: 'container',
  defaultProps: {
    tabPosition: 'top',
    children: [],
  },
  propsSchema: [
    {
      name: 'tabPosition',
      label: '标签位置',
      type: 'select',
      options: [
        { label: '顶部', value: 'top' },
        { label: '底部', value: 'bottom' },
        { label: '左侧', value: 'left' },
        { label: '右侧', value: 'right' },
      ],
      defaultValue: 'top',
    },
  ],
};

// Card Widget Definition
export const CardWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.CARD,
  name: 'CardWidget',
  label: '卡片',
  icon: 'card',
  category: 'container',
  defaultProps: {
    title: '卡片标题',
    collapsible: false,
    collapsed: false,
  },
  propsSchema: [
    { name: 'title', label: '标题', type: 'string', defaultValue: '卡片标题' },
    { name: 'collapsible', label: '可折叠', type: 'boolean', defaultValue: false },
  ],
};

// Collapse Widget Definition
export const CollapseWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.COLLAPSE,
  name: 'CollapseWidget',
  label: '折叠面板',
  icon: 'collapse',
  category: 'container',
  defaultProps: {
    accordion: false,
    children: [],
  },
  propsSchema: [
    { name: 'accordion', label: '手风琴模式', type: 'boolean', defaultValue: false },
  ],
};

// Divider Widget Definition
export const DividerWidgetDefinition: WidgetDefinition = {
  type: WIDGET_TYPE.DIVIDER,
  name: 'DividerWidget',
  label: '分割线',
  icon: 'divider',
  category: 'container',
  defaultProps: {
    type: 'default',
    dashed: false,
    title: '',
    orientation: 'center',
  },
  propsSchema: [
    {
      name: 'type',
      label: '类型',
      type: 'select',
      options: [
        { label: '水平', value: 'default' },
        { label: '垂直', value: 'vertical' },
      ],
      defaultValue: 'default',
    },
    { name: 'dashed', label: '虚线', type: 'boolean', defaultValue: false },
    { name: 'title', label: '标题', type: 'string', defaultValue: '' },
    {
      name: 'orientation',
      label: '标题位置',
      type: 'select',
      options: [
        { label: '居左', value: 'left' },
        { label: '居中', value: 'center' },
        { label: '居右', value: 'right' },
      ],
      defaultValue: 'center',
    },
  ],
};

// All container widget definitions array
export const ALL_CONTAINER_WIDGET_DEFINITIONS: WidgetDefinition[] = [
  GridWidgetDefinition,
  TabsWidgetDefinition,
  CardWidgetDefinition,
  CollapseWidgetDefinition,
  DividerWidgetDefinition,
];

// Register all container widgets to registry
export function registerContainerWidgets(registry: { register: (def: WidgetDefinition, component: any) => void }, components: any) {
  registry.register(GridWidgetDefinition, components.GridWidget);
  registry.register(TabsWidgetDefinition, components.TabsWidget);
  registry.register(CardWidgetDefinition, components.CardWidget);
  registry.register(CollapseWidgetDefinition, components.CollapseWidget);
  registry.register(DividerWidgetDefinition, components.DividerWidget);
}

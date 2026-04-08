/**
 * Integration Tests for Form Designer
 * 表单设计器集成测试
 */
import { describe, it, expect, beforeEach } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import { createApp } from 'vue';
import { widgetRegistry } from '@/composables/useWidgetRegistry';
import { useDesignerStore } from '@/stores/designerStore';
import { useHistoryStore } from '@/stores/historyStore';
import { useSchemaStore } from '@/stores/schemaStore';
import { createEmptySchema, validateSchema, jsonToSchema } from '@/utils/schema-transformer';
import { demoSchema, simpleSchema } from '@/demo';

// Mock widgets for testing
const mockWidgets = {
  InputWidget: { name: 'InputWidget' },
  SelectWidget: { name: 'SelectWidget' },
  GridWidget: { name: 'GridWidget' },
  CardWidget: { name: 'CardWidget' },
};

describe('Widget Registry', () => {
  it('should be empty initially after registry reset', () => {
    widgetRegistry.clear();
    expect(widgetRegistry.getAllDefinitions()).toHaveLength(0);
  });
});

describe('Designer Store', () => {
  beforeEach(() => {
    const pinia = createPinia();
    setActivePinia(pinia);
  });

  it('should create designer store', () => {
    const store = useDesignerStore();
    expect(store).toBeDefined();
  });

  it('should set schema', () => {
    const store = useDesignerStore();
    const schema = createEmptySchema();
    store.setSchema(schema);
    expect(store.schema).toEqual(schema);
  });

  it('should select widget', () => {
    const store = useDesignerStore();
    store.selectWidget('widget-1');
    expect(store.selectedWidgetId).toBe('widget-1');
  });

  it('should clear selection when selecting null', () => {
    const store = useDesignerStore();
    store.selectWidget('widget-1');
    store.selectWidget(null);
    expect(store.selectedWidgetId).toBeNull();
  });

  it('should add widget', () => {
    const store = useDesignerStore();
    store.setSchema(createEmptySchema());

    const widget = {
      id: 'test-widget',
      type: Symbol('INPUT'),
      widgetName: 'InputWidget',
      props: { label: '测试' },
      children: [],
    };

    store.addWidget(null, widget);
    expect(store.schema?.widgets).toHaveLength(1);
    expect(store.schema?.widgets[0].id).toBe('test-widget');
  });

  it('should remove widget', () => {
    const store = useDesignerStore();
    store.setSchema(createEmptySchema());

    const widget = {
      id: 'test-widget',
      type: Symbol('INPUT'),
      widgetName: 'InputWidget',
      props: { label: '测试' },
      children: [],
    };

    store.addWidget(null, widget);
    store.removeWidget('test-widget');
    expect(store.schema?.widgets).toHaveLength(0);
  });

  it('should update widget props', () => {
    const store = useDesignerStore();
    store.setSchema(createEmptySchema());

    const widget = {
      id: 'test-widget',
      type: Symbol('INPUT'),
      widgetName: 'InputWidget',
      props: { label: '测试' },
      children: [],
    };

    store.addWidget(null, widget);
    store.updateWidgetProps('test-widget', { label: '新标签' });
    expect(store.schema?.widgets[0].props.label).toBe('新标签');
  });

  it('should copy and paste widget', () => {
    const store = useDesignerStore();
    store.setSchema(createEmptySchema());

    const widget = {
      id: 'test-widget',
      type: Symbol('INPUT'),
      widgetName: 'InputWidget',
      props: { label: '测试' },
      children: [],
    };

    store.addWidget(null, widget);
    store.copyWidget(widget);
    expect(store.clipboardData).not.toBeNull();
  });

  it('should cut widget', () => {
    const store = useDesignerStore();
    store.setSchema(createEmptySchema());

    const widget = {
      id: 'test-widget',
      type: Symbol('INPUT'),
      widgetName: 'InputWidget',
      props: { label: '测试' },
      children: [],
    };

    store.addWidget(null, widget);
    store.cutWidget(widget);
    expect(store.schema?.widgets).toHaveLength(0);
    expect(store.clipboardData).not.toBeNull();
  });
});

describe('History Store', () => {
  beforeEach(() => {
    const pinia = createPinia();
    setActivePinia(pinia);
  });

  it('should create history store', () => {
    const store = useHistoryStore();
    expect(store).toBeDefined();
  });

  it('should push state', () => {
    const store = useHistoryStore();
    const schema = createEmptySchema();
    store.pushState(schema);
    expect(store.canUndo).toBe(true);
  });

  it('should undo', () => {
    const store = useHistoryStore();
    const schema1 = createEmptySchema();
    schema1.name = 'Form 1';

    const schema2 = createEmptySchema();
    schema2.name = 'Form 2';

    store.pushState(schema1);
    store.pushState(schema2);

    const undone = store.undo();
    expect(store.canRedo).toBe(true);
  });

  it('should redo', () => {
    const store = useHistoryStore();
    const schema1 = createEmptySchema();
    schema1.name = 'Form 1';

    const schema2 = createEmptySchema();
    schema2.name = 'Form 2';

    store.pushState(schema1);
    store.pushState(schema2);
    store.undo();
    const redone = store.redo();
    expect(redone).not.toBeNull();
  });

  it('should clear history', () => {
    const store = useHistoryStore();
    const schema = createEmptySchema();
    store.pushState(schema);
    store.clear();
    expect(store.canUndo).toBe(false);
    expect(store.canRedo).toBe(false);
  });
});

describe('Schema Store', () => {
  beforeEach(() => {
    const pinia = createPinia();
    setActivePinia(pinia);
  });

  it('should create schema store', () => {
    const store = useSchemaStore();
    expect(store).toBeDefined();
  });

  it('should init schema', () => {
    const store = useSchemaStore();
    store.initSchema();
    expect(store.schema).not.toBeNull();
    expect(store.schema?.widgets).toEqual([]);
  });

  it('should export schema', () => {
    const store = useSchemaStore();
    store.initSchema(demoSchema);
    const exported = store.exportSchema();
    expect(exported).toBeTruthy();
    expect(() => JSON.parse(exported)).not.toThrow();
  });

  it('should import schema', () => {
    const store = useSchemaStore();
    const json = JSON.stringify(demoSchema);
    const result = store.importSchema(json);
    expect(result).toBe(true);
  });

  it('should validate schema', () => {
    const store = useSchemaStore();
    store.initSchema(demoSchema);
    const validation = store.validateSchema();
    expect(validation.valid).toBe(true);
    expect(validation.errors).toHaveLength(0);
  });
});

describe('Schema Transformer', () => {
  it('should create empty schema', () => {
    const schema = createEmptySchema();
    expect(schema.id).toBeTruthy();
    expect(schema.name).toBe('未命名表单');
    expect(schema.widgets).toEqual([]);
  });

  it('should validate valid schema', () => {
    const validation = validateSchema(demoSchema);
    expect(validation.valid).toBe(true);
  });

  it('should validate invalid schema', () => {
    const invalid = { foo: 'bar' };
    const validation = validateSchema(invalid);
    expect(validation.valid).toBe(false);
    expect(validation.errors.length).toBeGreaterThan(0);
  });

  it('should parse valid JSON', () => {
    const json = JSON.stringify(demoSchema);
    const result = jsonToSchema(json);
    expect(result.schema).not.toBeNull();
    expect(result.error).toBeNull();
  });

  it('should handle invalid JSON', () => {
    const result = jsonToSchema('not valid json');
    expect(result.schema).toBeNull();
    expect(result.error).toBeTruthy();
  });
});

describe('Demo Schema', () => {
  it('should have valid demo schema', () => {
    const validation = validateSchema(demoSchema);
    expect(validation.valid).toBe(true);
  });

  it('should have valid simple schema', () => {
    const validation = validateSchema(simpleSchema);
    expect(validation.valid).toBe(true);
  });

  it('demo schema should have widgets', () => {
    expect(demoSchema.widgets.length).toBeGreaterThan(0);
  });

  it('simple schema should have widgets', () => {
    expect(simpleSchema.widgets.length).toBe(2);
  });
});

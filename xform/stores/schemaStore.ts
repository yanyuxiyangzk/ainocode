import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { FormSchema } from '@/types/form-schema';

export const useSchemaStore = defineStore('schema', () => {
  const schema = ref<FormSchema | null>(null);

  const isEmpty = computed(() => !schema.value || schema.value.widgets.length === 0);

  function initSchema(defaultSchema?: FormSchema) {
    schema.value = defaultSchema ?? createDefaultSchema();
  }

  function createDefaultSchema(): FormSchema {
    return {
      id: `form-${Date.now()}`,
      name: '未命名表单',
      version: '1.0.0',
      description: '',
      props: {
        layout: 'vertical',
        labelWidth: 120,
        labelAlign: 'left',
        hideRequiredMark: false,
        size: 'medium',
      },
      widgets: [],
      events: [],
      rules: [],
    };
  }

  function exportSchema(): string {
    if (!schema.value) return '';
    return JSON.stringify(schema.value, null, 2);
  }

  function importSchema(jsonString: string): boolean {
    try {
      const parsed = JSON.parse(jsonString);
      schema.value = parsed;
      return true;
    } catch {
      return false;
    }
  }

  function validateSchema(): { valid: boolean; errors: string[] } {
    const errors: string[] = [];
    if (!schema.value) {
      errors.push('Schema不存在');
      return { valid: false, errors };
    }

    if (!schema.value.id) errors.push('Schema缺少id');
    if (!schema.value.name) errors.push('Schema缺少name');
    if (!Array.isArray(schema.value.widgets)) errors.push('Schema缺少widgets数组');

    return { valid: errors.length === 0, errors };
  }

  return {
    schema,
    isEmpty,
    initSchema,
    createDefaultSchema,
    exportSchema,
    importSchema,
    validateSchema,
  };
});

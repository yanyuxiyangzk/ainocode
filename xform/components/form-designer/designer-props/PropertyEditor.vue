<script setup lang="ts">
/**
 * PropertyEditor - Property editor factory component
 */
import { computed, defineAsyncComponent } from 'vue';
import { propertyEditorRegistry, PROP_TYPE } from '@/utils/property-editor-registry';
import type { PropType, PropertySchema, SelectOption } from '@/types/property-editor';

const props = defineProps<{
  propType: PropType | string;
  modelValue: any;
  schema?: PropertySchema;
  disabled?: boolean;
  options?: SelectOption[];
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: any): void;
}>();

// Get editor component from registry
const EditorComponent = computed(() => {
  const editor = propertyEditorRegistry.get(props.propType);
  if (editor) {
    return editor;
  }
  // Fallback to string editor for unknown types
  return propertyEditorRegistry.get(PROP_TYPE.STRING);
});

// Merge schema options with passed options
const editorOptions = computed(() => {
  if (props.options) {
    return props.options;
  }
  if (props.schema?.options) {
    return props.schema.options;
  }
  return undefined;
});

// Pass through common props to editor
const editorProps = computed(() => {
  const result: Record<string, any> = {
    modelValue: props.modelValue,
    disabled: props.disabled,
  };

  if (props.schema?.props) {
    Object.assign(result, props.schema.props);
  }

  if (editorOptions.value) {
    result.options = editorOptions.value;
  }

  return result;
});

// Handle value update
function handleUpdate(value: any) {
  emit('update:modelValue', value);
}
</script>

<template>
  <component
    :is="EditorComponent"
    v-if="EditorComponent"
    v-bind="editorProps"
    @update:model-value="handleUpdate"
  />
  <span v-else class="unknown-editor">
    Unknown editor type: {{ propType }}
  </span>
</template>

<style scoped>
.unknown-editor {
  color: #d03050;
  font-size: 12px;
}
</style>

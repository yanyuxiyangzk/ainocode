<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { NModal, NCard, NButton, NSpace, NAlert } from 'naive-ui';
import type { FormSchema } from '@/types/form-schema';
import FormRenderer from '@/components/form-renderer/FormRenderer.vue';

const props = defineProps<{
  show: boolean;
  schema: FormSchema | null;
}>();

const emit = defineEmits<{
  (e: 'update:show', value: boolean): void;
  (e: 'submit', data: Record<string, unknown>): void;
  (e: 'reset'): void;
}>();

const formData = ref<Record<string, unknown>>({});
const isValid = ref(true);
const validationErrors = ref<Record<string, string>>({});

const visible = computed({
  get: () => props.show,
  set: (val) => emit('update:show', val),
});

function handleSubmit() {
  if (isValid.value) {
    emit('submit', formData.value);
  }
}

function handleReset() {
  formData.value = {};
  emit('reset');
}

function handleFieldChange(field: string, value: unknown) {
  formData.value[field] = value;
}

function handleValidate(valid: boolean, errors: Record<string, string>) {
  isValid.value = valid;
  validationErrors.value = errors;
}

watch(() => props.show, (newVal) => {
  if (newVal) {
    // 重置状态
    formData.value = {};
    isValid.value = true;
    validationErrors.value = {};
  }
});
</script>

<template>
  <NModal
    v-model:show="visible"
    preset="card"
    title="表单预览"
    style="width: 800px; max-width: 90vw;"
    :bordered="false"
  >
    <div class="preview-dialog">
      <NAlert v-if="!schema" type="warning" style="margin-bottom: 16px;">
        暂无表单数据，请先在设计器中添加组件
      </NAlert>

      <div v-else class="preview-dialog__content">
        <FormRenderer
          v-if="schema"
          :schema="schema"
          @field-change="handleFieldChange"
          @validate="handleValidate"
        />
      </div>

      <div class="preview-dialog__actions">
        <NSpace justify="end">
          <NButton @click="handleReset">重置</NButton>
          <NButton type="primary" @click="handleSubmit">提交</NButton>
        </NSpace>
      </div>
    </div>
  </NModal>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;
@use '@/styles/mixins' as *;

.preview-dialog {
  &__content {
    padding: $spacing-lg;
    background: #fafafa;
    border-radius: $border-radius;
    min-height: 300px;
    max-height: 60vh;
    overflow-y: auto;
    @include custom-scrollbar;
  }

  &__actions {
    margin-top: $spacing-lg;
    padding-top: $spacing-md;
    border-top: 1px solid $border-color;
  }
}
</style>

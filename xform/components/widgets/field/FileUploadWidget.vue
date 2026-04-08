<script setup lang="ts">
import { NUpload, NUploadDragger, NButton } from 'naive-ui';
import type { UploadFileInfo } from 'naive-ui';

const props = defineProps({
  value: {
    type: Array as PropType<{ name: string; url: string; size: number }[]>,
    default: () => [],
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  maxNumber: {
    type: Number,
    default: 10,
  },
  accept: {
    type: String,
    default: '*',
  },
  action: {
    type: String,
    default: '#',
  },
});

const emit = defineEmits<{
  (e: 'update:value', value: { name: string; url: string; size: number }[]): void;
}>();

function handleChange(options: { file: UploadFileInfo; fileList: UploadFileInfo[] }) {
  const newValue = options.fileList.map((file) => ({
    name: file.name,
    url: file.url || '',
    size: file.size || 0,
  }));
  emit('update:value', newValue);
}
</script>

<template>
  <n-upload
    :value="value.map(v => ({ name: v.name, url: v.url, status: 'finished' }))"
    :disabled="disabled"
    :max-number="maxNumber"
    :accept="accept"
    :action="action"
    multiple
    @change="handleChange"
  >
    <n-upload-dragger>
      <div>
        <p>点击或拖拽上传文件</p>
        <p style="font-size: 12px; color: #999;">支持任意文件格式</p>
      </div>
    </n-upload-dragger>
  </n-upload>
</template>

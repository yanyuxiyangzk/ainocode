<script setup lang="ts">
import { NUpload, NUploadDragger, NImage, NButton, NIcon } from 'naive-ui';
import { h } from 'vue';
import type { UploadFileInfo, UploadProps } from 'naive-ui';

const props = defineProps({
  value: {
    type: Array as PropType<{ name: string; url: string }[]>,
    default: () => [],
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  maxNumber: {
    type: Number,
    default: 5,
  },
  accept: {
    type: String,
    default: 'image/*',
  },
  action: {
    type: String,
    default: '#',
  },
  listType: {
    type: String as PropType<'image-list' | 'image-card'>,
    default: 'image-card',
  },
});

const emit = defineEmits<{
  (e: 'update:value', value: { name: string; url: string }[]): void;
}>();

function handleChange(options: { file: UploadFileInfo; fileList: UploadFileInfo[] }) {
  const newValue = options.fileList.map((file) => ({
    name: file.name,
    url: file.url || '',
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
    :list-type="listType"
    multiple
    @change="handleChange"
  >
    <n-button v-if="listType === 'image-card'" type="primary" dashed>
      上传图片
    </n-button>
    <n-upload-dragger v-else>
      <div>
        <p>点击或拖拽上传图片</p>
        <p style="font-size: 12px; color: #999;">支持 JPG、PNG、GIF 格式</p>
      </div>
    </n-upload-dragger>
  </n-upload>
</template>

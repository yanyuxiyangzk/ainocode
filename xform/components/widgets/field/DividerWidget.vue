<script setup lang="ts">
import { NDivider } from 'naive-ui';
import type { PropType } from 'vue';

defineProps({
  title: {
    type: String,
    default: '',
  },
  dashed: {
    type: Boolean,
    default: false,
  },
  vertical: {
    type: Boolean,
    default: false,
  },
  position: {
    type: String as PropType<'left' | 'right' | 'center'>,
    default: 'center',
  },
});
</script>

<template>
  <n-divider
    :title="title"
    :dashed="dashed"
    :vertical="vertical"
    :placement="position"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps({
  content: {
    type: String,
    default: '',
  },
});

const emit = defineEmits<{
  (e: 'update:content', value: string): void;
}>();

function handleInput(event: Event) {
  const target = event.target as HTMLDivElement;
  emit('update:content', target.innerHTML);
}
</script>

<template>
  <div class="html-widget">
    <div
      class="html-content"
      contenteditable="true"
      @input="handleInput"
      v-html="content"
    />
  </div>
</template>

<style scoped>
.html-widget {
  width: 100%;
}

.html-content {
  padding: 8px;
  min-height: 40px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  outline: none;
}

.html-content:focus {
  border-color: #18a058;
}
</style>

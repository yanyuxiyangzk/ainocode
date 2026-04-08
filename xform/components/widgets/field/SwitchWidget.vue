<script setup lang="ts">
import { NSwitch } from 'naive-ui';

const props = defineProps({
  value: {
    type: Boolean,
    default: false,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  checkedValue: {
    type: [String, Number, Boolean],
    default: true,
  },
  uncheckedValue: {
    type: [String, Number, Boolean],
    default: false,
  },
});

const emit = defineEmits<{
  (e: 'update:value', value: boolean): void;
}>();

function handleUpdate(value: boolean) {
  emit('update:value', value);
}
</script>

<template>
  <n-switch
    :value="value"
    :disabled="disabled"
    :checked-value="checkedValue"
    :unchecked-value="uncheckedValue"
    @update:value="handleUpdate"
  />
</template>

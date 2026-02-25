<template>
  <component :is="type" v-bind="linkProps">
    <slot />
  </component>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { isExternal } from '@/utils/validate'

defineOptions({ name: 'AppLink' })

const props = defineProps<{ to: string }>()

const type = computed(() => {
  return isExternal(props.to) ? 'a' : 'router-link'
})

const linkProps = computed(() => {
  if (isExternal(props.to)) {
    return {
      href: props.to,
      target: '_blank',
      rel: 'noopener noreferrer'
    }
  }
  return { to: props.to }
})
</script>

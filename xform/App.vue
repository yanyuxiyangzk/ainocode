<script setup lang="ts">
import { provide, onMounted } from 'vue';
import { NConfigProvider, NMessageProvider, NDialogProvider, NNotificationProvider } from 'naive-ui';
import FormDesigner from '@/components/form-designer/FormDesigner.vue';
import { useDesignerStore } from '@/stores/designerStore';
import { useHistoryStore } from '@/stores/historyStore';
import { createEmptySchema } from '@/utils/schema-transformer';
import { provideI18n, I18N_CONTEXT_KEY } from '@/composables/useI18n';
import '@/components/register-widgets';

// 确保注册组件
console.log('[App] Starting...');

const designerStore = useDesignerStore();
const historyStore = useHistoryStore();

// Provide i18n context
const i18nContext = provideI18n();
provide(I18N_CONTEXT_KEY, i18nContext);

const themeOverrides = {
  common: {
    primaryColor: '#18a058',
    primaryColorHover: '#36c77b',
    primaryColorPressed: '#0c7a43',
  },
};

onMounted(() => {
  console.log('[App] Mounted, initializing schema...');
  const initialSchema = createEmptySchema();
  designerStore.setSchema(initialSchema);
  historyStore.pushState(initialSchema);
  console.log('[App] Schema initialized:', initialSchema);
});
</script>

<template>
  <NConfigProvider :theme-overrides="themeOverrides">
    <NMessageProvider>
      <NDialogProvider>
        <NNotificationProvider>
          <div id="app-root">
            <FormDesigner />
          </div>
        </NNotificationProvider>
      </NDialogProvider>
    </NMessageProvider>
  </NConfigProvider>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  width: 100%;
  height: 100%;
}

#app-root {
  width: 100%;
  height: 100%;
}
</style>

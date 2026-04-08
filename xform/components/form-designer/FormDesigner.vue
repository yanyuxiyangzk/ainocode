<script setup lang="ts">
import { provide } from 'vue';
import FormDesignerProvider from './FormDesignerProvider.vue';
import DesignerToolbar from './designer-main/DesignerToolbar.vue';
import ComponentPalette from './designer-main/ComponentPalette.vue';
import DesignerCanvas from './designer-main/DesignerCanvas.vue';
import type { FormSchema } from '@/types/form-schema';

defineProps<{
  initialSchema?: FormSchema;
}>();

provide('designerMode', 'design');
</script>

<template>
  <FormDesignerProvider :initial-schema="initialSchema">
    <div class="form-designer">
      <div class="designer-toolbar-wrapper">
        <DesignerToolbar />
      </div>
      <div class="designer-main-wrapper">
        <div class="left-sider-wrapper">
          <ComponentPalette />
        </div>
        <div class="main-content-wrapper">
          <DesignerCanvas />
        </div>
        <div class="right-sider-wrapper">
          <div class="property-panel-placeholder">
            <div class="panel-header">
              <h3>属性配置</h3>
            </div>
            <div class="panel-content">
              <p>选择组件以编辑属性</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </FormDesignerProvider>
</template>

<style scoped>
.form-designer {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.designer-toolbar-wrapper {
  height: 56px;
  flex-shrink: 0;
}

.designer-main-wrapper {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.left-sider-wrapper {
  width: 260px;
  flex-shrink: 0;
  background: #fff;
  border-right: 1px solid #d9d9d9;
  overflow-y: auto;
}

.main-content-wrapper {
  flex: 1;
  overflow: hidden;
  background: #f5f5f5;
}

.right-sider-wrapper {
  width: 300px;
  flex-shrink: 0;
  background: #fff;
  border-left: 1px solid #d9d9d9;
  overflow-y: auto;
}

.property-panel-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.panel-header {
  padding: 16px;
  border-bottom: 1px solid #d9d9d9;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.panel-content {
  flex: 1;
  padding: 16px;
  color: #999;
}
</style>

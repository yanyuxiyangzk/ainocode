<template>
  <div class="page-designer">
    <div class="designer-toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="pageName"
          placeholder="页面名称"
          style="width: 200px"
        />
        <el-select v-model="pageType" placeholder="页面类型" style="width: 150px">
          <el-option label="表单页" value="form" />
          <el-option label="列表页" value="list" />
          <el-option label="详情页" value="detail" />
        </el-select>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" icon="View" @click="handlePreview">预览</el-button>
        <el-button type="success" icon="Check" @click="handleSave">保存配置</el-button>
        <el-button type="warning" icon="Download" @click="handleExport">导出代码</el-button>
      </div>
    </div>

    <div class="designer-main">
      <!-- 组件面板 -->
      <div class="component-panel">
        <h4>组件库</h4>
        <el-collapse v-model="activeCollapse">
          <el-collapse-item title="基础组件" name="basic">
            <div class="component-list">
              <div
                v-for="comp in basicComponents"
                :key="comp.type"
                class="component-item"
                draggable="true"
                @dragstart="handleDragStart($event, comp)"
              >
                <el-icon :size="20"><component :is="comp.icon" /></el-icon>
                <span>{{ comp.name }}</span>
              </div>
            </div>
          </el-collapse-item>
          <el-collapse-item title="表单组件" name="form">
            <div class="component-list">
              <div
                v-for="comp in formComponents"
                :key="comp.type"
                class="component-item"
                draggable="true"
                @dragstart="handleDragStart($event, comp)"
              >
                <el-icon :size="20"><component :is="comp.icon" /></el-icon>
                <span>{{ comp.name }}</span>
              </div>
            </div>
          </el-collapse-item>
          <el-collapse-item title="布局组件" name="layout">
            <div class="component-list">
              <div
                v-for="comp in layoutComponents"
                :key="comp.type"
                class="component-item"
                draggable="true"
                @dragstart="handleDragStart($event, comp)"
              >
                <el-icon :size="20"><component :is="comp.icon" /></el-icon>
                <span>{{ comp.name }}</span>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>

      <!-- 设计区域 -->
      <div class="design-area">
        <div
          class="canvas"
          @drop="handleDrop"
          @dragover.prevent
          @dragenter.prevent
        >
          <div v-if="designComponents.length === 0" class="empty-placeholder">
            <el-icon :size="60"><Document /></el-icon>
            <p>拖拽左侧组件到此处</p>
          </div>
          <div
            v-for="(comp, index) in designComponents"
            :key="index"
            class="design-component"
            :class="{ active: selectedComponentIndex === index }"
            @click="handleSelectComponent(index)"
          >
            <component
              :is="getComponentRender(comp)"
              v-bind="comp.props"
              :style="comp.style"
            />
            <div class="component-actions">
              <el-button type="primary" link icon="Delete" size="small" @click.stop="handleRemoveComponent(index)" />
            </div>
          </div>
        </div>
      </div>

      <!-- 属性面板 -->
      <div class="property-panel">
        <h4>属性配置</h4>
        <div v-if="selectedComponent" class="property-form">
          <el-form label-width="80px" size="small">
            <el-form-item label="组件名称">
              <el-input v-model="selectedComponent.name" />
            </el-form-item>
            <el-form-item label="标签文本" v-if="selectedComponent.props?.label !== undefined">
              <el-input v-model="selectedComponent.props.label" />
            </el-form-item>
            <el-form-item label="占位文本" v-if="selectedComponent.props?.placeholder !== undefined">
              <el-input v-model="selectedComponent.props.placeholder" />
            </el-form-item>
            <el-form-item label="是否必填" v-if="selectedComponent.props?.required !== undefined">
              <el-switch v-model="selectedComponent.props.required" />
            </el-form-item>
            <el-form-item label="是否禁用">
              <el-switch v-model="selectedComponent.props.disabled" />
            </el-form-item>
            <el-form-item label="宽度">
              <el-input v-model="selectedComponent.style.width" placeholder="如: 100%" />
            </el-form-item>
          </el-form>
        </div>
        <el-empty v-else description="请选择组件" :image-size="80" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElInput, ElSelect, ElButton, ElSwitch, ElDatePicker, ElRadio, ElCheckbox } from 'element-plus'
import {
  Document, Edit, List, Grid, Calendar, Clock, Link, Picture, Switch, Operation
} from '@element-plus/icons-vue'

defineOptions({ name: 'NocodePage' })

const pageName = ref('')
const pageType = ref('form')
const activeCollapse = ref(['basic', 'form', 'layout'])
const designComponents = ref<any[]>([])
const selectedComponentIndex = ref(-1)

const basicComponents = [
  { type: 'input', name: '输入框', icon: 'Edit' },
  { type: 'select', name: '下拉框', icon: 'List' },
  { type: 'button', name: '按钮', icon: 'Operation' },
  { type: 'text', name: '文本', icon: 'Document' }
]

const formComponents = [
  { type: 'date', name: '日期选择', icon: 'Calendar' },
  { type: 'time', name: '时间选择', icon: 'Clock' },
  { type: 'switch', name: '开关', icon: 'Switch' },
  { type: 'radio', name: '单选框', icon: 'Grid' },
  { type: 'checkbox', name: '复选框', icon: 'Grid' }
]

const layoutComponents = [
  { type: 'row', name: '行容器', icon: 'Grid' },
  { type: 'divider', name: '分割线', icon: 'Operation' }
]

const selectedComponent = computed(() => {
  if (selectedComponentIndex.value >= 0 && selectedComponentIndex.value < designComponents.value.length) {
    return designComponents.value[selectedComponentIndex.value]
  }
  return null
})

const handleDragStart = (event: DragEvent, comp: any) => {
  event.dataTransfer?.setData('component', JSON.stringify(comp))
}

const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  const data = event.dataTransfer?.getData('component')
  if (data) {
    const comp = JSON.parse(data)
    const newComponent = {
      type: comp.type,
      name: comp.name,
      props: getDefaultProps(comp.type),
      style: { width: '100%', marginBottom: '10px' }
    }
    designComponents.value.push(newComponent)
    selectedComponentIndex.value = designComponents.value.length - 1
  }
}

const getDefaultProps = (type: string) => {
  const propsMap: Record<string, any> = {
    input: { label: '标签', placeholder: '请输入', required: false, disabled: false },
    select: { label: '标签', placeholder: '请选择', required: false, disabled: false },
    button: { type: 'primary', text: '按钮' },
    text: { content: '文本内容' },
    date: { label: '日期', placeholder: '选择日期' },
    time: { label: '时间', placeholder: '选择时间' },
    switch: { label: '开关', disabled: false },
    radio: { label: '单选', disabled: false },
    checkbox: { label: '复选', disabled: false },
    row: {},
    divider: {}
  }
  return { ...propsMap[type] }
}

const getComponentRender = (comp: any) => {
  const componentMap: Record<string, any> = {
    input: ElInput,
    select: ElSelect,
    button: ElButton,
    switch: ElSwitch,
    date: ElDatePicker,
    radio: ElRadio,
    checkbox: ElCheckbox
  }
  return componentMap[comp.type] || 'div'
}

const handleSelectComponent = (index: number) => {
  selectedComponentIndex.value = index
}

const handleRemoveComponent = (index: number) => {
  designComponents.value.splice(index, 1)
  if (selectedComponentIndex.value >= designComponents.value.length) {
    selectedComponentIndex.value = designComponents.value.length - 1
  }
}

const handlePreview = () => {
  ElMessage.success('预览功能开发中...')
}

const handleSave = () => {
  ElMessage.success('保存成功')
}

const handleExport = () => {
  ElMessage.success('代码导出成功')
}
</script>

<style lang="scss" scoped>
.page-designer {
  height: calc(100vh - 130px);
  display: flex;
  flex-direction: column;
  background: #f0f2f5;

  .designer-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 20px;
    background: #fff;
    border-bottom: 1px solid #e4e7ed;

    .toolbar-left,
    .toolbar-right {
      display: flex;
      gap: 10px;
    }
  }

  .designer-main {
    flex: 1;
    display: flex;
    overflow: hidden;

    .component-panel {
      width: 260px;
      background: #fff;
      border-right: 1px solid #e4e7ed;
      overflow-y: auto;

      h4 {
        padding: 15px;
        margin: 0;
        border-bottom: 1px solid #e4e7ed;
      }

      .component-list {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 10px;
        padding: 10px;
      }

      .component-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 10px;
        border: 1px solid #e4e7ed;
        border-radius: 4px;
        cursor: grab;
        transition: all 0.3s;

        &:hover {
          border-color: #409eff;
          color: #409eff;
        }

        span {
          margin-top: 5px;
          font-size: 12px;
        }
      }
    }

    .design-area {
      flex: 1;
      padding: 20px;
      overflow-y: auto;

      .canvas {
        min-height: 100%;
        background: #fff;
        border-radius: 4px;
        padding: 20px;

        .empty-placeholder {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          height: 400px;
          color: #909399;

          p {
            margin-top: 20px;
          }
        }

        .design-component {
          position: relative;
          padding: 10px;
          border: 1px dashed transparent;
          border-radius: 4px;
          cursor: pointer;

          &:hover,
          &.active {
            border-color: #409eff;
            background: rgba(64, 158, 255, 0.05);
          }

          .component-actions {
            position: absolute;
            top: 5px;
            right: 5px;
            opacity: 0;
            transition: opacity 0.3s;
          }

          &:hover .component-actions {
            opacity: 1;
          }
        }
      }
    }

    .property-panel {
      width: 300px;
      background: #fff;
      border-left: 1px solid #e4e7ed;
      overflow-y: auto;

      h4 {
        padding: 15px;
        margin: 0;
        border-bottom: 1px solid #e4e7ed;
      }

      .property-form {
        padding: 15px;
      }
    }
  }
}
</style>

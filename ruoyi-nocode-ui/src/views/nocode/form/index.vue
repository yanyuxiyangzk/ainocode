<template>
  <div class="form-designer-container">
    <!-- Header -->
    <div class="form-designer-header">
      <div class="header-left">
        <h2>Form Designer</h2>
        <el-tag v-if="formConfig.status" :type="formConfig.status === 'PUBLISHED' ? 'success' : 'info'" size="small">
          {{ formConfig.status }}
        </el-tag>
      </div>
      <div class="header-actions">
        <el-button type="text" @click="handleFormList">Form List</el-button>
        <el-button type="primary" @click="handleNewForm">New Form</el-button>
        <el-button @click="handlePreview">Preview</el-button>
        <el-button type="success" @click="handleSave" :loading="saving">Save</el-button>
      </div>
    </div>

    <!-- Main Content -->
    <div class="form-designer-main">
      <!-- Left Panel: Component Library -->
      <div class="component-library">
        <div class="library-section">
          <h3>Basic Components</h3>
          <div class="component-list">
            <div
              v-for="comp in basicComponents"
              :key="comp.type"
              class="component-item"
              draggable="true"
              @dragstart="onDragStart($event, comp)"
              @click="addComponent(comp)"
            >
              <i :class="comp.icon"></i>
              <span>{{ comp.label }}</span>
            </div>
          </div>
        </div>
        <div class="library-section">
          <h3>Advanced Components</h3>
          <div class="component-list">
            <div
              v-for="comp in advancedComponents"
              :key="comp.type"
              class="component-item"
              draggable="true"
              @dragstart="onDragStart($event, comp)"
              @click="addComponent(comp)"
            >
              <i :class="comp.icon"></i>
              <span>{{ comp.label }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Center Panel: Form Canvas -->
      <div class="form-canvas">
        <div class="canvas-header">
          <el-input
            v-model="formConfig.name"
            placeholder="Form Name"
            class="form-name-input"
            size="medium"
          />
          <el-input
            v-model="formConfig.description"
            placeholder="Form Description"
            class="form-desc-input"
            size="medium"
          />
        </div>
        <div
          class="canvas-content"
          :class="{ 'drag-over': isDragOver }"
          @drop="onDrop"
          @dragover="onDragOver"
          @dragleave="onDragLeave"
          @click="selectWidget(null)"
        >
          <div v-if="widgets.length === 0" class="canvas-placeholder">
            <i class="el-icon-edit"></i>
            <p>Drag components here to build your form</p>
            <p class="hint">or click a component to add it</p>
          </div>
          <div
            v-for="(widget, index) in widgets"
            :key="widget.id"
            class="widget-item"
            :class="{
              selected: selectedWidget && selectedWidget.id === widget.id,
              'widget-half': widget.width === '50%',
              'widget-third': widget.width === '33%',
              'widget-quarter': widget.width === '25%'
            }"
            @click.stop="selectWidget(widget)"
          >
            <div class="widget-drag-handle">
              <i class="el-icon-rank"></i>
            </div>
            <div class="widget-content">
              <div class="widget-label" v-if="widget.label">
                {{ widget.label }}
                <span v-if="widget.required" class="required-star">*</span>
              </div>
              <component
                :is="getWidgetComponent(widget.type)"
                :config="widget"
                :value="widget.defaultValue"
                :disabled="widget.disabled"
                :placeholder="widget.placeholder"
                @input="handleWidgetInput(widget, $event)"
              />
            </div>
            <div class="widget-actions">
              <el-button size="mini" circle @click.stop="duplicateWidget(index)" title="Duplicate">
                <i class="el-icon-document-copy"></i>
              </el-button>
              <el-button size="mini" circle @click.stop="moveWidget(index, -1)" :disabled="index === 0" title="Move Up">
                <i class="el-icon-arrow-up"></i>
              </el-button>
              <el-button size="mini" circle @click.stop="moveWidget(index, 1)" :disabled="index === widgets.length - 1" title="Move Down">
                <i class="el-icon-arrow-down"></i>
              </el-button>
              <el-button size="mini" circle type="danger" @click.stop="removeWidget(index)" title="Delete">
                <i class="el-icon-delete"></i>
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Panel: Properties -->
      <div class="properties-panel">
        <el-tabs v-model="propertyTab" type="border-card">
          <el-tab-pane label="Properties" name="properties">
            <div v-if="selectedWidget" class="property-form">
              <el-form label-width="100px" size="small">
                <el-form-item label="Label">
                  <el-input v-model="selectedWidget.label" placeholder="Field label"></el-input>
                </el-form-item>
                <el-form-item label="Field Name">
                  <el-input v-model="selectedWidget.fieldName" placeholder="field_name">
                    <template slot="prepend">data.</template>
                  </el-input>
                </el-form-item>
                <el-form-item label="Placeholder">
                  <el-input v-model="selectedWidget.placeholder" placeholder="Placeholder text"></el-input>
                </el-form-item>
                <el-form-item label="Default Value">
                  <el-input v-model="selectedWidget.defaultValue" placeholder="Default value"></el-input>
                </el-form-item>
                <el-divider>Layout</el-divider>
                <el-form-item label="Width">
                  <el-radio-group v-model="selectedWidget.width" size="small">
                    <el-radio-button label="100%">Full</el-radio-button>
                    <el-radio-button label="50%">1/2</el-radio-button>
                    <el-radio-button label="33%">1/3</el-radio-button>
                    <el-radio-button label="25%">1/4</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="Show Label">
                  <el-switch v-model="selectedWidget.showLabel"></el-switch>
                </el-form-item>
                <el-divider>Behavior</el-divider>
                <el-form-item label="Required">
                  <el-switch v-model="selectedWidget.required"></el-switch>
                </el-form-item>
                <el-form-item label="Disabled">
                  <el-switch v-model="selectedWidget.disabled"></el-switch>
                </el-form-item>
                <el-form-item label="Read Only">
                  <el-switch v-model="selectedWidget.readOnly"></el-switch>
                </el-form-item>
                <el-form-item label="Visible">
                  <el-switch v-model="selectedWidget.visible"></el-switch>
                </el-form-item>
                <el-divider>Validation</el-divider>
                <el-form-item label="Rules">
                  <el-checkbox-group v-model="selectedWidget.validationRules">
                    <el-checkbox label="email">Email</el-checkbox>
                    <el-checkbox label="phone">Phone</el-checkbox>
                    <el-checkbox label="url">URL</el-checkbox>
                    <el-checkbox label="number">Number</el-checkbox>
                    <el-checkbox label="minLength">Min Length</el-checkbox>
                    <el-checkbox label="maxLength">Max Length</el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
                <el-form-item label="Min Length" v-if="selectedWidget.validationRules.includes('minLength')">
                  <el-input-number v-model="selectedWidget.minLength" :min="0" size="small"></el-input-number>
                </el-form-item>
                <el-form-item label="Max Length" v-if="selectedWidget.validationRules.includes('maxLength')">
                  <el-input-number v-model="selectedWidget.maxLength" :min="0" size="small"></el-input-number>
                </el-form-item>
                <el-form-item label="Options" v-if="hasOptions(selectedWidget.type)">
                  <el-input
                    type="textarea"
                    v-model="selectedWidget.options"
                    placeholder="One option per line"
                    :rows="4"
                  ></el-input>
                </el-form-item>
              </el-form>
            </div>
            <div v-else class="no-selection">
              <i class="el-icon-cursor"></i>
              <p>Select a component to edit its properties</p>
            </div>
          </el-tab-pane>
          <el-tab-pane label="Events" name="events">
            <div v-if="selectedWidget" class="property-form">
              <el-form label-width="100px" size="small">
                <el-form-item label="On Focus">
                  <el-select v-model="selectedWidget.events.onFocus" placeholder="Select action" clearable>
                    <el-option label="None" value=""></el-option>
                    <el-option label="Trigger Validation" value="triggerValidation"></el-option>
                    <el-option label="Clear Error" value="clearError"></el-option>
                    <el-option label="Custom Script" value="customScript"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="On Blur">
                  <el-select v-model="selectedWidget.events.onBlur" placeholder="Select action" clearable>
                    <el-option label="None" value=""></el-option>
                    <el-option label="Trigger Validation" value="triggerValidation"></el-option>
                    <el-option label="Clear Error" value="clearError"></el-option>
                    <el-option label="Custom Script" value="customScript"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="On Change">
                  <el-select v-model="selectedWidget.events.onChange" placeholder="Select action" clearable>
                    <el-option label="None" value=""></el-option>
                    <el-option label="Trigger Validation" value="triggerValidation"></el-option>
                    <el-option label="Update Field" value="updateField"></el-option>
                    <el-option label="Custom Script" value="customScript"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="On Click">
                  <el-select v-model="selectedWidget.events.onClick" placeholder="Select action" clearable>
                    <el-option label="None" value=""></el-option>
                    <el-option label="Submit Form" value="submitForm"></el-option>
                    <el-option label="Reset Form" value="resetForm"></el-option>
                    <el-option label="Custom Script" value="customScript"></el-option>
                  </el-select>
                </el-form-item>
                <el-divider>Data Binding</el-divider>
                <el-form-item label="Data Source">
                  <el-select v-model="selectedWidget.dataSource" placeholder="Select data source" clearable>
                    <el-option label="None" value=""></el-option>
                    <el-option label="API" value="api"></el-option>
                    <el-option label="Dictionary" value="dict"></el-option>
                    <el-option label="Static" value="static"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="API URL" v-if="selectedWidget.dataSource === 'api'">
                  <el-input v-model="selectedWidget.apiUrl" placeholder="/api/dict/items"></el-input>
                </el-form-item>
                <el-form-item label="Value Field" v-if="selectedWidget.dataSource">
                  <el-input v-model="selectedWidget.valueField" placeholder="value"></el-input>
                </el-form-item>
                <el-form-item label="Label Field" v-if="selectedWidget.dataSource">
                  <el-input v-model="selectedWidget.labelField" placeholder="label"></el-input>
                </el-form-item>
              </el-form>
            </div>
            <div v-else class="no-selection">
              <i class="el-icon-lightning"></i>
              <p>Select a component to configure events</p>
            </div>
          </el-tab-pane>
          <el-tab-pane label="Form Settings" name="settings">
            <el-form label-width="100px" size="small">
              <el-form-item label="Form Key">
                <el-input v-model="formConfig.formKey" placeholder="form_key"></el-input>
              </el-form-item>
              <el-form-item label="Layout">
                <el-radio-group v-model="formConfig.layout">
                  <el-radio label="vertical">Vertical</el-radio>
                  <el-radio label="horizontal">Horizontal</el-radio>
                  <el-radio label="inline">Inline</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="Label Position">
                <el-radio-group v-model="formConfig.labelPosition">
                  <el-radio label="left">Left</el-radio>
                  <el-radio label="top">Top</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="Submit URL">
                <el-input v-model="formConfig.submitUrl" placeholder="/api/form/submit"></el-input>
              </el-form-item>
              <el-form-item label="Success Msg">
                <el-input v-model="formConfig.successMessage" placeholder="Submitted successfully"></el-input>
              </el-form-item>
              <el-form-item label="Redirect URL">
                <el-input v-model="formConfig.redirectUrl" placeholder="/form/list"></el-input>
              </el-form-item>
              <el-divider>Advanced</el-divider>
              <el-form-item label="CSS Class">
                <el-input v-model="formConfig.cssClass" placeholder="custom-form"></el-input>
              </el-form-item>
              <el-form-item label="Custom JS">
                <el-input type="textarea" v-model="formConfig.customJs" :rows="4" placeholder="// Custom JavaScript"></el-input>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- Form List Dialog -->
    <el-dialog title="Form List" :visible.sync="formListVisible" width="80%">
      <el-table :data="formList" border>
        <el-table-column prop="name" label="Name" width="150"></el-table-column>
        <el-table-column prop="description" label="Description"></el-table-column>
        <el-table-column prop="status" label="Status" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 'PUBLISHED' ? 'success' : 'info'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="Create Time" width="180"></el-table-column>
        <el-table-column label="Actions" width="200">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEditForm(scope.row)">Edit</el-button>
            <el-button size="mini" type="success" @click="handlePublishForm(scope.row.id)">Publish</el-button>
            <el-button size="mini" type="danger" @click="handleDeleteForm(scope.row.id)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- Preview Dialog -->
    <el-dialog title="Form Preview" :visible.sync="previewVisible" width="60%" fullscreen>
      <div class="preview-form" :class="'layout-' + formConfig.layout">
        <div class="preview-header">
          <h3>{{ formConfig.name }}</h3>
          <p>{{ formConfig.description }}</p>
        </div>
        <el-form
          :label-position="formConfig.labelPosition || 'top'"
          :label-width="formConfig.layout === 'horizontal' ? '120px' : ''"
        >
          <el-form-item
            v-for="widget in widgets"
            :key="widget.id"
            :label="widget.showLabel !== false ? widget.label : ''"
            :required="widget.required"
            :style="{ display: widget.visible === false ? 'none' : 'block' }"
          >
            <component
              :is="getWidgetComponent(widget.type)"
              :config="widget"
              :value="widget.defaultValue"
              :disabled="widget.disabled"
              :placeholder="widget.placeholder"
            />
          </el-form>
        </el-form>
        <div class="preview-actions">
          <el-button type="primary" size="large">Submit</el-button>
          <el-button size="large">Reset</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listFormConfig,
  getFormConfig,
  createFormConfig,
  updateFormConfig,
  deleteFormConfig,
  publishFormConfig
} from '@/api/nocode/form'

export default {
  name: 'FormDesigner',
  data() {
    return {
      propertyTab: 'properties',
      formConfig: {
        id: null,
        name: 'New Form',
        description: '',
        formKey: '',
        layout: 'vertical',
        labelPosition: 'top',
        submitUrl: '',
        successMessage: 'Submitted successfully',
        redirectUrl: '',
        cssClass: '',
        customJs: '',
        formConfig: '',
        status: 'DRAFT'
      },
      widgets: [],
      selectedWidget: null,
      saving: false,
      formListVisible: false,
      formList: [],
      previewVisible: false,
      currentEditId: null,
      draggedComponent: null,
      isDragOver: false,
      basicComponents: [
        { type: 'input', label: 'Input', icon: 'el-icon-edit' },
        { type: 'textarea', label: 'Textarea', icon: 'el-icon-document' },
        { type: 'number', label: 'Number', icon: 'el-icon-plus' },
        { type: 'password', label: 'Password', icon: 'el-icon-lock' }
      ],
      advancedComponents: [
        { type: 'select', label: 'Select', icon: 'el-icon-arrow-down' },
        { type: 'radio', label: 'Radio', icon: 'el-icon-bangzhu' },
        { type: 'checkbox', label: 'Checkbox', icon: 'el-icon-check' },
        { type: 'date', label: 'Date', icon: 'el-icon-date' },
        { type: 'datetime', label: 'DateTime', icon: 'el-icon-time' },
        { type: 'daterange', label: 'Date Range', icon: 'el-icon-date' },
        { type: 'switch', label: 'Switch', icon: 'el-icon-open' },
        { type: 'slider', label: 'Slider', icon: 'el-icon-minus' },
        { type: 'cascader', label: 'Cascader', icon: 'el-icon-menu' },
        { type: 'upload', label: 'Upload', icon: 'el-icon-upload2' },
        { type: 'editor', label: 'Editor', icon: 'el-icon-edit-outline' },
        { type: 'divider', label: 'Divider', icon: 'el-icon-minus' }
      ]
    }
  },
  computed: {
    componentTypes() {
      return [...this.basicComponents, ...this.advancedComponents]
    }
  },
  methods: {
    handleFormList() {
      this.loadFormList()
    },
    handleNewForm() {
      this.formConfig = {
        id: null,
        name: 'New Form',
        description: '',
        formKey: 'form_' + Date.now(),
        layout: 'vertical',
        labelPosition: 'top',
        submitUrl: '',
        successMessage: 'Submitted successfully',
        redirectUrl: '',
        cssClass: '',
        customJs: '',
        formConfig: '',
        status: 'DRAFT'
      }
      this.widgets = []
      this.selectedWidget = null
      this.currentEditId = null
      this.propertyTab = 'properties'
    },
    handleSave() {
      if (!this.formConfig.name) {
        this.$message.warning('Please enter form name')
        return
      }
      this.saving = true
      this.formConfig.formConfig = JSON.stringify(this.widgets)

      const savePromise = this.currentEditId
        ? updateFormConfig(this.currentEditId, this.formConfig)
        : createFormConfig(this.formConfig)

      savePromise.then(res => {
        if (res.code === 200) {
          this.$message.success('Form saved successfully')
          if (!this.currentEditId && res.data && res.data.id) {
            this.currentEditId = res.data.id
            this.formConfig.id = res.data.id
          }
        } else {
          this.$message.error(res.msg || 'Save failed')
        }
      }).finally(() => {
        this.saving = false
      })
    },
    handlePreview() {
      this.previewVisible = true
    },
    onDragStart(event, comp) {
      this.draggedComponent = comp
      event.dataTransfer.effectAllowed = 'copy'
      event.dataTransfer.setData('componentType', JSON.stringify(comp))
    },
    onDragOver(event) {
      event.preventDefault()
      event.dataTransfer.dropEffect = 'copy'
      this.isDragOver = true
    },
    onDragLeave() {
      this.isDragOver = false
    },
    onDrop(event) {
      event.preventDefault()
      this.isDragOver = false
      const componentType = event.dataTransfer.getData('componentType')
      const comp = componentType ? JSON.parse(componentType) : this.draggedComponent
      if (comp) {
        const rect = this.$refs.canvas ? this.$refs.canvas.getBoundingClientRect() : event.target.getBoundingClientRect()
        const newWidget = this.createWidget(comp)
        this.widgets.push(newWidget)
        this.selectWidget(newWidget)
        this.draggedComponent = null
      }
    },
    addComponent(comp) {
      const newWidget = this.createWidget(comp)
      this.widgets.push(newWidget)
      this.selectWidget(newWidget)
    },
    createWidget(comp) {
      return {
        id: 'widget_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9),
        type: comp.type,
        label: comp.label,
        fieldName: this.toCamelCase(comp.label) + '_' + Date.now().toString(36).substr(-4),
        placeholder: '',
        defaultValue: '',
        required: false,
        disabled: false,
        readOnly: false,
        visible: true,
        width: '100%',
        showLabel: true,
        options: '',
        validationRules: [],
        minLength: null,
        maxLength: null,
        dataSource: '',
        apiUrl: '',
        valueField: 'value',
        labelField: 'label',
        events: {
          onFocus: '',
          onBlur: '',
          onChange: '',
          onClick: ''
        }
      }
    },
    selectWidget(widget) {
      this.selectedWidget = widget
      this.propertyTab = 'properties'
    },
    duplicateWidget(index) {
      const original = this.widgets[index]
      const duplicate = JSON.parse(JSON.stringify(original))
      duplicate.id = 'widget_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
      duplicate.label = original.label + ' (Copy)'
      duplicate.fieldName = original.fieldName + '_copy'
      this.widgets.splice(index + 1, 0, duplicate)
      this.selectWidget(duplicate)
    },
    moveWidget(index, direction) {
      const newIndex = index + direction
      if (newIndex >= 0 && newIndex < this.widgets.length) {
        const temp = this.widgets[index]
        this.widgets.splice(index, 1, this.widgets[newIndex])
        this.widgets.splice(newIndex, 1, temp)
      }
    },
    removeWidget(index) {
      this.$confirm('Are you sure to delete this component?').then(() => {
        this.widgets.splice(index, 1)
        if (this.selectedWidget && this.widgets.indexOf(this.selectedWidget) === -1) {
          this.selectedWidget = null
        }
        this.$message.success('Component deleted')
      }).catch(() => {})
    },
    handleWidgetInput(widget, value) {
      widget.defaultValue = value
    },
    getWidgetComponent(type) {
      const componentMap = {
        input: 'el-input',
        textarea: 'el-input',
        number: 'el-input-number',
        password: 'el-input',
        select: 'el-select',
        radio: 'el-radio-group',
        checkbox: 'el-checkbox-group',
        date: 'el-date-picker',
        datetime: 'el-date-picker',
        daterange: 'el-date-picker',
        switch: 'el-switch',
        slider: 'el-slider',
        cascader: 'el-cascader',
        upload: 'el-upload',
        editor: 'el-input',
        divider: 'div'
      }
      return componentMap[type] || 'el-input'
    },
    hasOptions(type) {
      return ['select', 'radio', 'checkbox'].includes(type)
    },
    toCamelCase(str) {
      if (!str) return ''
      return str.replace(/[^a-zA-Z0-9]+(.)/g, (_, c) => c.toUpperCase())
    },
    loadFormList() {
      listFormConfig().then(res => {
        if (res.code === 200) {
          this.formList = res.data
          this.formListVisible = true
        }
      }).catch(() => {
        this.formList = [
          { id: 1, name: 'User Registration', description: 'User registration form', status: 'PUBLISHED', createTime: '2026-04-01 10:00' },
          { id: 2, name: 'Contact Form', description: 'Contact us form', status: 'DRAFT', createTime: '2026-04-02 15:00' }
        ]
        this.formListVisible = true
      })
    },
    handleEditForm(row) {
      getFormConfig(row.id).then(res => {
        if (res.code === 200) {
          this.formConfig = { ...this.formConfig, ...res.data }
          this.widgets = res.data.formConfig ? JSON.parse(res.data.formConfig) : []
          this.currentEditId = row.id
          this.formListVisible = false
        }
      }).catch(() => {
        this.formConfig = {
          id: row.id,
          name: row.name,
          description: row.description,
          status: row.status,
          formKey: 'form_' + row.id,
          layout: 'vertical',
          labelPosition: 'top'
        }
        this.widgets = [
          { id: 'w1', type: 'input', label: 'Username', fieldName: 'username', width: '100%', required: true },
          { id: 'w2', type: 'input', label: 'Email', fieldName: 'email', width: '100%', required: true }
        ]
        this.currentEditId = row.id
        this.formListVisible = false
      })
    },
    handlePublishForm(id) {
      publishFormConfig(id).then(res => {
        if (res.code === 200) {
          this.$message.success('Form published successfully')
          this.loadFormList()
        } else {
          this.$message.error(res.msg || 'Publish failed')
        }
      })
    },
    handleDeleteForm(id) {
      this.$confirm('Are you sure to delete this form?').then(() => {
        deleteFormConfig(id).then(res => {
          if (res.code === 200) {
            this.$message.success('Form deleted successfully')
            this.loadFormList()
          }
        })
      })
    }
  },
  mounted() {
    // Check for edit query param
    const formId = this.$route.query.id
    if (formId) {
      this.handleEditForm({ id: formId })
    }
  }
}
</script>

<style scoped>
.form-designer-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
}

.form-designer-header {
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.form-designer-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.form-designer-main {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.component-library {
  width: 200px;
  background: #fff;
  border-right: 1px solid #e8e8e8;
  overflow-y: auto;
}

.library-section {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.library-section:last-child {
  border-bottom: none;
}

.library-section h3 {
  margin: 0 0 12px 0;
  font-size: 12px;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.component-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.component-item {
  padding: 8px 12px;
  background: #f5f7fa;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  cursor: move;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
  font-size: 13px;
}

.component-item:hover {
  border-color: #667eea;
  background: #f0f2ff;
  color: #667eea;
}

.component-item i {
  font-size: 16px;
  color: #666;
}

.component-item:hover i {
  color: #667eea;
}

.form-canvas {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f0f2f5;
}

.canvas-header {
  margin-bottom: 16px;
  display: flex;
  gap: 16px;
}

.form-name-input {
  width: 280px;
}

.form-desc-input {
  flex: 1;
}

.canvas-content {
  min-height: 500px;
  background: #fff;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s;
}

.canvas-content.drag-over {
  border-color: #667eea;
  background: #f0f7ff;
}

.canvas-placeholder {
  height: 468px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 14px;
}

.canvas-placeholder i {
  font-size: 48px;
  margin-bottom: 16px;
}

.canvas-placeholder .hint {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.widget-item {
  padding: 16px;
  margin-bottom: 12px;
  border: 2px solid transparent;
  border-radius: 6px;
  position: relative;
  background: #fff;
  transition: all 0.2s;
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.widget-item:hover {
  border-color: #dcdfe6;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.widget-item.selected {
  border-color: #667eea;
  background: #f0f7ff;
}

.widget-item.widget-half { width: calc(50% - 6px); }
.widget-item.widget-third { width: calc(33.33% - 6px); }
.widget-item.widget-quarter { width: calc(25% - 6px); }

.widget-drag-handle {
  cursor: move;
  color: #c0c4cc;
  padding: 4px;
  font-size: 16px;
}

.widget-drag-handle:hover {
  color: #667eea;
}

.widget-content {
  flex: 1;
  min-width: 0;
}

.widget-label {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
  font-weight: 500;
}

.required-star {
  color: #f56c6c;
  margin-left: 2px;
}

.widget-actions {
  position: absolute;
  right: 8px;
  top: 8px;
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s;
}

.widget-item:hover .widget-actions,
.widget-item.selected .widget-actions {
  opacity: 1;
}

.properties-panel {
  width: 340px;
  background: #fff;
  border-left: 1px solid #e8e8e8;
  overflow-y: auto;
}

.property-form {
  padding: 8px;
}

.no-selection {
  color: #909399;
  text-align: center;
  padding: 60px 20px;
}

.no-selection i {
  font-size: 48px;
  color: #dcdfe6;
  margin-bottom: 16px;
}

.no-selection p {
  margin: 0;
  font-size: 14px;
}

.preview-form {
  padding: 24px;
}

.preview-header {
  text-align: center;
  margin-bottom: 32px;
}

.preview-header h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.preview-header p {
  margin: 0;
  color: #909399;
}

.preview-actions {
  margin-top: 32px;
  text-align: center;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.layout-horizontal .el-form-item {
  display: inline-block;
  margin-right: 20px;
}

.layout-inline .el-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.layout-inline .el-form-item {
  flex: 0 0 auto;
}
</style>
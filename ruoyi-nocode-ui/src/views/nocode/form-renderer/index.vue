<template>
  <div class="form-renderer-container">
    <div class="form-renderer-header" v-if="formConfig.formName">
      <h2>{{ formConfig.formName }}</h2>
    </div>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      :label-position="labelPosition"
      :label-width="labelWidth"
      :class="['form-renderer', layoutClass]"
    >
      <el-form-item
        v-for="component in formConfig.components"
        :key="component.field"
        :label="component.label"
        :prop="component.field"
        :required="component.required"
        :class="getComponentClass(component)"
      >
        <!-- Input -->
        <el-input
          v-if="component.type === 'input'"
          v-model="formData[component.field]"
          :placeholder="component.placeholder || ''"
          :disabled="component.disabled"
          :clearable="true"
        />

        <!-- Textarea -->
        <el-input
          v-else-if="component.type === 'textarea'"
          v-model="formData[component.field]"
          type="textarea"
          :rows="component.rows || 3"
          :placeholder="component.placeholder || ''"
          :disabled="component.disabled"
        />

        <!-- Number -->
        <el-input-number
          v-else-if="component.type === 'number'"
          v-model="formData[component.field]"
          :min="component.min"
          :max="component.max"
          :step="component.step || 1"
          :disabled="component.disabled"
          :placeholder="component.placeholder || ''"
        />

        <!-- Select -->
        <el-select
          v-else-if="component.type === 'select'"
          v-model="formData[component.field]"
          :placeholder="component.placeholder || 'Please select'"
          :disabled="component.disabled"
          :clearable="true"
          style="width: 100%"
        >
          <el-option
            v-for="option in parseOptions(component.options)"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>

        <!-- Radio -->
        <el-radio-group
          v-else-if="component.type === 'radio'"
          v-model="formData[component.field]"
          :disabled="component.disabled"
        >
          <el-radio
            v-for="option in parseOptions(component.options)"
            :key="option.value"
            :label="option.value"
          >
            {{ option.label }}
          </el-radio>
        </el-radio-group>

        <!-- Checkbox -->
        <el-checkbox-group
          v-else-if="component.type === 'checkbox'"
          v-model="formData[component.field]"
          :disabled="component.disabled"
        >
          <el-checkbox
            v-for="option in parseOptions(component.options)"
            :key="option.value"
            :label="option.value"
          >
            {{ option.label }}
          </el-checkbox>
        </el-checkbox-group>

        <!-- Date -->
        <el-date-picker
          v-else-if="component.type === 'date'"
          v-model="formData[component.field]"
          type="date"
          :placeholder="component.placeholder || 'Please select date'"
          :disabled="component.disabled"
          :format="component.format || 'YYYY-MM-DD'"
          :value-format="component.valueFormat || 'YYYY-MM-DD'"
          style="width: 100%"
        />

        <!-- DateTime -->
        <el-date-picker
          v-else-if="component.type === 'datetime'"
          v-model="formData[component.field]"
          type="datetime"
          :placeholder="component.placeholder || 'Please select date and time'"
          :disabled="component.disabled"
          :format="component.format || 'YYYY-MM-DD HH:mm:ss'"
          :value-format="component.valueFormat || 'YYYY-MM-DD HH:mm:ss'"
          style="width: 100%"
        />

        <!-- Switch -->
        <el-switch
          v-else-if="component.type === 'switch'"
          v-model="formData[component.field]"
          :disabled="component.disabled"
          :active-text="component.activeText || ''"
          :inactive-text="component.inactiveText || ''"
        />

        <!-- Slider -->
        <el-slider
          v-else-if="component.type === 'slider'"
          v-model="formData[component.field]"
          :min="component.min || 0"
          :max="component.max || 100"
          :step="component.step || 1"
          :disabled="component.disabled"
          :show-stops="component.showStops"
          :range="component.range"
        />

        <!-- Default input fallback -->
        <el-input
          v-else
          v-model="formData[component.field]"
          :placeholder="component.placeholder || ''"
          :disabled="component.disabled"
        />
      </el-form-item>

      <!-- Form Actions -->
      <el-form-item class="form-actions">
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEditMode ? 'Update' : 'Submit' }}
        </el-button>
        <el-button @click="handleReset" :disabled="submitting">Reset</el-button>
        <el-button v-if="showBackButton" @click="handleBack">Back</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'FormRenderer',
  props: {
    // Form configuration JSON
    formConfig: {
      type: Object,
      required: true,
      default: () => ({
        formId: '',
        formName: '',
        datasource: '',
        table: '',
        components: []
      })
    },
    // Initial data (for pre-filling)
    initialData: {
      type: Object,
      default: () => ({})
    },
    // Label position
    labelPosition: {
      type: String,
      default: 'top'
    },
    // Label width
    labelWidth: {
      type: String,
      default: '120px'
    },
    // Show back button
    showBackButton: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      formData: {},
      formRules: {},
      submitting: false,
      isEditMode: false,
      recordId: null
    }
  },
  computed: {
    layoutClass() {
      return this.formConfig.layout ? `layout-${this.formConfig.layout}` : ''
    }
  },
  watch: {
    formConfig: {
      handler(newConfig) {
        this.initializeFormData()
      },
      immediate: true,
      deep: true
    },
    initialData: {
      handler(newData) {
        if (newData && Object.keys(newData).length > 0) {
          this.formData = { ...this.formData, ...newData }
        }
      },
      deep: true
    }
  },
  created() {
    this.initializeFormData()
    this.buildFormRules()
    this.loadData()
  },
  methods: {
    // Initialize form data structure based on components
    initializeFormData() {
      const data = {}
      const config = this.formConfig

      if (config.components && Array.isArray(config.components)) {
        config.components.forEach(component => {
          // Set default values based on component type
          if (component.type === 'checkbox') {
            data[component.field] = component.defaultValue || []
          } else if (component.type === 'switch') {
            data[component.field] = component.defaultValue || false
          } else if (component.type === 'number' || component.type === 'slider') {
            data[component.field] = component.defaultValue ?? null
          } else {
            data[component.field] = component.defaultValue || null
          }
        })
      }

      this.formData = data
    },

    // Build validation rules based on component configuration
    buildFormRules() {
      const rules = {}
      const config = this.formConfig

      if (config.components && Array.isArray(config.components)) {
        config.components.forEach(component => {
          const fieldRules = []

          // Required validation
          if (component.required) {
            const rule = { required: true, message: `Please enter ${component.label}`, trigger: this.getTriggerType(component.type) }
            fieldRules.push(rule)
          }

          // Custom validation rules
          if (component.validation) {
            if (component.validation.type === 'email') {
              fieldRules.push({
                type: 'email',
                message: 'Please enter a valid email',
                trigger: this.getTriggerType(component.type)
              })
            }
            if (component.validation.type === 'phone') {
              fieldRules.push({
                pattern: /^1[3-9]\d{9}$/,
                message: 'Please enter a valid phone number',
                trigger: this.getTriggerType(component.type)
              })
            }
            if (component.validation.pattern) {
              fieldRules.push({
                pattern: new RegExp(component.validation.pattern),
                message: component.validation.message || 'Invalid format',
                trigger: this.getTriggerType(component.type)
              })
            }
            if (component.validation.minLength) {
              fieldRules.push({
                min: component.validation.minLength,
                message: `Minimum length is ${component.validation.minLength}`,
                trigger: this.getTriggerType(component.type)
              })
            }
            if (component.validation.maxLength) {
              fieldRules.push({
                max: component.validation.maxLength,
                message: `Maximum length is ${component.validation.maxLength}`,
                trigger: this.getTriggerType(component.type)
              })
            }
          }

          if (fieldRules.length > 0) {
            rules[component.field] = fieldRules
          }
        })
      }

      this.formRules = rules
    },

    // Get trigger type for validation based on component type
    getTriggerType(type) {
      const triggerMap = {
        input: 'blur',
        textarea: 'blur',
        number: 'blur',
        select: 'change',
        radio: 'change',
        checkbox: 'change',
        date: 'change',
        datetime: 'change',
        switch: 'change',
        slider: 'change'
      }
      return triggerMap[type] || 'blur'
    },

    // Parse options string/array to options array
    parseOptions(options) {
      if (!options) return []
      if (Array.isArray(options)) return options
      if (typeof options === 'string') {
        // Parse options from string format: "value1:label1\nvalue2:label2" or "label1\nlabel2"
        return options.split('\n').filter(line => line.trim()).map(line => {
          const parts = line.split(':')
          if (parts.length >= 2) {
            return { value: parts[0].trim(), label: parts[1].trim() }
          }
          return { value: line.trim(), label: line.trim() }
        })
      }
      return []
    },

    // Get CSS class for component
    getComponentClass(component) {
      const classes = []
      if (component.width) {
        classes.push(`component-width-${component.width}`)
      }
      return classes.join(' ')
    },

    // Load existing data for edit mode
    loadData() {
      const { datasource, table } = this.formConfig
      const { id } = this.$route.params

      if (id && datasource && table) {
        this.isEditMode = true
        this.recordId = id

        request({
          url: `/api/${datasource}/${table}/${id}`,
          method: 'get'
        }).then(res => {
          if (res.code === 200 && res.data) {
            this.formData = { ...this.formData, ...res.data }
            this.$emit('data-loaded', res.data)
          }
        }).catch(err => {
          console.error('Failed to load form data:', err)
          this.$message.error('Failed to load form data')
        })
      }
    },

    // Submit form data
    handleSubmit() {
      this.$refs.formRef.validate(valid => {
        if (!valid) {
          this.$message.error('Please check the form for errors')
          return false
        }

        this.submitting = true

        const { datasource, table } = this.formConfig
        const method = this.isEditMode ? 'put' : 'post'
        const url = this.isEditMode
          ? `/api/${datasource}/${table}/${this.recordId}`
          : `/api/${datasource}/${table}`

        request({
          url,
          method,
          data: this.formData
        }).then(res => {
          if (res.code === 200) {
            this.$message.success(this.isEditMode ? 'Updated successfully' : 'Submitted successfully')
            this.$emit('submit-success', res.data)
          } else {
            this.$message.error(res.msg || 'Operation failed')
            this.$emit('submit-error', res)
          }
        }).catch(err => {
          console.error('Form submit error:', err)
          this.$message.error('Operation failed')
          this.$emit('submit-error', err)
        }).finally(() => {
          this.submitting = false
        })
      })
    },

    // Reset form
    handleReset() {
      this.$refs.formRef.resetFields()
      this.initializeFormData()
      this.$emit('reset')
    },

    // Navigate back
    handleBack() {
      this.$router.back()
    },

    // Public method to set form data programmatically
    setFormData(data) {
      this.formData = { ...this.formData, ...data }
    },

    // Public method to get form data
    getFormData() {
      return this.formData
    },

    // Public method to validate specific field
    validateField(field) {
      return this.$refs.formRef.validateField(field)
    },

    // Public method to clear validation
    clearValidate() {
      this.$refs.formRef.clearValidate()
    }
  }
}
</script>

<style scoped>
.form-renderer-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.form-renderer-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.form-renderer-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.form-renderer {
  width: 100%;
}

.form-renderer.layout-horizontal {
  display: flex;
  flex-wrap: wrap;
}

.form-renderer.layout-horizontal >>> .el-form-item {
  flex: 0 0 100%;
}

.form-renderer.layout-inline >>> .el-form-item {
  display: inline-block;
  margin-right: 20px;
  vertical-align: top;
}

.component-width-50 {
  width: calc(50% - 10px);
  display: inline-block;
}

.component-width-33 {
  width: calc(33.33% - 14px);
  display: inline-block;
}

.component-width-25 {
  width: calc(25% - 18px);
  display: inline-block;
}

.form-actions {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  text-align: center;
}

.form-actions >>> .el-form-item__content {
  display: flex;
  justify-content: center;
  gap: 12px;
}

@media (max-width: 768px) {
  .form-renderer-container {
    padding: 16px;
  }

  .component-width-50,
  .component-width-33,
  .component-width-25 {
    width: 100%;
  }
}
</style>

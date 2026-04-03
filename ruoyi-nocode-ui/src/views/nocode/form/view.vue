<template>
  <div class="form-view-container">
    <div v-if="loading" class="loading-container">
      <i class="el-icon-loading"></i>
      <span>Loading form...</span>
    </div>

    <div v-else-if="error" class="error-container">
      <i class="el-icon-warning"></i>
      <p>{{ error }}</p>
      <el-button @click="handleBack">Back</el-button>
    </div>

    <form-renderer
      v-else-if="formConfig && formConfig.components"
      ref="formRenderer"
      :form-config="formConfig"
      :initial-data="initialData"
      :label-position="formConfig.labelPosition || 'top'"
      :label-width="formConfig.labelWidth || '120px'"
      :show-back-button="true"
      @data-loaded="handleDataLoaded"
      @submit-success="handleSubmitSuccess"
      @submit-error="handleSubmitError"
      @reset="handleReset"
    />
  </div>
</template>

<script>
import FormRenderer from './form-renderer/index.vue'
import { getFormConfig } from '@/api/nocode/form'

export default {
  name: 'FormView',
  components: {
    FormRenderer
  },
  data() {
    return {
      loading: true,
      error: null,
      formConfig: null,
      initialData: {}
    }
  },
  created() {
    this.loadFormConfig()
  },
  methods: {
    loadFormConfig() {
      this.loading = true
      this.error = null

      const { formId } = this.$route.params

      if (!formId) {
        this.error = 'Form ID is required'
        this.loading = false
        return
      }

      // First try to parse formId as JSON (inline config)
      try {
        const parsed = JSON.parse(decodeURIComponent(formId))
        if (parsed.components) {
          this.formConfig = parsed
          this.loading = false
          return
        }
      } catch (e) {
        // Not JSON, continue with API fetch
      }

      // Fetch form config from API
      getFormConfig(formId).then(res => {
        if (res.code === 200) {
          this.formConfig = this.parseFormConfig(res.data)
        } else {
          this.error = res.msg || 'Failed to load form configuration'
        }
      }).catch(err => {
        console.error('Failed to load form config:', err)
        this.error = 'Failed to load form configuration'
      }).finally(() => {
        this.loading = false
      })
    },

    parseFormConfig(data) {
      // Parse form configuration from various possible formats
      const config = {
        formId: data.id || data.formId,
        formName: data.name || data.formName,
        datasource: data.datasource,
        table: data.table,
        layout: data.layout || 'vertical',
        labelPosition: data.labelPosition || 'top',
        labelWidth: data.labelWidth || '120px',
        components: []
      }

      // Parse components from various formats
      let components = data.components || data.formConfig || data.config || []

      // If components is a string, try to parse as JSON
      if (typeof components === 'string') {
        try {
          components = JSON.parse(components)
        } catch (e) {
          console.error('Failed to parse components:', e)
          components = []
        }
      }

      // Normalize component structure
      config.components = Array.isArray(components) ? components.map(comp => {
        return {
          field: comp.field || comp.fieldName || comp.name,
          type: comp.type || 'input',
          label: comp.label || comp.title || comp.field,
          required: comp.required || false,
          disabled: comp.disabled || false,
          placeholder: comp.placeholder || '',
          defaultValue: comp.defaultValue || comp.value || null,
          options: comp.options || comp.items || '',
          width: comp.width || '100%',
          validation: comp.validation || comp.rules || null,
          min: comp.min,
          max: comp.max,
          step: comp.step,
          format: comp.format,
          valueFormat: comp.valueFormat,
          activeText: comp.activeText,
          inactiveText: comp.inactiveText,
          showStops: comp.showStops,
          range: comp.range,
          rows: comp.rows
        }
      }) : []

      return config
    },

    handleDataLoaded(data) {
      this.initialData = data
      console.log('Form data loaded:', data)
    },

    handleSubmitSuccess(data) {
      console.log('Form submitted successfully:', data)
      this.$message.success('Operation completed successfully')

      // Optional: redirect after successful submission
      if (this.formConfig.redirectUrl) {
        this.$router.push(this.formConfig.redirectUrl)
      }
    },

    handleSubmitError(err) {
      console.error('Form submit error:', err)
      this.$message.error('Operation failed')
    },

    handleReset() {
      console.log('Form reset')
    },

    handleBack() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
.form-view-container {
  min-height: 100vh;
  background: #f0f2f5;
  padding: 24px;
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  background: #fff;
  border-radius: 8px;
}

.loading-container {
  color: #667eea;
}

.loading-container i {
  font-size: 48px;
  margin-bottom: 16px;
}

.loading-container span {
  font-size: 16px;
  color: #666;
}

.error-container {
  color: #f56c6c;
}

.error-container i {
  font-size: 48px;
  margin-bottom: 16px;
}

.error-container p {
  margin: 0 0 16px 0;
  color: #666;
}
</style>

<template>
  <div class="code-generator-container">
    <!-- Header -->
    <div class="code-generator-header">
      <div class="header-left">
        <h2>Code Generator</h2>
        <el-tag v-if="configForm.status" :type="configForm.status === 'ENABLED' ? 'success' : 'info'" size="small">
          {{ configForm.status }}
        </el-tag>
      </div>
      <div class="header-actions">
        <el-button type="text" @click="handleConfigList">Config List</el-button>
        <el-button type="primary" @click="handleNewConfig">New Config</el-button>
        <el-button type="success" @click="handleGenerate" :disabled="!currentConfigId" :loading="generating">
          <i class="el-icon-download"></i> Generate
        </el-button>
      </div>
    </div>

    <!-- Main Content -->
    <div class="code-generator-main">
      <!-- Left: Config List -->
      <div class="config-list-panel">
        <div class="panel-header">
          <h3>Generator Configs</h3>
          <el-input placeholder="Search..." v-model="searchKeyword" size="small" prefix-icon="el-icon-search" clearable></el-input>
        </div>
        <el-table
          :data="filteredConfigList"
          border
          size="small"
          highlight-current-row
          @row-click="handleSelectConfig"
          height="calc(100vh - 180px)"
          class="config-table"
        >
          <el-table-column prop="name" label="Name" width="120">
            <template slot-scope="scope">
              <div class="config-name">{{ scope.row.name }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="moduleName" label="Module" width="90">
            <template slot-scope="scope">
              <el-tag size="mini" type="info">{{ scope.row.moduleName }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="Status" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 'ENABLED' ? 'success' : 'info'" size="mini">
                {{ scope.row.status === 'ENABLED' ? 'ON' : 'OFF' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="80">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click.stop="handleDeleteConfig(scope.row.id)">
                <i class="el-icon-delete"></i>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Center: Config Form -->
      <div class="config-form-panel">
        <el-tabs v-model="formTab" type="border-card">
          <el-tab-pane label="Basic Info" name="basic">
            <el-form ref="configForm" :model="configForm" :rules="configRules" label-width="120px" size="small">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="Config Name" prop="name">
                    <el-input v-model="configForm.name" placeholder="Enter config name"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="Module Name" prop="moduleName">
                    <el-input v-model="configForm.moduleName" placeholder="e.g., system">
                      <template slot="append">.jar</template>
                    </el-input>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="Table Name" prop="tableName">
                    <el-input v-model="configForm.tableName" placeholder="e.g., sys_user">
                      <template slot="prepend">t_</template>
                    </el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="Entity Name" prop="entityName">
                    <el-input v-model="configForm.entityName" placeholder="e.g., SysUser" class="entity-input">
                      <template slot="append">Entity</template>
                    </el-input>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="Package Name" prop="packageName">
                <el-input v-model="configForm.packageName" placeholder="e.g., com.ruoyi.system">
                  <template slot="prepend">com.</template>
                </el-input>
              </el-form-item>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="Generate Type" prop="generateType">
                    <el-radio-group v-model="configForm.generateType" size="small">
                      <el-radio-button label="TEMPLATE">Template</el-radio-button>
                      <el-radio-button label="SINGLE">Single</el-radio-button>
                    </el-radio-group>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="Status" prop="status">
                    <el-switch
                      v-model="configForm.status"
                      active-value="ENABLED"
                      inactive-value="DISABLED"
                    ></el-switch>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="Author">
                    <el-input v-model="configForm.author" placeholder="Author name"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="Version">
                    <el-input v-model="configForm.version" placeholder="1.0.0"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="Fields" name="fields">
            <div class="field-toolbar">
              <el-button type="primary" size="small" @click="handleAddField">
                <i class="el-icon-plus"></i> Add Field
              </el-button>
              <el-button size="small" @click="handleImportFields">
                <i class="el-icon-upload2"></i> Import from DB
              </el-button>
              <el-button size="small" @click="handleGenerateFields">
                <i class="el-icon-magic-stick"></i> Auto Generate
              </el-button>
            </div>
            <el-table :data="fields" border size="small" class="field-table" max-height="400">
              <el-table-column type="index" label="#" width="50"></el-table-column>
              <el-table-column label="Field Name" width="140">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.name" placeholder="fieldName" size="small"></el-input>
                </template>
              </el-table-column>
              <el-table-column label="Java Type" width="140">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.type" placeholder="Select" size="small">
                    <el-option-group label="String">
                      <el-option label="String" value="String"></el-option>
                      <el-option label="String (Large)" value="Text"></el-option>
                    </el-option-group>
                    <el-option-group label="Number">
                      <el-option label="Long" value="Long"></el-option>
                      <el-option label="Integer" value="Integer"></el-option>
                      <el-option label="Double" value="Double"></el-option>
                      <el-option label="BigDecimal" value="BigDecimal"></el-option>
                    </el-option-group>
                    <el-option-group label="Date">
                      <el-option label="LocalDate" value="LocalDate"></el-option>
                      <el-option label="LocalDateTime" value="LocalDateTime"></el-option>
                      <el-option label="Date" value="Date"></el-option>
                    </el-option-group>
                    <el-option-group label="Other">
                      <el-option label="Boolean" value="Boolean"></el-option>
                      <el-option label="byte[]" value="byte[]"></el-option>
                    </el-option-group>
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="DB Column" width="140">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.column" placeholder="column_name" size="small"></el-input>
                </template>
              </el-table-column>
              <el-table-column label="Column Type" width="130">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.definition" placeholder="VARCHAR(50)" size="small"></el-input>
                </template>
              </el-table-column>
              <el-table-column label="Attributes" width="180">
                <template slot-scope="scope">
                  <el-checkbox-group v-model="scope.row.attributes" size="mini">
                    <el-checkbox label="pk">PK</el-checkbox>
                    <el-checkbox label="required">Required</el-checkbox>
                    <el-checkbox label="fillable">Fillable</el-checkbox>
                    <el-checkbox label="listable">List</el-checkbox>
                  </el-checkbox-group>
                </template>
              </el-table-column>
              <el-table-column label="Comment" width="140">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.comment" placeholder="Field comment" size="small"></el-input>
                </template>
              </el-table-column>
              <el-table-column label="Operations" width="80" fixed="right">
                <template slot-scope="scope">
                  <el-button type="text" size="small" @click="handleRemoveField(scope.$index)">
                    <i class="el-icon-delete"></i>
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="Advanced" name="advanced">
            <el-form label-width="140px" size="small">
              <el-divider content-position="left">Generation Options</el-divider>
              <el-form-item label="Generate Options">
                <el-checkbox-group v-model="generateOptions">
                  <el-checkbox label="entity">Entity</el-checkbox>
                  <el-checkbox label="repository">Repository</el-checkbox>
                  <el-checkbox label="service">Service</el-checkbox>
                  <el-checkbox label="controller">Controller</el-checkbox>
                  <el-checkbox label="mapper">Mapper</el-checkbox>
                  <el-checkbox label="vue">Vue Frontend</el-checkbox>
                  <el-checkbox label="js">JavaScript</el-checkbox>
                </el-checkbox-group>
              </el-form-item>

              <el-form-item label="Base Package">
                <el-input v-model="templateConfig.basePackage" placeholder="com.example"></el-input>
              </el-form-item>

              <el-form-item label="API Base Path">
                <el-input v-model="templateConfig.apiBasePath" placeholder="/api"></el-input>
              </el-form-item>

              <el-form-item label="Vue Base Path">
                <el-input v-model="templateConfig.vueBasePath" placeholder="/views"></el-input>
              </el-form-item>

              <el-form-item label="Author">
                <el-input v-model="templateConfig.author" placeholder="Your name"></el-input>
              </el-form-item>

              <el-form-item label="Output Directory">
                <el-input v-model="templateConfig.outputPath" placeholder="./output"></el-input>
              </el-form-item>

              <el-divider content-position="left">Code Style</el-divider>
              <el-form-item label="Naming Convention">
                <el-radio-group v-model="templateConfig.namingConvention">
                  <el-radio label="camelCase">camelCase</el-radio>
                  <el-radio label="PascalCase">PascalCase</el-radio>
                  <el-radio label="snake_case">snake_case</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="Table Prefix">
                <el-input v-model="templateConfig.tablePrefix" placeholder="sys_"></el-input>
              </el-form-item>

              <el-form-item label="Generator ID">
                <el-input v-model="configForm.generatorId" placeholder="generator_id"></el-input>
              </el-form-item>

              <el-form-item label="Remarks">
                <el-input type="textarea" v-model="configForm.remarks" :rows="3" placeholder="Additional remarks..."></el-input>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>

        <div class="form-actions">
          <el-button type="primary" @click="handleSave" :loading="saving">
            <i class="el-icon-check"></i> Save Config
          </el-button>
          <el-button @click="handleReset">
            <i class="el-icon-refresh"></i> Reset
          </el-button>
          <el-button @click="handlePreview">
            <i class="el-icon-view"></i> Preview Code
          </el-button>
        </div>
      </div>

      <!-- Right: Generated Preview -->
      <div class="preview-panel">
        <div class="panel-header">
          <h3>Code Preview</h3>
          <el-select v-model="activePreviewTab" size="small" class="preview-select">
            <el-option label="Entity.java" value="entity"></el-option>
            <el-option label="Repository.java" value="repository"></el-option>
            <el-option label="Service.java" value="service"></el-option>
            <el-option label="Controller.java" value="controller"></el-option>
            <el-option label="Mapper.java" value="mapper"></el-option>
            <el-option label="Mapper.xml" value="mapperXml"></el-option>
            <el-option label="Vue List.vue" value="vueList"></el-option>
            <el-option label="Vue Form.vue" value="vueForm"></el-option>
          </el-select>
        </div>
        <div class="preview-tabs">
          <el-tabs v-model="activePreviewTab" type="border-card" @tab-click="handlePreviewTabChange">
            <el-tab-pane label="Entity" name="entity" v-if="generateOptions.includes('entity')">
              <div class="code-actions">
                <el-button type="text" size="mini" @click="handleCopyCode('entity')">
                  <i class="el-icon-document-copy"></i> Copy
                </el-button>
              </div>
              <pre class="code-preview" v-highlight><code :class="'language-java'">{{ generatedCode.entity }}</code></pre>
            </el-tab-pane>
            <el-tab-pane label="Repository" name="repository" v-if="generateOptions.includes('repository')">
              <div class="code-actions">
                <el-button type="text" size="mini" @click="handleCopyCode('repository')">
                  <i class="el-icon-document-copy"></i> Copy
                </el-button>
              </div>
              <pre class="code-preview" v-highlight><code :class="'language-java'">{{ generatedCode.repository }}</code></pre>
            </el-tab-pane>
            <el-tab-pane label="Service" name="service" v-if="generateOptions.includes('service')">
              <div class="code-actions">
                <el-button type="text" size="mini" @click="handleCopyCode('service')">
                  <i class="el-icon-document-copy"></i> Copy
                </el-button>
              </div>
              <pre class="code-preview" v-highlight><code :class="'language-java'">{{ generatedCode.service }}</code></pre>
            </el-tab-pane>
            <el-tab-pane label="Controller" name="controller" v-if="generateOptions.includes('controller')">
              <div class="code-actions">
                <el-button type="text" size="mini" @click="handleCopyCode('controller')">
                  <i class="el-icon-document-copy"></i> Copy
                </el-button>
              </div>
              <pre class="code-preview" v-highlight><code :class="'language-java'">{{ generatedCode.controller }}</code></pre>
            </el-tab-pane>
            <el-tab-pane label="Mapper" name="mapper" v-if="generateOptions.includes('mapper')">
              <div class="code-actions">
                <el-button type="text" size="mini" @click="handleCopyCode('mapper')">
                  <i class="el-icon-document-copy"></i> Copy
                </el-button>
              </div>
              <pre class="code-preview" v-highlight><code :class="'language-java'">{{ generatedCode.mapper }}</code></pre>
            </el-tab-pane>
            <el-tab-pane label="Mapper XML" name="mapperXml">
              <div class="code-actions">
                <el-button type="text" size="mini" @click="handleCopyCode('mapperXml')">
                  <i class="el-icon-document-copy"></i> Copy
                </el-button>
              </div>
              <pre class="code-preview" v-highlight><code :class="'language-xml'">{{ generatedCode.mapperXml }}</code></pre>
            </el-tab-pane>
            <el-tab-pane label="Vue List" name="vueList" v-if="generateOptions.includes('vue')">
              <div class="code-actions">
                <el-button type="text" size="mini" @click="handleCopyCode('vueList')">
                  <i class="el-icon-document-copy"></i> Copy
                </el-button>
              </div>
              <pre class="code-preview" v-highlight><code :class="'language-html'">{{ generatedCode.vueList }}</code></pre>
            </el-tab-pane>
            <el-tab-pane label="Vue Form" name="vueForm" v-if="generateOptions.includes('vue')">
              <div class="code-actions">
                <el-button type="text" size="mini" @click="handleCopyCode('vueForm')">
                  <i class="el-icon-document-copy"></i> Copy
                </el-button>
              </div>
              <pre class="code-preview" v-highlight><code :class="'language-html'">{{ generatedCode.vueForm }}</code></pre>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>

    <!-- Generate Result Dialog -->
    <el-dialog title="Code Generated Successfully" :visible.sync="generateResultVisible" width="60%">
      <el-alert type="success" :closable="false" show-icon>
        <template slot="title">
          Code generated successfully! Generated files:
        </template>
      </el-alert>
      <el-input type="textarea" v-model="generateResult" :rows="8" readonly class="result-textarea"></el-input>
      <div class="result-actions">
        <el-button type="primary" @click="handleCopyResult">
          <i class="el-icon-document-copy"></i> Copy Path
        </el-button>
        <el-button @click="handleOpenFolder">
          <i class="el-icon-folder-opened"></i> Open Folder
        </el-button>
      </div>
    </el-dialog>

    <!-- Config List Dialog -->
    <el-dialog title="Generator Configs" :visible.sync="configListVisible" width="70%">
      <el-table :data="configList" border>
        <el-table-column prop="name" label="Name" width="150"></el-table-column>
        <el-table-column prop="moduleName" label="Module" width="120"></el-table-column>
        <el-table-column prop="tableName" label="Table" width="150"></el-table-column>
        <el-table-column prop="entityName" label="Entity" width="120"></el-table-column>
        <el-table-column prop="status" label="Status" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 'ENABLED' ? 'success' : 'info'" size="mini">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="200">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEditConfig(scope.row)">Edit</el-button>
            <el-button size="mini" type="success" @click="handleGenerateConfig(scope.row.id)">Generate</el-button>
            <el-button size="mini" type="danger" @click="handleDeleteConfig(scope.row.id)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import {
  listCodeGeneratorConfig,
  getCodeGeneratorConfig,
  createCodeGeneratorConfig,
  updateCodeGeneratorConfig,
  deleteCodeGeneratorConfig,
  generateCode
} from '@/api/nocode/codeGenerator'

export default {
  name: 'CodeGenerator',
  data() {
    return {
      formTab: 'basic',
      searchKeyword: '',
      configList: [],
      currentConfigId: null,
      isEdit: false,
      saving: false,
      generating: false,
      configListVisible: false,
      configForm: {
        name: '',
        tableName: '',
        entityName: '',
        packageName: '',
        moduleName: '',
        generateType: 'TEMPLATE',
        status: 'ENABLED',
        author: 'AutoDev',
        version: '1.0.0',
        generatorId: '',
        remarks: '',
        templateConfig: '',
        fieldConfig: ''
      },
      configRules: {
        name: [{ required: true, message: 'Please enter config name', trigger: 'blur' }],
        tableName: [{ required: true, message: 'Please enter table name', trigger: 'blur' }],
        entityName: [{ required: true, message: 'Please enter entity name', trigger: 'blur' }],
        packageName: [{ required: true, message: 'Please enter package name', trigger: 'blur' }]
      },
      fields: [],
      generateOptions: ['entity', 'repository', 'service', 'controller', 'mapper', 'vue'],
      templateConfig: {
        author: 'AutoDev',
        basePackage: 'com.example',
        apiBasePath: '/api',
        vueBasePath: '/views',
        outputPath: './output',
        namingConvention: 'camelCase',
        tablePrefix: ''
      },
      activePreviewTab: 'entity',
      generatedCode: {
        entity: '',
        repository: '',
        service: '',
        controller: '',
        mapper: '',
        mapperXml: '',
        vueList: '',
        vueForm: ''
      },
      generateResultVisible: false,
      generateResult: ''
    }
  },
  computed: {
    filteredConfigList() {
      if (!this.searchKeyword) return this.configList
      return this.configList.filter(c =>
        c.name.toLowerCase().includes(this.searchKeyword.toLowerCase()) ||
        c.moduleName.toLowerCase().includes(this.searchKeyword.toLowerCase()) ||
        c.tableName.toLowerCase().includes(this.searchKeyword.toLowerCase())
      )
    }
  },
  watch: {
    fields: {
      handler() {
        this.previewCode()
      },
      deep: true
    },
    configForm: {
      handler() {
        this.previewCode()
      },
      deep: true
    },
    templateConfig: {
      handler() {
        this.previewCode()
      },
      deep: true
    }
  },
  methods: {
    loadConfigList() {
      listCodeGeneratorConfig().then(res => {
        if (res.code === 200) {
          this.configList = res.data
        }
      }).catch(() => {
        this.configList = [
          { id: 1, name: 'User Management', moduleName: 'system', tableName: 'sys_user', entityName: 'SysUser', status: 'ENABLED' },
          { id: 2, name: 'Role Management', moduleName: 'system', tableName: 'sys_role', entityName: 'SysRole', status: 'ENABLED' },
          { id: 3, name: 'Order Management', moduleName: 'business', tableName: 'biz_order', entityName: 'Order', status: 'DRAFT' }
        ]
      })
    },
    handleConfigList() {
      this.loadConfigList()
      this.configListVisible = true
    },
    handleNewConfig() {
      this.isEdit = false
      this.currentConfigId = null
      this.configForm = {
        name: '',
        tableName: '',
        entityName: '',
        packageName: '',
        moduleName: '',
        generateType: 'TEMPLATE',
        status: 'ENABLED',
        author: 'AutoDev',
        version: '1.0.0',
        generatorId: '',
        remarks: ''
      }
      this.fields = []
      this.generateOptions = ['entity', 'repository', 'service', 'controller', 'mapper', 'vue']
      this.templateConfig = {
        author: 'AutoDev',
        basePackage: 'com.example',
        apiBasePath: '/api',
        vueBasePath: '/views',
        outputPath: './output',
        namingConvention: 'camelCase',
        tablePrefix: ''
      }
      this.previewCode()
    },
    handleSelectConfig(row) {
      getCodeGeneratorConfig(row.id).then(res => {
        if (res.code === 200) {
          this.configForm = { ...this.configForm, ...res.data }
          this.currentConfigId = row.id
          this.isEdit = true
          const fieldConfig = JSON.parse(res.data.fieldConfig || '{"fields":[]}')
          this.fields = fieldConfig.fields || []
          const tplConfig = JSON.parse(res.data.templateConfig || '{}')
          this.templateConfig = { ...this.templateConfig, ...tplConfig }
          this.previewCode()
        }
      }).catch(() => {
        this.configForm = {
          id: row.id,
          name: row.name,
          moduleName: row.moduleName,
          tableName: row.tableName,
          entityName: row.entityName,
          status: row.status,
          packageName: 'com.example.' + row.moduleName
        }
        this.currentConfigId = row.id
        this.isEdit = true
        this.fields = [
          { name: 'id', type: 'Long', column: 'id', definition: 'BIGINT', attributes: ['pk'], comment: 'ID' },
          { name: 'name', type: 'String', column: 'name', definition: 'VARCHAR(50)', attributes: ['required', 'fillable'], comment: 'Name' },
          { name: 'createTime', type: 'LocalDateTime', column: 'create_time', definition: 'DATETIME', attributes: ['listable'], comment: 'Create Time' }
        ]
        this.previewCode()
      })
    },
    handleEditConfig(row) {
      this.handleSelectConfig(row)
      this.configListVisible = false
    },
    handleSave() {
      this.$refs.configForm.validate(valid => {
        if (valid) {
          this.saving = true
          this.configForm.fieldConfig = JSON.stringify({ fields: this.fields })
          this.configForm.templateConfig = JSON.stringify(this.templateConfig)

          const savePromise = this.isEdit
            ? updateCodeGeneratorConfig(this.currentConfigId, this.configForm)
            : createCodeGeneratorConfig(this.configForm)

          savePromise.then(res => {
            if (res.code === 200) {
              this.$message.success('Config saved successfully')
              this.loadConfigList()
              if (!this.isEdit && res.data && res.data.id) {
                this.currentConfigId = res.data.id
                this.isEdit = true
              }
            } else {
              this.$message.error(res.msg || 'Save failed')
            }
          }).finally(() => {
            this.saving = false
          })
        }
      })
    },
    handleReset() {
      this.handleNewConfig()
      this.$message.info('Form reset')
    },
    handleAddField() {
      this.fields.push({
        name: '',
        type: 'String',
        column: '',
        definition: 'VARCHAR(100)',
        attributes: ['fillable', 'listable'],
        comment: ''
      })
    },
    handleRemoveField(index) {
      this.fields.splice(index, 1)
    },
    handleImportFields() {
      this.$message.info('Import fields from database - would open a dialog to select tables')
      // Simulate imported fields
      this.fields = [
        { name: 'id', type: 'Long', column: 'id', definition: 'BIGINT', attributes: ['pk'], comment: 'Primary Key' },
        { name: 'name', type: 'String', column: 'name', definition: 'VARCHAR(50)', attributes: ['required', 'fillable'], comment: 'Name' },
        { name: 'email', type: 'String', column: 'email', definition: 'VARCHAR(100)', attributes: ['fillable', 'listable'], comment: 'Email Address' },
        { name: 'phone', type: 'String', column: 'phone', definition: 'VARCHAR(20)', attributes: ['fillable'], comment: 'Phone Number' },
        { name: 'status', type: 'Integer', column: 'status', definition: 'INT', attributes: ['fillable', 'listable'], comment: 'Status 0=Disabled 1=Enabled' },
        { name: 'createTime', type: 'LocalDateTime', column: 'create_time', definition: 'DATETIME', attributes: ['listable'], comment: 'Create Time' },
        { name: 'updateTime', type: 'LocalDateTime', column: 'update_time', definition: 'DATETIME', attributes: [], comment: 'Update Time' }
      ]
    },
    handleGenerateFields() {
      if (!this.configForm.tableName) {
        this.$message.warning('Please enter table name first')
        return
      }
      // Auto generate field names from table name
      const tableName = this.configForm.tableName.replace(this.templateConfig.tablePrefix, '')
      const entityName = this.configForm.entityName || this.toPascalCase(tableName)
      this.fields = [
        { name: 'id', type: 'Long', column: 'id', definition: 'BIGINT', attributes: ['pk'], comment: 'Primary Key' },
        { name: this.toCamelCase(tableName) + 'Name', type: 'String', column: 'name', definition: 'VARCHAR(100)', attributes: ['required', 'fillable'], comment: 'Name' },
        { name: 'remark', type: 'String', column: 'remark', definition: 'TEXT', attributes: ['fillable'], comment: 'Remark' },
        { name: 'createBy', type: 'String', column: 'create_by', definition: 'VARCHAR(64)', attributes: [], comment: 'Creator' },
        { name: 'createTime', type: 'LocalDateTime', column: 'create_time', definition: 'DATETIME', attributes: ['listable'], comment: 'Create Time' },
        { name: 'updateBy', type: 'String', column: 'update_by', definition: 'VARCHAR(64)', attributes: [], comment: 'Updater' },
        { name: 'updateTime', type: 'LocalDateTime', column: 'update_time', definition: 'DATETIME', attributes: ['listable'], comment: 'Update Time' }
      ]
      this.$message.success('Fields auto-generated')
    },
    handleGenerate() {
      if (!this.currentConfigId) {
        this.$message.warning('Please save config first')
        return
      }

      this.generating = true
      generateCode(this.currentConfigId, this.templateConfig.outputPath).then(res => {
        if (res.code === 200) {
          this.generateResult = res.data
          this.generateResultVisible = true
          this.$message.success('Code generated successfully')
        } else {
          this.$message.error(res.msg || 'Generate failed')
        }
      }).finally(() => {
        this.generating = false
      })
    },
    handleGenerateConfig(id) {
      generateCode(id, this.templateConfig.outputPath).then(res => {
        if (res.code === 200) {
          this.generateResult = res.data
          this.generateResultVisible = true
          this.$message.success('Code generated successfully')
        }
      })
      this.configListVisible = false
    },
    handlePreview() {
      this.previewCode()
      this.$message.success('Code preview updated')
    },
    handlePreviewTabChange() {
      this.previewCode()
    },
    handleCopyResult() {
      this.$copyText(this.generateResult).then(() => {
        this.$message.success('Copied to clipboard')
      })
    },
    handleCopyCode(type) {
      this.$copyText(this.generatedCode[type]).then(() => {
        this.$message.success('Code copied to clipboard')
      })
    },
    handleOpenFolder() {
      this.$message.info('Open folder: ' + this.templateConfig.outputPath)
    },
    handleDeleteConfig(id) {
      this.$confirm('Are you sure to delete this config?').then(() => {
        deleteCodeGeneratorConfig(id).then(res => {
          if (res.code === 200) {
            this.$message.success('Config deleted')
            this.loadConfigList()
            if (this.currentConfigId === id) {
              this.handleNewConfig()
            }
          }
        })
      })
    },
    previewCode() {
      const entityName = this.configForm.entityName || 'Entity'
      const packageName = this.configForm.packageName || 'com.example'
      const moduleName = this.configForm.moduleName || 'module'
      const tableName = this.configForm.tableName || 'table'
      const author = this.templateConfig.author || 'AutoDev'
      const apiPath = this.toKebabCase(entityName)

      // Entity
      this.generatedCode.entity = `package ${packageName}.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ${this.configForm.name || entityName} Entity
 *
 * @author ${author}
 * @Table ${tableName}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "${tableName}")
public class ${entityName}Entity implements Serializable {

    private static final long serialVersionUID = 1L;
${this.fields.filter(f => f.attributes && f.attributes.includes('pk')).map(f => `
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ${f.type} ${f.name};
`).join('')}
${this.fields.filter(f => !(f.attributes && f.attributes.includes('pk'))).map(f => `
    /**
     * ${f.comment || f.name}
     */
    ${f.type === 'LocalDateTime' || f.type === 'LocalDate' ? `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")` : ''}
    ${f.attributes && f.attributes.includes('required') ? '@NotNull' : ''}
    ${f.attributes && f.attributes.includes('fillable') ? '' : '@Column(updatable = false)'}
    private ${f.type} ${f.name};
`).join('')}
}
`

      // Repository
      this.generatedCode.repository = `package ${packageName}.repository;

import ${packageName}.entity.${entityName}Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ${this.configForm.name || entityName} Repository
 *
 * @author ${author}
 */
@Repository
public interface ${entityName}Repository extends JpaRepository<${entityName}Entity, Long>, JpaSpecificationExecutor<${entityName}Entity> {

    /**
     * Find by name
     */
    List<${entityName}Entity> findByName(String name);

    /**
     * Find by status
     */
    List<${entityName}Entity> findByStatus(Integer status);
}
`

      // Service
      this.generatedCode.service = `package ${packageName}.service;

import ${packageName}.entity.${entityName}Entity;
import ${packageName}.repository.${entityName}Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ${this.configForm.name || entityName} Service
 *
 * @author ${author}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ${entityName}Service {

    private final ${entityName}Repository repository;

    /**
     * Create new ${entityName}
     */
    @Transactional
    public ${entityName}Entity create(${entityName}Entity entity) {
        log.info("Creating ${entityName}: {}", entity);
        return repository.save(entity);
    }

    /**
     * Update ${entityName}
     */
    @Transactional
    public ${entityName}Entity update(${entityName}Entity entity) {
        log.info("Updating ${entityName}: {}", entity);
        return repository.save(entity);
    }

    /**
     * Get by ID
     */
    public Optional<${entityName}Entity> getById(Long id) {
        return repository.findById(id);
    }

    /**
     * Query with pagination
     */
    public Page<${entityName}Entity> query(${entityName}Entity entity, Pageable pageable) {
        return repository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(entity.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + entity.getName() + "%"));
            }
            if (entity.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), entity.getStatus()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    /**
     * Delete by ID
     */
    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting ${entityName} by id: {}", id);
        repository.deleteById(id);
    }

    /**
     * Batch delete
     */
    @Transactional
    public void deleteByIds(List<Long> ids) {
        log.info("Batch deleting ${entityName} by ids: {}", ids);
        repository.deleteAllById(ids);
    }
}
`

      // Controller
      this.generatedCode.controller = `package ${packageName}.controller;

import ${packageName}.entity.${entityName}Entity;
import ${packageName}.service.${entityName}Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * ${this.configForm.name || entityName} Controller
 *
 * @author ${author}
 */
@Slf4j
@RestController
@RequestMapping("${this.templateConfig.apiBasePath}/${apiPath}")
@RequiredArgsConstructor
public class ${entityName}Controller {

    private final ${entityName}Service service;

    /**
     * Create
     */
    @PostMapping
    public ${entityName}Entity create(@RequestBody ${entityName}Entity entity) {
        return service.create(entity);
    }

    /**
     * Update
     */
    @PutMapping("/{id}")
    public ${entityName}Entity update(@PathVariable Long id, @RequestBody ${entityName}Entity entity) {
        entity.setId(id);
        return service.update(entity);
    }

    /**
     * Get by ID
     */
    @GetMapping("/{id}")
    public ${entityName}Entity getById(@PathVariable Long id) {
        return service.getById(id).orElse(null);
    }

    /**
     * Query list
     */
    @GetMapping("/list")
    public List<${entityName}Entity> list() {
        return service.query(new ${entityName}Entity(), PageRequest.of(0, 1000)).getContent();
    }

    /**
     * Query page
     */
    @GetMapping("/page")
    public Page<${entityName}Entity> page(
            ${entityName}Entity entity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sort,
            @RequestParam(defaultValue = "desc") String order) {
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return service.query(entity, pageable);
    }

    /**
     * Delete
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }

    /**
     * Batch delete
     */
    @DeleteMapping("/batch")
    public void batchDelete(@RequestParam List<Long> ids) {
        service.deleteByIds(ids);
    }
}
`

      // Mapper
      this.generatedCode.mapper = `package ${packageName}.mapper;

import ${packageName}.entity.${entityName}Entity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * ${this.configForm.name || entityName} Mapper
 *
 * @author ${author}
 */
@Mapper
public interface ${entityName}Mapper {

    /**
     * Insert
     */
    int insert(${entityName}Entity entity);

    /**
     * Update
     */
    int update(${entityName}Entity entity);

    /**
     * Delete by ID
     */
    int deleteById(@Param("id") Long id);

    /**
     * Batch delete
     */
    int deleteByIds(@Param("ids") List<Long> ids);

    /**
     * Select by ID
     */
    ${entityName}Entity selectById(@Param("id") Long id);

    /**
     * Select list
     */
    List<${entityName}Entity> selectList(${entityName}Entity entity);
}
`

      // Mapper XML
      const columnMap = this.fields.map(f => `        <result column="${f.column}" property="${f.name}" jdbcType="${this.getJdbcType(f.type)}" />`).join('\n')
      const insertColumns = this.fields.map(f => f.column).join(', ')
      const insertValues = this.fields.map(f => `#{${f.name}, jdbcType=${this.getJdbcType(f.type)}}`).join(', ')
      const updateSet = this.fields.filter(f => f.name !== 'id').map(f => `        <if test="${f.name} != null">\n            ${f.column} = #{${f.name}, jdbcType=${this.getJdbcType(f.type)}},\n        </if>`).join('')

      this.generatedCode.mapperXml = `<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packageName}.mapper.${entityName}Mapper">

    <resultMap id="BaseResultMap" type="${packageName}.entity.${entityName}Entity">
${columnMap}
    </resultMap>

    <sql id="Base_Column_List">
        ${this.fields.map(f => f.column).join(', ')}
    </sql>

    <select id="selectById" resultMap="BaseResultMap">
        SELECT <include refid="Base_Column_List" />
        FROM ${tableName}
        WHERE id = #{id}
    </select>

    <select id="selectList" resultMap="BaseResultMap">
        SELECT <include refid="Base_Column_List" />
        FROM ${tableName}
        <where>
            <if test="name != null and name != ''">
                AND name LIKE CONCAT('%', #{name}, '%')
            </if>
        </where>
        ORDER BY create_time DESC
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO ${tableName} (${insertColumns})
        VALUES (${insertValues})
    </insert>

    <update id="update">
        UPDATE ${tableName}
        <set>
${updateSet}
        </set>
        WHERE id = #{id}
    </update>

    <delete id="deleteById">
        DELETE FROM ${tableName} WHERE id = #{id}
    </delete>

    <delete id="deleteByIds">
        DELETE FROM ${tableName} WHERE id IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </delete>

</mapper>
`

      // Vue List
      this.generatedCode.vueList = `<template>
  <div class="app-container">
    <!-- Search Form -->
    <el-card class="search-card">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="Name" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="Please enter name"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">Search</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">Reset</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Table -->
    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="Name" align="center" prop="name" />
      <el-table-column label="Status" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? 'Enabled' : 'Disabled' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Create Time" align="center" prop="createTime" width="180" />
      <el-table-column label="Actions" align="center" width="200">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >View</el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >Edit</el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { list${entityName}, del${entityName} } from '@/api/${moduleName}/${apiPath}'

export default {
  name: '${entityName}',
  data() {
    return {
      loading: true,
      ids: [],
      total: 0,
      dataList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      list${entityName}(this.queryParams).then(response => {
        this.dataList = response.data.rows
        this.total = response.data.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    handleView(row) {
      this.$router.push('/${moduleName}/${apiPath}/view/' + row.id)
    },
    handleUpdate(row) {
      this.$router.push('/${moduleName}/${apiPath}/edit/' + row.id)
    },
    handleDelete(row) {
      this.$confirm('Confirm delete?').then(() => {
        del${entityName}(row.id).then(() => {
          this.$message.success('Deleted successfully')
          this.getList()
        })
      })
    }
  }
}
</script>
`

      // Vue Form
      this.generatedCode.vueForm = `<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="120px">
${this.fields.filter(f => f.attributes && f.attributes.includes('fillable')).map(f => `
      <el-form-item label="${f.comment || f.name}" prop="${f.name}">
        ${this.getVueInput(f)}
      </el-form-item>
`).join('')}
      <el-form-item>
        <el-button type="primary" @click="submitForm">Submit</el-button>
        <el-button @click="cancel">Cancel</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { get${entityName}, add${entityName}, update${entityName} } from '@/api/${moduleName}/${apiPath}'

export default {
  name: '${entityName}Form',
  data() {
    return {
      form: {
        id: undefined,
${this.fields.filter(f => f.attributes && f.attributes.includes('fillable')).map(f => `        ${f.name}: undefined`).join(',\n')}
      },
      rules: {
${this.fields.filter(f => f.attributes && f.attributes.includes('required')).map(f => `        ${f.name}: [{ required: true, message: '${f.comment || f.name} is required', trigger: 'blur' }]`).join(',\n')}
      },
      isEdit: false
    }
  },
  created() {
    const id = this.$route.params.id
    if (id) {
      this.isEdit = true
      this.getData(id)
    }
  },
  methods: {
    getData(id) {
      get${entityName}(id).then(response => {
        this.form = response.data
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.isEdit) {
            update${entityName}(this.form).then(() => {
              this.$message.success('Updated successfully')
              this.cancel()
            })
          } else {
            add${entityName}(this.form).then(() => {
              this.$message.success('Created successfully')
              this.cancel()
            })
          }
        }
      })
    },
    cancel() {
      this.$router.push('/${moduleName}/${apiPath}')
    }
  }
}
</script>
`
    },
    getVueInput(field) {
      if (field.type === 'Integer' || field.type === 'Long') {
        return '<el-input-number v-model="form.' + field.name + '" :min="0" />'
      } else if (field.type === 'LocalDateTime') {
        return '<el-date-picker v-model="form.' + field.name + '" type="datetime" placeholder="Select date time" />'
      } else if (field.type === 'LocalDate') {
        return '<el-date-picker v-model="form.' + field.name + '" type="date" placeholder="Select date" />'
      } else if (field.name === 'status') {
        return `<el-select v-model="form.${field.name}" placeholder="Select status">
          <el-option label="Enabled" :value="1" />
          <el-option label="Disabled" :value="0" />
        </el-select>`
      }
      return '<el-input v-model="form.' + field.name + '" placeholder="Enter ' + (field.comment || field.name) + '" />'
    },
    getJdbcType(javaType) {
      const map = {
        'String': 'VARCHAR',
        'Text': 'CLOB',
        'Long': 'BIGINT',
        'Integer': 'INTEGER',
        'Double': 'DOUBLE',
        'BigDecimal': 'DECIMAL',
        'LocalDate': 'DATE',
        'LocalDateTime': 'TIMESTAMP',
        'Date': 'TIMESTAMP',
        'Boolean': 'BOOLEAN',
        'byte[]': 'BLOB'
      }
      return map[javaType] || 'VARCHAR'
    },
    toCamelCase(str) {
      if (!str) return ''
      return str.replace(/_([a-zA-Z])/g, (_, c) => c.toUpperCase())
    },
    toPascalCase(str) {
      if (!str) return ''
      return str.split('_').map(s => s.charAt(0).toUpperCase() + s.slice(1).toLowerCase()).join('')
    },
    toKebabCase(str) {
      if (!str) return ''
      return str.replace(/([a-z0-9])([A-Z])/g, '$1-$2').toLowerCase()
    }
  },
  mounted() {
    this.loadConfigList()
    this.previewCode()
  }
}
</script>

<style scoped>
.code-generator-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
}

.code-generator-header {
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

.code-generator-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.code-generator-main {
  flex: 1;
  display: flex;
  overflow: hidden;
  padding: 16px;
  gap: 16px;
}

.config-list-panel {
  width: 320px;
  background: #fff;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
}

.panel-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.panel-header h3 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #303133;
}

.config-table {
  flex: 1;
}

.config-name {
  font-weight: 500;
}

.config-form-panel {
  flex: 1;
  background: #fff;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.config-form-panel .el-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.config-form-panel .el-tabs__content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.config-form-panel .el-tab-pane {
  height: 100%;
}

.field-toolbar {
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
}

.field-table {
  margin-top: 12px;
}

.form-actions {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 8px;
}

.preview-panel {
  width: 480px;
  background: #fff;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
}

.preview-select {
  width: 150px;
}

.preview-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.preview-tabs .el-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.preview-tabs .el-tabs__content {
  flex: 1;
  overflow: hidden;
}

.preview-tabs .el-tab-pane {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.code-actions {
  padding: 8px 12px;
  background: #f5f7fa;
  border-bottom: 1px solid #e8e8e8;
}

.code-preview {
  flex: 1;
  background: #1e1e1e;
  padding: 16px;
  border-radius: 0 0 4px 4px;
  font-size: 12px;
  line-height: 1.5;
  overflow: auto;
  margin: 0;
  color: #d4d4d4;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  white-space: pre-wrap;
  word-break: break-all;
}

.result-textarea {
  margin-top: 16px;
  font-family: monospace;
  font-size: 12px;
}

.result-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}

.entity-input :deep(.el-input-group__append) {
  background: #f5f7fa;
  color: #909399;
}
</style>
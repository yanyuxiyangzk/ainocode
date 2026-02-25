<template>
  <div class="create-table-page">
    <div class="page-container">
      <!-- 左侧导航树 -->
      <div class="sidebar">
        <div class="sidebar-header">
          <span class="title">数据库列表</span>
          <el-button type="primary" size="mini" icon="el-icon-refresh" @click="refreshDatasources"></el-button>
        </div>
        <div class="search-box">
          <el-input v-model="searchKeyword" placeholder="搜索数据库对象" size="small" prefix-icon="el-icon-search" clearable></el-input>
        </div>
        <div class="tree-container" v-loading="loadingTree">
          <el-tree
            v-if="treeData.length > 0"
            :data="treeData"
            :props="treeProps"
            node-key="id"
            default-expand-all
            :expand-on-click-node="false"
            @node-click="handleNodeClick">
            <span slot-scope="{ node, data }" class="tree-node">
              <i :class="getNodeIcon(data)"></i>
              <span>{{ node.label }}</span>
            </span>
          </el-tree>
          <el-empty v-else description="暂无数据源" :image-size="60"></el-empty>
        </div>
      </div>

      <!-- 主工作区 -->
      <div class="main-content">
        <!-- 顶部工具栏 -->
        <div class="toolbar">
          <div class="toolbar-left">
            <el-button icon="el-icon-back" size="small" @click="goBack">返回</el-button>
            <span class="connection-name">{{ currentDatasource || '请选择数据源' }}</span>
          </div>
          <div class="toolbar-right">
            <el-button size="small" icon="el-icon-refresh" @click="refreshSchema">刷新架构</el-button>
          </div>
        </div>

        <!-- 表单区域 -->
        <div class="form-container">
          <el-form :model="tableForm" label-width="100px" class="table-form">
            <!-- 基本信息 -->
            <el-card class="form-card">
              <div slot="header" class="card-header">
                <span>基本信息</span>
              </div>
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-form-item label="表名" required prop="tableName">
                    <el-input v-model="tableForm.tableName" placeholder="请输入表名（英文）" maxlength="64" clearable></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="模式" prop="schema">
                    <el-select v-model="currentSchema" placeholder="选择模式" style="width: 100%;" filterable allow-create @change="handleSchemaChange">
                      <el-option v-for="schema in schemas" :key="schema" :label="schema" :value="schema"></el-option>
                      <el-option label="public (默认)" value="public"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="表注释" prop="comment">
                    <el-input v-model="tableForm.comment" placeholder="表的中文描述" maxlength="256"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="表类型" prop="tableType">
                    <el-select v-model="tableForm.tableType" placeholder="选择表类型" style="width: 100%;">
                      <el-option label="正常表" value="normal"></el-option>
                      <el-option label="临时表" value="temporary"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-form-item label="选项">
                    <el-checkbox v-model="tableForm.ifNotExists">IF NOT EXISTS</el-checkbox>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-card>

            <!-- 字段定义 -->
            <el-card class="form-card">
              <div slot="header" class="card-header">
                <span>字段定义</span>
                <div class="card-actions">
                  <el-button size="mini" type="primary" icon="el-icon-plus" @click="addColumn">添加字段</el-button>
                  <el-button size="mini" icon="el-icon-document-add" @click="addCommonFields">常用字段</el-button>
                </div>
              </div>

              <!-- 字段表格 - 紧凑模式 -->
              <div class="columns-table-wrapper">
                <table class="columns-table">
                  <thead>
                    <tr>
                      <th class="col-order">顺序</th>
                      <th class="col-name">字段名 <span class="required">*</span></th>
                      <th class="col-type">数据类型</th>
                      <th class="col-length">长度</th>
                      <th class="col-decimal">小数位</th>
                      <th class="col-pk">主键</th>
                      <th class="col-nn">非空</th>
                      <th class="col-ai">自增</th>
                      <th class="col-unique">唯一</th>
                      <th class="col-default">默认值</th>
                      <th class="col-comment">注释</th>
                      <th class="col-actions">操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(col, index) in tableForm.columns" :key="index" :class="{ 'pk-row': col.primaryKey }">
                      <td class="col-order">
                        <div class="order-controls">
                          <i class="el-icon-top" @click="moveColumn(index, -1)" :class="{ disabled: index === 0 }"></i>
                          <i class="el-icon-bottom" @click="moveColumn(index, 1)" :class="{ disabled: index === tableForm.columns.length - 1 }"></i>
                        </div>
                      </td>
                      <td class="col-name">
                        <el-input v-model="col.name" placeholder="字段名" size="mini" @change="validateColumnName(index)"></el-input>
                      </td>
                      <td class="col-type">
                        <el-select
                          v-model="col.type"
                          placeholder="选择类型"
                          size="mini"
                          style="width: 100%;"
                          filterable
                          allow-create
                          @change="handleTypeChange(index)"
                        >
                          <el-option-group v-for="group in groupedDataTypes" :key="group.label" :label="group.label">
                            <el-option v-for="t in group.types" :key="t.value" :label="t.label" :value="t.value">
                              <span>{{ t.label }}</span>
                              <span class="type-desc">{{ t.desc }}</span>
                            </el-option>
                          </el-option-group>
                        </el-select>
                      </td>
                      <td class="col-length">
                        <el-input-number
                          v-model="col.length"
                          :min="0"
                          :max="9999"
                          size="mini"
                          style="width: 70px;"
                          :disabled="!needLength(col.type)"
                        ></el-input-number>
                      </td>
                      <td class="col-decimal">
                        <el-input-number
                          v-model="col.scale"
                          :min="0"
                          :max="38"
                          size="mini"
                          style="width: 60px;"
                          :disabled="!needScale(col.type)"
                        ></el-input-number>
                      </td>
                      <td class="col-pk">
                        <el-checkbox v-model="col.primaryKey" @change="handlePrimaryKeyChange(index)"></el-checkbox>
                      </td>
                      <td class="col-nn">
                        <el-checkbox v-model="col.nullable" :disabled="col.primaryKey"></el-checkbox>
                      </td>
                      <td class="col-ai">
                        <el-checkbox v-model="col.autoIncrement" :disabled="!canAutoIncrement(col.type)"></el-checkbox>
                      </td>
                      <td class="col-unique">
                        <el-checkbox v-model="col.unique" :disabled="col.primaryKey"></el-checkbox>
                      </td>
                      <td class="col-default">
                        <el-select
                          v-model="col.defaultValue"
                          placeholder="选择"
                          size="mini"
                          style="width: 100%;"
                          clearable
                          allow-create
                        >
                          <el-option label="NULL" value="NULL"></el-option>
                          <el-option label="空字符串" value="''"></el-option>
                          <el-option label="当前时间" value="CURRENT_TIMESTAMP"></el-option>
                          <el-option label="0" value="0"></el-option>
                          <el-option label="1" value="1"></el-option>
                          <el-option label="FALSE" value="FALSE"></el-option>
                          <el-option label="TRUE" value="TRUE"></el-option>
                        </el-select>
                      </td>
                      <td class="col-comment">
                        <el-input v-model="col.comment" placeholder="注释" size="mini" maxlength="500"></el-input>
                      </td>
                      <td class="col-actions">
                        <el-button type="text" size="mini" icon="el-icon-delete" @click="removeColumn(index)" :disabled="tableForm.columns.length <= 1"></el-button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <!-- 字段统计 -->
              <div class="columns-footer">
                <el-tag type="info">字段数: {{ tableForm.columns.length }}</el-tag>
                <el-tag type="warning" v-if="primaryKeyCount > 0">主键字段: {{ primaryKeyCount }}</el-tag>
                <el-tag type="danger" v-if="hasAutoIncrement">自增字段: {{ autoIncrementCount }}</el-tag>
              </div>
            </el-card>

            <!-- 索引管理 -->
            <el-card class="form-card">
              <div slot="header" class="card-header">
                <span>索引管理</span>
                <div class="card-actions">
                  <el-button size="mini" type="primary" icon="el-icon-plus" @click="addIndex">添加索引</el-button>
                </div>
              </div>

              <el-table :data="tableForm.indexes" border size="small">
                <el-table-column prop="name" label="索引名" width="150">
                  <template slot-scope="{row, $index}">
                    <el-input v-model="row.name" placeholder="索引名" size="mini"></el-input>
                  </template>
                </el-table-column>
                <el-table-column prop="type" label="索引类型" width="120">
                  <template slot-scope="{row}">
                    <el-select v-model="row.type" size="mini" style="width: 100%;">
                      <el-option label="普通索引" value="normal"></el-option>
                      <el-option label="唯一索引" value="unique"></el-option>
                      <el-option label="全文索引" value="fulltext"></el-option>
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column prop="columns" label="索引字段" min-width="200">
                  <template slot-scope="{row, $index}">
                    <el-select
                      v-model="row.columns"
                      multiple
                      filterable
                      size="mini"
                      style="width: 100%;"
                      placeholder="选择字段"
                    >
                      <el-option v-for="col in availableColumns" :key="col" :label="col" :value="col"></el-option>
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column prop="comment" label="注释" width="150">
                  <template slot-scope="{row}">
                    <el-input v-model="row.comment" placeholder="注释" size="mini"></el-input>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80" align="center">
                  <template slot-scope="{row, $index}">
                    <el-button type="text" size="mini" icon="el-icon-delete" @click="removeIndex($index)" style="color: #f56c6c;"></el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>

            <!-- 外键约束 -->
            <el-card class="form-card">
              <div slot="header" class="card-header">
                <span>外键约束</span>
                <div class="card-actions">
                  <el-button size="mini" type="primary" icon="el-icon-plus" @click="addForeignKey">添加外键</el-button>
                </div>
              </div>

              <el-table :data="tableForm.foreignKeys" border size="small" v-if="tableForm.foreignKeys.length > 0">
                <el-table-column prop="name" label="约束名" width="150">
                  <template slot-scope="{row}">
                    <el-input v-model="row.name" placeholder="约束名" size="mini"></el-input>
                  </template>
                </el-table-column>
                <el-table-column prop="column" label="本表字段" width="150">
                  <template slot-scope="{row}">
                    <el-select v-model="row.column" size="mini" style="width: 100%;">
                      <el-option v-for="col in availableColumns" :key="col" :label="col" :value="col"></el-option>
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column prop="refTable" label="引用表" width="150">
                  <template slot-scope="{row}">
                    <el-input v-model="row.refTable" placeholder="引用表名" size="mini"></el-input>
                  </template>
                </el-table-column>
                <el-table-column prop="refColumn" label="引用字段" width="150">
                  <template slot-scope="{row}">
                    <el-input v-model="row.refColumn" placeholder="引用字段" size="mini"></el-input>
                  </template>
                </el-table-column>
                <el-table-column prop="onDelete" label="删除规则" width="120">
                  <template slot-scope="{row}">
                    <el-select v-model="row.onDelete" size="mini" style="width: 100%;">
                      <el-option label="NO ACTION" value="NO ACTION"></el-option>
                      <el-option label="CASCADE" value="CASCADE"></el-option>
                      <el-option label="SET NULL" value="SET NULL"></el-option>
                      <el-option label="RESTRICT" value="RESTRICT"></el-option>
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80" align="center">
                  <template slot-scope="{row, $index}">
                    <el-button type="text" size="mini" icon="el-icon-delete" @click="removeForeignKey($index)" style="color: #f56c6c;"></el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-else description="暂无外键约束" :image-size="60"></el-empty>
            </el-card>

            <!-- SQL预览 -->
            <el-card class="form-card">
              <div slot="header" class="card-header">
                <span>SQL 预览</span>
                <div class="card-actions">
                  <el-button size="mini" icon="el-icon-document-copy" @click="copySql">复制</el-button>
                  <el-button size="mini" icon="el-icon-view" :type="showSql ? 'primary' : ''" @click="showSql = !showSql">{{ showSql ? '隐藏' : '显示' }}</el-button>
                </div>
              </div>

              <div v-if="showSql" class="sql-preview">
                <pre class="sql-content">{{ generatedSql }}</pre>
              </div>
            </el-card>
          </el-form>

          <!-- 底部操作按钮 -->
          <div class="form-actions">
            <el-button @click="resetForm">重置</el-button>
            <el-button @click="validateBeforeCreate">验证</el-button>
            <el-button type="primary" @click="executeCreate" :loading="executing">创建表</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'CreateTable',
  data() {
    return {
      loading: false,
      loadingTree: false,
      executing: false,
      datasources: [],
      treeData: [],
      treeProps: {
        label: 'label',
        children: 'children'
      },
      searchKeyword: '',
      currentDatasource: '',
      currentSchema: 'public',
      schemas: [],
      databaseType: 'postgresql',
      showSql: true,

      // 分组的数据类型
      groupedDataTypes: [
        {
          label: '整数类型',
          types: [
            { value: 'tinyint', label: 'TINYINT', desc: '微整数 1字节' },
            { value: 'smallint', label: 'SMALLINT', desc: '小整数 2字节' },
            { value: 'int', label: 'INT', desc: '整数 4字节' },
            { value: 'bigint', label: 'BIGINT', desc: '大整数 8字节' }
          ]
        },
        {
          label: '自增类型',
          types: [
            { value: 'serial', label: 'SERIAL', desc: '自增整数' },
            { value: 'bigserial', label: 'BIGSERIAL', desc: '大自增整数' },
            { value: 'int auto_increment', label: 'INT AUTO_INC', desc: '自增整数(MySQL)' }
          ]
        },
        {
          label: '小数类型',
          types: [
            { value: 'decimal', label: 'DECIMAL', desc: '精确小数' },
            { value: 'numeric', label: 'NUMERIC', desc: '精确数值' },
            { value: 'real', label: 'REAL', desc: '单精度浮点' },
            { value: 'double precision', label: 'DOUBLE', desc: '双精度浮点' },
            { value: 'float', label: 'FLOAT', desc: '浮点数' }
          ]
        },
        {
          label: '字符串类型',
          types: [
            { value: 'varchar(255)', label: 'VARCHAR(n)', desc: '变长字符串' },
            { value: 'char(1)', label: 'CHAR(n)', desc: '定长字符串' },
            { value: 'text', label: 'TEXT', desc: '长文本' }
          ]
        },
        {
          label: '日期时间',
          types: [
            { value: 'date', label: 'DATE', desc: '日期' },
            { value: 'time', label: 'TIME', desc: '时间' },
            { value: 'timestamp', label: 'TIMESTAMP', desc: '时间戳' },
            { value: 'timestamptz', label: 'TIMESTAMPTZ', desc: '带时区时间戳' },
            { value: 'datetime', label: 'DATETIME', desc: '日期时间(MySQL)' }
          ]
        },
        {
          label: '布尔类型',
          types: [
            { value: 'boolean', label: 'BOOLEAN', desc: '布尔值' },
            { value: 'bool', label: 'BOOL', desc: '布尔值' }
          ]
        },
        {
          label: '其他类型',
          types: [
            { value: 'json', label: 'JSON', desc: 'JSON 数据' },
            { value: 'jsonb', label: 'JSONB', desc: '二进制 JSON' },
            { value: 'uuid', label: 'UUID', desc: '通用唯一标识' },
            { value: 'bytea', label: 'BYTEA', desc: '二进制数据' }
          ]
        }
      ],

      tableForm: {
        tableName: '',
        comment: '',
        tableType: 'normal',
        ifNotExists: false,
        columns: [
          { name: 'id', type: 'bigint', length: null, scale: null, primaryKey: true, nullable: false, autoIncrement: true, unique: true, defaultValue: '', comment: '主键ID' }
        ],
        indexes: [],
        foreignKeys: []
      }
    }
  },
  computed: {
    generatedSql() {
      const parts = []
      const tableName = this.tableForm.tableName.trim()
      const schema = this.currentSchema || 'public'
      const datasource = this.currentDatasource || ''

      if (!tableName) {
        return '-- 请输入表名'
      }

      // 确定数据库类型
      const isMysql = this.databaseType === 'mysql'

      // 构建 CREATE TABLE 语句
      let sql = 'CREATE TABLE '
      if (this.tableForm.ifNotExists) {
        sql += (isMysql ? 'IF NOT EXISTS ' : 'IF NOT EXISTS ')
      }
      // MySQL 不使用 schema，PostgreSQL 使用
      if (isMysql) {
        sql += tableName + ' (\n'
      } else {
        sql += schema + '.' + tableName + ' (\n'
      }

      // 字段定义
      const columnDefs = []
      this.tableForm.columns.forEach(col => {
        let def = '  ' + col.name + ' ' + this.formatType(col)
        if (isMysql && col.autoIncrement) {
          def += ' AUTO_INCREMENT'
        }
        if (col.defaultValue && col.defaultValue !== 'NULL') {
          def += ' DEFAULT ' + col.defaultValue
        }
        if (!col.nullable && !col.primaryKey) {
          def += ' NOT NULL'
        }
        columnDefs.push(def)
      })

      // 主键约束
      const pkColumns = this.tableForm.columns.filter(c => c.primaryKey).map(c => c.name)
      if (pkColumns.length > 0) {
        const pkName = tableName + '_pkey'
        columnDefs.push('  CONSTRAINT ' + pkName + ' PRIMARY KEY (' + pkColumns.join(', ') + ')')
      }

      // 唯一约束（非主键的）
      this.tableForm.columns.filter(c => c.unique && !c.primaryKey).forEach((col, idx) => {
        const uniqueName = tableName + '_' + col.name + '_key'
        columnDefs.push('  CONSTRAINT ' + uniqueName + ' UNIQUE (' + col.name + ')')
      })

      // 外键约束
      this.tableForm.foreignKeys.forEach(fk => {
        const fkName = fk.name || tableName + '_' + fk.column + '_fkey'
        columnDefs.push('  CONSTRAINT ' + fkName + ' FOREIGN KEY (' + fk.column + ') REFERENCES ' + fk.refTable + ' (' + fk.refColumn + ')')
        if (fk.onDelete && fk.onDelete !== 'NO ACTION') {
          columnDefs[columnDefs.length - 1] += ' ON DELETE ' + fk.onDelete
        }
      })

      sql += columnDefs.join(',\n')
      sql += '\n)'

      // MySQL 表选项
      if (isMysql) {
        sql += ' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci'
      }

      sql += ';\n'

      // 表注释
      if (this.tableForm.comment) {
        sql += "\n-- 表注释\n"
        if (isMysql) {
          sql += "COMMENT ON TABLE " + tableName + " IS '" + this.tableForm.comment + "';\n"
        } else {
          sql += "COMMENT ON TABLE " + schema + '.' + tableName + " IS '" + this.tableForm.comment + "';\n"
        }
      }

      // 字段注释
      this.tableForm.columns.forEach(col => {
        if (col.comment) {
          sql += "\n-- " + col.name + " 字段注释\n"
          if (isMysql) {
            sql += "COMMENT ON COLUMN " + tableName + '.' + col.name + " IS '" + col.comment + "';\n"
          } else {
            sql += "COMMENT ON COLUMN " + schema + '.' + tableName + '.' + col.name + " IS '" + col.comment + "';\n"
          }
        }
      })

      // 创建索引
      if (this.tableForm.indexes.length > 0 && !isMysql) {
        sql += '\n-- 索引定义\n'
        this.tableForm.indexes.forEach(idx => {
          const idxName = idx.name || tableName + '_' + idx.columns.join('_') + '_idx'
          sql += 'CREATE '
          if (idx.type === 'unique') sql += 'UNIQUE '
          sql += 'INDEX ' + idxName + ' ON ' + schema + '.' + tableName + ' (' + idx.columns.join(', ') + ');\n'
          if (idx.comment) {
            sql += "COMMENT ON INDEX " + idxName + " IS '" + idx.comment + "';\n"
          }
        })
      }

      return sql
    },

    availableColumns() {
      return this.tableForm.columns.map(c => c.name).filter(n => n)
    },

    primaryKeyCount() {
      return this.tableForm.columns.filter(c => c.primaryKey).length
    },

    hasAutoIncrement() {
      return this.tableForm.columns.some(c => c.autoIncrement)
    },

    autoIncrementCount() {
      return this.tableForm.columns.filter(c => c.autoIncrement).length
    },

    hasValidationErrors() {
      const errors = []
      if (!this.tableForm.tableName.trim()) errors.push('表名不能为空')
      if (this.tableForm.columns.some(c => !c.name.trim())) errors.push('字段名不能为空')
      if (this.primaryKeyCount === 0) errors.push('请至少设置一个主键')
      if (this.hasAutoIncrement && this.primaryKeyCount !== 1) errors.push('自增字段必须是主键且只能有一个')
      return errors
    }
  },
  async mounted() {
    await this.loadDatasources()
    if (this.$route.query.datasource) {
      this.currentDatasource = this.$route.query.datasource
      this.buildTree()
      await this.loadSchemas()
    }
  },
  methods: {
    async loadDatasources() {
      this.loadingTree = true
      try {
        this.datasources = await api.getDatasources()
        this.buildTree()
        // 如果有数据源但没有选中的，自动选中第一个
        if (this.datasources.length > 0 && !this.currentDatasource) {
          this.currentDatasource = this.datasources[0].name
          this.buildTree()
          await this.loadSchemas()
        }
      } catch (e) {
        this.$message.error('加载数据源失败: ' + e.message)
      } finally {
        this.loadingTree = false
      }
    },

    async loadSchemas() {
      if (!this.currentDatasource) return
      try {
        const result = await api.executeSql(this.currentDatasource, "SELECT version()")
        if (result.data && result.data.length > 0) {
          const versionInfo = result.data[0]
          if (versionInfo.version && versionInfo.version.toLowerCase().includes('mysql')) {
            this.databaseType = 'mysql'
            this.currentSchema = ''
          } else {
            this.databaseType = 'postgresql'
          }
        }
      } catch (e) {
        console.log('检测数据库类型失败，使用默认 PostgreSQL')
      }

      // PostgreSQL 尝试获取模式列表
      if (this.databaseType === 'postgresql') {
        try {
          this.schemas = await api.getSchemas(this.currentDatasource)
          console.log('获取到模式列表:', this.schemas)
          // 如果没有模式，添加默认的 public
          if (!this.schemas.includes('public')) {
            this.schemas.unshift('public')
          }
          // 如果还没有选中模式，选中 public
          if (!this.currentSchema) {
            this.currentSchema = 'public'
          }
        } catch (e) {
          console.error('获取模式列表失败，使用默认 public:', e.message)
          this.schemas = ['public']
          this.currentSchema = 'public'
        }
      } else {
        this.schemas = []
        this.currentSchema = ''
      }
    },

    handleSchemaChange() {
      console.log('模式已切换:', this.currentSchema)
    },

    buildTree() {
      this.treeData = this.datasources.map((ds, index) => ({
        id: 'ds_' + index,
        label: ds.name,
        type: 'datasource',
        children: []
      }))
    },

    async refreshDatasources() {
      await this.loadDatasources()
    },

    async refreshSchema() {
      if (!this.currentDatasource) return
      try {
        await api.refreshDatasource(this.currentDatasource)
        await this.loadSchemas()
        this.$message.success('架构已刷新')
      } catch (e) {
        this.$message.error('刷新失败: ' + e.message)
      }
    },

    getNodeIcon(data) {
      if (data.type === 'datasource') return 'el-icon-connection'
      return 'el-icon-document'
    },

    handleNodeClick(data) {
      if (data.type === 'datasource') {
        this.currentDatasource = data.label
        this.$router.replace({ path: '/create-table', query: { datasource: data.label } })
        this.loadSchemas()
      }
    },

    // 字段类型相关方法
    needLength(type) {
      if (!type || typeof type !== 'string') return false
      const lowerType = type.toLowerCase()
      return ['varchar', 'char', 'nvarchar', 'varbinary'].some(t => lowerType.includes(t))
    },

    needScale(type) {
      if (!type || typeof type !== 'string') return false
      const lowerType = type.toLowerCase()
      return ['decimal', 'numeric', 'dec', 'float', 'double'].some(t => lowerType.includes(t))
    },

    canAutoIncrement(type) {
      if (!type || typeof type !== 'string') return false
      const lowerType = type.toLowerCase()
      return ['serial', 'bigserial', 'int auto_increment', 'bigint auto_increment', 'int(11) auto_increment'].some(t => lowerType.includes(t))
    },

    formatType(col) {
      let type = col.type || 'varchar(255)'
      if (typeof type !== 'string') {
        type = 'varchar(255)'
      }
      if (this.databaseType === 'mysql') {
        // MySQL 类型格式化
        if (type === 'int auto_increment') return 'INT AUTO_INCREMENT'
        if (type === 'bigint auto_increment') return 'BIGINT AUTO_INCREMENT'
        if (type === 'serial') return 'INT AUTO_INCREMENT'
        if (type === 'bigserial') return 'BIGINT AUTO_INCREMENT'
      }

      if (this.needLength(type) && col.length) {
        if (!type.includes('(')) {
          type = type + '(' + col.length + ')'
        }
      }
      if (this.needScale(type) && col.scale !== null && col.scale !== undefined) {
        const baseType = type.split('(')[0]
        type = baseType + '(' + (col.length || 10) + ',' + col.scale + ')'
      }
      return type
    },

    // 字段操作
    addColumn() {
      this.tableForm.columns.push({
        name: '',
        type: 'varchar(255)',
        length: 255,
        scale: null,
        primaryKey: false,
        nullable: true,
        autoIncrement: false,
        unique: false,
        defaultValue: '',
        comment: ''
      })
    },

    addCommonFields() {
      const commonFields = [
        { name: 'created_at', type: 'timestamp', length: null, scale: null, primaryKey: false, nullable: false, autoIncrement: false, unique: false, defaultValue: 'CURRENT_TIMESTAMP', comment: '创建时间' },
        { name: 'updated_at', type: 'timestamp', length: null, scale: null, primaryKey: false, nullable: false, autoIncrement: false, unique: false, defaultValue: 'CURRENT_TIMESTAMP', comment: '更新时间' },
        { name: 'is_deleted', type: 'boolean', length: null, scale: null, primaryKey: false, nullable: false, autoIncrement: false, unique: false, defaultValue: 'FALSE', comment: '是否删除' },
        { name: 'remark', type: 'text', length: null, scale: null, primaryKey: false, nullable: true, autoIncrement: false, unique: false, defaultValue: '', comment: '备注' }
      ]
      this.tableForm.columns.push(...commonFields)
    },

    removeColumn(index) {
      if (this.tableForm.columns.length <= 1) {
        this.$message.warning('至少保留一个字段')
        return
      }
      this.tableForm.columns.splice(index, 1)
    },

    moveColumn(index, direction) {
      const newIndex = index + direction
      if (newIndex < 0 || newIndex >= this.tableForm.columns.length) return
      const temp = this.tableForm.columns[index]
      this.tableForm.columns[index] = this.tableForm.columns[newIndex]
      this.tableForm.columns[newIndex] = temp
    },

    validateColumnName(index) {
      const name = this.tableForm.columns[index].name
      if (name && !/^[a-zA-Z][a-zA-Z0-9_]*$/.test(name)) {
        this.$message.warning('字段名必须以字母开头，只能包含字母、数字和下划线')
        this.tableForm.columns[index].name = ''
      }
    },

    handlePrimaryKeyChange(index) {
      const col = this.tableForm.columns[index]
      if (col.primaryKey) {
        col.nullable = false
        col.unique = true
      }
    },

    handleTypeChange(index) {
      const col = this.tableForm.columns[index]
      // 根据类型自动设置属性
      if (this.canAutoIncrement(col.type)) {
        col.autoIncrement = true
        col.primaryKey = true
      }
    },

    // 索引操作
    addIndex() {
      this.tableForm.indexes.push({
        name: '',
        type: 'normal',
        columns: [],
        comment: ''
      })
    },

    removeIndex(index) {
      this.tableForm.indexes.splice(index, 1)
    },

    // 外键操作
    addForeignKey() {
      this.tableForm.foreignKeys.push({
        name: '',
        column: '',
        refTable: '',
        refColumn: 'id',
        onDelete: 'NO ACTION'
      })
    },

    removeForeignKey(index) {
      this.tableForm.foreignKeys.splice(index, 1)
    },

    // SQL 操作
    copySql() {
      const sql = this.generatedSql
      if (navigator.clipboard) {
        navigator.clipboard.writeText(sql)
      } else {
        const textarea = document.createElement('textarea')
        textarea.value = sql
        document.body.appendChild(textarea)
        textarea.select()
        document.execCommand('copy')
        document.body.removeChild(textarea)
      }
      this.$message.success('已复制到剪贴板')
    },

    validateBeforeCreate() {
      const errors = this.hasValidationErrors
      if (errors.length > 0) {
        this.$message.warning(errors.join('；'))
        return false
      }
      this.$message.success('验证通过')
      return true
    },

    resetForm() {
      this.$confirm('确定要重置所有内容吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.tableForm = {
          tableName: '',
          comment: '',
          tableType: 'normal',
          ifNotExists: false,
          columns: [
            { name: 'id', type: 'bigint', length: null, scale: null, primaryKey: true, nullable: false, autoIncrement: true, unique: true, defaultValue: '', comment: '主键ID' }
          ],
          indexes: [],
          foreignKeys: []
        }
        this.$message.success('已重置')
      }).catch(() => {})
    },

    async executeCreate() {
      if (!this.currentDatasource) {
        this.$message.warning('请先选择数据源')
        return
      }

      const errors = this.hasValidationErrors
      if (errors.length > 0) {
        this.$message.warning(errors.join('；'))
        return
      }

      let sql = this.generatedSql

      // PostgreSQL 需要设置 search_path
      if (this.databaseType === 'postgresql' && this.currentSchema && this.currentSchema !== 'public') {
        sql = "SET search_path TO " + this.currentSchema + ";\n\n" + sql
      }

      this.executing = true
      try {
        const res = await api.executeSql(this.currentDatasource, sql)
        if (res.success || !res.message?.includes('失败')) {
          this.$message.success('表创建成功')
          await api.refreshDatasource(this.currentDatasource)
          this.$router.push({ path: '/tables', query: { datasource: this.currentDatasource } })
        } else {
          this.$message.error(res.message || '创建失败')
        }
      } catch (e) {
        this.$message.error('执行失败: ' + e.message)
      } finally {
        this.executing = false
      }
    },

    goBack() {
      if (this.currentDatasource) {
        this.$router.push({ path: '/tables', query: { datasource: this.currentDatasource } })
      } else {
        this.$router.push('/tables')
      }
    }
  }
}
</script>

<style scoped>
.create-table-page {
  height: 100%;
  background: #f5f7fa;
}

.page-container {
  display: flex;
  height: calc(100vh - 60px);
}

.sidebar {
  width: 240px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-header .title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.search-box {
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
}

.tree-container {
  flex: 1;
  overflow: auto;
  padding: 8px;
}

.tree-node {
  display: flex;
  align-items: center;
  font-size: 13px;
}

.tree-node i {
  margin-right: 6px;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.toolbar {
  height: 48px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.connection-name {
  font-size: 14px;
  font-weight: 500;
  color: #409EFF;
}

.form-container {
  flex: 1;
  padding: 16px;
  overflow: auto;
}

.form-card {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 14px;
  font-weight: 500;
}

.card-actions {
  display: flex;
  gap: 8px;
}

/* 字段表格样式 */
.columns-table-wrapper {
  overflow-x: auto;
}

.columns-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}

.columns-table th,
.columns-table td {
  border: 1px solid #ebeef5;
  padding: 4px 6px;
  text-align: center;
}

.columns-table th {
  background: #fafafa;
  color: #606266;
  font-weight: 500;
  white-space: nowrap;
}

.columns-table .pk-row {
  background: #ecf5ff;
}

.columns-table .required {
  color: #f56c6c;
}

.col-order { width: 40px; }
.col-name { width: 100px; }
.col-type { width: 140px; }
.col-length { width: 70px; }
.col-decimal { width: 60px; }
.col-pk { width: 40px; }
.col-nn { width: 40px; }
.col-ai { width: 40px; }
.col-unique { width: 40px; }
.col-default { width: 100px; }
.col-comment { width: 100px; }
.col-actions { width: 50px; }

.order-controls {
  display: flex;
  flex-direction: column;
  gap: 2px;
  align-items: center;
}

.order-controls i {
  cursor: pointer;
  color: #409EFF;
  font-size: 12px;
}

.order-controls i.disabled {
  color: #c0c4cc;
  cursor: not-allowed;
}

.type-desc {
  float: right;
  color: #909399;
  font-size: 10px;
}

.columns-footer {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

.sql-preview {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.sql-content {
  margin: 0;
  padding: 12px;
  background: #2d2d2d;
  color: #f8f8f2;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  max-height: 300px;
  overflow: auto;
}

.form-actions {
  margin-top: 16px;
  text-align: right;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.form-actions .el-button {
  margin-left: 12px;
}
</style>
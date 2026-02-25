<template>
  <div class="tables-page">
    <div class="page-container">
      <!-- 左侧导航树 -->
      <div class="sidebar">
        <div class="sidebar-header">
          <span class="title">数据库列表</span>
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
              <span class="table-count" v-if="data.type === 'schema' && data.count > 0">({{ data.count }})</span>
            </span>
          </el-tree>
          <el-empty v-else-if="!loadingTree" description="暂无数据源" :image-size="60"></el-empty>
        </div>
      </div>

      <!-- 主工作区 -->
      <div class="main-content">
        <!-- 顶部工具栏 -->
        <div class="toolbar">
          <div class="toolbar-left">
            <span class="current-ds">{{ currentDatasource || '请选择数据源' }}</span>
          </div>
          <div class="toolbar-right">
            <el-button size="small" icon="el-icon-connection" @click="goToErDiagram">ER图</el-button>
            <el-button size="small" icon="el-icon-plus" type="primary" @click="goToCreateTable">新建表</el-button>
            <el-button size="small" icon="el-icon-refresh" @click="refreshTables">刷新</el-button>
          </div>
        </div>

        <!-- 数据表列表 -->
        <div class="table-container" v-loading="loadingTables">
          <!-- 筛选区域 -->
          <div class="filter-bar">
            <el-input v-model="tableSearch" placeholder="搜索表名" prefix-icon="el-icon-search" clearable style="width: 240px;"></el-input>
            <span class="table-stats">共 {{ filteredTables.length }} 张表</span>
          </div>

          <!-- 表格 -->
          <el-table :data="filteredTables" border stripe row-key="tableName" class="data-table">
            <el-table-column type="index" width="50" align="center"></el-table-column>
            <el-table-column prop="tableName" label="表名" min-width="180">
              <template slot-scope="{row}">
                <i class="el-icon-table" style="margin-right: 5px; color: #409EFF;"></i>
                <span>{{ row.tableName }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="comment" label="注释" min-width="200" show-overflow-tooltip>
              <template slot-scope="{row}">
                <span>{{ row.comment || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="字段数" width="100" align="center">
              <template slot-scope="{row}">
                <el-tag type="info" size="small">{{ row.columns ? row.columns.length : 0 }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="主键" width="120" align="center">
              <template slot-scope="{row}">
                <el-tooltip :content="row.primaryKeyType || '未知'" placement="top">
                  <el-tag size="small" effect="plain">{{ row.primaryKeyColumn || '无主键' }}</el-tag>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
              <template slot-scope="{row}">
                <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, row)">
                  <el-button type="text" size="mini">
                    操作 <i class="el-icon-arrow-down el-icon--right"></i>
                  </el-button>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="view">
                      <i class="el-icon-view"></i> 查看结构
                    </el-dropdown-item>
                    <el-dropdown-item command="apidoc">
                      <i class="el-icon-document"></i> API文档
                    </el-dropdown-item>
                    <el-dropdown-item command="test">
                      <i class="el-icon-search"></i> 在线测试
                    </el-dropdown-item>
                    <el-dropdown-item command="copy">
                      <i class="el-icon-document-copy"></i> 复制表名
                    </el-dropdown-item>
                    <el-dropdown-item command="ddl" divided>
                      <i class="el-icon-tickets"></i> 查看DDL
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" v-if="currentDatasource === 'postgres_primary'" style="color: #f56c6c;">
                      <i class="el-icon-delete"></i> 删除表
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>

          <!-- 空状态 -->
          <el-empty v-if="!loadingTables && filteredTables.length === 0" description="暂无数据表">
            <el-button type="primary" @click="refreshTables">刷新列表</el-button>
          </el-empty>
        </div>
      </div>
    </div>

    <!-- 表结构对话框 -->
    <el-dialog :title="'表结构 - ' + currentTable" :visible.sync="schemaDialogVisible" width="900px" class="schema-dialog">
      <div class="schema-header">
        <el-descriptions :column="4" border size="small">
          <el-descriptions-item label="表名">{{ currentTable }}</el-descriptions-item>
          <el-descriptions-item label="主键">{{ currentTableInfo.primaryKeyColumn || '-' }}</el-descriptions-item>
          <el-descriptions-item label="字段数">{{ currentTableInfo.columns ? currentTableInfo.columns.length : 0 }}</el-descriptions-item>
          <el-descriptions-item label="注释">{{ currentTableInfo.comment || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <el-table :data="currentTableInfo.columns || []" stripe size="small" style="margin-top: 15px;" max-height="400">
        <el-table-column property="name" label="字段名" width="150">
          <template slot-scope="{row}">
            <code>{{ row.name }}</code>
          </template>
        </el-table-column>
        <el-table-column property="type" label="数据库类型" width="120"></el-table-column>
        <el-table-column property="javaType" label="Java类型" width="140"></el-table-column>
        <el-table-column property="comment" label="注释" min-width="150"></el-table-column>
        <el-table-column label="主键" width="70" align="center">
          <template slot-scope="{row}">
            <el-tag v-if="row.primaryKey" type="success" size="mini">PRI</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="可空" width="70" align="center">
          <template slot-scope="{row}">
            <el-tag v-if="row.nullable" type="info" size="mini">是</el-tag>
            <span v-else type="danger">否</span>
          </template>
        </el-table-column>
        <el-table-column label="自增" width="70" align="center">
          <template slot-scope="{row}">
            <el-tag v-if="row.autoIncrement" type="warning" size="mini">是</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer">
        <el-button @click="schemaDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="viewApiDoc({tableName: currentTable})">API文档</el-button>
      </div>
    </el-dialog>

    <!-- DDL对话框 -->
    <el-dialog title="表DDL" :visible.sync="ddlDialogVisible" width="700px">
      <el-input type="textarea" v-model="currentDDL" :rows="15" readonly class="ddl-editor"></el-input>
      <div slot="footer">
        <el-button @click="ddlDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="copyDDL">复制DDL</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'Tables',
  data() {
    return {
      loading: false,
      loadingTree: false,
      loadingTables: false,
      datasources: [],
      treeData: [],
      treeProps: {
        label: 'label',
        children: 'children'
      },
      searchKeyword: '',
      tableSearch: '',
      currentDatasource: '',
      currentSchema: '',  // 当前选中的模式
      schemas: [],  // 当前数据源的模式列表
      schemaTableCounts: {},  // 模式下表数量
      loadedTables: [],  // 保存已加载的表数据
      tables: [],
      currentTable: '',
      currentTableInfo: { columns: [] },
      schemaDialogVisible: false,
      ddlDialogVisible: false,
      currentDDL: ''
    }
  },
  computed: {
    filteredTables() {
      if (!this.tableSearch.trim()) {
        return this.tables
      }
      const keyword = this.tableSearch.trim().toLowerCase()
      return this.tables.filter(t => t.tableName.toLowerCase().includes(keyword))
    }
  },
  async mounted() {
    await this.loadDatasources()
    if (this.$route.query.datasource) {
      this.currentDatasource = this.$route.query.datasource
      this.loadSchemas()
    }
  },
  methods: {
    async loadDatasources() {
      this.loadingTree = true
      try {
        this.datasources = await api.getDatasources()
        // 重置状态
        this.currentDatasource = ''
        this.currentSchema = ''
        this.schemas = []
        this.tables = []
        this.loadedTables = []
        this.buildTree()
        // 如果有数据源，自动选中第一个
        if (this.datasources.length > 0) {
          this.currentDatasource = this.datasources[0].name
          this.loadSchemas()
        }
      } catch (e) {
        this.$message.error('加载数据源失败: ' + e.message)
      } finally {
        this.loadingTree = false
      }
    },

    async loadSchemas() {
      if (!this.currentDatasource) return
      this.loadingTree = true
      // 清空之前的表数据
      this.tables = []
      this.loadedTables = []
      this.currentSchema = ''

      try {
        this.schemas = await api.getSchemas(this.currentDatasource)
        console.log('获取到模式列表:', this.schemas)

        if (this.schemas && this.schemas.length > 0) {
          // 有模式，自动选中第一个并加载表
          this.currentSchema = this.schemas[0]
          await this.loadTables()
        } else {
          // 没有模式（MySQL等），直接加载表
          this.schemas = []
          await this.loadTables()
        }
        this.buildTree()
      } catch (e) {
        console.error('加载模式列表失败:', e)
        // 如果获取模式失败，直接加载表列表（兼容不支持模式的数据库如MySQL）
        this.schemas = []
        await this.loadTables()
        this.buildTree()
      } finally {
        this.loadingTree = false
      }
    },

    async buildTree() {
      const treeData = []

      for (let i = 0; i < this.datasources.length; i++) {
        const ds = this.datasources[i]
        const dsNode = {
          id: 'ds_' + i,
          label: ds.name,
          type: 'datasource',
          children: []
        }

        // 只为当前选中的数据源加载详细信息
        if (this.currentDatasource === ds.name) {
          if (this.schemas && this.schemas.length > 0) {
            // PostgreSQL 等支持 schema 的数据库
            for (let j = 0; j < this.schemas.length; j++) {
              const schema = this.schemas[j]
              const schemaNode = {
                id: 'schema_' + i + '_' + j,
                label: schema,
                type: 'schema',
                count: this.currentSchema === schema ? this.tables.length : 0,
                children: []
              }

              // 只为当前选中的 schema 加载表列表
              if (this.currentSchema === schema && this.tables.length > 0) {
                schemaNode.children = this.tables.map((table, tIndex) => ({
                  id: 'table_' + i + '_' + j + '_' + tIndex,
                  label: table.tableName,
                  type: 'table',
                  comment: table.comment
                }))
              }

              dsNode.children.push(schemaNode)
            }
          } else if (this.tables.length > 0) {
            // MySQL 等不支持 schema 的数据库，直接显示表
            dsNode.children = this.tables.map((table, tIndex) => ({
              id: 'table_' + i + '_' + tIndex,
              label: table.tableName,
              type: 'table',
              comment: table.comment
            }))
          }
        } else {
          // 其他数据源显示占位符，点击时再加载
          dsNode.children = [{
            id: 'placeholder_' + i,
            label: '点击加载...',
            type: 'placeholder'
          }]
        }

        treeData.push(dsNode)
      }
      this.treeData = treeData
    },
    
    getNodeIcon(data) {
      if (data.type === 'datasource') {
        return 'el-icon-connection'
      } else if (data.type === 'schema') {
        return 'el-icon-folder-opened'
      }
      return 'el-icon-document'
    },
    
    handleNodeClick(data) {
      if (data.type === 'datasource') {
        // 切换数据源
        if (this.currentDatasource !== data.label) {
          this.currentDatasource = data.label
          this.currentSchema = ''
          this.schemas = []
          this.tables = []
          this.loadedTables = []
          this.loadSchemas()
        }
      } else if (data.type === 'schema') {
        // 切换 schema
        if (this.currentSchema !== data.label) {
          this.currentSchema = data.label
          this.tables = []
          this.loadedTables = []
          this.loadTables()
        }
      } else if (data.type === 'table') {
        // 点击表，显示表信息
        const table = this.tables.find(t => t.tableName === data.label)
        if (table) {
          this.viewTableInfo(table)
        }
      } else if (data.type === 'placeholder') {
        // 点击占位符，加载数据源
        const dsName = data.label.replace('点击加载...', '')
        // 找到父节点
        const parentNode = this.treeData.find(node =>
          node.children && node.children.some(child => child.id === data.id)
        )
        if (parentNode) {
          this.currentDatasource = parentNode.label
          this.currentSchema = ''
          this.schemas = []
          this.tables = []
          this.loadedTables = []
          this.loadSchemas()
        }
      }
    },
    
    async loadTables() {
      if (!this.currentDatasource) return
      this.loadingTables = true
      const newTables = []
      try {
        let tableNames
        if (this.currentSchema) {
          // 按模式获取表列表
          tableNames = await api.getTablesBySchema(this.currentDatasource, this.currentSchema)
        } else {
          // 获取所有表（兼容不支持模式的数据库）
          tableNames = await api.getTables(this.currentDatasource)
        }
        console.log('获取到表名列表:', tableNames)

        for (const name of tableNames) {
          try {
            const info = await api.getTableInfo(this.currentDatasource, name, this.currentSchema)
            newTables.push(info)
          } catch (e) {
            console.error('获取表详情失败:', name, e)
            newTables.push({
              tableName: name,
              columns: [],
              comment: '',
              primaryKeyColumn: null
            })
          }
        }
        // 保存到 loadedTables
        this.loadedTables = newTables
        this.tables = newTables
        // 重新构建树，显示表列表
        this.buildTree()
        console.log('最终 tables:', this.tables)
      } catch (e) {
        this.$message.error('加载表列表失败: ' + e.message)
      } finally {
        this.loadingTables = false
      }
    },
    
    async refreshTables() {
      if (!this.currentDatasource) return
      try {
        await api.refreshDatasource(this.currentDatasource)
        // 重新加载模式列表和表列表
        await this.loadSchemas()
        this.$message.success('刷新成功')
      } catch (e) {
        this.$message.error('刷新失败: ' + e.message)
      }
    },
    
    handleCommand(command, row) {
      switch (command) {
        case 'view':
          this.viewTableInfo(row)
          break
        case 'apidoc':
          this.$router.push({
            path: '/api-doc',
            query: { datasource: this.currentDatasource, table: row.tableName }
          })
          break
        case 'test':
          this.$router.push({
            path: '/test',
            query: { datasource: this.currentDatasource, table: row.tableName }
          })
          break
        case 'copy':
          this.copyTableName(row.tableName)
          break
        case 'ddl':
          this.viewDDL(row)
          break
        case 'delete':
          this.deleteTable(row)
          break
      }
    },
    
    viewTableInfo(row) {
      this.currentTable = row.tableName
      this.currentTableInfo = row
      this.schemaDialogVisible = true
    },
    
    viewApiDoc(row) {
      this.$router.push({
        path: '/api-doc',
        query: { datasource: this.currentDatasource, table: row.tableName }
      })
    },
    
    viewDDL(row) {
      // 简单生成DDL
      let ddl = '-- 表结构: ' + row.tableName + '\n\n'
      ddl += 'CREATE TABLE ' + row.tableName + ' ('
      
      const columnDefs = []
      row.columns.forEach(col => {
        let def = '  ' + col.name + ' ' + col.type
        if (!col.nullable) {
          def += ' NOT NULL'
        }
        if (col.autoIncrement) {
          def += ' AUTO_INCREMENT'
        }
        columnDefs.push(def)
      })
      
      if (row.primaryKeyColumn) {
        columnDefs.push('  PRIMARY KEY (' + row.primaryKeyColumn + ')')
      }
      
      ddl += columnDefs.join(',')
      ddl += '\n);\n'
      
      if (row.comment) {
        ddl += "\nCOMMENT ON TABLE " + row.tableName + " IS '" + row.comment + "';\n"
      }
      
      this.currentDDL = ddl
      this.ddlDialogVisible = true
    },
    
    copyTableName(name) {
      if (navigator.clipboard) {
        navigator.clipboard.writeText(name)
      } else {
        const input = document.createElement('input')
        input.value = name
        document.body.appendChild(input)
        input.select()
        document.execCommand('copy')
        document.body.removeChild(input)
      }
      this.$message.success('已复制表名: ' + name)
    },
    
    copyDDL() {
      if (navigator.clipboard) {
        navigator.clipboard.writeText(this.currentDDL)
      } else {
        const textarea = document.createElement('textarea')
        textarea.value = this.currentDDL
        document.body.appendChild(textarea)
        textarea.select()
        document.execCommand('copy')
        document.body.removeChild(textarea)
      }
      this.$message.success('已复制DDL')
    },
    
    async deleteTable(row) {
      try {
        await this.$confirm('确定要删除表 "' + row.tableName + '" 吗？此操作不可恢复！', '警告', {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const sql = 'DROP TABLE ' + row.tableName
        const res = await api.executeSql(this.currentDatasource, sql)
        if (res.success) {
          this.$message.success('删除成功')
          await this.refreshTables()
        } else {
          this.$message.error(res.message || '删除失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error('删除失败: ' + e.message)
        }
      }
    },
    
    goToCreateTable() {
      this.$router.push({ path: '/create-table', query: { datasource: this.currentDatasource } })
    },

    goToErDiagram() {
      if (!this.currentDatasource) {
        this.$message.warning('请先选择数据源')
        return
      }
      const query = { datasource: this.currentDatasource }
      if (this.currentSchema) {
        query.schema = this.currentSchema
      }
      this.$router.push({ path: '/er-diagram', query })
    }
  }
}
</script>

<style scoped>
.tables-page {
  height: 100%;
  background: #f5f7fa;
}

.page-container {
  display: flex;
  height: calc(100vh - 60px);
}

.sidebar {
  width: 260px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
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

.table-count {
  margin-left: 4px;
  color: #909399;
  font-size: 12px;
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

.toolbar-left .current-ds {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.toolbar-right {
  display: flex;
  gap: 8px;
}

.table-container {
  flex: 1;
  padding: 16px;
  overflow: auto;
}

.filter-bar {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.table-stats {
  margin-left: 12px;
  color: #909399;
  font-size: 13px;
}

.data-table {
  width: 100%;
}

.schema-dialog .el-dialog__body {
  padding: 20px;
}

.ddl-editor textarea {
  font-family: 'Consolas', monospace;
  font-size: 13px;
}
</style>
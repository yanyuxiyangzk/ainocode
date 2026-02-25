<template>
  <div class="er-diagram-page">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button icon="el-icon-back" size="small" @click="goBack">返回</el-button>
        <span class="title">数据模型</span>
        <el-select v-model="currentSchema" placeholder="选择模式" size="small" @change="onSchemaChange" v-if="schemas.length > 0" style="margin-left: 12px; width: 150px;">
          <el-option v-for="schema in schemas" :key="schema" :label="schema" :value="schema"></el-option>
        </el-select>
      </div>
      <div class="toolbar-center">
        <el-button-group>
          <el-button size="mini" @click="zoomIn" title="放大"><i class="el-icon-zoom-in"></i></el-button>
          <el-button size="mini" @click="zoomOut" title="缩小"><i class="el-icon-zoom-out"></i></el-button>
          <el-button size="mini" @click="fitView" title="适应画布"><i class="el-icon-full-screen"></i></el-button>
          <el-button size="mini" @click="resetView" title="重置视图"><i class="el-icon-refresh"></i></el-button>
        </el-button-group>
        <el-button-group style="margin-left: 10px;">
          <el-button size="mini" :type="layoutType === 'dagre' ? 'primary' : ''" @click="changeLayout('dagre')">层次布局</el-button>
          <el-button size="mini" :type="layoutType === 'force' ? 'primary' : ''" @click="changeLayout('force')">力导向</el-button>
          <el-button size="mini" :type="layoutType === 'grid' ? 'primary' : ''" @click="changeLayout('grid')">网格布局</el-button>
        </el-button-group>
      </div>
      <div class="toolbar-right">
        <el-tag type="info" size="small">表: {{ tableCount }}</el-tag>
        <el-tag type="success" size="small" style="margin-left: 8px;">关系: {{ relationshipCount }}</el-tag>
        <el-button size="small" icon="el-icon-refresh" @click="loadData" :loading="loading" style="margin-left: 12px;">刷新</el-button>
        <el-button size="small" icon="el-icon-download" @click="exportImage">导出图片</el-button>
        <el-button size="small" type="primary" icon="el-icon-save" @click="saveLayout" :loading="saving">保存布局</el-button>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 左侧表列表 -->
      <div class="sidebar" :class="{ collapsed: sidebarCollapsed }">
        <div class="sidebar-header">
          <span v-if="!sidebarCollapsed">表列表</span>
          <el-button type="text" size="mini" @click="sidebarCollapsed = !sidebarCollapsed">
            <i :class="sidebarCollapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'"></i>
          </el-button>
        </div>
        <div class="sidebar-content" v-if="!sidebarCollapsed">
          <el-input v-model="searchKeyword" placeholder="搜索表名" size="mini" prefix-icon="el-icon-search" clearable style="margin-bottom: 10px;"></el-input>
          <div class="table-list">
            <div
              v-for="table in filteredTableList"
              :key="table.id"
              class="table-item"
              :class="{ active: selectedTable && selectedTable.id === table.id }"
              @click="focusTable(table)"
            >
              <i class="el-icon-document"></i>
              <span>{{ table.label }}</span>
              <el-tag size="mini" type="info">{{ table.columns ? table.columns.length : 0 }}</el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- G6 画布容器 -->
      <div class="canvas-wrapper">
        <div id="er-graph-container" ref="graphContainer"></div>
        <!-- 小地图 -->
        <div id="minimap-container" ref="minimapContainer"></div>
      </div>

      <!-- 右侧详情面板 -->
      <div class="detail-panel" v-if="selectedTable">
        <div class="panel-header">
          <span>{{ selectedTable.label }}</span>
          <el-button type="text" size="mini" @click="selectedTable = null"><i class="el-icon-close"></i></el-button>
        </div>
        <div class="panel-content">
          <div class="info-section">
            <div class="section-title">基本信息</div>
            <el-descriptions :column="1" border size="mini">
              <el-descriptions-item label="表名">{{ selectedTable.label }}</el-descriptions-item>
              <el-descriptions-item label="注释">{{ selectedTable.comment || '-' }}</el-descriptions-item>
              <el-descriptions-item label="字段数">{{ selectedTable.columns ? selectedTable.columns.length : 0 }}</el-descriptions-item>
            </el-descriptions>
          </div>
          <div class="info-section">
            <div class="section-title">字段列表</div>
            <el-table :data="selectedTable.columns || []" size="mini" max-height="300">
              <el-table-column prop="name" label="字段名" width="120">
                <template slot-scope="{row}">
                  <span :class="{ 'pk-field': row.primaryKey }">{{ row.name }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" width="100"></el-table-column>
              <el-table-column label="主键" width="50" align="center">
                <template slot-scope="{row}">
                  <el-tag v-if="row.primaryKey" type="danger" size="mini">PK</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="可空" width="50" align="center">
                <template slot-scope="{row}">
                  <span v-if="!row.nullable" style="color: #f56c6c;">N</span>
                  <span v-else style="color: #67c23a;">Y</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="info-section" v-if="tableRelationships.length > 0">
            <div class="section-title">关联关系</div>
            <div class="relationship-list">
              <div v-for="rel in tableRelationships" :key="rel.id" class="relationship-item">
                <el-tag size="mini">{{ rel.relationType }}</el-tag>
                <span>{{ rel.sourceTable }} → {{ rel.targetTable }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as G6 from '@antv/g6'
import api from '../api'

export default {
  name: 'ErDiagram',
  data() {
    return {
      loading: false,
      saving: false,
      datasource: '',
      currentSchema: '',
      schemas: [],
      graph: null,
      tableCount: 0,
      relationshipCount: 0,
      tableList: [],
      searchKeyword: '',
      selectedTable: null,
      sidebarCollapsed: false,
      layoutType: 'dagre',
      layoutConfig: {
        dagre: {
          type: 'dagre',
          rankdir: 'LR',
          nodesep: 80,
          ranksep: 150,
          preventOverlap: true,
          nodeSize: [260, 300]
        },
        force: {
          type: 'force',
          preventOverlap: true,
          linkDistance: 200,
          nodeStrength: 300,
          edgeStrength: 100,
          collide: {
            radius: 150
          }
        },
        grid: {
          type: 'grid',
          begin: [0, 0],
          width: 260,
          height: 300
        }
      }
    }
  },
  computed: {
    filteredTableList() {
      if (!this.searchKeyword) return this.tableList
      const keyword = this.searchKeyword.toLowerCase()
      return this.tableList.filter(t => t.label.toLowerCase().includes(keyword))
    },
    tableRelationships() {
      if (!this.selectedTable) return []
      const tableName = this.selectedTable.label
      return this.graph ? this.graph.getEdges()
        .filter(edge => {
          const model = edge.getModel()
          return model.source === tableName || model.target === tableName
        })
        .map(edge => {
          const model = edge.getModel()
          return {
            id: model.id,
            sourceTable: model.source,
            targetTable: model.target,
            relationType: model.source === tableName ? '外键引用' : '被引用'
          }
        }) : []
    }
  },
  async mounted() {
    this.datasource = this.$route.query.datasource || ''
    this.currentSchema = this.$route.query.schema || ''
    
    await this.loadSchemas()
    await this.loadData()
    await this.loadSavedLayout()
    
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    if (this.graph) {
      this.graph.destroy()
    }
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    async loadSchemas() {
      try {
        this.schemas = await api.getSchemas(this.datasource)
        // 如果传入了 schema，确保它在列表中
        if (this.currentSchema && this.schemas.length > 0) {
          // 检查当前 schema 是否在列表中
          if (!this.schemas.includes(this.currentSchema)) {
            // 如果不在列表中，可能是大小写问题，尝试查找匹配的
            const found = this.schemas.find(s => s.toLowerCase() === this.currentSchema.toLowerCase())
            if (found) {
              this.currentSchema = found
            }
          }
        } else if (this.schemas.length > 0 && !this.currentSchema) {
          this.currentSchema = this.schemas.includes('public') ? 'public' : this.schemas[0]
        }
      } catch (e) {
        console.log('该数据源不支持schema')
      }
    },
    
    async loadData() {
      this.loading = true
      try {
        const data = await api.getErDiagram(this.datasource, this.currentSchema)
        this.tableCount = data.tables ? data.tables.length : 0
        this.relationshipCount = data.relationships ? data.relationships.length : 0
        
        if (this.tableCount === 0) {
          this.$message.warning('该模式下没有找到表')
        }
        
        this.initGraph(data)
        this.buildTableList(data.tables)
      } catch (e) {
        this.$message.error('加载数据失败: ' + e.message)
      } finally {
        this.loading = false
      }
    },
    
    initGraph(data) {
      const container = this.$refs.graphContainer
      const width = container.offsetWidth
      const height = container.offsetHeight
      
      // 如果已存在实例，先销毁
      if (this.graph) {
        this.graph.destroy()
      }
      
      // 注册自定义节点
      this.registerNode()
      
      // 构建节点和边数据
      const nodes = data.tables.map(table => ({
        id: table.tableName,
        label: table.tableName,
        comment: table.comment,
        columns: table.columns || [],
        type: 'table-node'
      }))
      
      const edges = data.relationships.map((rel, index) => ({
        id: 'edge-' + index,
        source: rel.tableName,
        target: rel.referencedTableName,
        sourceColumn: rel.columnName,
        targetColumn: rel.referencedColumnName,
        constraintName: rel.constraintName,
        type: 'polyline-edge'
      }))
      
      // 创建 G6 实例
      this.graph = new G6.Graph({
        container: 'er-graph-container',
        width,
        height,
        modes: {
          default: [
            'drag-canvas',
            'zoom-canvas',
            'drag-node',
            'click-select'
          ]
        },
        layout: this.layoutConfig[this.layoutType],
        defaultNode: {
          type: 'table-node'
        },
        defaultEdge: {
          type: 'polyline-edge',
          style: {
            stroke: '#A3B1BF',
            lineWidth: 2,
            endArrow: {
              path: G6.Arrow.triangle(8, 10, 0),
              fill: '#A3B1BF'
            }
          }
        },
        nodeStateStyles: {
          selected: {
            stroke: '#409EFF',
            lineWidth: 3,
            shadowBlur: 10,
            shadowColor: '#409EFF'
          },
          hover: {
            stroke: '#409EFF',
            lineWidth: 2
          }
        },
        edgeStateStyles: {
          hover: {
            stroke: '#409EFF',
            lineWidth: 3
          }
        }
      })
      
      // 添加小地图
      const minimap = new G6.Minimap({
        container: 'minimap-container',
        width: 150,
        height: 100
      })
      this.graph.addPlugin(minimap)
      
      // 绑定事件
      this.bindGraphEvents()
      
      // 渲染数据
      this.graph.data({ nodes, edges })
      this.graph.render()
      
      // 适应画布
      setTimeout(() => {
        this.graph.fitView(20)
      }, 100)
    },
    
    registerNode() {
      const self = this
      
      G6.registerNode('table-node', {
        draw(cfg, group) {
          const padding = 10
          const headerHeight = 32
          const rowHeight = 24
          const maxRows = 8
          const width = 240
          
          const columns = cfg.columns || []
          const displayColumns = columns.slice(0, maxRows)
          const hasMore = columns.length > maxRows
          
          const bodyHeight = displayColumns.length * rowHeight + (hasMore ? rowHeight : 0)
          const totalHeight = headerHeight + bodyHeight + padding * 2
          
          // 主容器
          const keyShape = group.addShape('rect', {
            attrs: {
              x: 0,
              y: 0,
              width,
              height: totalHeight,
              radius: 6,
              fill: '#fff',
              stroke: '#409EFF',
              lineWidth: 2,
              cursor: 'move',
              shadowBlur: 5,
              shadowColor: 'rgba(0,0,0,0.1)'
            },
            name: 'main-box'
          })
          
          // 表头背景
          group.addShape('rect', {
            attrs: {
              x: 0,
              y: 0,
              width,
              height: headerHeight,
              radius: [6, 6, 0, 0],
              fill: '#409EFF',
              cursor: 'move'
            },
            name: 'header-bg'
          })
          
          // 表名
          group.addShape('text', {
            attrs: {
              x: width / 2,
              y: headerHeight / 2 + 1,
              text: cfg.label,
              fill: '#fff',
              fontSize: 14,
              fontWeight: 'bold',
              textAlign: 'center',
              textBaseline: 'middle',
              cursor: 'move'
            },
            name: 'table-name'
          })
          
          // 字段列表
          displayColumns.forEach((col, idx) => {
            const y = headerHeight + padding + idx * rowHeight + 14
            
            // 字段名
            const prefix = col.primaryKey ? '🔑 ' : ''
            group.addShape('text', {
              attrs: {
                x: padding + 5,
                y,
                text: prefix + col.name,
                fill: col.primaryKey ? '#f56c6c' : '#303133',
                fontSize: 12,
                fontFamily: 'Consolas, Monaco, monospace',
                cursor: 'pointer'
              },
              name: `col-${idx}`,
              columnName: col.name
            })
            
            // 类型
            group.addShape('text', {
              attrs: {
                x: width - padding - 5,
                y,
                text: col.type,
                fill: '#909399',
                fontSize: 11,
                fontFamily: 'Consolas, Monaco, monospace',
                textAlign: 'right',
                cursor: 'pointer'
              },
              name: `col-type-${idx}`
            })
          })
          
          // 更多字段提示
          if (hasMore) {
            group.addShape('text', {
              attrs: {
                x: padding + 5,
                y: headerHeight + padding + maxRows * rowHeight + 15,
                text: `... 共 ${columns.length} 个字段`,
                fill: '#909399',
                fontSize: 11,
                cursor: 'pointer'
              },
              name: 'more-fields'
            })
          }
          
          return keyShape
        },
        
        getAnchorPoints() {
          return [
            [0.5, 0],
            [1, 0.5],
            [0.5, 1],
            [0, 0.5]
          ]
        }
      })
      
      // 注册边
      G6.registerEdge('polyline-edge', {
        draw(cfg, group) {
          const startPoint = cfg.startPoint
          const endPoint = cfg.endPoint
          
          const stroke = cfg.style?.stroke || '#A3B1BF'
          const lineWidth = cfg.style?.lineWidth || 2
          
          const path = [
            ['M', startPoint.x, startPoint.y],
            ['L', endPoint.x, endPoint.y]
          ]
          
          const keyShape = group.addShape('path', {
            attrs: {
              path,
              stroke,
              lineWidth,
              endArrow: {
                path: G6.Arrow.triangle(8, 10, 0),
                fill: stroke
              }
            },
            name: 'edge-path'
          })
          
          return keyShape
        }
      })
    },
    
    bindGraphEvents() {
      // 节点点击
      this.graph.on('node:click', (evt) => {
        const model = evt.item.getModel()
        this.selectedTable = model
        this.graph.getNodes().forEach(node => {
          this.graph.setItemState(node, 'selected', node.getModel().id === model.id)
        })
      })
      
      // 节点悬停
      this.graph.on('node:mouseenter', (evt) => {
        this.graph.setItemState(evt.item, 'hover', true)
      })
      
      this.graph.on('node:mouseleave', (evt) => {
        this.graph.setItemState(evt.item, 'hover', false)
      })
      
      // 边悬停
      this.graph.on('edge:mouseenter', (evt) => {
        this.graph.setItemState(evt.item, 'hover', true)
      })
      
      this.graph.on('edge:mouseleave', (evt) => {
        this.graph.setItemState(evt.item, 'hover', false)
      })
      
      // 画布点击
      this.graph.on('canvas:click', () => {
        this.selectedTable = null
        this.graph.getNodes().forEach(node => {
          this.graph.setItemState(node, 'selected', false)
        })
      })
    },
    
    buildTableList(tables) {
      this.tableList = tables.map(t => ({
        id: t.tableName,
        label: t.tableName,
        comment: t.comment,
        columns: t.columns
      }))
    },
    
    focusTable(table) {
      if (!this.graph) return
      
      const node = this.graph.findById(table.id)
      if (node) {
        this.graph.focusItem(node, true, { duration: 300 })
        this.selectedTable = table
        this.graph.setItemState(node, 'selected', true)
      }
    },
    
    zoomIn() {
      if (this.graph) {
        const zoom = this.graph.getZoom()
        this.graph.zoomTo(zoom * 1.2, { x: this.graph.getWidth() / 2, y: this.graph.getHeight() / 2 })
      }
    },
    
    zoomOut() {
      if (this.graph) {
        const zoom = this.graph.getZoom()
        this.graph.zoomTo(zoom / 1.2, { x: this.graph.getWidth() / 2, y: this.graph.getHeight() / 2 })
      }
    },
    
    fitView() {
      if (this.graph) {
        this.graph.fitView(20)
      }
    },
    
    resetView() {
      if (this.graph) {
        this.graph.changeLayout(this.layoutConfig[this.layoutType])
        this.graph.fitView(20)
      }
    },
    
    changeLayout(type) {
      this.layoutType = type
      if (this.graph) {
        this.graph.changeLayout(this.layoutConfig[type])
        if (type !== 'grid') {
          this.graph.layout()
        }
        this.graph.fitView(20)
      }
    },
    
    async saveLayout() {
      if (!this.graph) return
      
      this.saving = true
      try {
        const nodes = this.graph.getNodes().map(node => {
          const model = node.getModel()
          return {
            id: model.id,
            x: model.x,
            y: model.y
          }
        })
        
        const zoom = this.graph.getZoom()
        const center = this.graph.getCenter()
        
        await api.saveDiagramLayout(this.datasource, {
          schema: this.currentSchema,
          layoutData: { nodes },
          viewConfig: { zoom, center }
        })
        
        this.$message.success('布局保存成功')
      } catch (e) {
        this.$message.error('保存失败: ' + e.message)
      } finally {
        this.saving = false
      }
    },
    
    async loadSavedLayout() {
      try {
        const layout = await api.getDiagramLayout(this.datasource, this.currentSchema)
        if (layout && layout.layoutData && this.graph) {
          const nodes = this.graph.getNodes()
          layout.layoutData.nodes.forEach(pos => {
            const node = this.graph.findById(pos.id)
            if (node) {
              this.graph.updateItem(node, { x: pos.x, y: pos.y })
            }
          })
          
          if (layout.viewConfig) {
            if (layout.viewConfig.zoom) {
              this.graph.zoomTo(layout.viewConfig.zoom)
            }
          }
        }
      } catch (e) {
        console.log('未找到保存的布局')
      }
    },
    
    exportImage() {
      if (!this.graph) return
      
      this.graph.downloadFullImage('er-diagram', 'image/png', {
        backgroundColor: '#fff'
      })
    },
    
    onSchemaChange() {
      this.loadData()
      this.loadSavedLayout()
    },
    
    handleResize() {
      if (this.graph) {
        const container = this.$refs.graphContainer
        this.graph.changeSize(container.offsetWidth, container.offsetHeight)
      }
    },
    
    goBack() {
      const query = { datasource: this.datasource }
      if (this.currentSchema) {
        query.schema = this.currentSchema
      }
      this.$router.push({ path: '/tables', query })
    }
  }
}
</script>

<style scoped>
.er-diagram-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
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
}

.toolbar-left .title {
  font-size: 16px;
  font-weight: 500;
  margin-left: 12px;
  color: #303133;
}

.toolbar-center {
  display: flex;
  align-items: center;
}

.toolbar-right {
  display: flex;
  align-items: center;
}

.main-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.sidebar {
  width: 240px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
}

.sidebar.collapsed {
  width: 40px;
}

.sidebar-header {
  padding: 12px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-content {
  flex: 1;
  padding: 12px;
  overflow: auto;
}

.table-list {
  max-height: calc(100vh - 250px);
  overflow: auto;
}

.table-item {
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  transition: background 0.2s;
}

.table-item:hover {
  background: #ecf5ff;
}

.table-item.active {
  background: #409EFF;
  color: #fff;
}

.table-item i {
  margin-right: 8px;
}

.table-item span {
  flex: 1;
}

.canvas-wrapper {
  flex: 1;
  position: relative;
  overflow: hidden;
}

#er-graph-container {
  width: 100%;
  height: 100%;
}

#minimap-container {
  position: absolute;
  right: 10px;
  bottom: 10px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.detail-panel {
  width: 300px;
  background: #fff;
  border-left: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.panel-header {
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
}

.panel-content {
  flex: 1;
  overflow: auto;
  padding: 12px;
}

.info-section {
  margin-bottom: 16px;
}

.section-title {
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #606266;
}

.pk-field {
  color: #f56c6c;
  font-weight: 500;
}

.relationship-list {
  max-height: 150px;
  overflow: auto;
}

.relationship-item {
  padding: 8px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
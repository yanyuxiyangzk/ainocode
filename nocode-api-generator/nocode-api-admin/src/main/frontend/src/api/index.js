import axios from 'axios'

const request = axios.create({
  baseURL: '/nocode-admin/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 响应拦截器
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

export default {
  // 系统信息
  getSystemInfo() {
    return request.get('/admin/system/info')
  },
  // 数据源管理
  getDatasources() {
    return request.get('/admin/datasources')
  },
  addDatasource(data) {
    return request.post('/admin/datasources', data)
  },
  deleteDatasource(name) {
    return request.delete('/admin/datasources/' + name)
  },
  refreshDatasource(name) {
    return request.post('/admin/datasources/' + name + '/refresh')
  },
  // 表管理
  getTables(datasource) {
    return request.get('/admin/datasources/' + datasource + '/tables')
  },
  // 模式管理
  getSchemas(datasource) {
    return request.get('/admin/datasources/' + datasource + '/schemas')
  },
  getTablesBySchema(datasource, schema) {
    return request.get('/admin/datasources/' + datasource + '/schemas/' + schema + '/tables')
  },
  getTableInfo(datasource, table, schema) {
    const params = schema ? { schema } : {}
    return request.get('/admin/datasources/' + datasource + '/tables/' + table, { params })
  },
  // 创建表
  executeSql(datasource, sql) {
    return request.post('/admin/datasources/' + datasource + '/execute', { sql })
  },
  // API文档
  getApiDoc(datasource, table) {
    return request.get('/admin/datasources/' + datasource + '/tables/' + table + '/api-doc')
  },
  // API测试 - 调用真实的 API 端点
  testQueryList(datasource, table, params) {
    return request.get('/' + datasource + '/' + table, { params })
  },
  testQueryOne(datasource, table, id) {
    return request.get('/' + datasource + '/' + table + '/' + id)
  },
  testInsert(datasource, table, data) {
    return request.post('/' + datasource + '/' + table, data)
  },
  testUpdate(datasource, table, id, data) {
    return request.put('/' + datasource + '/' + table + '/' + id, data)
  },
  testDelete(datasource, table, id) {
    return request.delete('/' + datasource + '/' + table + '/' + id)
  },
  testBatchDelete(datasource, table, ids) {
    return request.delete('/' + datasource + '/' + table, { data: ids })
  },
  testBatchInsert(datasource, table, dataList) {
    return request.post('/' + datasource + '/' + table + '/batch', dataList)
  },
  testCustomQuery(datasource, table, data) {
    return request.post('/' + datasource + '/' + table + '/query', data)
  },
  // ER图数据
  getErDiagram(datasource, schema) {
    const params = schema ? { schema } : {}
    return request.get('/admin/datasources/' + datasource + '/er-diagram', { params })
  },
  // ER图布局
  saveDiagramLayout(datasource, data) {
    return request.post('/admin/datasources/' + datasource + '/diagram-layout', data)
  },
  getDiagramLayout(datasource, schema) {
    const params = schema ? { schema } : {}
    return request.get('/admin/datasources/' + datasource + '/diagram-layout', { params })
  },
  deleteDiagramLayout(datasource, schema) {
    const params = schema ? { schema } : {}
    return request.delete('/admin/datasources/' + datasource + '/diagram-layout', { params })
  }
}

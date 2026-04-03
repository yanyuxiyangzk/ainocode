/**
 * NoCode API - RuoYi Vue 2 调用模块
 *
 * 使用方式：
 *
 * 方式一：通用函数（传入数据源和表名）
 *   import { list, get, add, update, del, batchDelete, customQuery } from '@/api/nocode-api'
 *
 *   // 查询列表
 *   list('business', 'des_chuhuodan9_1', { page: 1, size: 10, schema: 'hrp_base' }).then(res => { ... })
 *
 *   // 查询单条
 *   get('business', 'des_chuhuodan9_1', id, { schema: 'hrp_base' }).then(res => { ... })
 *
 *   // 新增
 *   add('business', 'des_chuhuodan9_1', data, { schema: 'hrp_base' }).then(res => { ... })
 *
 *   // 更新
 *   update('business', 'des_chuhuodan9_1', id, data, { schema: 'hrp_base' }).then(res => { ... })
 *
 *   // 删除
 *   del('business', 'des_chuhuodan9_1', id, { schema: 'hrp_base' }).then(res => { ... })
 *
 * 方式二：为特定表创建快捷函数
 *   import { createTableApi } from '@/api/nocode-api'
 *
 *   const chuhuoApi = createTableApi('business', 'des_chuhuodan9_1', 'hrp_base')
 *   chuhuoApi.list({ page: 1, size: 10 }).then(res => { ... })
 *   chuhuoApi.get(id).then(res => { ... })
 *
 * 方式三：自定义快捷函数（参考下方示例）
 *   取消注释下面的代码，修改数据源和表名后使用
 */

import axios from 'axios'

// 创建请求实例
// baseURL 需要与 nocode-api-starter 配置的 context-path 保持一致
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
    console.error('NoCode API 请求错误:', error)
    return Promise.reject(error)
  }
)

// ================================ 动态 API 调用 ================================

/**
 * GET 请求 - 查询列表（支持分页+过滤）
 * @param {string} datasource 数据源名称
 * @param {string} table 表名
 * @param {object} query 查询参数 { page, size, ...filters }
 * @param {string} schema 可选的schema
 */
export function list(datasource, table, query, schema) {
  const params = { ...query }
  if (schema) {
    // schema 可以作为 query 对象的属性，也可以作为单独参数传入
    params.schema = schema
  }
  return request({
    url: `/${datasource}/${table}`,
    method: 'get',
    params
  })
}

/**
 * GET 请求 - 查询单条
 * @param {string} datasource 数据源名称
 * @param {string} table 表名
 * @param {string|number} id 主键ID
 * @param {string} schema 可选的schema
 */
export function get(datasource, table, id, schema) {
  const params = {}
  if (schema) params.schema = schema
  return request({
    url: `/${datasource}/${table}/${id}`,
    method: 'get',
    params
  })
}

/**
 * POST 请求 - 新增记录
 * @param {string} datasource 数据源名称
 * @param {string} table 表名
 * @param {object} data 记录数据
 * @param {string} schema 可选的schema
 */
export function add(datasource, table, data, schema) {
  const params = {}
  if (schema) params.schema = schema
  return request({
    url: `/${datasource}/${table}`,
    method: 'post',
    params,
    data
  })
}

/**
 * PUT 请求 - 更新记录
 * @param {string} datasource 数据源名称
 * @param {string} table 表名
 * @param {string|number} id 主键ID
 * @param {object} data 更新数据
 * @param {string} schema 可选的schema
 */
export function update(datasource, table, id, data, schema) {
  const params = {}
  if (schema) params.schema = schema
  return request({
    url: `/${datasource}/${table}/${id}`,
    method: 'put',
    params,
    data
  })
}

/**
 * DELETE 请求 - 删除单条
 * @param {string} datasource 数据源名称
 * @param {string} table 表名
 * @param {string|number} id 主键ID
 * @param {string} schema 可选的schema
 */
export function del(datasource, table, id, schema) {
  const params = {}
  if (schema) params.schema = schema
  return request({
    url: `/${datasource}/${table}/${id}`,
    method: 'delete',
    params
  })
}

/**
 * DELETE 请求 - 批量删除
 * @param {string} datasource 数据源名称
 * @param {string} table 表名
 * @param {array} ids ID数组
 * @param {string} schema 可选的schema
 */
export function batchDelete(datasource, table, ids, schema) {
  const params = {}
  if (schema) params.schema = schema
  return request({
    url: `/${datasource}/${table}`,
    method: 'delete',
    params,
    data: ids
  })
}

/**
 * POST 请求 - 批量新增
 * @param {string} datasource 数据源名称
 * @param {string} table 表名
 * @param {array} dataList 数据数组
 * @param {string} schema 可选的schema
 */
export function batchAdd(datasource, table, dataList, schema) {
  const params = {}
  if (schema) params.schema = schema
  return request({
    url: `/${datasource}/${table}/batch`,
    method: 'post',
    params,
    data: dataList
  })
}

/**
 * POST 请求 - 自定义查询
 * @param {string} datasource 数据源名称
 * @param {string} table 表名
 * @param {object} query 查询条件 { where, orderBy, select, ... }
 */
export function customQuery(datasource, table, query) {
  return request({
    url: `/${datasource}/${table}/query`,
    method: 'post',
    data: query
  })
}

/**
 * GET 请求 - 获取表结构
 * @param {string} datasource 数据源名称
 * @param {string} table 表名
 * @param {string} schema 可选的schema
 */
export function getSchema(datasource, table, schema) {
  const params = {}
  if (schema) params.schema = schema
  return request({
    url: `/${datasource}/${table}/schema`,
    method: 'get',
    params
  })
}

// ================================ 便捷快捷函数 ================================

/**
 * 创建针对特定表/数据源的快捷函数
 * 用法:
 *   const api = createTableApi('business', 'des_chuhuodan9_1', 'hrp_base')
 *   api.list({ page: 1, size: 10 })
 *   api.get(id)
 *   api.add(data)
 *
 * @param {string} datasource 数据源名称
 * @param {string} table 表名
 * @param {string} schema 可选的schema
 */
export function createTableApi(datasource, table, schema) {
  return {
    list: (query) => list(datasource, table, query, schema),
    get: (id) => get(datasource, table, id, schema),
    add: (data) => add(datasource, table, data, schema),
    update: (id, data) => update(datasource, table, id, data, schema),
    del: (id) => del(datasource, table, id, schema),
    batchDelete: (ids) => batchDelete(datasource, table, ids, schema),
    batchAdd: (dataList) => batchAdd(datasource, table, dataList, schema),
    customQuery: (query) => customQuery(datasource, table, query),
    getSchema: () => getSchema(datasource, table, schema)
  }
}

// ================================ 示例 ================================

// 示例：创建针对发货单表的快捷函数
// const chuhuoApi = createTableApi('business', 'des_chuhuodan9_1', 'hrp_base')
// export const listChuhuo = chuhuoApi.list
// export const getChuhuo = chuhuoApi.get
// export const addChuhuo = chuhuoApi.add
// export const updateChuhuo = chuhuoApi.update
// export const delChuhuo = chuhuoApi.del

export default {
  list,
  get,
  add,
  update,
  del,
  batchDelete,
  batchAdd,
  customQuery,
  getSchema,
  createTableApi
}

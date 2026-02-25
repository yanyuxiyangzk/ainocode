import request from '@/utils/request'
import { AxiosPromise } from 'axios'

// 表管理 API

interface TableColumn {
  columnName: string
  columnComment: string
  columnType: string
  isPk: string
  isRequired: string
  javaType: string
  javaField: string
}

interface TableInfo {
  tableId: number
  tableName: string
  tableComment: string
  className: string
  columns: TableColumn[]
  createTime: string
}

// 获取表列表
export const listTable = (query: any): AxiosPromise<{ rows: TableInfo[]; total: number }> => {
  return request({
    url: '/nocode/table/list',
    method: 'get',
    params: query
  })
}

// 获取表详情
export const getTable = (tableId: number): AxiosPromise<TableInfo> => {
  return request({
    url: `/nocode/table/${tableId}`,
    method: 'get'
  })
}

// 创建表
export const createTable = (data: TableInfo): AxiosPromise<void> => {
  return request({
    url: '/nocode/table',
    method: 'post',
    data
  })
}

// 更新表
export const updateTable = (data: TableInfo): AxiosPromise<void> => {
  return request({
    url: '/nocode/table',
    method: 'put',
    data
  })
}

// 删除表
export const deleteTable = (tableId: number): AxiosPromise<void> => {
  return request({
    url: `/nocode/table/${tableId}`,
    method: 'delete'
  })
}

// 同步表结构
export const syncTable = (tableId: number): AxiosPromise<void> => {
  return request({
    url: `/nocode/table/sync/${tableId}`,
    method: 'post'
  })
}

// 生成代码
export const generateCode = (tableId: number): AxiosPromise<void> => {
  return request({
    url: `/nocode/table/generate/${tableId}`,
    method: 'post'
  })
}

import request from '@/utils/request'
import { AxiosPromise } from 'axios'

// 实体配置 API

interface EntityField {
  fieldId: number
  fieldName: string
  fieldType: string
  fieldComment: string
  isRequired: string
  isUnique: string
  defaultValue: string
  validateRule: string
  sortOrder: number
}

interface EntityConfig {
  entityId: number
  entityName: string
  entityComment: string
  tableName: string
  fields: EntityField[]
  status: string
  createTime: string
}

// 获取实体列表
export const listEntity = (query: any): AxiosPromise<{ rows: EntityConfig[]; total: number }> => {
  return request({
    url: '/nocode/entity/list',
    method: 'get',
    params: query
  })
}

// 获取实体详情
export const getEntity = (entityId: number): AxiosPromise<EntityConfig> => {
  return request({
    url: `/nocode/entity/${entityId}`,
    method: 'get'
  })
}

// 创建实体
export const createEntity = (data: EntityConfig): AxiosPromise<void> => {
  return request({
    url: '/nocode/entity',
    method: 'post',
    data
  })
}

// 更新实体
export const updateEntity = (data: EntityConfig): AxiosPromise<void> => {
  return request({
    url: '/nocode/entity',
    method: 'put',
    data
  })
}

// 删除实体
export const deleteEntity = (entityId: number): AxiosPromise<void> => {
  return request({
    url: `/nocode/entity/${entityId}`,
    method: 'delete'
  })
}

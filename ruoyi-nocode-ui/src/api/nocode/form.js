import request from '@/utils/request'

// Form Config API
export function listFormConfig(query) {
  return request({
    url: '/api/form/list',
    method: 'get',
    params: query
  })
}

export function getFormConfig(id) {
  return request({
    url: `/api/form/${id}`,
    method: 'get'
  })
}

export function createFormConfig(data) {
  return request({
    url: '/api/form',
    method: 'post',
    data
  })
}

export function updateFormConfig(id, data) {
  return request({
    url: `/api/form/${id}`,
    method: 'put',
    data
  })
}

export function deleteFormConfig(id) {
  return request({
    url: `/api/form/${id}`,
    method: 'delete'
  })
}

export function publishFormConfig(id) {
  return request({
    url: `/api/form/${id}/publish`,
    method: 'post'
  })
}

export function searchFormConfig(name) {
  return request({
    url: '/api/form/search',
    method: 'get',
    params: { name }
  })
}

export function listFormConfigByStatus(status) {
  return request({
    url: `/api/form/list/status/${status}`,
    method: 'get'
  })
}

// Form Component API
export function listFormComponent(query) {
  return request({
    url: '/api/form-component/list',
    method: 'get',
    params: query
  })
}

export function getFormComponent(id) {
  return request({
    url: `/api/form-component/${id}`,
    method: 'get'
  })
}

export function createFormComponent(data) {
  return request({
    url: '/api/form-component',
    method: 'post',
    data
  })
}

export function updateFormComponent(id, data) {
  return request({
    url: `/api/form-component/${id}`,
    method: 'put',
    data
  })
}

export function deleteFormComponent(id) {
  return request({
    url: `/api/form-component/${id}`,
    method: 'delete'
  })
}

export function getComponentTypes() {
  return request({
    url: '/api/form-component/types',
    method: 'get'
  })
}

// Form Data Operations - for form renderer
export function getFormData(datasource, table, id) {
  return request({
    url: `/api/${datasource}/${table}/${id}`,
    method: 'get'
  })
}

export function listFormData(datasource, table, query) {
  return request({
    url: `/api/${datasource}/${table}`,
    method: 'get',
    params: query
  })
}

export function createFormData(datasource, table, data) {
  return request({
    url: `/api/${datasource}/${table}`,
    method: 'post',
    data
  })
}

export function updateFormData(datasource, table, id, data) {
  return request({
    url: `/api/${datasource}/${table}/${id}`,
    method: 'put',
    data
  })
}

export function deleteFormData(datasource, table, id) {
  return request({
    url: `/api/${datasource}/${table}/${id}`,
    method: 'delete'
  })
}
import request from '@/utils/request'

// Code Generator Config API
export function listCodeGeneratorConfig(query) {
  return request({
    url: '/api/code-generator/config/list',
    method: 'get',
    params: query
  })
}

export function getCodeGeneratorConfig(id) {
  return request({
    url: `/api/code-generator/config/${id}`,
    method: 'get'
  })
}

export function createCodeGeneratorConfig(data) {
  return request({
    url: '/api/code-generator/config',
    method: 'post',
    data
  })
}

export function updateCodeGeneratorConfig(id, data) {
  return request({
    url: `/api/code-generator/config/${id}`,
    method: 'put',
    data
  })
}

export function deleteCodeGeneratorConfig(id) {
  return request({
    url: `/api/code-generator/config/${id}`,
    method: 'delete'
  })
}

export function generateCode(configId, outputPath) {
  return request({
    url: `/api/code-generator/generate/${configId}`,
    method: 'post',
    params: { outputPath }
  })
}

export function listCodeGeneratorConfigByStatus(status) {
  return request({
    url: `/api/code-generator/config/list/status/${status}`,
    method: 'get'
  })
}
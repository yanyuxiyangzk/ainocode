import request from '@/utils/request'

// Workflow Definition API
export function listWorkflowDefinition(query) {
  return request({
    url: '/api/workflow/definition/list',
    method: 'get',
    params: query
  })
}

export function getWorkflowDefinition(id) {
  return request({
    url: `/api/workflow/definition/${id}`,
    method: 'get'
  })
}

export function createWorkflowDefinition(data) {
  return request({
    url: '/api/workflow/definition',
    method: 'post',
    data
  })
}

export function updateWorkflowDefinition(id, data) {
  return request({
    url: `/api/workflow/definition/${id}`,
    method: 'put',
    data
  })
}

export function deleteWorkflowDefinition(id) {
  return request({
    url: `/api/workflow/definition/${id}`,
    method: 'delete'
  })
}

export function deployWorkflow(id) {
  return request({
    url: `/api/workflow/definition/${id}/deploy`,
    method: 'post'
  })
}

export function suspendWorkflow(id) {
  return request({
    url: `/api/workflow/definition/${id}/suspend`,
    method: 'post'
  })
}

export function startWorkflow(id, data) {
  return request({
    url: `/api/workflow/definition/${id}/start`,
    method: 'post',
    params: data
  })
}

export function completeWorkflowTask(instanceId, userId) {
  return request({
    url: `/api/workflow/definition/instance/${instanceId}/complete`,
    method: 'post',
    params: { userId }
  })
}

// Workflow Instance API
export function listWorkflowInstance(query) {
  return request({
    url: '/api/workflow/instance/list',
    method: 'get',
    params: query
  })
}

export function getWorkflowInstance(id) {
  return request({
    url: `/api/workflow/instance/${id}`,
    method: 'get'
  })
}
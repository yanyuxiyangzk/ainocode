import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { JobForm, JobQuery, JobVO } from './types';

/**
 * 查询定时任务列表
 * @param query
 */
export const listJob = (query: JobQuery): AxiosPromise<JobVO[]> => {
  return request({
    url: '/monitor/job/list',
    method: 'get',
    params: query
  });
};

/**
 * 获取定时任务详情
 * @param jobId
 */
export const getJob = (jobId: string | number): AxiosPromise<JobVO> => {
  return request({
    url: '/monitor/job/' + jobId,
    method: 'get'
  });
};

/**
 * 新增定时任务
 * @param data
 */
export const addJob = (data: JobForm) => {
  return request({
    url: '/monitor/job',
    method: 'post',
    data: data
  });
};

/**
 * 修改定时任务
 * @param data
 */
export const updateJob = (data: JobForm) => {
  return request({
    url: '/monitor/job',
    method: 'put',
    data: data
  });
};

/**
 * 删除定时任务
 * @param jobId
 */
export const delJob = (jobId: string | number | Array<string | number>) => {
  return request({
    url: '/monitor/job/' + jobId,
    method: 'delete'
  });
};

/**
 * 立即执行定时任务
 * @param jobId
 */
export const runJob = (jobId: string | number) => {
  return request({
    url: '/monitor/job/run/' + jobId,
    method: 'put'
  });
};

/**
 * 停止定时任务
 * @param jobId
 */
export const stopJob = (jobId: string | number) => {
  return request({
    url: '/monitor/job/stop/' + jobId,
    method: 'put'
  });
};

/**
 * 获取定时任务执行日志
 * @param jobId
 */
export const getJobLog = (jobId: string | number): AxiosPromise<any> => {
  return request({
    url: '/monitor/job/log/' + jobId,
    method: 'get'
  });
};

export default {
  listJob,
  getJob,
  addJob,
  updateJob,
  delJob,
  runJob,
  stopJob,
  getJobLog
};

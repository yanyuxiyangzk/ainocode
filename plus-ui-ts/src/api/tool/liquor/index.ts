import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { LiquorCompileForm, LiquorCompileVO, LiquorHotSwapForm, LiquorHotSwapVO, LiquorExecuteForm, LiquorExecuteVO, LiquorDomainVO, LiquorDomainQuery } from './types';

/**
 * Compile Java source code
 * @param data - Compilation request
 */
export const compileCode = (data: LiquorCompileForm): AxiosPromise<LiquorCompileVO> => {
  return request({
    url: '/tool/liquor/compile',
    method: 'post',
    data: data
  });
};

/**
 * Compile and instantiate Java class
 * @param data - Compilation request
 */
export const compileAndInstantiate = (data: LiquorCompileForm): AxiosPromise<LiquorCompileVO> => {
  return request({
    url: '/tool/liquor/compile-and-instantiate',
    method: 'post',
    data: data
  });
};

/**
 * Hot swap Java class
 * @param data - Hot swap request
 */
export const hotSwap = (data: LiquorHotSwapForm): AxiosPromise<LiquorHotSwapVO> => {
  return request({
    url: '/tool/liquor/hot-swap',
    method: 'post',
    data: data
  });
};

/**
 * Rollback to previous version
 * @param domain - Domain name
 * @param version - Target version
 */
export const rollback = (domain: string, version: number): AxiosPromise<LiquorHotSwapVO> => {
  return request({
    url: '/tool/liquor/rollback',
    method: 'post',
    params: { domain, version }
  });
};

/**
 * Execute compiled class in sandbox
 * @param data - Execution request
 */
export const executeCode = (data: LiquorExecuteForm): AxiosPromise<LiquorExecuteVO> => {
  return request({
    url: '/tool/liquor/execute',
    method: 'post',
    data: data
  });
};

/**
 * Get domain list
 * @param query - Query params
 */
export const listDomains = (query?: LiquorDomainQuery): AxiosPromise<LiquorDomainVO[]> => {
  return request({
    url: '/tool/liquor/domains',
    method: 'get',
    params: query
  });
};

/**
 * Get domain details
 * @param domain - Domain name
 */
export const getDomain = (domain: string): AxiosPromise<LiquorDomainVO> => {
  return request({
    url: '/tool/liquor/domains/' + domain,
    method: 'get'
  });
};

/**
 * Clear domain
 * @param domain - Domain name
 */
export const clearDomain = (domain: string): AxiosPromise<void> => {
  return request({
    url: '/tool/liquor/domains/' + domain,
    method: 'delete'
  });
};

/**
 * Get class loader info
 * @param domain - Domain name
 */
export const getClassLoaderInfo = (domain: string): AxiosPromise<any> => {
  return request({
    url: '/tool/liquor/classloader/' + domain,
    method: 'get'
  });
};

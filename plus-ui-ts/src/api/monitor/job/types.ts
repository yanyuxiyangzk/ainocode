/**
 * 定时任务表单
 */
export interface JobForm {
  jobId?: string | number;
  jobName?: string;
  jobGroup?: string;
  invokeTarget?: string;
  cronExpression?: string;
  misfirePolicy?: string;
  concurrent?: string;
  status?: string;
  remark?: string;
}

/**
 * 定时任务查询
 */
export interface JobQuery {
  pageNum?: number;
  pageSize?: number;
  jobName?: string;
  jobGroup?: string;
  status?: string;
}

/**
 * 定时任务信息
 */
export interface JobVO {
  jobId: string | number;
  jobName: string;
  jobGroup: string;
  invokeTarget: string;
  cronExpression: string;
  misfirePolicy: string;
  concurrent: string;
  status: string;
  createTime: string;
  remark: string;
}

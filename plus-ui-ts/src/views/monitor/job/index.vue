<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="任务名称" prop="jobName">
              <el-input v-model="queryParams.jobName" placeholder="请输入任务名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="任务组名" prop="jobGroup">
              <el-input v-model="queryParams.jobGroup" placeholder="请输入任务组名" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="任务状态" clearable>
                <el-option label="启用" value="0" />
                <el-option label="停用" value="1" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10">
          <el-col :span="1.5">
            <el-button v-hasPermi="['monitor:job:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['monitor:job:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['monitor:job:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['monitor:job:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:show-search="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="jobList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="false" label="任务编号" align="center" prop="jobId" />
        <el-table-column label="任务名称" align="center" prop="jobName" :show-overflow-tooltip="true" />
        <el-table-column label="任务组名" align="center" prop="jobGroup" :show-overflow-tooltip="true" />
        <el-table-column label="调用目标" align="center" prop="invokeTarget" :show-overflow-tooltip="true" />
        <el-table-column label="cron表达式" align="center" prop="cronExpression" width="120" />
        <el-table-column label="状态" align="center" width="80">
          <template #default="scope">
            <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="执行策略" align="center" width="100">
          <template #default="scope">
            <span>{{ scope.row.misfirePolicy === '1' ? '立即执行' : scope.row.misfirePolicy === '2' ? '执行一次' : '放弃执行' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="是否并发" align="center" width="80">
          <template #default="scope">
            <span>{{ scope.row.concurrent === '0' ? '允许' : '禁止' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ proxy.parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button v-hasPermi="['monitor:job:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button v-hasPermi="['monitor:job:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="立即执行" placement="top">
              <el-button v-hasPermi="['monitor:job:edit']" link type="primary" icon="VideoPlay" @click="handleRun(scope.row)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        :total="total"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加或修改定时任务对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="600px" append-to-body>
      <el-form ref="jobFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="任务名称" prop="jobName">
              <el-input v-model="form.jobName" placeholder="请输入任务名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务组名" prop="jobGroup">
              <el-input v-model="form.jobGroup" placeholder="请输入任务组名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="调用目标" prop="invokeTarget">
              <el-input v-model="form.invokeTarget" placeholder="请输入调用目标" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="cron表达式" prop="cronExpression">
              <el-input v-model="form.cronExpression" placeholder="请输入cron表达式" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="执行策略">
              <el-select v-model="form.misfirePolicy" placeholder="请选择">
                <el-option label="立即执行" value="1" />
                <el-option label="执行一次" value="2" />
                <el-option label="放弃执行" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="是否并发">
              <el-radio-group v-model="form.concurrent">
                <el-radio value="0">允许</el-radio>
                <el-radio value="1">禁止</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio value="0">启用</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Job" lang="ts">
import { listJob, addJob, updateJob, delJob, runJob } from '@/api/monitor/job';
import { JobForm, JobQuery, JobVO } from '@/api/monitor/job/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const jobList = ref<JobVO[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const jobFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: JobForm = {
  jobId: undefined,
  jobName: '',
  jobGroup: '',
  invokeTarget: '',
  cronExpression: '',
  misfirePolicy: '1',
  concurrent: '0',
  status: '0',
  remark: ''
};

const data = reactive<PageData<JobForm, JobQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    jobName: '',
    jobGroup: '',
    status: ''
  },
  rules: {
    jobName: [{ required: true, message: '任务名称不能为空', trigger: 'blur' }],
    jobGroup: [{ required: true, message: '任务组名不能为空', trigger: 'blur' }],
    invokeTarget: [{ required: true, message: '调用目标不能为空', trigger: 'blur' }],
    cronExpression: [{ required: true, message: 'cron表达式不能为空', trigger: 'blur' }]
  }
});

const { queryParams, form, rules } = toRefs<PageData<JobForm, JobQuery>>(data);

/** 查询定时任务列表 */
const getList = async () => {
  loading.value = true;
  const res = await listJob(queryParams.value);
  jobList.value = res.rows;
  total.value = res.total;
  loading.value = false;
};

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
};

/** 表单重置 */
const reset = () => {
  form.value = { ...initFormData };
  jobFormRef.value?.resetFields();
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

/** 多选框选中数据 */
const handleSelectionChange = (selection: JobVO[]) => {
  ids.value = selection.map((item) => item.jobId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = '添加定时任务';
};

/** 修改按钮操作 */
const handleUpdate = async (row?: JobVO) => {
  reset();
  const jobId = row?.jobId || ids.value[0];
  const { data: res } = await listJob({ jobId } as JobQuery);
  Object.assign(form.value, res[0]);
  dialog.visible = true;
  dialog.title = '修改定时任务';
};

/** 提交按钮 */
const submitForm = () => {
  jobFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      form.value.jobId ? await updateJob(form.value) : await addJob(form.value);
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row?: JobVO) => {
  const jobIds = row?.jobId || ids.value;
  await proxy?.$modal.confirm('是否确认删除任务编号为"' + jobIds + '"的数据项？');
  await delJob(jobIds);
  await getList();
  proxy?.$modal.msgSuccess('删除成功');
};

/** 状态修改 */
const handleStatusChange = async (row: JobVO) => {
  const text = row.status === '0' ? '启用' : '停用';
  try {
    await proxy?.$modal.confirm('确认要"' + text + '"任务"' + row.jobName + '"吗?');
    await updateJob(row);
    proxy?.$modal.msgSuccess(text + '成功');
  } catch {
    row.status = row.status === '0' ? '1' : '0';
  }
};

/** 立即执行按钮 */
const handleRun = async (row: JobVO) => {
  await proxy?.$modal.confirm('是否确认立即执行任务"' + row.jobName + '"?');
  await runJob(row.jobId);
  proxy?.$modal.msgSuccess('执行成功');
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'monitor/job/export',
    {
      ...queryParams.value
    },
    `job_${new Date().getTime()}.xlsx`
  );
};

onMounted(() => {
  getList();
});
</script>

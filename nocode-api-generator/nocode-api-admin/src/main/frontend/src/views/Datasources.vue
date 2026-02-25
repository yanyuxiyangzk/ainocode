<template>
  <div class="datasources-page">
    <el-card shadow="never">
      <div slot="header">
        <span><i class="el-icon-connection"></i> 数据源管理</span>
        <el-button type="primary" size="small" style="float: right;" @click="showAddDialog">
          <i class="el-icon-plus"></i> 添加数据源
        </el-button>
      </div>

      <!-- 数据源列表 -->
      <el-table :data="datasources" stripe v-loading="loading">
        <el-table-column type="index" width="50" align="center"></el-table-column>
        <el-table-column prop="name" label="名称" width="150">
          <template slot-scope="{row}">
            <el-tag size="small" effect="plain">{{ row.name }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="jdbcUrl" label="JDBC URL" min-width="300" show-overflow-tooltip></el-table-column>
        <el-table-column prop="driverClassName" label="驱动类" width="220"></el-table-column>
        <el-table-column prop="tableCount" label="表数量" width="100" align="center">
          <template slot-scope="{row}">
            <el-badge :value="row.tableCount || 0" type="info" class="table-badge"></el-badge>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="{row}">
            <el-tag v-if="row.enabled !== false" type="success" size="small">已启用</el-tag>
            <el-tag v-else type="info" size="small">已禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="{row}">
            <el-tooltip content="查看数据表">
              <el-button type="text" size="small" icon="el-icon-menu" @click="viewTables(row.name)"></el-button>
            </el-tooltip>
            <el-tooltip content="刷新缓存">
              <el-button type="text" size="small" icon="el-icon-refresh" @click="refreshDatasource(row.name)"></el-button>
            </el-tooltip>
            <el-tooltip content="API文档">
              <el-button type="text" size="small" icon="el-icon-document" @click="viewApiDoc(row.name)"></el-button>
            </el-tooltip>
            <el-tooltip content="删除">
              <el-button type="text" size="small" icon="el-icon-delete" style="color: #f56c6c;" @click="deleteDatasource(row.name)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && datasources.length === 0" description="暂无数据源">
        <el-button type="primary" @click="showAddDialog">添加数据源</el-button>
      </el-empty>
    </el-card>

    <!-- 添加数据源对话框 -->
    <el-dialog
      title="添加数据源"
      :visible.sync="dialogVisible"
      width="550px"
      :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="唯一标识，如: primary" maxlength="50"></el-input>
        </el-form-item>
        <el-form-item label="JDBC URL" prop="jdbcUrl">
          <el-input v-model="form.jdbcUrl" placeholder="jdbc:mysql://localhost:3306/mydb"></el-input>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="数据库用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="数据库密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="驱动类" prop="driverClassName">
          <el-select v-model="form.driverClassName" style="width: 100%;">
            <el-option label="MySQL 5.x/8.x" value="com.mysql.cj.jdbc.Driver"></el-option>
            <el-option label="PostgreSQL" value="org.postgresql.Driver"></el-option>
            <el-option label="Oracle" value="oracle.jdbc.OracleDriver"></el-option>
            <el-option label="SQL Server" value="com.microsoft.sqlserver.jdbc.SQLServerDriver"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addDatasource" :loading="submitLoading">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'Datasources',
  data() {
    return {
      loading: false,
      datasources: [],
      dialogVisible: false,
      submitLoading: false,
      form: {
        name: '',
        jdbcUrl: '',
        username: '',
        password: '',
        driverClassName: 'com.mysql.cj.jdbc.Driver'
      },
      rules: {
        name: [
          { required: true, message: '请输入名称', trigger: 'blur' },
          { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '名称必须以字母开头，只能包含字母、数字和下划线', trigger: 'blur' }
        ],
        jdbcUrl: [
          { required: true, message: '请输入JDBC URL', trigger: 'blur' }
        ],
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        driverClassName: [
          { required: true, message: '请选择驱动类', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.loadDatasources()
  },
  methods: {
    async loadDatasources() {
      this.loading = true
      try {
        this.datasources = await api.getDatasources()
      } catch (e) {
        this.$message.error('加载数据源失败: ' + e.message)
      } finally {
        this.loading = false
      }
    },
    showAddDialog() {
      this.form = {
        name: '',
        jdbcUrl: '',
        username: '',
        password: '',
        driverClassName: 'com.mysql.cj.jdbc.Driver'
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    async addDatasource() {
      try {
        await this.$refs.form.validate()
        this.submitLoading = true
        const res = await api.addDatasource(this.form)
        if (res.success) {
          this.$message.success('添加成功')
          this.dialogVisible = false
          await this.loadDatasources()
        } else {
          this.$message.error(res.message)
        }
      } catch (e) {
        if (e.message !== 'cancel') {
          this.$message.error('添加失败: ' + e.message)
        }
      } finally {
        this.submitLoading = false
      }
    },
    async deleteDatasource(name) {
      try {
        await this.$confirm('确定要删除数据源 "' + name + '" 吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const res = await api.deleteDatasource(name)
        if (res.success) {
          this.$message.success('删除成功')
          await this.loadDatasources()
        } else {
          this.$message.error(res.message)
        }
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error('删除失败: ' + e.message)
        }
      }
    },
    async refreshDatasource(name) {
      try {
        const res = await api.refreshDatasource(name)
        if (res.success) {
          this.$message.success('刷新成功')
          await this.loadDatasources()
        } else {
          this.$message.error(res.message)
        }
      } catch (e) {
        this.$message.error('刷新失败: ' + e.message)
      }
    },
    viewTables(name) {
      this.$router.push({ path: '/tables', query: { datasource: name } })
    },
    viewApiDoc(name) {
      this.$router.push({ path: '/api-doc', query: { datasource: name } })
    }
  }
}
</script>

<style scoped>
.datasources-page {
  max-width: 1200px;
  margin: 0 auto;
}
.table-badge {
  margin-right: 5px;
}
</style>

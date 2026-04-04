<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :lg="12" style="padding-left: 20px">
        <h2>RuoYi-Cloud-Nocode 多租户管理系统</h2>
        <p>
          基于 RuoYi-Cloud-Plus 微服务通用权限管理系统
          <br />
          * 前端开发框架 Vue3、TS、Element Plus<br />
          * 后端开发框架 Spring Boot<br />
          * 微服务开发框架 Spring Cloud、Spring Cloud Alibaba<br />
          * 权限认证框架 Sa-Token、Jwt 支持多终端认证系统<br />
          * 关系数据库 MySQL 适配 8.X 最低 5.7<br />
          * 缓存数据库 Redis 适配 6.X 最低 5.X<br />
          * 分布式注册中心 Alibaba Nacos<br />
          * 分布式配置中心 Alibaba Nacos<br />
          * 服务网关 Spring Cloud Gateway<br />
          * RPC远程调用 Apache Dubbo<br />
          * 分布式限流熔断 Alibaba Sentinel<br />
          * 分布式事务 Alibaba Seata<br />
          * 分布式消息队列 Apache Kafka<br />
        </p>
        <p><b>当前版本:</b> <span>v1.0.0</span></p>
        <p>
          <el-tag type="danger">&yen;免费开源</el-tag>
        </p>
      </el-col>

      <el-col :xs="24" :sm="24" :lg="12" style="padding-left: 20px">
        <h2>快速开始</h2>
        <p>
          欢迎使用 RuoYi-Cloud-Nocode 无代码开发平台
          <br />
          * 拖拽式页面编排<br />
          * 可视化流程设计<br />
          * 灵活的表单配置<br />
          * 动态数据模型<br />
          * 多端适配支持<br />
        </p>
        <p><b>平台版本:</b> <span>v1.0.0</span></p>
        <p>
          <el-tag type="success">&yen;开源免费</el-tag>
        </p>
      </el-col>
    </el-row>
    <el-divider />
    <el-row :gutter="20">
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-user">
            <svg-icon icon-class="user" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">用户总数</div>
            <div class="card-panel-num">1,024</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-menu">
            <svg-icon icon-class="menu" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">菜单总数</div>
            <div class="card-panel-num">256</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-role">
            <svg-icon icon-class="role" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">角色总数</div>
            <div class="card-panel-num">32</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6" :lg="6">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-dict">
            <svg-icon icon-class="dict" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">字典总数</div>
            <div class="card-panel-num">128</div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index" lang="ts">
</script>

<style lang="scss" scoped>
.home {
  padding: 20px;

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;
    color: #676a6c;
  }

  .card-panel {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;

    .card-panel-icon-wrapper {
      float: left;
      width: 60px;
      height: 60px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;

      &.icon-user {
        background: #40c9c6;
      }
      &.icon-menu {
        background: #36a3f7;
      }
      &.icon-role {
        background: #f4516c;
      }
      &.icon-dict {
        background: #ffba00;
      }

      .card-panel-icon {
        font-size: 30px;
        color: #fff;
      }
    }

    .card-panel-description {
      float: right;
      text-align: right;

      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 14px;
        margin-bottom: 12px;
      }

      .card-panel-num {
        font-size: 30px;
        font-weight: bold;
      }
    }
  }
}
</style>

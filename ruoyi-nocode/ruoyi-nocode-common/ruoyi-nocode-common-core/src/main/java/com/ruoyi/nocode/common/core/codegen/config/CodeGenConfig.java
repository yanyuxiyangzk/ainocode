package com.ruoyi.nocode.common.core.codegen.config;

import lombok.Data;

/**
 * 代码生成配置
 *
 * @author ruoyi
 */
@Data
public class CodeGenConfig {

    /**
     * 作者
     */
    private String author = "ruoyi";

    /**
     * 邮箱
     */
    private String email = "ruoyi@example.com";

    /**
     * 默认包名
     */
    private String packageName = "com.ruoyi";

    /**
     * 模块名
     */
    private String moduleName = "system";

    /**
     * 业务名（功能名）
     */
    private String businessName;

    /**
     * 功能名（中文描述）
     */
    private String functionName;

    /**
     * 表前缀
     */
    private String tablePrefix = "sys_";

    /**
     * 生成路径（为空则使用项目根路径）
     */
    private String outputPath;

    /**
     * 前端输出路径
     */
    private String vueOutputPath;

    /**
     * 后端输出路径
     */
    private String javaOutputPath;

    /**
     * 是否覆盖已有文件
     */
    private boolean overwrite = false;

    /**
     * 是否生成Entity
     */
    private boolean generateEntity = true;

    /**
     * 是否生成Mapper
     */
    private boolean generateMapper = true;

    /**
     * 是否生成Service
     */
    private boolean generateService = true;

    /**
     * 是否生成Controller
     */
    private boolean generateController = true;

    /**
     * 是否生成Vue
     */
    private boolean generateVue = true;

    /**
     * 是否使用Lombok
     */
    private boolean useLombok = true;

    /**
     * 是否使用Swagger
     */
    private boolean useSwagger = true;

    /**
     * 是否使用逻辑删除
     */
    private boolean useLogicDelete = true;

    /**
     * 逻辑删除字段
     */
    private String logicDeleteField = "del_flag";

    /**
     * 逻辑删除值
     */
    private String logicDeleteValue = "2";

    /**
     * 未删除值
     */
    private String logicNotDeleteValue = "0";

    /**
     * 是否支持租户
     */
    private boolean useTenant = false;

    /**
     * 租户字段
     */
    private String tenantField = "tenant_id";

    /**
     * 数据库类型：mysql/postgresql/oracle/sqlserver
     */
    private String dbType = "mysql";

    /**
     * JDBC驱动类名
     */
    private String jdbcDriver = "com.mysql.cj.jdbc.Driver";

    /**
     * JDBC URL
     */
    private String jdbcUrl;

    /**
     * 数据库用户名
     */
    private String jdbcUsername;

    /**
     * 数据库密码
     */
    private String jdbcPassword;

    /**
     * 菜单图标
     */
    private String menuIcon = "fa fa-file-code-o";

    /**
     * 路由基础路径
     */
    private String routeBasePath = "/";

    /**
     * 是否自动去除表前缀
     */
    private boolean autoRemovePrefix = true;

    /**
     * Vue文件路径基础
     */
    private String vueFileBasePath = "/views";

    /**
     * 生成代码类型
     * - all: 生成全部
     * - single: 仅生成Entity
     */
    private String generateType = "all";
}

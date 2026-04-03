package com.nocode.admin.entity;

/**
 * 表单组件类型枚举
 *
 * @author auto-dev
 * @since 2026-04-03
 */
public enum FormComponentType {

    // 输入组件
    INPUT("input", "文本输入框"),
    TEXTAREA("textarea", "多行文本框"),
    NUMBER("number", "数字输入框"),
    PASSWORD("password", "密码输入框"),
    EMAIL("email", "邮箱输入框"),
    PHONE("phone", "手机号输入框"),
    URL("url", "URL输入框"),

    // 选择组件
    SELECT("select", "下拉选择框"),
    MULTI_SELECT("multiSelect", "多选下拉框"),
    RADIO("radio", "单选按钮组"),
    CHECKBOX("checkbox", "多选按钮组"),
    SWITCH("switch", "开关"),
    SLIDER("slider", "滑块"),
    RATE("rate", "评分"),

    // 日期时间组件
    DATE("date", "日期选择器"),
    TIME("time", "时间选择器"),
    DATETIME("datetime", "日期时间选择器"),
    DATERANGE("dateRange", "日期范围选择器"),
    TIMERANGE("timeRange", "时间范围选择器"),
    DATETIMERANGE("datetimeRange", "日期时间范围选择器"),

    // 文件上传组件
    UPLOAD("upload", "文件上传"),
    IMAGE_UPLOAD("imageUpload", "图片上传"),
    FILE_UPLOAD("fileUpload", "文件上传"),
    DRagger("dragger", "拖拽上传"),

    // 高级组件
    EDITOR("editor", "富文本编辑器"),
    MARKDOWN("markdown", "Markdown编辑器"),
    JSON_EDITOR("jsonEditor", "JSON编辑器"),
    TREE_SELECT("treeSelect", "树形选择"),
    CASCADER("cascader", "级联选择"),
    TRANSFER("transfer", "穿梭框"),
    COLOR_PICKER("colorPicker", "颜色选择器"),

    // 布局组件
    DIVIDER("divider", "分割线"),
    CARD("card", "卡片"),
    TABS("tabs", "标签页"),
    COLLAPSE("collapse", "折叠面板"),
    STEPPER("stepper", "步进器"),

    // 数据展示组件
    TEXT("text", "文本展示"),
    HTML("html", "HTML展示"),
    LINK("link", "链接"),
    IMAGE("image", "图片展示"),
    TABLE("table", "表格"),
    LIST("list", "列表"),

    // 业务组件
    ADDRESS("address", "地址选择器"),
    CITY_SELECT("citySelect", "城市选择器"),
    PROVINCE_SELECT("provinceSelect", "省份选择器"),
    AREA_SELECT("areaSelect", "区县选择器"),
    ORGANIZATION_SELECT("organizationSelect", "组织选择器"),
    USER_SELECT("userSelect", "用户选择器"),
    DEPT_SELECT("deptSelect", "部门选择器"),
    ROLE_SELECT("roleSelect", "角色选择器"),
    POSITION_SELECT("positionSelect", "职位选择器"),

    // 高级业务组件
    SIGNATURE("signature", "签名板"),
    BARCODE("barcode", "条形码"),
    QRCODE("qrcode", "二维码"),
    LATLNG_PICKER("latlngPicker", "经纬度选择器"),
    MAP_PICKER("mapPicker", "地图选择器"),

    // 容器组件
    FORM("form", "子表单"),
    GRID("grid", "栅格布局"),
    POPOVER("popover", "气泡卡片"),
    MODAL("modal", "对话框"),
    DRAWER("drawer", "抽屉"),
    CONTAINER("container", "容器");

    private final String code;
    private final String description;

    FormComponentType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 组件编码
     * @return 组件类型枚举
     */
    public static FormComponentType fromCode(String code) {
        for (FormComponentType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
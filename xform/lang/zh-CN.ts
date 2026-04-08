// 中文语言包
export default {
  // 应用名称
  app: {
    name: '表单设计器',
    version: 'v1.0.0',
  },

  // 组件面板
  palette: {
    title: '组件面板',
    searchPlaceholder: '搜索组件...',
    categories: {
      field: '基础字段',
      container: '容器组件',
      upload: '上传组件',
      advanced: '高级组件',
    },
    empty: '暂无组件',
  },

  // 工具栏
  toolbar: {
    title: '表单设计器',
    modes: {
      design: '设计模式',
      preview: '预览模式',
      json: 'JSON模式',
    },
    buttons: {
      undo: '撤销',
      redo: '重做',
      copy: '复制',
      cut: '剪切',
      paste: '粘贴',
      import: '导入JSON',
      export: '导出JSON',
      clear: '清空',
      generateCode: '生成代码',
      preview: '预览',
      fullscreen: '全屏',
      settings: '表单设置',
    },
    shortcuts: {
      undo: '撤销 (Ctrl+Z)',
      redo: '重做 (Ctrl+Y)',
      copy: '复制 (Ctrl+C)',
      cut: '剪切 (Ctrl+X)',
      paste: '粘贴 (Ctrl+V)',
    },
  },

  // 属性面板
  property: {
    title: '属性配置',
    empty: '请选择一个组件',
    groups: {
      basic: '基本属性',
      advanced: '高级属性',
      events: '事件配置',
      style: '样式配置',
    },
    fields: {
      label: '标签',
      placeholder: '占位符',
      defaultValue: '默认值',
      disabled: '禁用',
      clearable: '可清空',
      maxlength: '最大长度',
      showCount: '显示计数',
      rows: '行数',
      autosize: '自适应高度',
      min: '最小值',
      max: '最大值',
      step: '步进',
      precision: '精度',
      showButton: '显示按钮',
      filterable: '可搜索',
      multiple: '多选',
      options: '选项',
      type: '类型',
      format: '显示格式',
      valueFormat: '值格式',
      isRange: '时间范围',
      checkedValue: '选中值',
      uncheckedValue: '未选中值',
      count: '数量',
      allowHalf: '允许半星',
      range: '范围选择',
      vertical: '垂直',
      showAlpha: '显示透明度',
      defaultColor: '默认颜色',
      maxNumber: '最大数量',
      accept: '接受类型',
      listType: '列表类型',
      action: '上传地址',
      height: '高度',
      language: '语言',
      title: '标题',
      dashed: '虚线',
      position: '位置',
      text: '文本',
      loading: '加载中',
      size: '尺寸',
      ghost: '幽灵按钮',
      circle: '圆形',
    },
    // 日期类型选项
    dateTypes: {
      date: '日期',
      datetime: '日期时间',
      daterange: '日期范围',
      month: '月',
      year: '年',
    },
    // 按钮类型选项
    buttonTypes: {
      default: '默认',
      primary: '主要',
      success: '成功',
      warning: '警告',
      error: '错误',
      info: '信息',
    },
    // 按钮尺寸选项
    buttonSizes: {
      small: '小',
      medium: '中',
      large: '大',
    },
    // 分割线位置选项
    dividerPositions: {
      left: '居左',
      center: '居中',
      right: '居右',
    },
  },

  // 画布
  canvas: {
    empty: '从左侧拖拽组件到此处',
    emptyHint: '或者点击组件进行添加',
  },

  // 消息提示
  messages: {
    saveSuccess: '保存成功',
    saveFailed: '保存失败',
    deleteConfirm: '确定要删除该组件吗？',
    deleteSuccess: '删除成功',
    clearConfirm: '确定要清空所有组件吗？',
    clearSuccess: '已清空画布',
    importSuccess: '导入成功',
    importFailed: '导入失败，JSON格式错误',
    exportSuccess: '导出成功',
    copySuccess: '已复制到剪贴板',
    pasteSuccess: '粘贴成功',
    undoSuccess: '已撤销',
    redoSuccess: '已重做',
    generateCodeSuccess: '代码生成成功',
    invalidSchema: '无效的Schema格式',
    noData: '暂无数据',
    required: '该项为必填项',
  },

  // 预览对话框
  preview: {
    title: '表单预览',
    empty: '暂无表单数据，请先在设计器中添加组件',
    submit: '提交',
    reset: '重置',
  },

  // JSON编辑器
  jsonEditor: {
    title: 'JSON 编辑器',
    format: '格式化',
    compress: '压缩',
    copy: '复制',
    invalidJson: 'JSON格式错误',
    formatSuccess: '格式化成功',
    compressSuccess: '压缩成功',
    copySuccess: '已复制到剪贴板',
  },

  // 代码生成
  codeGen: {
    title: '生成代码',
    copy: '复制代码',
    download: '下载文件',
    copySuccess: '代码已复制到剪贴板',
  },

  // 组件名称
  widgets: {
    InputWidget: '输入框',
    TextareaWidget: '文本域',
    NumberWidget: '数字输入',
    SelectWidget: '下拉选择',
    RadioGroupWidget: '单选组',
    CheckboxGroupWidget: '复选组',
    DatePickerWidget: '日期选择',
    TimePickerWidget: '时间选择',
    SwitchWidget: '开关',
    RateWidget: '评分',
    SliderWidget: '滑块',
    ColorPickerWidget: '颜色选择',
    PictureUploadWidget: '图片上传',
    FileUploadWidget: '文件上传',
    RichEditorWidget: '富文本编辑器',
    CodeEditorWidget: '代码编辑器',
    HtmlWidget: 'HTML',
    DividerWidget: '分割线',
    ButtonWidget: '按钮',
    GridWidget: '栅格布局',
    TabsWidget: '标签页',
    CardWidget: '卡片',
    CollapseWidget: '折叠面板',
  },

  // 容器组件
  containers: {
    GridWidget: {
      columns: '列数',
      gutter: '间距',
    },
    TabsWidget: {
      tabs: '标签页',
      type: '类型',
      tabTitle: '标签{0}',
      addTab: '添加标签',
      removeTab: '删除标签',
    },
    CardWidget: {
      title: '标题',
      bordered: '显示边框',
      hoverable: '悬停效果',
    },
    CollapseWidget: {
      panels: '折叠面板',
      accordion: '手风琴模式',
    },
  },

  // 表单属性
  formProps: {
    layout: '布局',
    layouts: {
      horizontal: '水平',
      vertical: '垂直',
      inline: '行内',
    },
    labelWidth: '标签宽度',
    labelAlign: '标签对齐',
    labelAligns: {
      left: '左对齐',
      right: '右对齐',
    },
    hideRequiredMark: '隐藏必填标记',
    size: '尺寸',
    sizes: {
      small: '小',
      medium: '中',
      large: '大',
    },
  },

  // 通用
  common: {
    confirm: '确定',
    cancel: '取消',
    close: '关闭',
    save: '保存',
    delete: '删除',
    edit: '编辑',
    add: '添加',
    remove: '移除',
    reset: '重置',
    search: '搜索',
    submit: '提交',
    loading: '加载中...',
    success: '操作成功',
    error: '操作失败',
    warning: '警告',
    info: '提示',
    yes: '是',
    no: '否',
    ok: '确定',
    yesText: '是',
    noText: '否',
  },
};

// English language pack
export default {
  // App name
  app: {
    name: 'Form Designer',
    version: 'v1.0.0',
  },

  // Component palette
  palette: {
    title: 'Components',
    searchPlaceholder: 'Search components...',
    categories: {
      field: 'Basic Fields',
      container: 'Containers',
      upload: 'Upload',
      advanced: 'Advanced',
    },
    empty: 'No components',
  },

  // Toolbar
  toolbar: {
    title: 'Form Designer',
    modes: {
      design: 'Design Mode',
      preview: 'Preview Mode',
      json: 'JSON Mode',
    },
    buttons: {
      undo: 'Undo',
      redo: 'Redo',
      copy: 'Copy',
      cut: 'Cut',
      paste: 'Paste',
      import: 'Import JSON',
      export: 'Export JSON',
      clear: 'Clear',
      generateCode: 'Generate Code',
      preview: 'Preview',
      fullscreen: 'Fullscreen',
      settings: 'Form Settings',
    },
    shortcuts: {
      undo: 'Undo (Ctrl+Z)',
      redo: 'Redo (Ctrl+Y)',
      copy: 'Copy (Ctrl+C)',
      cut: 'Cut (Ctrl+X)',
      paste: 'Paste (Ctrl+V)',
    },
  },

  // Property panel
  property: {
    title: 'Properties',
    empty: 'Select a component to edit',
    groups: {
      basic: 'Basic',
      advanced: 'Advanced',
      events: 'Events',
      style: 'Style',
    },
    fields: {
      label: 'Label',
      placeholder: 'Placeholder',
      defaultValue: 'Default Value',
      disabled: 'Disabled',
      clearable: 'Clearable',
      maxlength: 'Max Length',
      showCount: 'Show Count',
      rows: 'Rows',
      autosize: 'Auto Size',
      min: 'Min',
      max: 'Max',
      step: 'Step',
      precision: 'Precision',
      showButton: 'Show Buttons',
      filterable: 'Filterable',
      multiple: 'Multiple',
      options: 'Options',
      type: 'Type',
      format: 'Format',
      valueFormat: 'Value Format',
      isRange: 'Range',
      checkedValue: 'Checked Value',
      uncheckedValue: 'Unchecked Value',
      count: 'Count',
      allowHalf: 'Allow Half',
      range: 'Range',
      vertical: 'Vertical',
      showAlpha: 'Show Alpha',
      defaultColor: 'Default Color',
      maxNumber: 'Max Number',
      accept: 'Accept',
      listType: 'List Type',
      action: 'Action',
      height: 'Height',
      language: 'Language',
      title: 'Title',
      dashed: 'Dashed',
      position: 'Position',
      text: 'Text',
      loading: 'Loading',
      size: 'Size',
      ghost: 'Ghost',
      circle: 'Circle',
    },
    // Date type options
    dateTypes: {
      date: 'Date',
      datetime: 'Date Time',
      daterange: 'Date Range',
      month: 'Month',
      year: 'Year',
    },
    // Button type options
    buttonTypes: {
      default: 'Default',
      primary: 'Primary',
      success: 'Success',
      warning: 'Warning',
      error: 'Error',
      info: 'Info',
    },
    // Button size options
    buttonSizes: {
      small: 'Small',
      medium: 'Medium',
      large: 'Large',
    },
    // Divider position options
    dividerPositions: {
      left: 'Left',
      center: 'Center',
      right: 'Right',
    },
  },

  // Canvas
  canvas: {
    empty: 'Drag components from the left panel',
    emptyHint: 'Or click a component to add',
  },

  // Messages
  messages: {
    saveSuccess: 'Saved successfully',
    saveFailed: 'Save failed',
    deleteConfirm: 'Are you sure you want to delete this component?',
    deleteSuccess: 'Deleted successfully',
    clearConfirm: 'Are you sure you want to clear all components?',
    clearSuccess: 'Canvas cleared',
    importSuccess: 'Imported successfully',
    importFailed: 'Import failed, invalid JSON format',
    exportSuccess: 'Exported successfully',
    copySuccess: 'Copied to clipboard',
    pasteSuccess: 'Pasted successfully',
    undoSuccess: 'Undone',
    redoSuccess: 'Redone',
    generateCodeSuccess: 'Code generated successfully',
    invalidSchema: 'Invalid schema format',
    noData: 'No data',
    required: 'This field is required',
  },

  // Preview dialog
  preview: {
    title: 'Form Preview',
    empty: 'No form data, add components in the designer first',
    submit: 'Submit',
    reset: 'Reset',
  },

  // JSON editor
  jsonEditor: {
    title: 'JSON Editor',
    format: 'Format',
    compress: 'Compress',
    copy: 'Copy',
    invalidJson: 'Invalid JSON format',
    formatSuccess: 'Formatted successfully',
    compressSuccess: 'Compressed successfully',
    copySuccess: 'Copied to clipboard',
  },

  // Code generation
  codeGen: {
    title: 'Generate Code',
    copy: 'Copy Code',
    download: 'Download',
    copySuccess: 'Code copied to clipboard',
  },

  // Widget names
  widgets: {
    InputWidget: 'Input',
    TextareaWidget: 'Textarea',
    NumberWidget: 'Number Input',
    SelectWidget: 'Select',
    RadioGroupWidget: 'Radio Group',
    CheckboxGroupWidget: 'Checkbox Group',
    DatePickerWidget: 'Date Picker',
    TimePickerWidget: 'Time Picker',
    SwitchWidget: 'Switch',
    RateWidget: 'Rate',
    SliderWidget: 'Slider',
    ColorPickerWidget: 'Color Picker',
    PictureUploadWidget: 'Picture Upload',
    FileUploadWidget: 'File Upload',
    RichEditorWidget: 'Rich Editor',
    CodeEditorWidget: 'Code Editor',
    HtmlWidget: 'HTML',
    DividerWidget: 'Divider',
    ButtonWidget: 'Button',
    GridWidget: 'Grid',
    TabsWidget: 'Tabs',
    CardWidget: 'Card',
    CollapseWidget: 'Collapse',
  },

  // Container components
  containers: {
    GridWidget: {
      columns: 'Columns',
      gutter: 'Gutter',
    },
    TabsWidget: {
      tabs: 'Tabs',
      type: 'Type',
      tabTitle: 'Tab {0}',
      addTab: 'Add Tab',
      removeTab: 'Remove Tab',
    },
    CardWidget: {
      title: 'Title',
      bordered: 'Bordered',
      hoverable: 'Hoverable',
    },
    CollapseWidget: {
      panels: 'Panels',
      accordion: 'Accordion',
    },
  },

  // Form properties
  formProps: {
    layout: 'Layout',
    layouts: {
      horizontal: 'Horizontal',
      vertical: 'Vertical',
      inline: 'Inline',
    },
    labelWidth: 'Label Width',
    labelAlign: 'Label Align',
    labelAligns: {
      left: 'Left',
      right: 'Right',
    },
    hideRequiredMark: 'Hide Required Mark',
    size: 'Size',
    sizes: {
      small: 'Small',
      medium: 'Medium',
      large: 'Large',
    },
  },

  // Common
  common: {
    confirm: 'Confirm',
    cancel: 'Cancel',
    close: 'Close',
    save: 'Save',
    delete: 'Delete',
    edit: 'Edit',
    add: 'Add',
    remove: 'Remove',
    reset: 'Reset',
    search: 'Search',
    submit: 'Submit',
    loading: 'Loading...',
    success: 'Success',
    error: 'Error',
    warning: 'Warning',
    info: 'Info',
    yes: 'Yes',
    no: 'No',
    ok: 'OK',
    yesText: 'Yes',
    noText: 'No',
  },
};

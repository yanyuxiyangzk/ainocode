<template>
  <div class="json-viewer">
    <pre class="json-content" v-html="formattedJson"></pre>
  </div>
</template>

<script>
export default {
  name: 'JsonViewer',
  props: {
    data: {
      type: [Object, Array, String, Number, Boolean],
      default: null
    },
    depth: {
      type: Number,
      default: 10
    }
  },
  computed: {
    formattedJson() {
      if (this.data === null || this.data === undefined) {
        return '<span class="json-null">null</span>'
      }
      try {
        const jsonStr = JSON.stringify(this.data, null, 2)
        return this.highlight(jsonStr)
      } catch (e) {
        return '<span class="json-string">' + String(this.data) + '</span>'
      }
    }
  },
  methods: {
    highlight(json) {
      let result = json
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
      result = result.replace(
        /(".*?"):/g,
        '<span class="json-key">$1</span>:'
      )
      result = result.replace(
        /"(.*?)"/g,
        (match) => {
          if (match === '""') return '<span class="json-string">""</span>'
          return '<span class="json-string">' + match + '</span>'
        }
      )
      result = result.replace(
        /\b(true|false)\b/g,
        '<span class="json-boolean">$1</span>'
      )
      result = result.replace(
        /\b(-?\d+\.?\d*)\b/g,
        '<span class="json-number">$1</span>'
      )
      result = result.replace(
        /\bnull\b/g,
        '<span class="json-null">null</span>'
      )
      return result
    }
  }
}
</script>

<style scoped>
.json-viewer {
  background: #282c34;
  border-radius: 4px;
  overflow: auto;
  max-height: 500px;
}
.json-content {
  padding: 15px;
  margin: 0;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  color: #abb2bf;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>

import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';

export default defineConfig(({ command, mode }) => {
  const isLib = command === 'build' && mode === 'lib';

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': resolve(__dirname),
      },
    },
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@use "@/styles/mixins.scss" as *;`,
        },
      },
    },
    server: {
      port: 3000,
      open: true,
      cors: true,
    },
    build: isLib ? libBuildConfig() : appBuildConfig(),
  };
});

function appBuildConfig() {
  return {
    outDir: 'dist',
    sourcemap: false,
    minify: 'esbuild',
    rollupOptions: {
      output: {
        manualChunks: {
          'naive-ui': ['naive-ui'],
          'vue': ['vue'],
          'vue-router': ['vue-router'],
          'pinia': ['pinia'],
          'vue-draggable-plus': ['vue-draggable-plus'],
        },
      },
    },
  };
}

function libBuildConfig() {
  return {
    outDir: 'lib',
    sourcemap: true,
    minify: 'esbuild',
    lib: {
      entry: resolve(__dirname, 'index.ts'),
      name: 'VueAicodingFormDesigner',
      formats: ['es', 'cjs', 'umd'],
      fileName: (format) => `vue-aicoding-form-designer.${format}.js`,
    },
    rollupOptions: {
      // External dependencies that should not be bundled
      external: [
        'vue',
        'naive-ui',
        'vue-draggable-plus',
        'pinia',
      ],
      output: {
        // Provide global variable names for UMD build
        globals: {
          vue: 'Vue',
          'naive-ui': 'NaiveUI',
          'vue-draggable-plus': 'VueDraggablePlus',
          pinia: 'Pinia',
        },
        assetFileNames: (assetInfo) => {
          if (assetInfo.name === 'style.css') {
            return 'vue-aicoding-form-designer.css';
          }
          return assetInfo.name || 'asset';
        },
      },
    },
  };
}

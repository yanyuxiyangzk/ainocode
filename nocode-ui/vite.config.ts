import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import UnoCSS from 'unocss/vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import Icons from 'unplugin-icons/vite'
import IconsResolver from 'unplugin-icons/resolver'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons-ng'
import vueSetupExtend from 'unplugin-vue-setup-extend-plus/vite'
import compression from 'vite-plugin-compression'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())
  const { VITE_APP_ENV } = env

  return {
    base: VITE_APP_ENV === 'production' ? '/' : '/',
    plugins: [
      vue(),
      UnoCSS(),
      vueDevTools(),
      // 自动导入
      AutoImport({
        imports: ['vue', 'vue-router', 'pinia'],
        dts: 'src/types/auto-imports.d.ts',
        resolvers: [
          ElementPlusResolver(),
          IconsResolver({ prefix: 'Icon' })
        ]
      }),
      // 自动注册组件
      Components({
        dirs: ['src/components'],
        include: [/\.vue$/, /\.vue\?vue/, /\.tsx$/],
        exclude: [/[\\/]node_modules[\\/]/, /[\\/]\.git[\\/]/, /[\\/]\.nuxt[\\/]/],
        dts: 'src/types/components.d.ts',
        resolvers: [
          ElementPlusResolver(),
          IconsResolver({ enabledCollections: ['ep'] })
        ]
      }),
      // 图标
      Icons({ autoInstall: true, scale: 1, defaultStyle: '' }),
      // SVG 图标
      createSvgIconsPlugin({
        iconDirs: [resolve(process.cwd(), 'src/assets/icons/svg')],
        symbolId: 'icon-[dir]-[name]',
        inject: 'body-last',
        customDomId: '__svg__icons__dom__'
      }),
      vueSetupExtend(),
      // Gzip 压缩
      VITE_APP_ENV === 'production' &&
        compression({
          verbose: true,
          disable: false,
          threshold: 10240,
          algorithm: 'gzip',
          ext: '.gz'
        })
    ].filter(Boolean),
    resolve: {
      alias: {
        '~': resolve(__dirname, './'),
        '@': resolve(__dirname, 'src')
      },
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
    },
    server: {
      host: '0.0.0.0',
      port: 80,
      open: true,
      proxy: {
        '/dev-api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          rewrite: (p) => p.replace(/^\/dev-api/, '')
        }
      }
    },
    build: {
      outDir: 'dist',
      sourcemap: VITE_APP_ENV !== 'production',
      chunkSizeWarningLimit: 2000,
      rollupOptions: {
        output: {
          manualChunks(id) {
            if (id.includes('node_modules')) {
              if (id.includes('element-plus')) {
                return 'element-plus'
              }
              if (id.includes('echarts')) {
                return 'echarts'
              }
              if (id.includes('vxe-table')) {
                return 'vxe-table'
              }
              if (id.includes('monaco-editor')) {
                return 'monaco-editor'
              }
              return 'vendor'
            }
          }
        }
      }
    },
    css: {
      preprocessorOptions: {
        scss: {
          api: 'modern-compiler'
        }
      }
    }
  }
})

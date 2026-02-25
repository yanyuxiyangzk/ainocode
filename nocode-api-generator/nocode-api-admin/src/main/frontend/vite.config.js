import { defineConfig } from 'vite'
import vue2 from '@vitejs/plugin-vue2'
import path from 'path'

export default defineConfig({
  plugins: [vue2()],
  base: './',
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/nocode-admin/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
})

import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';

console.log('[main] Creating app...');

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);

console.log('[main] Mounting app...');
app.mount('#app');

console.log('[main] App mounted');

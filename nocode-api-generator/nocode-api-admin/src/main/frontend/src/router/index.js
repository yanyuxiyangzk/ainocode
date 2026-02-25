import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Datasources from '../views/Datasources.vue'
import Tables from '../views/Tables.vue'
import CreateTable from '../views/CreateTable.vue'
import ApiDoc from '../views/ApiDoc.vue'
import ApiTest from '../views/ApiTest.vue'
import ErDiagram from '../views/ErDiagram.vue'

Vue.use(VueRouter)

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/datasources', name: 'Datasources', component: Datasources },
  { path: '/tables', name: 'Tables', component: Tables },
  { path: '/create-table', name: 'CreateTable', component: CreateTable },
  { path: '/api-doc', name: 'ApiDoc', component: ApiDoc },
  { path: '/test', name: 'ApiTest', component: ApiTest },
  { path: '/er-diagram', name: 'ErDiagram', component: ErDiagram }
]

const router = new VueRouter({
  routes
})

export default router

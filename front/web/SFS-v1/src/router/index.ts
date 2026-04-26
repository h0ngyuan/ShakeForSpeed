import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: () => import('../views/Login.vue') },
    {
      path: '/',
      component: () => import('../views/Layout.vue'),
      children: [
        { path: '', component: () => import('../views/Dashboard.vue') },
        { path: 'activities', component: () => import('../views/Activities.vue') },
        { path: 'activity/:id', component: () => import('../views/ActivityDetail.vue') },
      ]
    }
  ]
})

export default router

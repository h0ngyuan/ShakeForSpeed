import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import LoginView from '@/views/LoginView.vue';
import HomeView from '@/views/HomeView.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login',
    component: () => import('@/layout/MainLayout.vue'),
    children: [
      // 用户路由组
      {
        path: '/user',
        redirect: '/user/home',
        children: [
          {
            path: 'home',
            name: 'home',
            component: () => import('@/views/user/HomeView.vue')
          },
          {
            path: 'tasks',
            name: 'tasks',
            component: () => import('@/views/user/TaskView.vue')
          },
          {
            path: 'task-execution/:id',
            name: 'taskExecution',
            component: () => import('@/views/user/TaskExecutionView.vue')
          },
          {
            path: 'task-live/:id',
            name: 'taskLive',
            component: () => import('@/views/user/TaskLiveView.vue')
          },
          {
            path: 'task-result/:id',
            name: 'taskResult',
            component: () => import('@/views/user/TaskResultView.vue')
          }
        ]
      },
      // 管理员路由组
      {
        path: '/admin',
        redirect: '/admin/home',
        children: [
          {
            path: 'home',
            name: 'adminHome',
            component: () => import('@/views/admin/AdminHomeView.vue')
          },
          {
            path: 'data-view',
            name: 'dataView',
            component: () => import('@/views/admin/DataView.vue')
          },
          {
            path: 'user-management',
            name: 'userManagement',
            component: () => import('@/views/admin/UserManagementView.vue')
          }
        ]
      }
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue')
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from) => {
  const isAdmin = localStorage.getItem('isAdmin') === 'true';
  
  if (!localStorage.getItem('username') && to.name !== 'login') {
    return { name: 'login' }
  }
  
  // 防止普通用户访问管理员页面
  if ((to.name === 'adminHome' || to.name === 'dataView' || to.name === 'userManagement') && !isAdmin) {
    return { name: 'home' }
  }
});

export default router;
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
      },
      // 商户路由组
      {
        path: '/merchant',
        redirect: '/merchant/home',
        children: [
          {
            path: 'home',
            name: 'merchantHome',
            component: () => import('@/views/merchant/HomeView.vue')
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
  const username = localStorage.getItem('username');
  const role = localStorage.getItem('role');
  
  // 未登录且不是去登录页，则跳转到登录页
  if (!username && to.name !== 'login') {
    return { name: 'login' }
  }
  
  // 已登录且是去登录页，则根据角色跳转到对应主页
  if (username && to.name === 'login') {
    if (role === 'admin') {
      return { name: 'adminHome' };
    } else if (role === 'merchant') {
      return { name: 'merchantHome' };
    }
  }
  
  // 防止普通用户访问管理员页面
  if ((to.name === 'adminHome' || to.name === 'dataView' || to.name === 'userManagement') && role !== 'admin') {
    if (role === 'merchant') {
      return { name: 'merchantHome' };
    } else {
      return { name: 'login' };
    }
  }
  
  // 防止管理员和商户访问用户页面
  if ((to.name === 'home' || to.name === 'tasks' || to.name === 'taskExecution' || to.name === 'taskLive' || to.name === 'taskResult') && role !== 'user') {
    if (role === 'admin') {
      return { name: 'adminHome' };
    } else if (role === 'merchant') {
      return { name: 'merchantHome' };
    }
  }
});

export default router;
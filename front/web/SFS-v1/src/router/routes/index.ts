import type { ElegantRoute } from '../elegant/types';
import { builtinRoutes } from './builtin';

/**
 * 应用路由
 */
const appRoutes: ElegantRoute[] = [
  {
    name: 'login',
    path: '/',
    component: 'login',
    meta: {
      title: '登录',
      hide: true
    }
  },
  {
    name: 'login',
    path: '/login',
    component: 'login',
    meta: {
      title: '登录',
      hide: true
    }
  },
  {
    name: 'main',
    path: '/main',
    component: 'base',
    children: [
      // 用户路由组
      {
        name: 'user',
        path: '/user',
        redirect: { name: 'user-home' },
        children: [
          {
            name: 'user-home',
            path: 'home',
            component: 'base$view.user-home',
            meta: {
              title: '用户首页',
              requiresAuth: true,
              roles: ['0']
            }
          },
          {
            name: 'user-tasks',
            path: 'tasks',
            component: 'base$view.user-tasks',
            meta: {
              title: '任务列表',
              requiresAuth: true,
              roles: ['user']
            }
          },
          {
            name: 'user-task-execution',
            path: 'task-execution/:id',
            component: 'base$view.user-task-execution',
            meta: {
              title: '任务执行',
              requiresAuth: true,
              roles: ['user']
            }
          },
          {
            name: 'user-task-live',
            path: 'task-live/:id',
            component: 'base$view.user-task-live',
            meta: {
              title: '任务直播',
              requiresAuth: true,
              roles: ['user']
            }
          },
          {
            name: 'user-task-result',
            path: 'task-result/:id',
            component: 'base$view.user-task-result',
            meta: {
              title: '任务结果',
              requiresAuth: true,
              roles: ['user']
            }
          }
        ]
      },
      // 管理员路由组
      {
        name: 'admin',
        path: '/admin',
        redirect: { name: 'admin-home' },
        children: [
          {
            name: 'admin-home',
            path: 'home',
            component: 'base$view.admin-home',
            meta: {
              title: '管理员首页',
              requiresAuth: true,
              roles: ['1']
            }
          },
          {
            name: 'admin-data-view',
            path: 'data-view',
            component: 'base$view.admin-data-view',
            meta: {
              title: '数据统计',
              requiresAuth: true,
              roles: ['admin']
            }
          },
          {
            name: 'admin-user-management',
            path: 'user-management',
            component: 'base$view.admin-user-management',
            meta: {
              title: '用户管理',
              requiresAuth: true,
              roles: ['admin']
            }
          }
        ]
      },
      // 商户路由组
      {
        name: 'merchant',
        path: '/merchant',
        redirect: { name: 'merchant-home' },
        children: [
          {
            name: 'merchant-home',
            path: 'home',
            component: 'base$view.merchant-home',
            meta: {
              title: '商户首页',
              requiresAuth: true,
              roles: ['2']
            }
          }
        ]
      }
    ]
  }
];

/**
 * 获取所有路由
 */
export function createBuiltinVueRoutes(): ElegantRoute[] {
  return [...builtinRoutes, ...appRoutes];
}
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
    meta: { title: '登录', hide: true }
  },
  {
    name: 'login',
    path: '/login',
    component: 'login',
    meta: { title: '登录', hide: true }
  },
  {
    name: 'main',
    path: '/main',
    component: 'base',
    children: [
      // 用户路由组 - 实际供商家使用
      {
        name: 'user',
        path: '/user',
        redirect: { name: 'user-home' },
        children: [
          {
            name: 'user-home',
            path: 'home',
            component: 'base$view.user-home',
            meta: { title: '用户首页', requiresAuth: true, roles: ['merchant'] }
          },
          {
            name: 'user-tasks',
            path: 'tasks',
            component: 'base$view.user-tasks',
            meta: { title: '任务列表', requiresAuth: true, roles: ['merchant'] }
          },
          {
            name: 'user-task-execution',
            path: 'task-execution/:id',
            component: 'base$view.user-task-execution',
            meta: { title: '任务执行', requiresAuth: true, roles: ['merchant'] }
          },
          {
            name: 'user-task-live',
            path: 'task-live/:id',
            component: 'base$view.user-task-live',
            meta: { title: '任务直播', requiresAuth: true, roles: ['merchant'] }
          },
          {
            name: 'user-task-result',
            path: 'task-result/:id',
            component: 'base$view.user-task-result',
            meta: { title: '任务结果', requiresAuth: true, roles: ['merchant'] }
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
            meta: { title: '管理员首页', requiresAuth: true, roles: ['admin'] }
          },
          {
            name: 'admin-data-view',
            path: 'data-view',
            component: 'base$view.admin-data-view',
            meta: { title: '数据统计', requiresAuth: true, roles: ['admin'] }
          },
          {
            name: 'admin-user-management',
            path: 'user-management',
            component: 'base$view.admin-user-management',
            meta: { title: '用户管理', requiresAuth: true, roles: ['admin'] }
          }
        ]
      },
      // 商户路由组 - 保持重定向到用户页面
      {
        name: 'merchant',
        path: '/merchant',
        redirect: { name: 'user-home' },
        children: [
          // 商户首页重定向到用户首页
          {
            name: 'merchant-home',
            path: 'home',
            redirect: { name: 'user-home' },
            meta: { title: '商户首页', requiresAuth: true, roles: ['merchant'] }
          },
          // 商户任务列表重定向到用户任务列表
          {
            name: 'merchant-tasks',
            path: 'tasks',
            redirect: { name: 'user-tasks' },
            meta: { title: '任务列表', requiresAuth: true, roles: ['merchant'] }
          },
          // 商户任务执行重定向到用户任务执行
          {
            name: 'merchant-task-execution',
            path: 'task-execution/:id',
            redirect: { name: 'user-task-execution' },
            meta: { title: '任务执行', requiresAuth: true, roles: ['merchant'] }
          },
          // 商户任务直播重定向到用户任务直播
          {
            name: 'merchant-task-live',
            path: 'task-live/:id',
            redirect: { name: 'user-task-live' },
            meta: { title: '任务直播', requiresAuth: true, roles: ['merchant'] }
          },
          // 商户任务结果重定向到用户任务结果
          {
            name: 'merchant-task-result',
            path: 'task-result/:id',
            redirect: { name: 'user-task-result' },
            meta: { title: '任务结果', requiresAuth: true, roles: ['merchant'] }
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
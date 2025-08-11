import type { Router } from 'vue-router';
import { createRouteGuard } from './route';

/**
 * 设置路由守卫
 * @param router 路由实例
 */
export function setupRouterGuard(router: Router) {
  createRouteGuard(router);
}
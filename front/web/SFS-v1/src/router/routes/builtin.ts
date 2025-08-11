import type { ElegantRoute } from '../elegant/types';

/**
 * 根路由
 */
export const ROOT_ROUTE: ElegantRoute = {
  name: 'root',
  path: '/',
  redirect: { name: 'login' }
};

/**
 * 未找到路由
 */
export const NOT_FOUND_ROUTE: ElegantRoute = {
  name: 'not-found',
  path: '/:pathMatch(.*)*',
  component: 'layout.base$view.404'
};

/**
 * 内置路由
 */
export const builtinRoutes: ElegantRoute[] = [ROOT_ROUTE, NOT_FOUND_ROUTE];
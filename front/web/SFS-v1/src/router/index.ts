import {
  createRouter,
  createWebHistory,
  type Router
} from 'vue-router';
import { createBuiltinVueRoutes } from './routes';
import { transformElegantRoutesToVueRoutes } from './elegant/transform';
import { setupRouterGuard } from './guard';

/**
 * 创建Vue Router实例
 */
export function createVueRouter(): Router {
  // 获取路由配置
  const elegantRoutes = createBuiltinVueRoutes();
  
  // 转换为Vue Router格式
  const routes = transformElegantRoutesToVueRoutes(elegantRoutes);
  
  // 创建路由实例
  const router = createRouter({
    history: createWebHistory(),
    routes
  });
  
  // 设置路由守卫
  setupRouterGuard(router);
  
  return router;
}

// 导出路由实例
export const router = createVueRouter();

// 默认导出
export default router;
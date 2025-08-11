import type { RouteRecordRaw } from 'vue-router';
import type { ElegantRoute } from './types';
import { layoutComponents, viewComponents } from './imports';

/**
 * 将优雅路由转换为Vue Router格式
 * @param routes 优雅路由数组
 * @returns Vue Router路由数组
 */
export function transformElegantRoutesToVueRoutes(routes: ElegantRoute[]): RouteRecordRaw[] {
  return routes.map(route => transformElegantRouteToVueRoute(route));
}

/**
 * 将单个优雅路由转换为Vue Router格式
 * @param route 优雅路由
 * @returns Vue Router路由
 */
function transformElegantRouteToVueRoute(route: ElegantRoute): RouteRecordRaw {
  // 使用类型断言确保TypeScript接受这个对象
  const vueRoute = {
    name: route.name,
    path: route.path,
    ...(route.redirect && { redirect: route.redirect })
  } as RouteRecordRaw;

  // 处理组件
  if (route.component) {
    const [layout, view] = route.component.split('$view.');
    
    if (layout && view) {
      // 处理布局和视图组件
      const layoutComponent = layoutComponents[layout as keyof typeof layoutComponents];
      const viewComponent = viewComponents[view as keyof typeof viewComponents];
      
      if (layoutComponent && viewComponent) {
        vueRoute.component = layoutComponent;
        vueRoute.children = [
          {
            path: '',
            name: `${route.name}-wrapper`,
            component: viewComponent
          }
        ];
      }
    } else {
      // 仅视图组件
      const viewComponent = viewComponents[route.component as keyof typeof viewComponents];
      if (viewComponent) {
        vueRoute.component = viewComponent;
      }
    }
  }

  // 处理子路由
  if (route.children) {
    vueRoute.children = transformElegantRoutesToVueRoutes(route.children);
  }

  return vueRoute;
}
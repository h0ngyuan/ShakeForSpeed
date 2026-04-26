/**
 * 优雅路由类型定义
 */
export interface ElegantRoute {
  /** 路由名称 */
  name: string;
  /** 路由路径 */
  path: string;
  /** 重定向 */
  redirect?: { name: string } | string;
  /** 组件 */
  component?: string;
  /** 子路由 */
  children?: ElegantRoute[];
  /** 元数据 */
  meta?: {
    /** 标题 */
    title?: string;
    /** 是否需要权限 */
    requiresAuth?: boolean;
    /** 角色 */
    roles?: string[];
    /** 是否隐藏 */
    hide?: boolean;
  };
}

/**
 * 布局组件类型
 */
export type LayoutComponentType = 'base' | 'blank';

/**
 * 视图组件类型
 */
export type ViewComponentType = '404' | 'login' | 'user-home' | 'user-tasks' | 'user-task-execution' | 'user-task-live' | 'user-task-result' | 'admin-home' | 'admin-data-view' | 'admin-user-management' | 'merchant-home';
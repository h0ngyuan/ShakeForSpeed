// 布局组件
export const layoutComponents = {
  base: () => import('@/layout/MainLayout.vue'),
  blank: () => import('@/layout/BlankLayout.vue')
};

// 视图组件
export const viewComponents = {
  '404': () => import('@/views/exception/404.vue'),
  'login': () => import('@/views/LoginView.vue'),
  'user-home': () => import('@/views/user/HomeView.vue'),
  'user-tasks': () => import('@/views/user/TaskView.vue'),
  'user-task-execution': () => import('@/views/user/TaskExecutionView.vue'),
  'user-task-live': () => import('@/views/user/TaskLiveView.vue'),
  'user-task-result': () => import('@/views/user/TaskResultView.vue'),
  'admin-home': () => import('@/views/admin/AdminHomeView.vue'),
  'admin-data-view': () => import('@/views/admin/DataView.vue'),
  'admin-user-management': () => import('@/views/admin/UserManagementView.vue'),
  'merchant-home': () => import('@/views/merchant/HomeView.vue')
};
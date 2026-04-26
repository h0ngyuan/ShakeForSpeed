// 布局组件
const layoutComponents = {
  base: () => import('@/layout/MainLayout.vue'),
  blank: () => import('@/layout/BlankLayout.vue')
};

// 视图组件
const viewComponents = {
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
  // 商户首页指向用户首页
  'merchant-home': () => import('@/views/user/HomeView.vue')
};

export { layoutComponents, viewComponents };
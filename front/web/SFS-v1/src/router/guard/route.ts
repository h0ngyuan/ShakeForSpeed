import type { Router } from 'vue-router';


export function createRouteGuard(router: Router) {
  router.beforeEach((to, from) => {
    // 根路径直接放行，让路由配置的重定向生效
    if (to.path === '/') {
      return { name: 'login' };
    }
    
    const username = localStorage.getItem('username');
    const roleId = localStorage.getItem('roleId');
    
    // 未登录且不是去登录页，则跳转到登录页
    if (!username && to.name !== 'login') {
      return { name: 'login' };
    }
    
    // 已登录且是去登录页，则根据角色跳转到对应主页
    if (username && to.name === 'login') {
      if (roleId === '1') {
        return { name: 'admin-home' };
      } else if (roleId === '2') {
        return { name: 'merchant-home' };
      } else {
        return { name: 'user-home' };
      }
    }
    
    // 权限验证
    if (to.meta?.roles && roleId) {
      // 将roleId转换为字符串数组进行比较
      const roles = to.meta.roles as string[];
      if (!roles.includes(roleId)) {
        // 没有权限访问，跳转到对应角色的主页
        if (roleId === '1') {
          return { name: 'admin-home' };
        } else if (roleId === '2') {
          return { name: 'merchant-home' };
        } else {
          return { name: 'user-home' };
        }
      }
    }
  });
}
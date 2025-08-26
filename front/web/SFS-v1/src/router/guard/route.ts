import type { Router } from 'vue-router';

export function createRouteGuard(router: Router) {
  router.beforeEach((to, from) => {
    // 根路径直接放行，让路由配置的重定向生效
    if (to.path === '/') {
      console.log('访问根路径，重定向到登录页');
      return { name: 'login' };
    }
    
    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');
    console.log('路由守卫 - 当前role:', role);
    console.log('路由守卫 - 当前路径:', to.path);
    
    console.log('路由守卫 - 当前用户:', username, '角色:', role);
    console.log('路由守卫 - 目标路由:', to.name, '路径:', to.path);
    
    // 未登录且不是去登录页，则跳转到登录页
    if (!username && to.name !== 'login') {
      console.log('未登录，重定向到登录页');
      return { name: 'login' };
    }
    
    // 已登录且是去登录页，则根据角色跳转到对应主页
    if (username && to.name === 'login') {
      console.log('已登录访问登录页，根据角色重定向');
      if (role === 'admin') {
        console.log('管理员角色(admin)，重定向到admin-home');
        return { name: 'admin-home' };
      } else if (role === 'merchant') {
        console.log('商家角色(merchant)，重定向到user-home');
        return { name: 'user-home' };
      } else {
        console.log('其他角色，重定向到user-home');
        return { name: 'user-home' };
      }
    }
    
    // 权限验证
    if (to.meta?.roles && role) {
      // 角色权限验证
      const roles = to.meta.roles as string[];
      console.log('路由守卫 - 目标路由角色要求:', roles);
      if (!roles.includes(role)) {
        console.log('无权限访问，当前角色:', role, '重定向到对应主页');
        // 没有权限访问，跳转到对应角色的主页
        if (role === 'admin') {
          return { name: 'admin-home' };
        } else {
          return { name: 'user-home' };
        }
      } else {
        console.log('有权限访问，放行');
      }
    } else {
      console.log('无需权限验证，放行');
    }
  });
}
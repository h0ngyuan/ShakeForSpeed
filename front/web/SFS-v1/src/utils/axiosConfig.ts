import axios from 'axios';
import { ElMessage } from 'element-plus';

// 创建axios实例
const apiClient = axios.create({
  baseURL: '/api', // 这里的/api会在vite.config.ts中被代理到实际服务器地址
  timeout: 100000, // 请求超时时间
});

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    console.log('请求拦截器:', config.url);
    // 不需要token的接口列表
    const noTokenUrls = ['/auth/login', '/auth/register'];
    
    // 检查当前请求是否需要token
    const needToken = !noTokenUrls.some(url => config.url?.includes(url));
    
    if (needToken) {
      // 从localStorage中获取token
      const token = localStorage.getItem('token');
      console.log('获取到的token:', token);
      
      // 如果token存在，则在请求头中添加Authorization字段
      if (token) {
        // 自动添加 Authorization 头
        config.headers['token'] = 'Bearer_' + token;
        console.log('已添加Authorization头:', config.headers['token']);
      } else {
        console.log('未找到token，请求将不带token');
      }
    } else {
      console.log('不需要token的接口:', config.url);
    }
    
    return config;
  },
  (error: any) => {
    console.error('请求拦截器错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    // 对响应数据做些什么
    console.log('响应拦截器:', response.config.url, response.status);
    return response;
  },
  (error: any) => {
    // 对响应错误做些什么
    console.log('响应错误:', error.response?.status, error.response?.config?.url);
    if (error.response && error.response.status === 401) {
      // token过期或无效，清除本地存储的用户信息
      localStorage.removeItem('username');
      localStorage.removeItem('role');
      localStorage.removeItem('token');
      
      // 提示用户重新登录
      ElMessage.error('登录已过期，请重新登录');
      
      // 跳转到登录页
      window.location.href = '/#/login';
    }
    
    return Promise.reject(error);
  }
);

export default apiClient;
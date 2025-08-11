import axios from 'axios';

// 创建axios实例
const apiClient = axios.create({
  baseURL: '/api', // 这里的/api会在vite.config.ts中被代理到实际服务器地址
  timeout: 10000, // 请求超时时间
});

export default apiClient;
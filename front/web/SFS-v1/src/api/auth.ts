import apiClient from '../utils/axiosConfig';

/**
 * 用户登录
 * @param data 登录数据
 * @returns 登录结果
 */
export const login = (data: { username: string; password: string }) => {
  return apiClient.post('/auth/login', data);
};

/**
 * 用户注册
 * @param data 注册数据
 * @returns 注册结果
 */
export const register = (data: { username: string; password: string; email: string }) => {
  return apiClient.post('/auth/register', data);
};

/**
 * 获取用户信息
 * @returns 用户信息
 */
export const getUserInfo = () => {
  return apiClient.get('/auth/userInfo');
};

/**
 * 用户登出
 * @returns 登出结果
 */
export const logout = () => {
  return apiClient.post('/auth/logout');
};
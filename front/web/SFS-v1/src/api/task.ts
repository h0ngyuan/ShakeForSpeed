import apiClient from '../utils/axiosConfig';

/**
 * 获取任务列表
 * @param params 查询参数
 * @returns 任务列表
 */
export const getTaskList = (params?: { page?: number; pageSize?: number; status?: string }) => {
  return apiClient.get('/activity/queryActivities', { params });
};

/**
 * 获取任务详情
 * @param taskId 任务ID
 * @returns 任务详情
 */
export const getTaskDetail = (taskId: number) => {
  return apiClient.get(`/activity/${taskId}`);
};

/**
 * 创建任务
 * @param data 任务数据
 * @returns 创建结果
 */
export const createTask = (data: any) => {
  return apiClient.post('/activity/createActivity', data);
};

/**
 * 更新任务
 * @param taskId 任务ID
 * @param data 更新数据
 * @returns 更新结果
 */
export const updateTask = (taskId: number, data: any) => {
  return apiClient.put(`/activity/${taskId}`, data);
};

/**
 * 参与任务
 * @param taskId 任务ID
 * @returns 参与结果
 */
export const joinTask = (taskId: number) => {
  return apiClient.post(`/activity/${taskId}/join`);
};

/**
 * 提交任务成绩
 * @param taskId 任务ID
 * @param score 成绩
 * @returns 提交结果
 */
export const submitScore = (taskId: number, score: number) => {
  return apiClient.post(`/activity/${taskId}/score`, { score });
};

/**
 * 获取任务排名
 * @param taskId 任务ID
 * @returns 排名列表
 */
export const getTaskRanking = (taskId: number) => {
  return apiClient.get(`/activity/${taskId}/ranking`);
};
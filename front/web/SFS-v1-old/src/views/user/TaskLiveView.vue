<template>
  <div class="task-live-container">
    <el-card class="task-live-card">
      <div class="task-header">
        <div class="header-info">
          <h1>{{ task.name }}</h1>
          <p class="task-location">{{ task.location }}</p>
          <div v-if="task.imageUrl" class="task-image">
            <img :src="processMinioUrl(task.imageUrl)" alt="任务图片" class="task-img">
          </div>
        </div>
      </div>

      <div class="main-content-wrapper">
        <div class="countdown-section">
          <h2>任务开始倒计时</h2>
          <div v-if="!isTaskStarted">
            <div class="countdown-display">
              <div class="time-block">
                <span class="time-value">{{ countdown.days }}</span>
                <span class="time-label">天</span>
              </div>
              <div class="time-separator">:</div>
              <div class="time-block">
                <span class="time-value">{{ countdown.hours }}</span>
                <span class="time-label">时</span>
              </div>
              <div class="time-separator">:</div>
              <div class="time-block">
                <span class="time-value">{{ countdown.minutes }}</span>
                <span class="time-label">分</span>
              </div>
              <div class="time-separator">:</div>
              <div class="time-block">
                <span class="time-value">{{ countdown.seconds }}</span>
                <span class="time-label">秒</span>
              </div>
            </div>
            <el-button @click="startShaking" class="start-button">开始摇一摇</el-button>
          </div>
          <div v-else class="task-started">
            <el-button type="primary" size="large" @click="startShaking" :disabled="isShaking">
              {{ isShaking ? '摇一摇中...' : '开始摇一摇' }}
            </el-button>
          </div>
        </div>

        <div v-if="task.rewardRules && task.rewardRules.length > 0" class="reward-table">
          <h3>奖励说明</h3>
          <el-table :data="task.rewardRules" border style="width: 100%;">
            <el-table-column prop="range.start" label="排名开始" min-width="80"></el-table-column>
            <el-table-column prop="range.end" label="排名结束" min-width="80"></el-table-column>
            <el-table-column prop="reward" label="奖励名称" min-width="180"></el-table-column>
          </el-table>
        </div>
      </div>

      <div class="task-info">
        <div class="info-item">
          <span class="info-label">开始时间：</span>
          <span>{{ formatTime(task.startTime) }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">持续时间：</span>
          <span>{{ Math.floor(task.duration / 60) }}分{{ task.duration % 60 }}秒</span>
        </div>
        <div class="info-item">
          <span class="info-label">当前人数：</span>
          <span>{{ currentParticipants }}</span>
        </div>
      </div>

      <div class="back-button">
        <el-button @click="goBack">返回任务列表</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';

/**
 * 处理Minio图片URL，确保特殊字符被正确转义
 * @param url 原始URL
 * @returns 转义后的URL
 */
const processMinioUrl = (url: string) => {
  if (!url) return '';
  // 确保URL中的斜杠和特殊字符被正确转义
  return encodeURI(url);
};


import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getTaskDetail, getTaskRanking, joinTask, submitScore } from '../../api/task';
import wsService from '../../api/wsService';

interface RewardRule {
  type: 'range';
  range: { start: number; end: number };
  reward: string;
}

interface Task {
  id: number;
  name: string;
  location: string;
  imageUrl?: string;
  rewardRules: Array<{
    id?: number;
    range: { start: number; end: number };
    reward: string;
  }>;
  createTime: string;
  startTime?: string | Date;
  duration: number;
  status: 'not-started' | 'in-progress' | 'completed';
}

const route = useRoute();
const router = useRouter();
// 确保正确获取路由参数
const taskId = Number(route.params.id);
console.log('TaskLiveView - taskId:', taskId);

// 验证任务ID是否有效
if (isNaN(taskId) || taskId <= 0) {
  ElMessage.error('无效的任务ID');
  router.push('/user/tasks');
}

const task = ref<Task>({
  id: 0,
  name: '',
  location: '',
  rewardRules: [],
  createTime: '',
  duration: 300,
  status: 'not-started'
});

// 实时排名数据
// 修改rankingList的类型定义，使其兼容API返回的数据
const rankingList = ref<Array<{ id?: number; username: string; score: number; isYou: boolean }>>([
  { username: '加载中...', score: 0, isYou: false }
]);

const countdown = ref({
  days: 0,
  hours: 0,
  minutes: 0,
  seconds: 0,
  total: 0
});

const isTaskStarted = ref(false);
const isShaking = ref(false);

// 修复setInterval返回类型问题
let timer: number | undefined = undefined;
let simulationInterval: number | undefined = undefined;

// 从API加载任务数据
const loadTask = async () => {
  try {
    ElMessage.info('加载任务详情中...');
    console.log('TaskLiveView - 调用getTaskDetail，taskId:', taskId);
    const response = await getTaskDetail(taskId);
    console.log('TaskLiveView - getTaskDetail响应:', response);
    if (response && response.data) {
      // 转换后端返回的数据结构以匹配前端Task接口
      task.value = {
        id: response.data.id,
        name: response.data.activityName,
        location: `${response.data.longitude || 0},${response.data.latitude || 0}`,
        rewardRules: response.data.rewards?.map((reward: any) => ({
          id: reward.id,
          range: { start: reward.rankStart, end: reward.rankEnd },
          reward: reward.name
        })) || [],
        createTime: response.data.createTime,
        startTime: response.data.beginTime,
        duration: response.data.durTime || 300,
        status: 'not-started'
      };
      startCountdown();
      // 初始化WebSocket连接
      initWebSocket();
      // 获取初始排名
      loadRanking();
    } else {
      ElMessage.error('获取任务详情失败');
      router.push('/user/tasks');
    }
  } catch (error) {
    console.error('Failed to load task', error);
    ElMessage.error('网络错误，无法加载任务详情');
    router.push('/user/tasks');
  } finally {
    // 移除ElMessage.closeAll()，因为closeAll方法不存在
  }
};

// 加载排名数据
const loadRanking = async () => {
  try {
    const response = await getTaskRanking(taskId);
    if (response.data) {
      // 为API返回的数据添加isYou字段
    const rankingData = response.data.map((item: any) => ({
      id: item.id || 0,
      username: item.username,
      score: item.score,
      isYou: item.isYou || false
    }));
      rankingList.value = rankingData;
    }
  } catch (error) {
    console.error('Failed to load ranking', error);
    ElMessage.error('无法加载排名数据');
  }
};

// 开始倒计时
const startCountdown = () => {
  if (!task.value.startTime) {
    // 如果没有设置开始时间，使用当前时间
    task.value.startTime = new Date();
  }

  const updateCountdown = () => {
    const now = new Date().getTime();
    let startTimeVal = task.value.startTime;
    if (typeof startTimeVal === 'string') {
      startTimeVal = new Date(startTimeVal);
    }
    const startTime = (startTimeVal instanceof Date ? startTimeVal : new Date()).getTime();
    const distance = startTime - now;

    if (distance <= 0) {
      countdown.value = {
        days: 0,
        hours: 0,
        minutes: 0,
        seconds: 0,
        total: 0
      };
      if (timer) {
        clearInterval(timer);
        timer = undefined;
      }
    } else {
      countdown.value = {
        days: Math.floor(distance / (1000 * 60 * 60 * 24)),
        hours: Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)),
        minutes: Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60)),
        seconds: Math.floor((distance % (1000 * 60)) / 1000),
        total: distance
      };
    }
  };

  updateCountdown();
  timer = setInterval(updateCountdown, 1000);
};

// 格式化时间
const formatTime = (timeValue: string | Date | undefined) => {
  if (!timeValue) return '未设置';
  return new Date(timeValue).toLocaleString('zh-CN');
};

// 模拟排名更新
const simulateRankingUpdates = () => {
  // 生成模拟用户数据
  const generateRandomUsers = () => {
    const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十'];
    return names.map((name, index) => ({
      id: Math.floor(Math.random() * 1000),
      username: name,
      score: Math.floor(Math.random() * 100),
      isYou: index === 0 // 第一个用户设为当前用户
    })).sort((a, b) => b.score - a.score);
  };

  // 初始排名
  rankingList.value = generateRandomUsers();

  // 每2秒更新一次排名
  simulationInterval = setInterval(() => {
    if (!isShaking.value) {
      clearInterval(simulationInterval);
      simulationInterval = undefined;
      return;
    }

    // 随机更新一些用户的分数
    rankingList.value = rankingList.value.map(user => ({
      ...user,
      score: user.score + Math.floor(Math.random() * 10)
    })).sort((a, b) => b.score - a.score);

    // 模拟任务结束
    if (Math.random() < 0.05) {
      clearInterval(simulationInterval);
      simulationInterval = undefined;
      isShaking.value = false;
      ElMessage.success('任务已结束，正在跳转到结果页面');
      setTimeout(() => {
        router.push({ name: 'user-task-result', params: { id: task.value.id } });
      }, 1500);
    }
  }, 2000);
};

// 开始摇一摇
const startShaking = async () => {
  if (isShaking.value) return;
  isShaking.value = true;
  isTaskStarted.value = true;
  startCountdown();

  try {
    // 显示摇动提示
    ElMessage.info('请开始摇晃手机');

    // 调用API加入任务
    await joinTask(taskId);

    // 发送加入任务消息到WebSocket
    wsService.send({
      type: 'join_task',
      taskId: task.value.id
    });

    // 设置设备摇动检测
    setupDeviceShakeDetection();
  } catch (error) {
    console.error('Failed to start shaking', error);
    ElMessage.error('无法开始摇一摇，请重试');
    isShaking.value = false;
  }
}

// 添加当前参与人数状态
const currentParticipants = ref(0);

// WebSocket连接初始化
const initWebSocket = () => {
  try {
    // 更新WebSocket连接URL，包含taskId
    const wsUrl = `ws://${window.location.host}/shake-ws/${taskId}`;
    console.log('TaskLiveView - WebSocket URL:', wsUrl);
    wsService.setUrl(wsUrl);

    // 连接WebSocket
    wsService.connect();

    // 添加WebSocket连接状态监听
    wsService.on('open', () => {
      console.log('TaskLiveView - WebSocket连接已建立');
      // 连接成功后发送获取人数请求
      wsService.send({
        type: 'get_participants',
        taskId: task.value.id
      });
    });

    wsService.on('error', (error) => {
      console.error('TaskLiveView - WebSocket错误:', error);
      ElMessage.error('WebSocket连接错误');
    });

    wsService.on('close', () => {
      console.log('TaskLiveView - WebSocket连接已关闭');
    });

    // 订阅人数更新
    wsService.subscribe('participant_update', (data: any) => {
      if (data.count !== undefined) {
        currentParticipants.value = data.count;
      }
    });

    // 订阅排名更新
    wsService.subscribe('ranking_update', (data: any) => {
      if (data.rankings) {
        rankingList.value = data.rankings;
      }
    });

    // 订阅任务结束通知
    wsService.subscribe('task_ended', () => {
      isShaking.value = false;
      ElMessage.success('任务已结束，正在跳转到结果页面');
      setTimeout(() => {
        console.log('TaskLiveView - 任务结束，跳转到结果页面:', task.value.id);
        router.push({ name: 'user-task-result', params: { id: task.value.id } });
      }, 1500);
    });
  } catch (error) {
    console.error('TaskLiveView - 初始化WebSocket失败:', error);
    ElMessage.error('初始化WebSocket连接失败');
  }
};

// 发送摇动数据
const sendShakeData = (intensity: number) => {
  wsService.send({
    type: 'shake',
    taskId: task.value.id,
    intensity
  });
};

// 模拟设备摇动（实际项目中应替换为真实的设备传感器API）
const simulateDeviceShaking = () => {
  let shakeCount = 0;
  const maxShakes = 100; // 模拟的最大摇动次数
  const interval = 100; // 摇动间隔（毫秒）

  const shakeInterval = setInterval(() => {
    if (!isShaking.value || shakeCount >= maxShakes) {
      clearInterval(shakeInterval);
      return;
    }

    // 模拟随机摇动强度
    const intensity = Math.floor(Math.random() * 5) + 1;
    sendShakeData(intensity);
    shakeCount++;

    // 每10次摇动更新一次排名
    if (shakeCount % 10 === 0) {
      loadRanking();
    }
  }, interval);
};

// 实际的设备摇动检测（在移动设备上实现）
const setupDeviceShakeDetection = () => {
  // 这里应该使用设备的加速度传感器API
  // 为简化示例，我们使用模拟方法
  simulateDeviceShaking();
};

// 返回任务列表
const goBack = () => {
  console.log('TaskLiveView - 调用goBack，跳转到/user/tasks');
  router.push('/user/tasks');
};

// 前往结算页面
const goToResult = () => {
  router.push({ name: 'user-task-result', params: { id: task.value.id } });
};

onMounted(() => {
  console.log('TaskLiveView - 组件已挂载');
  loadTask();
});

onUnmounted(() => {
  // 清理倒计时定时器
  if (timer) {
    clearInterval(timer);
    timer = undefined;
  }
  // 断开WebSocket连接
  wsService.disconnect();
  // 取消所有订阅
  wsService.unsubscribeAll();
  // 停止摇动状态
  isShaking.value = false;
  // 停止模拟更新
  if (simulationInterval) {
    clearInterval(simulationInterval);
    simulationInterval = undefined;
  }
});
</script>

<style scoped>
.task-live-container {
  padding: 20px;
  background-color: #ffffff; /* 恢复默认白色背景 */
  height: 850px; /* 设置固定高度 */
  width: 1700px; /* 设置固定宽度 */
  margin: 0 auto; /* 居中显示 */
  overflow: hidden; /* 防止滚动 */
}

.task-live-card {
  width: 100%;
  height: 100%;
  margin: 0;
  border-radius: 16px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.2);
  overflow: hidden;
}

.task-header {
  background-color: #4096ff;
  color: white;
  padding: 20px 30px;
  border-bottom: 4px solid #3a86ff;
}

.header-info {
  display: flex;
  flex-direction: column;
}

.task-header h1 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 600;
}

.task-image {
  margin-top: 15px;
  max-width: 100%;
  overflow: hidden;
  border-radius: 8px;
}

.task-img {
  width: 100%;
  height: auto;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.task-img:hover {
  transform: scale(1.02);
}

.task-location {
  margin-top: 5px;
  font-size: 1rem;
  opacity: 0.9;
}

.main-content-wrapper {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  margin: 30px;
  gap: 20px;
  height: calc(100% - 240px); /* 调整内容区域高度 */
}

.countdown-section {
  flex: 0 0 calc(60% - 10px); /* 扩大倒计时区域 */
  padding: 35px; /* 增加内边距 */
  background-color: #f0f7ff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.countdown-display {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 40px 0; /* 增加间距 */
}

.time-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 0 20px; /* 增加间距 */
  background-color: white;
  padding: 25px 30px; /* 增加内边距 */
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-width: 120px; /* 增加宽度 */
}

.time-value {
  font-size: 3.5rem; /* 增大字体 */
  font-weight: bold;
  color: #333;
  font-family: 'Arial', sans-serif;
}

.time-label {
  font-size: 1.1rem; /* 增大字体 */
  color: #666;
  margin-top: 8px;
}

.time-separator {
  font-size: 3.5rem; /* 增大字体 */
  color: #4096ff;
  font-weight: bold;
  margin: 0 10px;
}

.start-button {
  display: block;
  margin: 0 auto;
  background-color: #4096ff;
  color: white;
  border: none;
  padding: 15px 40px; /* 增加内边距 */
  border-radius: 30px;
  cursor: pointer;
  font-size: 1.2rem; /* 增大字体 */
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(64, 150, 255, 0.3);
}

.reward-table {
  flex: 0 0 calc(40% - 10px); /* 设置奖励表宽度 */
  padding: 25px;
  background-color: #f0f7ff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
}

.reward-table h3 {
  margin-top: 0;
  color: #333;
  font-size: 1.3rem;
  margin-bottom: 20px;
}

.task-info {
  width: 100%;
  padding: 25px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin-top: 20px;
  display: flex;
  justify-content: space-around;
}

.info-item {
  font-size: 1.1rem;
}

.realtime-ranking {
  display: none;
}

.task-info {
  width: 100%;
  padding: 25px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin-top: 20px;
}

.task-prize {
  font-size: 1.3rem;
  color: #e6a23c;
  font-weight: bold;
  margin-top: 15px;
  text-align: center;
  padding: 15px;
  background-color: #fff9e6;
  border-radius: 8px;
  border-left: 4px solid #e6a23c;
}

.task-started {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

/* 排名动画 */
.el-table__row {
  transition: all 0.5s ease;
}

.el-table__row:hover {
  background-color: #f0f7ff !important;
}

/* 手机响应式 */
@media (max-width: 992px) {
  .countdown-section,
  .realtime-ranking {
    flex: 0 0 100%;
  }
}

@media (max-width: 576px) {
  .time-block {
    margin: 0 8px;
    padding: 10px 15px;
    min-width: 60px;
  }

  .time-value {
    font-size: 1.8rem;
  }

  .time-separator {
    font-size: 1.8rem;
  }

  .main-content-wrapper {
    margin: 15px;
  }
}
</style>
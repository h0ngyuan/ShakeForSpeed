<template>
  <div class="task-live-container">
    <el-card class="task-live-card">
      
      
      <div class="main-content-wrapper">
      <div class="countdown-section">
              <h2>任务开始倒计时</h2>
              <div class="countdown-display" v-if="!isTaskStarted" style="font-size: 3rem; padding: 2rem 0;">
          <el-button @click="startShaking" v-if="!isTaskStarted" class="start-button" style="margin-top: 2rem;">开始摇一摇</el-button>
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
            <div v-else class="task-started">
              <el-button type="primary" size="large" @click="startShaking" :disabled="isShaking">
                {{ isShaking ? '摇一摇中...' : '开始摇一摇' }}
              </el-button>
            </div>
          </div>

      <div class="realtime-ranking">
          <el-table :data="rankingList" border style="width: 100%; flex: 1; border-top: none; border-right: none; border-bottom: none;">
            <el-table-column type="index" label="rate" min-width="80"></el-table-column>
            <el-table-column prop="username" label="name" min-width="180"></el-table-column>
            <el-table-column prop="score" label="number" min-width="120"></el-table-column>
          </el-table>
        </div>
    </div>

      <div class="task-info">
        <div class="info-item">
          <span class="info-label">创建时间：</span>
          <span>{{ formatTime(task.createTime) }}</span>
        </div>
        <div v-if="task.startTime" class="info-item">
          <span class="info-label">开始时间：</span>
          <span>{{ formatTime(task.startTime) }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">持续时间：</span>
          <span>{{ Math.floor(task.duration / 60) }}分{{ task.duration % 60 }}秒</span>
        </div>
      </div>

      <div class="back-button">
        <el-button @click="goBack">返回任务列表</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

interface RewardRule {
  type: 'range';
  range: { start: number; end: number };
  reward: string;
}

interface Task {
  id: number;
  name: string;
  location: string;
  startTime?: string | Date;
  duration: number;
  createTime: string;
  status: 'not-started' | 'in-progress' | 'ended';
  rewardRules: Array<{
    type: 'range';
    range: { start: number; end: number };
    reward: string;
  }>;
}

const route = useRoute();
const router = useRouter();
const taskId = Number(route.params.id);

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
const rankingList = ref([
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

let timer: number | null = null;

// 从localStorage加载任务数据
const loadTask = () => {
  const savedTasks = localStorage.getItem('shakeForSpeedTasks');
  if (savedTasks) {
    try {
      const tasks = JSON.parse(savedTasks);
      const foundTask = tasks.find((t: Task) => t.id === taskId);
      if (foundTask) {
        task.value = foundTask;
        startCountdown();
      } else {
        // 任务不存在，返回列表页
        router.push('/user/tasks');
      }
    } catch (e) {
      console.error('Failed to load task', e);
      router.push('/user/tasks');
    }
  } else {
    router.push('/user/tasks');
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
        timer = null;
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
  timer = window.setInterval(updateCountdown, 1000);
};

// 格式化时间
const formatTime = (timeValue: string | Date | undefined) => {
  if (!timeValue) return '未设置';
  return new Date(timeValue).toLocaleString('zh-CN');
};

// 开始摇一摇
const startShaking = () => {
  // 初始化WebSocket连接
  const ws = new WebSocket(`ws://${window.location.host}/shake-ws`);
  ws.onopen = () => {
    console.log('WebSocket连接已建立');
    isTaskStarted.value = true;
    startCountdown();
  };
  ws.onmessage = (event) => {
    const data = JSON.parse(event.data);
    if (data.type === 'ranking_update') {
      rankingList.value = data.rankings;
    }
  };
  ws.onclose = () => {
    console.log('WebSocket连接已关闭');
    if (isTaskStarted.value) {
      // 尝试重连
      setTimeout(() => startShaking(), 3000);
    }
  };
  ws.onerror = (error) => {
    console.error('WebSocket错误:', error);
  };
}

// WebSocket连接初始化
const initWebSocket = () => {
  // 模拟WebSocket连接
  let ws;
  try {
    // 实际项目中替换为真实WebSocket地址
    ws = new WebSocket('ws://localhost:8080/ws/ranking/' + task.value.id);

    ws.onopen = () => {
      console.log('WebSocket连接已建立');
      // 模拟数据更新
      simulateRankingUpdates();
    };

    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        if (data.type === 'ranking_update') {
          rankingList.value = data.ranking;
        } else if (data.type === 'task_ended') {
          // 任务结束，跳转到结算页面
          router.push({ name: 'taskResult', params: { taskId: task.value.id } });
        }
      } catch (e) {
        console.error('解析WebSocket消息失败', e);
      }
    };

    ws.onclose = () => {
      console.log('WebSocket连接已关闭');
      // 尝试重连
      setTimeout(initWebSocket, 3000);
    };

    ws.onerror = (error) => {
      console.error('WebSocket错误:', error);
    };
  } catch (e) {
    console.error('WebSocket初始化失败', e);
    // 降级为模拟数据
    simulateRankingUpdates();
  }
};

// 模拟排名数据更新
const simulateRankingUpdates = () => {
  // 模拟用户列表
  const users = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十'];
  let count = 0;

  const updateInterval = setInterval(() => {
    count++;
    // 生成随机排名数据
    const newRanking = users
      .sort(() => Math.random() - 0.5)
      .slice(0, 8)
      .map((username, index) => ({
        username,
        score: Math.floor(Math.random() * 100) + 50 * count,
        isYou: username === '你'
      }))
      .sort((a, b) => b.score - a.score);

    // 确保当前用户在列表中
    if (!newRanking.some(item => item.isYou)) {
      newRanking[Math.floor(Math.random() * newRanking.length)] = {
        username: '你',
        score: Math.floor(Math.random() * 100) + 50 * count,
        isYou: true
      };
      newRanking.sort((a, b) => b.score - a.score);
    }

    rankingList.value = newRanking;

    // 模拟任务结束（15秒后）
    if (count >= 15) {
      clearInterval(updateInterval);
      // 跳转到结算页面
      router.push({ name: 'taskResult', params: { taskId: task.value.id } });
    }
  }, 1000);
}

// 返回任务列表
const goBack = () => {
  router.push('/user/tasks');
};

// 前往结算页面
const goToResult = () => {
  router.push({ name: 'taskResult', params: { taskId: task.value.id } });
};

onMounted(() => {
  loadTask();
});

onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
});
</script>

<style scoped>
.task-live-container {
  padding: 0;
  margin: 0;
  width: 100%;
  height: 100vh;
  background-color: #fff;
  overflow: hidden;
}

.task-live-card {
  padding: 20px 40px;
  height: calc(100vh - 40px);
  display: flex;
  flex-direction: column;
}

.task-header {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 10px;
  padding: 8px 0;
  background-color: transparent;
  box-shadow: none;
}

.header-info {
  text-align: left;
}

.action-buttons {
  margin-left: auto;
}

.task-header h1 {
  color: #303133;
  margin: 0 0 10px 0;
  font-size: 32px;
}

.task-location {
  color: #606266;
  font-size: 18px;
  margin: 0;
}

.main-content-wrapper {
  display: flex;
  gap: 0;
  margin-bottom: 0;
  flex: 1;
  align-items: stretch;
  width: 100%;
  max-width: 100%;
  margin: 0;
  height: calc(100vh - 60px);
}

.countdown-section {
  text-align: center;
  flex: 2;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0;
  background: transparent;
  border-radius: 0;
  box-shadow: none;
}

.realtime-ranking {
  flex: 1;
  min-width: 0;
  padding: 0;
  background-color: transparent;
  border-radius: 0;
  box-shadow: none;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-left: 2px solid #000;
}
.realtime-ranking .el-table {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 0;
}

.time-display {
  color: #000;
  font-size: 120px;
  font-weight: bold;
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 1;
}

.countdown-display {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-bottom: 30px;
}

.time-block {
  background: transparent;
  color: #000;
  padding: 20px 10px;
  border: none;
  min-width: 80px;
  text-align: center;
  box-shadow: none;
}
.time-block:hover {
  transform: translateY(-5px);
}

.time-value {
  display: block;
  font-size: 72px;
  font-weight: bold;
  line-height: 1.2;
  letter-spacing: -2px;
}

.time-label {
  display: block;
  font-size: 14px;
  margin-top: 5px;
  opacity: 0.9;
}

.time-separator {
  font-size: 56px;
  font-weight: bold;
  color: #303133;
  padding: 0 15px;
}

.task-started {
  margin-top: 30px;
}

.reward-rules {
  margin-bottom: 40px;
}

.realtime-ranking h3 {
  color: #303133;
  margin-bottom: 15px;
  font-size: 20px;
  font-weight: 600;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.reward-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.reward-item {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.reward-info {
  font-size: 16px;
  color: #303133;
}

.task-info {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 15px 30px;
  margin-bottom: 20px;
  padding: 20px 25px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  max-width: 95%;
  margin-left: auto;
  margin-right: auto;
}

.info-item {
  flex: 1;
  min-width: 220px;
  margin-bottom: 0;
  color: #606266;
  font-size: 16px;
}
.info-item span {
  display: inline-block;
  padding: 5px 0;
}

.info-label {
  font-weight: bold;
  color: #303133;
}

.back-button {
  text-align: center;
}

@media (max-width: 992px) {
  .main-content-wrapper {
    flex-direction: column;
  }

  .countdown-section,
  .realtime-ranking {
    width: 100%;
    min-width: auto;
  }
  .task-live-card {
    padding: 20px;
  }
  
  .countdown-display {
    gap: 5px;
  }
  
  .time-block {
    padding: 15px;
    min-width: 60px;
  }
  
  .time-value {
    font-size: 24px;
  }
  
  .time-separator {
    font-size: 24px;
  }
}
</style>
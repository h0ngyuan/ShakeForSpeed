<template>
  <div class="task-result-container">
    <div class="task-header">
      <h2>{{ task.name }} - 结算结果</h2>
      <el-button type="primary" @click="goBack" size="small">返回任务列表</el-button>
    </div>

    <div class="main-horizontal-container">
  <div class="task-info">
      <el-row :gutter="20">
        <el-col :span="6"><strong>任务地点：</strong>{{ task.location }}</el-col>
        <el-col :span="6"><strong>房间号：</strong>{{ task.roomNumber }}</el-col>
        <el-col :span="6"><strong>开始时间：</strong>{{ formatTime(task.startTime) }}</el-col>
        <el-col :span="6"><strong>持续时间：</strong>{{ task.duration }}秒</el-col>
      </el-row>
    </div>
  </div>

    <div class="result-table">
      <h3>参与人奖励列表</h3>
      <el-table :data="resultList" stripe border style="width: 100%">
        <el-table-column type="index" label="排名" width="80" align="center"></el-table-column>
        <el-table-column prop="username" label="参与者" width="180" align="center"></el-table-column>
        <el-table-column prop="score" label="摇一摇次数" width="120" align="center"></el-table-column>
        <el-table-column prop="reward" label="获得奖励" align="center"></el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }"><el-tag type="success">已发放</el-tag></template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { formatTime } from '@/utils/date';

interface Task {
  id: number;
  name: string;
  location: string;
  roomNumber: number;
  startTime: string;
  duration: number;
  rewardRules: Array<{
    range: {
      start: number;
      end: number;
    };
    reward: string;
  }>;
}

interface ResultItem {
  username: string;
  score: number;
  reward: string;
  rank: number;
}

const route = useRoute();
const router = useRouter();
const taskId = route.params.taskId as string;

const task = ref<Task>({
  id: 0,
  name: '',
  location: '',
  roomNumber: 0,
  startTime: '',
  duration: 0,
  rewardRules: []
});

const resultList = ref<ResultItem[]>([]);

// 模拟获取任务结果数据
const fetchTaskResult = () => {
  // 模拟任务数据
  task.value = {
    id: parseInt(taskId),
    name: '春节摇一摇活动',
    location: '北京主会场',
    roomNumber: 1,
    startTime: '2025-07-24 02:00',
    duration: 1200,
    rewardRules: [
      { range: { start: 1, end: 1 }, reward: 'iPhone 15 Pro' },
      { range: { start: 2, end: 3 }, reward: 'AirPods Pro' },
      { range: { start: 4, end: 10 }, reward: '100元红包' },
      { range: { start: 11, end: 50 }, reward: '20元红包' }
    ]
  };

  // 模拟排名结果数据
  const users = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十', '郑一', '王二'];
  resultList.value = users
    .sort(() => Math.random() - 0.5)
    .slice(0, 10)
    .map((username, index) => {
      const rank = index + 1;
      let reward = '参与奖';
      
      // 根据排名匹配奖励
      for (const rule of task.value.rewardRules) {
        if (rank >= rule.range.start && rank <= rule.range.end) {
          reward = rule.reward;
          break;
        }
      }

      return {
        username: index === 0 ? '你' : username,
        score: Math.floor(Math.random() * 500) + 100 * (10 - index),
        reward,
        rank
      };
    })
    .sort((a, b) => a.rank - b.rank);
};

const goBack = () => {
  // 使用命名路由跳转，避免路径变更导致的问题
  router.push({ name: 'tasks' });
};

onMounted(() => {
  fetchTaskResult();
});
</script>

<style scoped>
.task-result-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  box-sizing: border-box;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.main-horizontal-container {
  display: flex;
  gap: 20px;
  flex: 1;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.task-info {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  flex: 1;
  min-width: 300px;
}

.result-table {
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  flex: 2;
  min-width: 550px;
}

.result-table h3 {
  margin-bottom: 15px;
  color: #1f2937;
}

@media (max-width: 920px) {
  .main-horizontal-container {
    flex-direction: column;
  }

  .task-info {
    margin-bottom: 20px;
  }
}
</style>
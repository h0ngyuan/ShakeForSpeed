<template>
  <div class="task-execution-container">
    <h2>任务执行界面</h2>
    <div class="task-header">
      <div class="task-info">
        <p>任务ID: {{ taskId }}</p>
        <p>任务名称: {{ taskName }}</p>
      </div>
      <div class="task-controls">
        <el-button type="primary" size="large" @click="handleStopTask">停止任务</el-button>
        <el-button type="success" size="large" @click="handleFinishTask">完成任务</el-button>
      </div>
    </div>
    <div class="main-content-wrapper">
        <div class="task-timer">
          <h3>任务倒计时</h3>
          <div class="countdown-display">{{ formattedTime }}</div>
        </div>
        <div class="task-ranking">
          <h3>参与排名</h3>
          <el-table :data="rankingList" size="small">
            <el-table-column prop="rank" label="排名" width="80"></el-table-column>
            <el-table-column prop="name" label="参与者" width="160"></el-table-column>
            <el-table-column prop="score" label="成绩"></el-table-column>
          </el-table>
        </div>
      </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

// 路由相关
const route = useRoute();
const router = useRouter();
const taskId = ref(route.params.id as string);
const taskName = ref('');

// 计时器状态
const seconds = ref(0);
// 将 NodeJS.Timeout 改为 number 类型以适配浏览器环境
const timer = ref<number | null>(null);

// 排名数据
const rankingList = ref([
  { rank: 1, name: '参与者A', score: '120次' },
  { rank: 2, name: '参与者B', score: '98次' },
  { rank: 3, name: '参与者C', score: '85次' }
]);

// 格式化时间显示
const formattedTime = computed(() => {
  // 只显示秒数，保留4位数字格式
  return seconds.value.toString().padStart(4, '0');
});

// 获取任务详情
const getTaskDetails = () => {
  // 实际项目中这里应该从API获取任务详情
  taskName.value = `任务${taskId.value}`;
};

// 开始计时器
const startTimer = () => {
  // setInterval 在浏览器环境返回 number 类型的计时器ID
  timer.value = window.setInterval(() => {
    seconds.value++;
  }, 1000);
};

// 停止任务
const handleStopTask = () => {
  if (timer.value) {
    clearInterval(timer.value);
    ElMessage.info('任务已暂停');
  }
};

// 完成任务
const handleFinishTask = () => {
  if (timer.value) {
    clearInterval(timer.value);
  }
  ElMessage.success('任务已完成');
  router.push('/tasks');
};

onMounted(() => {
  getTaskDetails();
  startTimer();
});

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value);
  }
});
</script>

<style scoped>
.task-execution-container {
  padding: 20px;
}

.task-info {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.task-controls {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.task-timer {
  margin-bottom: 20px;
  text-align: center;
}

.countdown-display {
  font-size: 80px;
  font-weight: bold;
  color: #1890ff;
  letter-spacing: 12px;
  margin: 20px 0;
  text-shadow: 0 4px 8px rgba(0,0,0,0.1);
  padding: 20px 0;
  background: linear-gradient(135deg, #f0f5ff 0%, #e6f7ff 100%);
  border-radius: 8px;
  display: inline-block;
  min-width: 320px;
}

.task-timer h3 {
  color: #40a9ff;
  font-size: 22px;
  margin-bottom: 15px;
}

.task-timer {
  margin-bottom: 30px;
  text-align: center;
  padding: 30px;
  background-color: #f0f5ff;
  border-radius: 12px;
}

.task-header {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.main-content-wrapper {
  display: flex;
  gap: 30px;
  align-items: center;
  margin-top: 30px;
}

.task-timer {
  flex: 1;
  min-width: 300px;
}

.task-ranking {
  flex: 2;
  min-width: 500px;
}

@media (max-width: 992px) {
  .main-content-wrapper {
    flex-direction: column;
  }
  .task-timer, .task-ranking {
    width: 100%;
    min-width: auto;
  }
}

.task-controls {
  margin-bottom: 30px;
  display: flex;
  gap: 15px;
  justify-content: center;
}

.task-execution-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.task-ranking {
  margin-top: 40px;
}
</style>
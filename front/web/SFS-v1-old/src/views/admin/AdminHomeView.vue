<template>
  <div class="admin-dashboard">
    <el-card>
      <h2>管理员控制台</h2>
      <!-- 统计卡片区域 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-title">总用户数</div>
            <div class="stat-value">{{ totalUsers }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-title">总活动数</div>
            <div class="stat-value">{{ totalEvents }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-title">总参与者</div>
            <div class="stat-value">{{ totalParticipants }}</div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 折线图区域 -->
      <el-card class="chart-card">
        <h3>过去一个月活动次数</h3>
        <div ref="activityChart" class="chart-container"></div>
      </el-card>

      <!-- 最新活动列表 -->
      <el-card class="recent-events-card">
        <h3>最新活动</h3>
        <el-table :data="recentEvents">
          <el-table-column prop="name" label="活动名称"></el-table-column>
          <el-table-column prop="date" label="日期"></el-table-column>
          <el-table-column prop="participants" label="参与人数"></el-table-column>
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import * as echarts from 'echarts';
import type { ECharts } from 'echarts';

// 模拟统计数据
const totalUsers = 1280;
const totalEvents = 356;
const totalParticipants = 2845;

// 模拟过去一个月活动数据
const generateMonthlyData = () => {
  const data = [];
  const today = new Date();
  for (let i = 29; i >= 0; i--) {
    const date = new Date(today);
    date.setDate(today.getDate() - i);
    data.push({
      date: date.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' }),
      count: Math.floor(Math.random() * 20) + 5
    });
  }
  return data;
};

// 模拟最新活动数据
const recentEvents = [
  { name: '春季马拉松', date: '2023-06-15', participants: 128 },
  { name: '编程大赛', date: '2023-06-10', participants: 89 },
  { name: '音乐节', date: '2023-06-05', participants: 215 },
  { name: '艺术展览', date: '2023-05-30', participants: 76 },
  { name: '慈善跑', date: '2023-05-25', participants: 156 }
];

// 初始化图表
const activityChart = ref<HTMLElement | null>(null);
let chartInstance: ECharts | null = null;

onMounted(() => {
  if (activityChart.value) {
    chartInstance = echarts.init(activityChart.value);
    const monthlyData = generateMonthlyData();

    chartInstance.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: monthlyData.map(item => item.date),
        axisLabel: {
          interval: 2 // 每隔2天显示一个标签，避免拥挤
        }
      },
      yAxis: {
        type: 'value',
        name: '活动次数'
      },
      series: [{
        data: monthlyData.map(item => item.count),
        type: 'line',
        smooth: true,
        itemStyle: {
          color: '#409EFF'
        },
        lineStyle: {
          width: 2
        }
      }]
    });

    // 响应窗口大小变化
    window.addEventListener('resize', () => {
      chartInstance?.resize();
    });
  }
});
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-title {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #1E88E5;
}

.chart-container {
  width: 100%;
  height: 400px;
}

.recent-events-card {
  margin-top: 20px;
}
</style>
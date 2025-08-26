<template>
  <el-table
    :data="tasks"
    stripe
    border
    style="width: 100%"
    :header-cell-style="{ 'background-color': '#f5f7fa', 'font-weight': 'bold' }"
  >
    <el-table-column prop="activityName" label="任务名称" min-width="120" align="center"></el-table-column>
    <el-table-column prop="activityType" label="任务地点" width="120" align="center"></el-table-column>
    <el-table-column label="奖励设置" width="180" align="center">
      <template #default="{ row }">
        <div v-if="row.rewards && row.rewards.length > 0">
          <div v-for="reward in row.rewards" :key="reward.id" class="reward-item">
            <span>{{ reward.name }} (排名: {{ reward.rankStart }}-{{ reward.rankEnd }})</span>
          </div>
        </div>
        <span v-else>无奖励设置</span>
      </template>
    </el-table-column>
    <el-table-column label="创建时间" width="140" align="center">
      <template #default="{ row }">
        <span>{{ formatTime(row.createTime) }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="id" label="房间号" width="80" align="center"></el-table-column>
    <el-table-column label="开始时间" width="140" align="center">
      <template #default="{ row }">
        <span>{{ row.beginTime ? formatTime(row.beginTime) : '未设置' }}</span>
      </template>
    </el-table-column>
    <el-table-column label="持续时间" width="100" align="center">
      <template #default="{ row }">
        <span>{{ Math.floor(row.durTime / 60) }}分{{ row.durTime % 60 }}秒</span>
      </template>
    </el-table-column>
    <el-table-column prop="status" label="状态" width="100" align="center">
      <template #default="{ row }">
        <el-tag :type="row.status === 'ended' ? 'info' : row.status === 'in-progress' ? 'danger' : 'primary'">
          {{ row.status === 'ended' ? '已结束' : row.status === 'in-progress' ? '进行中' : '未开始' }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="操作" width="220" fixed="right" align="center">
      <template #default="{ row }">
        <el-button 
          size="small" 
          :type="row.status === 'not-started' ? 'success' : 'warning'"
          @click="emit('start-task', row)"
          :disabled="row.status === 'ended'"
          style="margin-right: 5px;"
        >
          {{ row.status === 'not-started' ? '进入' : row.status === 'in-progress' ? '继续' : '已结束' }}
        </el-button>
        <el-button size="small" type="primary" @click="emit('edit-task', row)" style="margin-right: 5px;">编辑</el-button>
        <el-button size="small" type="danger" @click="emit('delete-task', row)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';
// 导入统一的接口定义
import type { Activity, Reward } from '@/components/task/TaskForm.vue';

type RewardRule = any;

defineProps<{
  tasks: Activity[];
}>();

const emit = defineEmits<{
  (e: 'start-task', task: Activity): void;
  (e: 'edit-task', task: Activity): void;
  (e: 'delete-task', task: Activity): void;
}>();

const onStartTask = (task: Activity) => {
  emit('start-task', task);
};

const onEditTask = (task: Activity) => {
  emit('edit-task', task);
};

const onDeleteTask = (task: Activity) => {
  emit('delete-task', task);
};

const formatTime = (timeValue: string | Date | undefined) => {
  if (!timeValue) return '未设置';
  const date = new Date(timeValue);
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};
</script>

<style scoped>
.reward-item {
  margin-bottom: 4px;
  padding: 2px 4px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>
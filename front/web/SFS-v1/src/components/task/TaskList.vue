<template>
  <el-table
    :data="tasks"
    stripe
    border
    style="width: 100%"
    :header-cell-style="{ 'background-color': '#f5f7fa', 'font-weight': 'bold' }"
  >
    <el-table-column prop="name" label="任务名称" min-width="120" align="center"></el-table-column>
    <el-table-column prop="location" label="任务地点" width="120" align="center"></el-table-column>
    <el-table-column label="奖励设置" width="180" align="center">
      <template #default="{ row }">
        <div v-if="row.rewardRules && row.rewardRules.length > 0">
          <div v-for="(rule, index) in [row.rewardRules[0]]" :key="index">
            <span v-if="rule.range.start === rule.range.end">
              第{{ rule.range.start }}名：{{ rule.reward }}
            </span>
            <span v-else>
              第{{ rule.range.start }}-{{ rule.range.end }}名：{{ rule.reward }}
            </span>
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
        <span>{{ row.startTime ? formatTime(row.startTime) : '未设置' }}</span>
      </template>
    </el-table-column>
    <el-table-column label="持续时间" width="100" align="center">
      <template #default="{ row }">
        <span>{{ Math.floor(row.duration / 60) }}分{{ row.duration % 60 }}秒</span>
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

interface RewardRule {
  type: 'range';
  range: { start: number; end: number };
  reward: string;
}

interface Task {
  id: number;
  name: string;
  location: string;
  rewardRules: RewardRule[];
  createTime: string;
  startTime?: string | Date;
  duration: number;
  status: 'not-started' | 'in-progress' | 'ended';
}

defineProps<{
  tasks: Task[];
}>();

const emit = defineEmits<{
  (e: 'start-task', task: Task): void;
  (e: 'edit-task', task: Task): void;
  (e: 'delete-task', task: Task): void;
}>();

const onStartTask = (task: Task) => {
  emit('start-task', task);
};

const onEditTask = (task: Task) => {
  emit('edit-task', task);
};

const onDeleteTask = (task: Task) => {
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
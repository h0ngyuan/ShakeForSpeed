<template>
  <div class="task-container">
    <el-card>
      <!-- 顶部操作栏 -->
      <div class="task-header">
        <h2>任务管理</h2>
        <el-button type="primary" @click="handleAddTask">新增任务</el-button>
      </div>

      <!-- 搜索栏 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索任务名称或地点"
            clearable
            @clear="handleSearch"
          />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-col>
      </el-row>

      <!-- 任务列表 -->
      <TaskList
        :tasks="paginatedTasks"
        @start-task="onStartTask"
        @edit-task="onEditTask"
        @delete-task="onDeleteTask"
      />

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="filteredTasks.length"
        :page-sizes="[5, 10, 15, 20]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; text-align: center;"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑任务对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <TaskFormComponent
        ref="taskFormRef"
        :initial-data="currentTask"
        @submit="onFormSubmit"
      />
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveTask">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Search } from '@element-plus/icons-vue';
import TaskList from '@/components/task/TaskList.vue';
import TaskFormComponent from '@/components/task/TaskForm.vue';
import type { TaskForm } from '@/components/task/TaskForm.vue';

const router = useRouter();

// 响应式数据
const tasks = ref<Task[]>([]);

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
const dialogVisible = ref(false);
const taskFormRef = ref<InstanceType<typeof TaskFormComponent>>();
const dialogTitle = ref('新增任务');
const currentTask = reactive<Partial<Task>>({});
const searchKeyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);

// 搜索过滤
const filteredTasks = computed(() => {
  let result = tasks.value;
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(task => 
      task.name.toLowerCase().includes(keyword) || 
      task.location.toLowerCase().includes(keyword)
    );
  }
  return result;
});

// 分页任务
const paginatedTasks = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredTasks.value.slice(start, end);
});

// 模拟数据初始化
onMounted(() => {
  // 开发环境强制使用示例数据
initDefaultTasks();
// 生产环境可恢复localStorage逻辑
// const savedTasks = localStorage.getItem('shakeForSpeedTasks');
// if (savedTasks) {
//   try {
//     tasks.value = JSON.parse(savedTasks);
//   } catch (e) {
//     console.error('Failed to parse saved tasks', e);
//     initDefaultTasks();
//   }
// } else {
//   initDefaultTasks();
// }
});

// 初始化默认任务数据
const initDefaultTasks = () => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  
  tasks.value = [
    {
      id: 1,
      name: '春节摇一摇活动',
      location: '北京主会场',
      rewardRules: [{
        type: 'range',
        range: { start: 88, end: 88 },
        reward: 'iPhone 15 Pro'
      }],
      createTime: new Date().toISOString(),
      startTime: new Date(today.getTime() + 2 * 60 * 60 * 1000).toISOString(), // 2小时后
      duration: 7200, // 2小时
      status: 'not-started'
    },
    {
      id: 2,
      name: '元宵晚会抽奖',
      location: '上海分会场',
      rewardRules: [{
        type: 'range',
        range: { start: 1, end: 10 },
        reward: '红包100元'
      }],
      createTime: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
      startTime: new Date(today.getTime() + 3 * 60 * 60 * 1000).toISOString(), // 3小时后
      duration: 7200, // 2小时
      status: 'not-started'
    },
    {
      id: 3,
      name: '新年祝福活动',
      location: '广州分会场',
      rewardRules: [{
        type: 'range',
        range: { start: 168, end: 168 },
        reward: '精美礼品'
      }],
      createTime: new Date(Date.now() - 48 * 60 * 60 * 1000).toISOString(),
      startTime: new Date(today.getTime() - 2 * 60 * 60 * 1000).toISOString(), // 2小时前
      duration: 3600, // 1小时
      status: 'ended'
    },
    {
      id: 4,
      name: '情人节特别活动',
      location: '成都分会场',
      rewardRules: [{
        type: 'range',
        range: { start: 520, end: 520 },
        reward: '情侣套餐'
      }],
      createTime: new Date(Date.now() - 36 * 60 * 60 * 1000).toISOString(),
      startTime: new Date(today.getTime() + 4 * 60 * 60 * 1000).toISOString(),
      duration: 3600,
      status: 'not-started'
    },
    {
      id: 5,
      name: '女神节福利',
      location: '杭州分会场',
      rewardRules: [{
        type: 'range',
        range: { start: 38, end: 38 },
        reward: '美容礼盒'
      }],
      createTime: new Date(Date.now() - 12 * 60 * 60 * 1000).toISOString(),
      startTime: new Date(today.getTime() + 5 * 60 * 60 * 1000).toISOString(),
      duration: 5400,
      status: 'not-started'
    },
    {
      id: 6,
      name: '五一劳动竞赛',
      location: '深圳分会场',
      rewardRules: [{
        type: 'range',
        range: { start: 1, end: 5 },
        reward: '劳模奖章'
      }],
      createTime: new Date(Date.now() - 60 * 60 * 60 * 1000).toISOString(),
      startTime: new Date(today.getTime() + 6 * 60 * 60 * 1000).toISOString(),
      duration: 7200,
      status: 'not-started'
    },
    {
      id: 7,
      name: '六一儿童节活动',
      location: '武汉分会场',
      rewardRules: [{
        type: 'range',
        range: { start: 61, end: 61 },
        reward: '儿童玩具套装'
      }],
      createTime: new Date(Date.now() - 72 * 60 * 60 * 1000).toISOString(),
      startTime: new Date(today.getTime() + 7 * 60 * 60 * 1000).toISOString(),
      duration: 3600,
      status: 'not-started'
    },
    {
      id: 8,
      name: '中秋团圆活动',
      location: '南京分会场',
      rewardRules: [{
        type: 'range',
        range: { start: 15, end: 15 },
        reward: '月饼礼盒'
      }],
      createTime: new Date(Date.now() - 84 * 60 * 60 * 1000).toISOString(),
      startTime: new Date(today.getTime() + 8 * 60 * 60 * 1000).toISOString(),
      duration: 5400,
      status: 'not-started'
    },
    {
      id: 9,
      name: '国庆欢乐颂',
      location: '重庆分会场',
      rewardRules: [{
        type: 'range',
        range: { start: 101, end: 101 },
        reward: '旅游基金'
      }],
      createTime: new Date(Date.now() - 96 * 60 * 60 * 1000).toISOString(),
      startTime: new Date(today.getTime() + 9 * 60 * 60 * 1000).toISOString(),
      duration: 7200,
      status: 'not-started'
    },
    {
      id: 10,
      name: '双十一秒杀',
      location: '杭州主会场',
      rewardRules: [{
        type: 'range',
        range: { start: 1111, end: 1111 },
        reward: '1111元红包'
      }],
      createTime: new Date(Date.now() - 108 * 60 * 60 * 1000).toISOString(),
      startTime: new Date(today.getTime() + 10 * 60 * 60 * 1000).toISOString(),
      duration: 10800,
      status: 'not-started'
    },
    {
      id: 11,
      name: '双十二特惠',
      location: '上海主会场',
      rewardRules: [{
        type: 'range',
        range: { start: 1212, end: 1212 },
        reward: '购物券'
      }],
      createTime: new Date(Date.now() - 120 * 60 * 60 * 1000).toISOString(),
      startTime: new Date(today.getTime() + 11 * 60 * 60 * 1000).toISOString(),
      duration: 10800,
      status: 'not-started'
    },
    {
      id: 12,
      name: '圣诞狂欢夜',
      location: '北京分会场',
      rewardRules: [{
        type: 'range',
        range: { start: 25, end: 25 },
        reward: '圣诞礼物'
      }],
      createTime: new Date(Date.now() - 132 * 60 * 60 * 1000).toISOString(),
      startTime: new Date(today.getTime() + 12 * 60 * 60 * 1000).toISOString(),
      duration: 7200,
      status: 'not-started'
    },
    {
      id: 13,
      name: '元旦倒计时',
      location: '广州主会场',
      rewardRules: [{
        type: 'range',
        range: { start: 0, end: 0 },
        reward: '新年大礼包'
      }],
      createTime: new Date(Date.now() - 144 * 60 * 60 * 1000).toISOString(),
      startTime: new Date(today.getTime() + 13 * 60 * 60 * 1000).toISOString(),
      duration: 3600,
      status: 'not-started'
    },
    {
      id: 14,
      name: '腊八节活动',
      location: '西安分会场',
      rewardRules: [{
        type: 'range',
        range: { start: 8, end: 8 },
        reward: '腊八粥礼盒'
      }],
      createTime: new Date(Date.now() - 156 * 60 * 60 * 1000).toISOString(),
      startTime: new Date(today.getTime() + 14 * 60 * 60 * 1000).toISOString(),
      duration: 5400,
      status: 'not-started'
    },
    {
      id: 15,
      name: '小年扫尘活动',
      location: '沈阳分会场',
      rewardRules: [{
        type: 'range',
        range: { start: 23, end: 23 },
        reward: '清洁套装'
      }],
      createTime: new Date(Date.now() - 168 * 60 * 60 * 1000).toISOString(),
      startTime: new Date(today.getTime() + 15 * 60 * 60 * 1000).toISOString(),
      duration: 3600,
      status: 'not-started'
    }
  ];
  saveTasksToLocalStorage();
};

// 保存任务数据到localStorage
const saveTasksToLocalStorage = () => {
  localStorage.setItem('shakeForSpeedTasks', JSON.stringify(tasks.value));
};

// 事件处理方法
const handleAddTask = () => {
  dialogTitle.value = '新增任务';
  Object.assign(currentTask, { 
      name: '', 
      location: '', 
      startTime: new Date(),
      duration: 300,
      rewardRules: [{
        type: 'range',
        range: { start: 1, end: 1 },
        reward: ''
      }]
    });
  dialogVisible.value = true;
};

const onEditTask = (task: Task) => {
  dialogTitle.value = '编辑任务';
  Object.assign(currentTask, { ...task });
  dialogVisible.value = true;
};

const handleSaveTask = () => {
  taskFormRef.value?.validateAndSubmit();
};

const onStartTask = (task: Task) => {
  // 使用Vue Router跳转到任务正式页
  router.push({
    name: 'taskLive',
    params: { id: task.id }
  });
};

const onDeleteTask = (task: Task) => {
  tasks.value = tasks.value.filter(t => t.id !== task.id);
  saveTasksToLocalStorage();
};

// 监听表单提交事件
const onFormSubmit = (formData: TaskForm) => {
  if (currentTask.id) {
    // 更新现有任务
    tasks.value = tasks.value.map(task => 
      task.id === currentTask.id ? { ...task, ...formData } : task
    );
    saveTasksToLocalStorage();
  } else {
    // 创建新任务
    tasks.value.push({
      ...formData,
      id: Date.now(),
      createTime: new Date().toISOString(),
      status: 'not-started'
    });
    saveTasksToLocalStorage();
  }
  dialogVisible.value = false;
};

const handleSearch = () => {
  currentPage.value = 1; // 搜索时重置到第一页
};

const handleSizeChange = (val: number) => {
  pageSize.value = val;
  currentPage.value = 1;
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
};
</script>

<style scoped>
.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.task-header h2 {
  margin: 0;
}

.task-container {
  padding: 20px;
}

/* 表格样式优化 */
.el-table {
  margin-top: 15px;
  border-radius: 8px;
  overflow: hidden;
}

.el-table tr:hover > td {
  background-color: #f5f7fa !important;
}

/* 状态标签样式优化 */
.el-tag {
  padding: 4px 8px;
  border-radius: 4px;
}

/* 按钮样式优化 */
.el-button--small {
  padding: 6px 12px;
  margin: 0 4px;
}

/* 对话框样式优化 */
.el-dialog__body {
  padding: 20px;
}

.el-form-item {
  margin-bottom: 18px;
}
</style>
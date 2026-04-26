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
import { ElMessageBox, ElMessage } from 'element-plus';
import TaskList from '@/components/task/TaskList.vue';
import TaskFormComponent from '@/components/task/TaskForm.vue';
import apiClient from '@/utils/axiosConfig';

const router = useRouter();

// 响应式数据
const tasks = ref<Activity[]>([]);

// 从TaskForm组件导入接口定义，确保类型一致
import type { Activity, Reward } from '@/components/task/TaskForm.vue';

const dialogVisible = ref(false);
const taskFormRef = ref<InstanceType<typeof TaskFormComponent>>();
const dialogTitle = ref('新增任务');
const currentTask = reactive<Partial<Activity>>({});
const searchKeyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);

// 搜索过滤
const filteredTasks = computed(() => {
  let result = tasks.value;
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter((task: Activity) => 
      task.activityName.toLowerCase().includes(keyword) || 
      task.activityType.toLowerCase().includes(keyword)
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

// 从后端获取活动数据
const fetchActivities = async () => {
  try {
    const response = await apiClient.post('/activity/queryActivities', {
      activityNameOrId: searchKeyword.value || null,
      pageIndex: currentPage.value,
      pageSize: pageSize.value
    });
    
    if (response.data.code === 200) {
      tasks.value = response.data.data.records.map((item: any) => ({
        ...item,
        activityName: item.activityName,
        activityType: item.activityType,
        durTime: item.durTime,
        beginTime: item.beginTime,
        status: 'not-started', // 简单设置状态，实际应根据时间计算
        // 将rewardId转换为rewards数组格式
        rewards: item.rewardId ? [{ id: item.rewardId.toString(), name: '奖励', image: '', rankStart: 1, rankEnd: 1 }] : []
      }));
    }
  } catch (error) {
    console.error('获取活动数据失败:', error);
  }
};

// 模拟数据初始化
onMounted(() => {
  fetchActivities();
});

// 事件处理方法
const handleAddTask = () => {
  dialogTitle.value = '新增任务';
  Object.assign(currentTask, { 
      activityName: '', 
      activityType: '', 
      beginTime: new Date(),
      durTime: 0,
      rewards: [],
      roomPwd: 0,
      longitude: 0,
      latitude: 0
    });
  dialogVisible.value = true;
};

const onEditTask = (task: Activity) => {
  dialogTitle.value = '编辑任务';
  Object.assign(currentTask, { ...task });
  dialogVisible.value = true;
};

const handleSaveTask = () => {
  taskFormRef.value?.validateAndSubmit();
};

const onStartTask = (task: Activity) => {
  // 使用Vue Router跳转到任务正式页
  router.push({
    name: 'user-task-live',
    params: { id: task.id }
  });
};

const onDeleteTask = (task: Activity) => {
  // 调用Element Plus的确认对话框
  ElMessageBox.confirm(
    '确定要删除这个任务吗？删除后将无法恢复。',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 调用后端删除接口
      const response = await apiClient.post('/activity/deleteActivity', null, {
        params: {
          id: task.id
        }
      });

      if (response.data.code === 200) {
        // 删除成功，从前端列表中移除任务
        tasks.value = tasks.value.filter((t: Activity) => t.id !== task.id);
        ElMessage.success('任务删除成功');
      } else {
        ElMessage.error('任务删除失败: ' + response.data.message);
      }
    } catch (error) {
      console.error('删除任务失败:', error);
      ElMessage.error('删除任务失败，请重试');
    }
  }).catch(() => {
    // 用户取消删除
    ElMessage.info('已取消删除');
  });
};

// 监听表单提交事件
const onFormSubmit = (formData: Activity) => {
  // 这里应该调用后端API保存任务
  // 确保rewards是数组，如果不是则初始化为空数组
  const rewards = Array.isArray(formData.rewards) ? formData.rewards : [];
  
  // 格式化每个reward项，确保包含所有必需字段
  const formattedRewards = rewards.map((reward: Reward) => ({
    id: reward.id || '',
    name: reward.name || '奖励',
    image: reward.image || '',
    rankStart: reward.rankStart || 1,
    rankEnd: reward.rankEnd || 1,
    fileList: reward.fileList || []
  }));

  if (currentTask.id) {
    // 更新现有任务
    tasks.value = tasks.value.map((task: Activity) => 
      task.id === currentTask.id 
        ? { ...task, ...formData, rewards: formattedRewards } 
        : task
    );
  } else {
    // 创建新任务
    tasks.value.push({
      ...formData,
      id: Date.now(),
      createTime: new Date().toISOString(),
      status: 'not-started',
      rewards: formattedRewards
    });
  }
  dialogVisible.value = false;
};

const handleSearch = () => {
  currentPage.value = 1; // 搜索时重置到第一页
  fetchActivities();
};

const handleSizeChange = (val: number) => {
  pageSize.value = val;
  currentPage.value = 1;
  fetchActivities();
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  fetchActivities();
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
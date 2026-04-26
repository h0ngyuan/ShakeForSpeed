<template>
  <div class="data-container">
    <!-- 查询条件 -->
    <el-card class="query-card">
      查询
      
      <el-form :inline="true">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="活动ID">
              <el-input v-model="queryParams.id" placeholder="请输入ID"/>
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="关键词">
              <el-input 
                v-model="queryParams.keyword" 
                placeholder="名称/创建者"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="日期范围">
              <el-date-picker
                v-model="queryParams.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                format="YYYY年MM月DD日"
                value-format="YYYY-MM-DD"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        
        <div class="search-btn">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table :data="filteredActivities" border class="responsive-table" :fit="true" style="width: 100%">
        <el-table-column prop="id" label="活动ID" width="80" align="center"/>
        <el-table-column prop="name" label="活动名称" min-width="150" align="center"/>
        <el-table-column prop="creator" label="创建者" width="100" align="center"/>
        <el-table-column prop="createTime" label="创建时间" min-width="160" align="center"/>
        <el-table-column label="第一名" min-width="120" align="center">
          <template #default="{ row }">
            {{ row.winners[0]?.name || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="reward" label="奖励" min-width="120" align="center"/>
        <el-table-column label="操作" width="140" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="showDetail(row)">详细</el-button>
            <el-button 
              size="small" 
              type="danger"
              @click="handleDelete(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页控件 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[5, 10, 20, 50]"
          :page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
        ></el-pagination>
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="活动详情" width="40%" class="activity-detail-dialog">

      <el-descriptions border :column="2">
        <el-descriptions-item label="活动ID">{{ currentActivity.id }}</el-descriptions-item>
        <el-descriptions-item label="活动名称">
          <template v-if="isEditing">
            <el-input v-model="currentActivity.name" placeholder="请输入活动名称"></el-input>
          </template>
          <template v-else>{{ currentActivity.name }}</template>
        </el-descriptions-item>
        <el-descriptions-item label="创建者">
          <template v-if="isEditing">
            <el-input v-model="currentActivity.creator" placeholder="请输入创建者"></el-input>
          </template>
          <template v-else>{{ currentActivity.creator }}</template>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentActivity.createTime }}</el-descriptions-item>
      </el-descriptions>

      <el-divider/>

      <h4>获奖名单</h4>
      <!-- 优化滚动容器 -->
      <div class="winners-scroll-container">
        <el-table :data="currentActivity.winners" border v-if="!isEditing">
          <el-table-column prop="rank" label="名次" width="80"/>
          <el-table-column prop="name" label="姓名"/>
          <el-table-column prop="reward" label="奖励"/>
        </el-table>
      
        <!-- 编辑模式下的获奖名单表格 -->
        <el-table :data="currentActivity.winners" border v-else>
          <el-table-column prop="rank" label="名次" width="80"/>
          <el-table-column prop="name" label="姓名">
            <template #default="{ row }">
              <el-input v-model="row.name" placeholder="请输入姓名"></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="reward" label="奖励">
            <template #default="{ row }">
              <el-input v-model="row.reward" placeholder="请输入奖励"></el-input>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button 
            v-if="!isEditing"
            type="primary"
            @click="isEditing = true"
          >
            编辑
          </el-button>
          <template v-else>
            <el-button type="success" @click="handleSave">完成</el-button>
            <el-button @click="isEditing = false">取消</el-button>
          </template>
          <el-button @click="detailVisible = false">返回</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import type { Activity } from './types';
import { ElMessage } from 'element-plus';

// 分页变量
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 模拟数据
const mockActivities = ref<Activity[]>([
  {
    id: '1001',
    name: '春季马拉松',
    creator: '大伟',
    createTime: '2023-03-15 09:00',
    winners: [
      { rank: 1, name: '王芳', reward: '奖金5000元' },
      { rank: 2, name: '李强', reward: '奖金3000元' },
      { rank: 3, name: '赵敏', reward: '奖金1000元' }
    ],
    reward: '奖金5000元'
  },
  {
    id: '1002',
    name: '编程大赛',
    creator: '李娜',
    createTime: '2023-04-20 14:30',
    winners: [
      { rank: 1, name: '陈伟', reward: 'MacBook Pro' },
      { rank: 2, name: '周杰', reward: 'iPad Pro' },
      { rank: 3, name: '吴倩', reward: 'AirPods Pro' },
      { rank: 4, name: '李四', reward: 'iPad pencil' },
      { rank: 5, name: '张三', reward: '苹果充电器' },
    ],
    reward: 'MacBook Pro'
  }
])

// 查询参数
const queryParams = ref({
  id: '',
  keyword: '',
  dateRange: [] as string[]
})

// 过滤逻辑
const filteredActivities = computed(() => {
  const filtered = mockActivities.value.filter(activity => {
    const matchesId = queryParams.value.id 
      ? activity.id.includes(queryParams.value.id) 
      : true
    
    const matchesKeyword = queryParams.value.keyword
      ? (activity.name.includes(queryParams.value.keyword) || 
         activity.creator.includes(queryParams.value.keyword))
      : true

    const matchesDate = queryParams.value.dateRange?.length === 2
      ? new Date(activity.createTime) >= new Date(queryParams.value.dateRange[0]) &&
        new Date(activity.createTime) <= new Date(queryParams.value.dateRange[1])
      : true

    return matchesId && matchesKeyword && matchesDate
  })
  
  // 计算总数
  total.value = filtered.length;
  
  // 分页处理
  const startIndex = (currentPage.value - 1) * pageSize.value;
  return filtered.slice(startIndex, startIndex + pageSize.value);
})

// 详情对话框
const detailVisible = ref(false)
const currentActivity = ref<Activity>({} as Activity)
const isEditing = ref(false)

const showDetail = (activity: Activity) => {
  currentActivity.value = { ...activity }
  detailVisible.value = true
  isEditing.value = false
}

const handleDelete = (id: string) => {
  const index = mockActivities.value.findIndex(a => a.id === id)
  if (index !== -1) {
    mockActivities.value.splice(index, 1)
  }
}

const handleSave = () => {
  // 找到原数据并更新
  const index = mockActivities.value.findIndex(a => a.id === currentActivity.value.id)
  if (index !== -1) {
    // 更新原数据
    mockActivities.value.splice(index, 1, { ...currentActivity.value })
    // 显示成功消息
    ElMessage.success('数据已更新')
  }
  isEditing.value = false
}

// 分页事件处理
const handleSizeChange = (size: number) => {
  pageSize.value = size;
};

const handleCurrentChange = (page: number) => {
  currentPage.value = page;
};

const handleSearch = () => {
  // 重置页码
  currentPage.value = 1;
};
</script>

<style scoped>
.data-container {
  padding: 20px;
  box-sizing: border-box;
}

.responsive-table {
  table-layout: auto;
}

/* 添加表格单元格样式防止内容溢出 */
.responsive-table .cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.query-card {
  margin-bottom: 20px;
}

.search-btn {
  text-align: right;
  margin-top: 10px;
}

.table-card {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

/* 对话框样式优化 */
::v-deep .activity-detail-dialog .el-dialog__body {
  padding: 20px;
  max-height: calc(100vh - 200px); /* 限制对话框内容高度 */
  overflow: hidden; /* 隐藏外层滚动 */
}

/* 获奖名单滚动容器优化 */
.winners-scroll-container {
  max-height: calc(100vh - 320px); /* 动态计算可用高度 */
  overflow-y: auto; /* 只显示垂直滚动条 */
  margin-top: 10px;
  padding-right: 5px;
}

/* 滚动条样式优化 */
.winners-scroll-container::-webkit-scrollbar {
  width: 6px;
}

.winners-scroll-container::-webkit-scrollbar-thumb {
  background-color: #ddd;
  border-radius: 3px;
}

.winners-scroll-container::-webkit-scrollbar-track {
  background-color: #f5f5f5;
}
/* 分页容器样式 */
.pagination-container {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}
</style>
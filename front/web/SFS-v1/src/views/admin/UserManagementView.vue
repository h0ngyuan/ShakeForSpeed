<template>
  <div class="user-management-container">
    <!-- 查询条件 -->
    <el-card class="query-card">
      <el-form>
        <!-- 修改：整合搜索条件与按钮到同一行 -->
        <el-row :gutter="22" class="search-row">
          <el-col :span="4">
            <el-form-item label="用户ID">
              <el-input v-model="queryParams.userId" placeholder="请输入用户ID"/>
            </el-form-item>
          </el-col>
          
          <el-col :span="4">
            <el-form-item label="用户名">
              <el-input v-model="queryParams.username" placeholder="请输入用户名"/>
            </el-form-item>
          </el-col>
          
          <el-col :span="4">
            <el-form-item label="用户身份">
              <el-select v-model="queryParams.role" placeholder="">
                <!-- 选项内容保持不变 -->
                <el-option label="管理员" value="admin"></el-option>
                <el-option label="商家" value="merchant"></el-option>
                <el-option label="普通用户" value="user"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="6">
            <el-form-item label="创号时间">
              <el-date-picker
                v-model="queryParams.createTimeRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                format="YYYY年MM月DD日"
                value-format="YYYY-MM-DD"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          
          <!-- 添加：按钮区域 -->
          <el-col :span="4" class="search-buttons">
            <el-form-item style="margin-right: 3px;">
              <el-button type="primary" @click="handleSearch" style="margin-right: 3px;">搜索</el-button>
              <el-button @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="table-card">
      <!-- 修改：移除border属性，添加stripe斑马纹，增加自定义样式类 -->
      <el-table 
        :data="paginatedUsers"
        stripe 
        class="responsive-table custom-table"
        :fit="true"
        style="width: 100%"
      >
        <el-table-column prop="id" label="用户ID" width="100" align="center"/>
        <el-table-column prop="username" label="用户名" min-width="120" align="center"/>
        <el-table-column prop="role" label="用户身份" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.role === 'admin'" type="primary">管理员</el-tag>
            <el-tag v-if="row.role === 'merchant'" type="success">商家</el-tag>
            <el-tag v-if="row.role === 'user'" type="info">普通用户</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创号时间" min-width="160" align="center"/>
        <el-table-column label="活动数量" min-width="140" align="center">
          <template #default="{ row }">
            <template v-if="row.role === 'admin'">-</template>
            <template v-else>
              {{ row.events }} ({{ row.role === 'merchant' ? '举办' : '参与' }})
            </template>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="showDetail(row)">详细</el-button>
            <el-button size="small" type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页控件 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50]"
          :page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
        ></el-pagination>
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="40%">
      <!-- 详情内容 -->
      <el-descriptions border :column="2">
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="用户身份">
          <el-tag v-if="currentUser.role === 'admin'" type="primary">管理员</el-tag>
          <el-tag v-if="currentUser.role === 'merchant'" type="success">商家</el-tag>
          <el-tag v-if="currentUser.role === 'user'" type="info">普通用户</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创号时间">{{ currentUser.createTime }}</el-descriptions-item>
        <el-descriptions-item label="活动数量" :span="2">
          <template v-if="currentUser.role === 'admin'">-</template>
          <template v-if="currentUser.role === 'merchant'">{{ currentUser.events }} 场 (举办)</template>
          <template v-if="currentUser.role === 'user'">{{ currentUser.events }} 场 (参与)</template>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 修改对话框 -->
    <el-dialog v-model="editVisible" title="修改用户" width="40%">
      <el-form ref="editForm" :model="editUser" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editUser.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="用户身份" prop="role">
          <el-select v-model="editUser.role" placeholder="请选择身份">
            <el-option label="管理员" value="admin"></el-option>
            <el-option label="商家" value="merchant"></el-option>
            <el-option label="普通用户" value="user"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';

// 分页变量
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 查询参数
const queryParams = ref({
  userId: '',
  username: '',
  role: '',
  createTimeRange: [] as string[]
});

// 用户类型定义
interface User {
  id: string;
  username: string;
  role: 'admin' | 'merchant' | 'user';
  createTime: string;
  events: number; // 统一使用events参数
}

// 模拟用户数据
const mockUsers = ref<User[]>([
  // 管理员
  {
    id: '1001',
    username: 'admin',
    role: 'admin',
    createTime: '2023-01-01 08:00',
    events: 0 // 管理员events为0
  },
  // 商家
  {
    id: '2001',
    username: '餐饮商家',
    role: 'merchant',
    createTime: '2023-02-15 10:30',
    events: 5 // 原hostedEvents值
  },
  {
    id: '2002',
    username: '零售商家',
    role: 'merchant',
    createTime: '2023-03-20 14:15',
    events: 3 // 原hostedEvents值
  },
  // 普通用户
  {
    id: '3001',
    username: '张三',
    role: 'user',
    createTime: '2023-01-10 09:20',
    events: 8 // 原participatedEvents值
  },
  {
    id: '3002',  // 修改：纯数字ID
    username: '李四',
    role: 'user',
    createTime: '2023-01-15 11:45',
    events: 8
  },
  {
    id: '3003',  // 修改：纯数字ID
    username: '王五',
    role: 'user',
    createTime: '2023-02-05 16:30',
    events: 3
  },
  {
    id: 'user004',
    username: '赵六',
    role: 'user',
    createTime: '2023-02-28 13:10',
    events: 6
  },
  {
    id: 'user005',
    username: '钱七',
    role: 'user',
    createTime: '2023-03-12 10:05',
    events: 2
  },
  {
    id: 'user006',
    username: '孙八',
    role: 'user',
    createTime: '2023-03-25 09:40',
    events: 7
  },
  {
    id: 'user007',
    username: '周九',
    role: 'user',
    createTime: '2023-04-02 15:20',
    events: 4
  },
  {
    id: 'user008',
    username: '吴十',
    role: 'user',
    createTime: '2023-04-15 11:15',
    events: 1
  }
]);

// 过滤逻辑
const filteredUsers = computed(() => {
  return mockUsers.value.filter(user => {
    const matchesUserId = queryParams.value.userId
      ? user.id.includes(queryParams.value.userId)
      : true;

    const matchesUsername = queryParams.value.username
      ? user.username.includes(queryParams.value.username)
      : true;

    const matchesRole = queryParams.value.role
      ? user.role === queryParams.value.role
      : true;

    const matchesDateRange = queryParams.value.createTimeRange?.length === 2
      ? new Date(user.createTime) >= new Date(queryParams.value.createTimeRange[0]) &&
        new Date(user.createTime) <= new Date(queryParams.value.createTimeRange[1])
      : true;

    return matchesUserId && matchesUsername && matchesRole && matchesDateRange;
  });
});

// 分页处理
const paginatedUsers = computed(() => {
  total.value = filteredUsers.value.length;
  const startIndex = (currentPage.value - 1) * pageSize.value;
  return filteredUsers.value.slice(startIndex, startIndex + pageSize.value);
});

// 对话框状态
const detailVisible = ref(false);
const editVisible = ref(false);
const currentUser = ref<User>({} as User);
const editUser = ref<User>({} as User);

// 详情查看
const showDetail = (user: User) => {
  currentUser.value = { ...user };
  detailVisible.value = true;
};

// 修改用户
const handleEdit = (user: User) => {
  editUser.value = { ...user };
  editVisible.value = true;
};

// 保存修改
const handleSaveEdit = () => {
  const index = mockUsers.value.findIndex(u => u.id === editUser.value.id);
  if (index !== -1) {
    mockUsers.value.splice(index, 1, { ...editUser.value });
    ElMessage.success('用户信息已更新');
    editVisible.value = false;
  }
};

// 删除用户
const handleDelete = (id: string) => {
  // 防止删除管理员
  if (id === 'admin001') {
    ElMessage.error('不能删除管理员账户');
    return;
  }

  const index = mockUsers.value.findIndex(u => u.id === id);
  if (index !== -1) {
    mockUsers.value.splice(index, 1);
    ElMessage.success('用户已删除');
  }
};

// 搜索事件
const handleSearch = () => {
  currentPage.value = 1; // 重置页码
};

// 重置搜索
const resetSearch = () => {
  queryParams.value = {
    userId: '',
    username: '',
    role: '',
    createTimeRange: [] as string[]
  };
  currentPage.value = 1;
};

// 分页事件
const handleSizeChange = (size: number) => {
  pageSize.value = size;
};

const handleCurrentChange = (page: number) => {
  currentPage.value = page;
};
</script>

<style scoped>
.user-management-container {
  padding: 20px;
  box-sizing: border-box;
}

.query-card {
  margin-bottom: 20px;
}

.table-card {
  margin-top: 20px;
}

.search-btn {
  margin-top: 15px;
  text-align: right;
}

.pagination-container {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

/* 添加：表格自定义样式 */
.custom-table {
  --el-table-border-color: transparent; /* 隐藏默认边框 */
  --el-table-header-bg-color: #f5f7fa; /* 表头背景色 */
}

/* 优化单元格间距和行高 */
.custom-table .el-table__cell {
  padding: 14px 0;
}

/* 斑马纹行背景色 */
.custom-table .el-table__row--striped {
  background-color: #fafafa;
}

/* 悬停行样式 */
.custom-table .el-table__body tr:hover > td {
  background-color: #f0f7ff;
}
</style>

<style scoped>
/* 添加：搜索行样式优化 */
.search-row {
  align-items: flex-end;
}

/* 添加：按钮区域样式 */
.search-buttons .el-form-item {
  margin-bottom: 0;
  display: flex;
  justify-content: flex-end;
}
</style>
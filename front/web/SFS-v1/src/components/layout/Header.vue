<template>
  <div class="header">
    <!-- 左侧：Logo和项目名称 -->
    <div class="header-left">
      <!-- <img src="@/assets/mono.svg" alt="Logo" class="logo"> -->
      <span class="project-name">ShakeForSpeed</span>
    </div>

    <!-- 右侧：欢迎信息和登出按钮 -->
    <div class="header-right">
      <span class="welcome-text">欢迎, {{ username }}</span>
      <el-button 
        type="text" 
        @click="handleLogout"
        class="logout-btn"
      >
        登出
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();
const username = localStorage.getItem('username') || 'Guest';

const handleLogout = () => {
  localStorage.removeItem('username');
  localStorage.removeItem('role');
  router.push('/login');
};
</script>

<style scoped>
.header {
  display: flex;
  /* 移除space-between，改用margin-left:auto实现更可靠的右对齐 */
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background-color: #ffffff;
  /* 确保header占据全屏宽度 */
  width: 100%;
  box-sizing: border-box;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 关键修改：使用margin-left:auto将右侧元素推到最右 */
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-left: auto;
}

.logo {
  height: 32px;
  width: auto;
  min-width: 32px;
  background-color: #fff;
  border-radius: 4px;
}

.project-name {
  font-size: 18px;
  font-weight: bold;
  color: #000000;
}

.welcome-text {
  color: #000000;
  text-shadow: 0 0 2px rgba(0,0,0,0.5);
}

.logout-btn {
  color: #000000;
  &:hover {
    color: #409EFF;
  }
}
</style>
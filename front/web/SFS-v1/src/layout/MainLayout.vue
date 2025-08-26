<template>
  <el-container class="main-container">
    <el-header class="header">
      <Header :username="username" />
    </el-header>
    <el-container class="content-container">
      <component :is="isAdmin ? AdminAside : UserAside" class="aside" />
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
    <el-footer class="footer">
      <Footer />
    </el-footer>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import Header from '@/components/layout/Header.vue';
import Footer from '@/components/layout/Footer.vue';
import AdminAside from '@/components/layout/AdminAside.vue';
import UserAside from '@/components/layout/UserAside.vue';

// 从localStorage获取角色信息，使用新的字符串角色模式(admin/merchant)
const role = computed(() => localStorage.getItem('role'));
const isAdmin = computed(() => {
  const value = role.value;
  console.log(value);
  if(value === 'admin'){
    return true;
  }else
  return false;
});
const username = computed(() => localStorage.getItem('username'));
</script>

<style scoped>
/* 确保页面使用Arial字体，基础大小16px */
:root {
  font-family: 'Arial', sans-serif;
  font-size: 16px;
}

/* 移除默认边距并设置盒模型 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 确保根元素占满全屏 */
html, body, #app {
  width: 100%;
  height: 100%;
  /* 移除overflow: hidden，允许内容区域滚动 */
}

/* 主容器设置为flex布局并占满高度 */
.main-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

/* 内容容器设置为flex并占满剩余空间 */
.content-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 侧边栏设置固定宽度 */
.aside {
  width: 200px;
  flex-shrink: 0;
  height: 100%;
  overflow-y: auto;
}

/* 主内容区域设置溢出滚动 */
.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

/* 头部和底部设置固定高度 */
.header {
  height: 60px;
  padding: 0;
}

.footer {
  height: 40px;
  padding: 0;
}

/* 确保Element Plus容器正确继承高度 */
:deep(.el-container) {
  width: 100%;
  height: 100%;
}
</style>
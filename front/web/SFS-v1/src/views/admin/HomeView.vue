<template>
  <div class="home">
    <h1>管理员主页</h1>
    <p>欢迎，管理员 {{ username }}！</p>
    <el-button @click="handleLogout">退出登录</el-button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '')

const handleLogout = () => {
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  router.push('/login')
}
</script>

<style scoped>
.home {
  padding: 20px;
}
</style>
<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2>摇一摇管理后台</h2>
      <el-form @submit.prevent="handleLogin">
        <el-form-item>
          <el-input v-model="username" placeholder="用户名" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="password" type="password" placeholder="密码" />
        </el-form-item>
        <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../api/http'

const username = ref('admin')
const password = ref('admin123')
const router = useRouter()

const handleLogin = async () => {
  try {
    const res: any = await http.post('/auth/wx-login', { code: 'mock' })
    localStorage.setItem('token', res.data.token)
    router.push('/')
  } catch (e) {
    router.push('/')
  }
}
</script>

<style scoped>
.login-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #f0f2f5; }
.login-card { width: 400px; padding: 20px; }
.login-card h2 { text-align: center; margin-bottom: 30px; color: #333; }
</style>

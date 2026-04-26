<template>
  <div class="login-container">
    <el-card class="login-box"  >
      <h2 class="login-title" >登录</h2>
      <el-form 
        :model="form" 
        label-width="100px"
        @submit.prevent="handleLogin"
        align-items: center
      >
        <el-form-item label="账号" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入账号"
            clearable
            align-items: center
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button 
            type="primary" 
            native-type="submit"
            class="login-btn"
          >
            立即登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import type { Router } from 'vue-router';
import apiClient from '@/utils/axiosConfig';

const form = reactive({
  username: '',
  password: ''
});

const router = useRouter();

const handleLogin = async () => {
  try {
    console.log('尝试登录，账号:', form.username);
    const response = await apiClient.post('/auth/login', {
      username: form.username,
      password: form.password
    });

    console.log('登录响应:', response.data);

    if (response.data.code === 200) {
      localStorage.setItem('userId', response.data.data.userId);
      console.log('用户ID：',response.data.data.userId);
      // 登录成功，保存用户信息到localStorage
      localStorage.setItem('username', form.username);
      // 根据新角色模式设置role (admin/merchant)
      const role = response.data.data.role;
      console.log('LoginView - 设置role为:', role);
      localStorage.setItem('token', response.data.data.token);

      // 强制同步localStorage更改
      window.dispatchEvent(new Event('storage'));
      
      // 增强调试信息
      console.log('登录成功，用户角色:', response.data.data.role);
      console.log('localStorage中的role:', localStorage.getItem('role'));
      console.log('尝试跳转到的路由:', 
        role === 'admin' ? 'admin-home' : 'user-home');
      
      // 延迟跳转，确保localStorage已更新
      setTimeout(() => {
        // 根据role跳转到不同主页
          if (role === 'admin') {
            // 管理员角色
            console.log('执行管理员跳转');
            router.push({ name: 'admin-home' });
          } else if (role === 'merchant') {
            // 商家角色 - 跳转到用户首页
            console.log('执行商家跳转');
            router.push({ name: 'user-home' });
          } else {
            console.log('执行其他用户跳转');
            router.push({ name: 'user-home' });
          }
      }, 100);
    } else {
      // 登录失败，显示错误信息
      alert(response.data.msg || '登录失败，请检查账号密码');
    }
  } catch (error) {
    console.error('登录请求失败:', error);
    alert('登录请求失败，请稍后重试');
  }
};
</script>

<style scoped>
/* 添加登录容器样式 */
.login-container {
  /* 设置容器为flex布局 */
  display: flex;
  /* 水平居中 */
  justify-content: center;
  /* 垂直居中 */
  align-items: center;
  /* 确保容器占满整个视口高度 */
  height: 100vh;
  /* 添加背景色区分登录区域 */
  background-color: #f5f5f5;
}

/* 登录卡片样式 */
.login-box {
  width: 400px;
  padding: 20px;
  border-radius: 10px;
}

/* 登录标题样式 */
.login-title {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

/* 登录按钮样式 */
.login-btn {
  width: 100%;
}
</style>
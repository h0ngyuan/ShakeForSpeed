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

const form = reactive({
  username: '',
  password: ''
});

const router = useRouter();

const handleLogin = () => {
  if (form.username === 'admin' && form.password === '123456') {
    localStorage.setItem('username', form.username);
    localStorage.setItem('isAdmin', 'true');
    router.push('/admin/home');
  } else if (form.username === 'user' && form.password === '123456') {
    localStorage.setItem('username', form.username);
    localStorage.setItem('isAdmin', 'false');
    router.push('/user/home');
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
  /* 添加内边距 */
  padding: 24px;
  /* 添加阴影效果 */
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 登录标题样式 */
.login-title {
  text-align: center;
  margin-bottom: 20px;
  color: #303133;
}

/* 登录按钮样式 */
.login-btn {
  width: 100%;
}
</style>
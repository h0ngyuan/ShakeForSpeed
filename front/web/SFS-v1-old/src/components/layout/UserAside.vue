<template>
  <el-aside width="200px">
    <el-menu>
      <el-menu-item index="1" @click="$router.push('/user/home')">控制台</el-menu-item>
      <el-menu-item index="2" @click="$router.push('/user/tasks')">任务管理</el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup lang="ts">
// 用户Aside逻辑
</script>
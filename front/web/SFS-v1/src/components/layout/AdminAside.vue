<template>
  <el-aside class="AdminAside">
    <el-menu>
      <el-menu-item index="1" @click="$router.push('/admin-home')">控制台</el-menu-item>
      <el-menu-item index="2" @click="$router.push('/data-view')">数据查看</el-menu-item>
      <el-menu-item index="3" @click="$router.push('/user-management')">用户管理</el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup lang="ts">
// 管理员Aside逻辑
</script>

<style>
.AdminAside { 
  justify-content: center;
  align-items: center;
  width: 150px;
}
</style>
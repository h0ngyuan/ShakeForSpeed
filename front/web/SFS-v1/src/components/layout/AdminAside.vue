<template>
  <el-aside class="AdminAside">
    <el-menu>
      <el-menu-item index="1" @click="$router.push({ name: 'admin-home' })">控制台</el-menu-item>
      <el-menu-item index="2" @click="$router.push({ name: 'admin-data-view' })">数据统计</el-menu-item>
      <el-menu-item index="3" @click="$router.push({ name: 'admin-user-management' })">用户管理</el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup lang="ts">
// 管理员Aside逻辑
</script>

<style>
.AdminAside { 
  width: 200px;
  height: 100%;
  overflow-y: auto;
}
</style>
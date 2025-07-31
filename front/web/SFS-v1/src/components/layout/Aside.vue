<template>
  <el-aside style="width: 150px; background-color: white; box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);">
    <el-menu
      default-active="1"
      class="el-menu-vertical-demo"
      background-color="#ffffff"
      text-color="#333"
      active-text-color="#4285F4"
    >
      <el-menu-item index="1" @click="$router.push('/home')">控制台
      </el-menu-item>
      <el-menu-item index="2" @click="$router.push('/tasks')">任务管理
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup lang="ts">
</script>

<style scoped>
/* 组件样式 */
</style>
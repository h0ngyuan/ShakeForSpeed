<template>
  <div>
    <h2>活动管理</h2>
    <el-table :data="activities" style="margin-top: 20px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="活动名称" />
      <el-table-column prop="state" label="状态" width="100" />
      <el-table-column prop="roomCode" label="房间码" width="100" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/activity/${row.id}`)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
const activities = ref([])
</script>

<style scoped>h2 { margin-bottom: 10px; }</style>

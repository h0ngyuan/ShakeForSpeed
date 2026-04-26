<template>
  <el-container style="min-height: 100vh">
    <el-aside width="200px">
      <el-menu :default-active="$route.path" router background-color="#001529" text-color="#fff">
        <h3 style="color: #fff; text-align: center; padding: 20px 0;">SFS 管理</h3>
        <el-menu-item index="/">数据看板</el-menu-item>
        <el-menu-item index="/activities">活动管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main><router-view /></el-main>
  </el-container>
</template>

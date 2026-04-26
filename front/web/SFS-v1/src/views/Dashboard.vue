<template>
  <div>
    <h2>数据看板</h2>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8"><el-card><h3>进行中活动</h3><p class="num">0</p></el-card></el-col>
      <el-col :span="8"><el-card><h3>总参与人数</h3><p class="num">0</p></el-card></el-col>
      <el-col :span="8"><el-card><h3>奖励发放</h3><p class="num">0</p></el-card></el-col>
    </el-row>
  </div>
</template>

<style scoped>h2 { margin-bottom: 10px; } .num { font-size: 36px; color: #FF6B35; text-align: center; margin-top: 10px; }</style>

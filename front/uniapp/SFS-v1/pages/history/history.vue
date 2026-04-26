<template>
  <view class="history-page">
    <view class="empty" v-if="historyList.length === 0">
      <text>暂无记录</text>
    </view>
    <view class="card" v-for="item in historyList" :key="item.id">
      <text class="activity-name">{{ item.name }}</text>
      <text class="activity-time">{{ item.time }}</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
const historyList = ref([])
</script>

<style scoped>
.history-page { min-height: 100vh; padding: 20rpx; }
.empty { text-align: center; padding: 100rpx 0; color: #999; }
.card { background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 20rpx; }
.activity-name { font-size: 30rpx; color: #333; display: block; margin-bottom: 10rpx; }
.activity-time { font-size: 24rpx; color: #999; }
</style>

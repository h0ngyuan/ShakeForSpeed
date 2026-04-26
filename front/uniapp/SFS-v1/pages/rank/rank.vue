<template>
  <view class="rank-page">
    <view class="rank-header">
      <text class="title">实时排行榜</text>
      <view class="live-dot" :class="{ active: isLive }"></view>
    </view>

    <view class="rank-list">
      <view v-if="rankList.length === 0" class="empty">暂无排行数据</view>
      <view class="rank-card" v-for="item in rankList" :key="item.rank">
        <view class="rank-item" :class="{ 'is-top3': item.rank <= 3 }">
          <text class="rank-num" :class="'rank-' + item.rank">{{ item.rank }}</text>
          <text class="rank-name">{{ item.userId }}</text>
          <text class="rank-score">{{ item.score }} 次</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import api from '../../api/index.js'

const activityId = ref(null)
const rankList = ref([])
const isLive = ref(false)
let timer = null

onLoad((options) => {
  if (options.activityId) {
    activityId.value = options.activityId
  }
})

onMounted(() => {
  if (activityId.value) {
    loadRank()
    timer = setInterval(loadRank, 2000)
    isLive.value = true
  }
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const loadRank = async () => {
  try {
    rankList.value = await api.getRealtimeRank(activityId.value, 50)
  } catch (e) {
    console.error('Failed to load rank:', e)
  }
}
</script>

<style scoped>
.rank-page { min-height: 100vh; padding: 20rpx; }
.rank-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30rpx 0;
}
.title { font-size: 36rpx; font-weight: bold; color: #333; }
.live-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: #999;
  margin-left: 20rpx;
}
.live-dot.active {
  background: #07C160;
  animation: pulse 1s infinite;
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}
.rank-card { margin-bottom: 12rpx; }
.rank-item {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
}
.is-top3 { background: linear-gradient(135deg, #FFF5E6, #FFE8CC); }
.rank-num {
  width: 60rpx;
  font-size: 28rpx;
  color: #999;
  text-align: center;
}
.rank-1 { color: #FFD700; font-weight: bold; }
.rank-2 { color: #C0C0C0; font-weight: bold; }
.rank-3 { color: #CD7F32; font-weight: bold; }
.rank-name { flex: 1; font-size: 30rpx; color: #333; margin: 0 20rpx; }
.rank-score { font-size: 28rpx; color: #FF6B35; font-weight: bold; }
.empty { text-align: center; padding: 100rpx 0; color: #999; }
</style>

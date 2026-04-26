<template>
  <view class="shake-page" :class="{ shaking: isShaking }">
    <view class="shake-content">
      <view class="count-section">
        <text class="count-label">当前摇动次数</text>
        <text class="count-number">{{ shakeCount }}</text>
      </view>

      <view class="shake-circle" @click="triggerShake">
        <text class="circle-text">{{ isShaking ? '继续摇!' : '摇一摇' }}</text>
      </view>

      <view class="rank-preview">
        <text class="rank-title">实时排名</text>
        <view class="rank-item" v-for="item in topRank" :key="item.rank">
          <text class="rank-num" :class="{ top3: item.rank <= 3 }">{{ item.rank }}</text>
          <text class="rank-user">{{ item.userId }}</text>
          <text class="rank-score">{{ item.score }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const roomCode = ref('')
const shakeCount = ref(0)
const isShaking = ref(false)
const topRank = ref([])
let ws = null
let rankTimer = null

onLoad((options) => {
  if (options.roomCode) {
    roomCode.value = options.roomCode
  }
})

onMounted(() => {
  connectWebSocket()
  startRankPolling()
})

onUnmounted(() => {
  if (ws) ws.close()
  if (rankTimer) clearInterval(rankTimer)
})

const connectWebSocket = () => {
  const token = uni.getStorageSync('token')
  const url = `ws://localhost:8080/ws/shake?token=${token}`
  ws = uni.connectSocket({ url })
  ws.onOpen(() => {
    console.log('WebSocket connected')
  })
  ws.onMessage((res) => {
    try {
      const data = JSON.parse(res.data)
      if (data.type === 'rank_update') {
        topRank.value = data.rank.slice(0, 5)
      }
    } catch (e) {}
  })
  ws.onError(() => {
    console.error('WebSocket error')
  })
}

const triggerShake = () => {
  isShaking.value = true
  shakeCount.value++
  if (ws && ws.readyState === 1) {
    ws.send({
      data: JSON.stringify({ action: 'shake', count: shakeCount.value, roomCode: roomCode.value })
    })
  }
  setTimeout(() => { isShaking.value = false }, 300)
}

const startRankPolling = () => {
  rankTimer = setInterval(() => {
    if (ws && ws.readyState === 1) {
      ws.send({ data: JSON.stringify({ action: 'get_rank' }) })
    }
  }, 1000)
}
</script>

<style scoped>
.shake-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #1A1A2E 0%, #16213E 100%);
  transition: transform 0.1s;
}
.shaking { animation: shake 0.3s ease-in-out; }
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-10rpx) rotate(-2deg); }
  75% { transform: translateX(10rpx) rotate(2deg); }
}
.shake-content {
  padding: 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.count-section { text-align: center; margin-bottom: 60rpx; }
.count-label { font-size: 28rpx; color: #888; display: block; margin-bottom: 10rpx; }
.count-number { font-size: 120rpx; font-weight: bold; color: #FF6B35; }
.shake-circle {
  width: 300rpx;
  height: 300rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #FF6B35, #FF8E53);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 60rpx;
  box-shadow: 0 0 60rpx rgba(255, 107, 53, 0.4);
}
.circle-text { font-size: 40rpx; color: #fff; font-weight: bold; }
.rank-preview {
  width: 100%;
  background: rgba(255,255,255,0.1);
  border-radius: 20rpx;
  padding: 30rpx;
}
.rank-title { font-size: 30rpx; color: #fff; font-weight: bold; margin-bottom: 20rpx; display: block; }
.rank-item {
  display: flex;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid rgba(255,255,255,0.1);
}
.rank-num {
  width: 60rpx;
  font-size: 28rpx;
  color: #888;
}
.top3 { color: #FFD700; font-weight: bold; }
.rank-user { flex: 1; font-size: 26rpx; color: #ccc; }
.rank-score { font-size: 30rpx; color: #FF6B35; font-weight: bold; }
</style>

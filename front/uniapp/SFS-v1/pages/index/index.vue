<template>
  <view class="index-page">
    <view class="header">
      <text class="title">摇一摇拼手速</text>
      <view class="user-info" v-if="isLoggedIn">
        <image class="avatar" :src="userInfo.avatar || '/static/avatar/default.png'" />
      </view>
    </view>

    <view class="join-section">
      <text class="section-title">加入活动</text>
      <view class="room-input">
        <input
          class="input"
          v-model="roomCode"
          maxlength="6"
          type="number"
          placeholder="请输入6位房间码"
        />
        <button class="btn-primary" @click="joinRoom" :loading="loading">加入房间</button>
      </view>
    </view>

    <view class="activities-section">
      <text class="section-title">进行中的活动</text>
      <view v-if="activities.length > 0">
        <view class="card activity-card" v-for="item in activities" :key="item.id" @click="goActivity(item)">
          <view class="activity-header">
            <text class="activity-name">{{ item.name }}</text>
            <text class="activity-state" :class="'state-' + item.state">{{ stateText(item.state) }}</text>
          </view>
          <text class="activity-desc">{{ item.description || '暂无描述' }}</text>
          <view class="activity-footer">
            <text class="room-code">房间码: {{ item.roomCode }}</text>
            <text class="time">{{ formatTime(item.beginTime) }}</text>
          </view>
        </view>
      </view>
      <view v-else class="empty">暂无活动</view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import api from '../../api/index.js'

const roomCode = ref('')
const loading = ref(false)
const activities = ref([])
const isLoggedIn = ref(false)
const userInfo = ref({})

const loadActivities = async () => {
  try {
    activities.value = await api.getActivityList({ state: 3, page: 1, size: 10 })
  } catch (e) {
    console.error('Failed to load activities:', e)
  }
}

onMounted(() => {
  const token = uni.getStorageSync('token')
  isLoggedIn.value = !!token
})

onShow(() => {
  const token = uni.getStorageSync('token')
  if (token) {
    loadActivities()
  }
})

const joinRoom = async () => {
  if (roomCode.value.length !== 6) {
    uni.showToast({ title: '请输入6位房间码', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const status = await api.joinRoom(roomCode.value, roomCode.value)
    uni.navigateTo({ url: `/pages/shake/shake?roomCode=${roomCode.value}` })
  } catch (e) {
    uni.showToast({ title: e.message || '加入失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const goActivity = (item) => {
  uni.navigateTo({ url: `/pages/rank/rank?activityId=${item.id}` })
}

const stateText = (state) => {
  const map = { 1: '草稿', 2: '待开始', 3: '进行中', 4: '已结束' }
  return map[state] || '未知'
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}
</script>

<style scoped>
.index-page {
  min-height: 100vh;
  padding: 20rpx;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 10rpx 30rpx;
}
.title { font-size: 40rpx; font-weight: bold; color: #333; }
.avatar { width: 70rpx; height: 70rpx; border-radius: 50%; }
.section-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}
.join-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
}
.room-input { display: flex; gap: 20rpx; align-items: center; }
.input {
  flex: 1;
  height: 88rpx;
  background: #f5f5f5;
  border-radius: 16rpx;
  padding: 0 24rpx;
  font-size: 36rpx;
  text-align: center;
  letter-spacing: 8rpx;
}
.activity-card {
  margin-bottom: 20rpx;
}
.activity-header { display: flex; justify-content: space-between; margin-bottom: 10rpx; }
.activity-name { font-size: 32rpx; font-weight: bold; }
.activity-state { font-size: 24rpx; padding: 4rpx 16rpx; border-radius: 20rpx; }
.state-3 { background: #E6F7ED; color: #07C160; }
.activity-desc { font-size: 26rpx; color: #666; margin-bottom: 16rpx; }
.activity-footer { display: flex; justify-content: space-between; font-size: 24rpx; color: #999; }
.empty { text-align: center; padding: 60rpx 0; color: #999; }
</style>

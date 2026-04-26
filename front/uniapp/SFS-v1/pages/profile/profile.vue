<template>
  <view class="profile-page">
    <view class="profile-header">
      <image class="avatar" :src="userInfo.avatar || '/static/avatar/default.png'" />
      <text class="nickname">{{ userInfo.nickname || '未登录用户' }}</text>
    </view>

    <view class="menu-section">
      <view class="menu-item" @click="goToHistory">
        <text class="menu-icon">📋</text>
        <text class="menu-text">参与历史</text>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @click="goToRewards">
        <text class="menu-icon">🎁</text>
        <text class="menu-text">我的奖励</text>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @click="logout">
        <text class="menu-icon">🚪</text>
        <text class="menu-text">退出登录</text>
        <text class="arrow">></text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../api/index.js'

const userInfo = ref({})

onMounted(() => {
  const token = uni.getStorageSync('token')
  if (!token) {
    uni.reLaunch({ url: '/pages/login/login' })
  }
})

const logout = async () => {
  try {
    await api.logout()
  } catch (e) {}
  uni.removeStorageSync('token')
  uni.reLaunch({ url: '/pages/login/login' })
}

const goToHistory = () => {
  uni.navigateTo({ url: '/pages/history/history' })
}

const goToRewards = () => {
  uni.navigateTo({ url: '/pages/history/history?type=rewards' })
}
</script>

<style scoped>
.profile-page { min-height: 100vh; background: #f5f5f5; }
.profile-header {
  background: linear-gradient(135deg, #FF6B35, #FF8E53);
  padding: 60rpx 40rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.avatar { width: 140rpx; height: 140rpx; border-radius: 50%; margin-bottom: 20rpx; border: 4rpx solid rgba(255,255,255,0.3); }
.nickname { font-size: 36rpx; color: #fff; font-weight: bold; }
.menu-section { margin-top: 20rpx; }
.menu-item {
  background: #fff;
  padding: 30rpx;
  display: flex;
  align-items: center;
  margin-bottom: 2rpx;
}
.menu-icon { font-size: 40rpx; margin-right: 20rpx; }
.menu-text { flex: 1; font-size: 30rpx; color: #333; }
.arrow { color: #ccc; font-size: 28rpx; }
</style>

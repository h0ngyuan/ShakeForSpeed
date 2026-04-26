<template>
  <view class="content">
    <view class="user-info" v-if="userInfo.nickName">
      <image :src="userInfo.avatarUrl" class="avatar"></image>
      <text class="nickname">{{ userInfo.nickName }}</text>
    </view>
    <view class="no-auth" v-else>
      <button type="primary" @click="getUserProfile">授权登录</button>
    </view>
    <view class="user-stats">
      <view class="stat-item">
        <text class="stat-value">{{ totalGames }}</text>
        <text class="stat-label">总参与</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ bestScore }}</text>
        <text class="stat-label">最高分</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ ranking }}</text>
        <text class="stat-label">总排名</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      userInfo: {},
      totalGames: 0,
      bestScore: 0,
      ranking: 0
    };
  },
  onLoad() {
    // 检查是否已授权
    this.checkAuthStatus();
  },
  methods: {
    checkAuthStatus() {
      // 先从本地存储读取用户信息
      const storedUserInfo = uni.getStorageSync('userInfo');
      if (storedUserInfo) {
        this.userInfo = storedUserInfo;
        this.loadUserStats();
        return;
      }
      
      // 本地没有则请求授权
      uni.getUserProfile({
        desc: '用于完善用户资料',
        success: (res) => {
          this.userInfo = res.userInfo;
          // 存储到本地
          uni.setStorageSync('userInfo', res.userInfo);
          this.loadUserStats();
        },
        fail: () => {
          console.log('用户未授权');
        }
      });
    },
    getUserProfile() {
      uni.getUserProfile({
        desc: '用于完善用户资料',
        success: (res) => {
          this.userInfo = res.userInfo;
          // 存储到本地
          uni.setStorageSync('userInfo', res.userInfo);
          this.loadUserStats();
        },
        fail: (err) => {
          console.error('获取用户信息失败', err);
          uni.showToast({
            title: '授权失败',
            icon: 'none'
          });
        }
      });
    },
    loadUserStats() {
      // 模拟加载用户统计数据
      setTimeout(() => {
        this.totalGames = 5;
        this.bestScore = 1250;
        this.ranking = 8;
      }, 500);
    }
  }
};
</script>

<style>
.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 40rpx 0;
}

.avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  margin-bottom: 20rpx;
  border: 4rpx solid #ffffff;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.nickname {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
}

.no-auth {
  margin: 60rpx 0;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  width: 100%;
  margin-top: 40rpx;
  padding: 20rpx 0;
  background-color: #ffffff;
  border-radius: 10rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 40rpx;
  font-weight: bold;
  color: #3498db;
  margin-bottom: 10rpx;
}

.stat-label {
  font-size: 28rpx;
  color: #666666;
}
</style>
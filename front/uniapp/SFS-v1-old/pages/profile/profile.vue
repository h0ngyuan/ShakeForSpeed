<template>
  <view class="profile-container">
    <view class="header">
      <text class="title">个人资料</text>
    </view>
    
    <view class="profile-content" v-if="userInfo.nickName">
      <image :src="userInfo.avatarUrl" class="avatar"></image>
      <text class="nickname">{{ userInfo.nickName }}</text>
      
      <view class="profile-stats">
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
    
    <view class="no-auth" v-else>
      <button type="primary" @click="getUserProfile">授权登录</button>
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
      const storedUserInfo = wx.getStorageSync('userInfo');
      if (storedUserInfo) {
        this.userInfo = storedUserInfo;
        this.loadUserStats();
        return;
      }
    },
    getUserProfile() {
      wx.getUserProfile({
        desc: '用于完善用户资料',
        success: (res) => {
          this.userInfo = res.userInfo;
          // 存储到本地
          wx.setStorageSync('userInfo', res.userInfo);
          this.loadUserStats();
        },
        fail: (err) => {
          console.error('获取用户信息失败', err);
          wx.showToast({
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

<style scoped>
.profile-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx 20rpx;
}

.header {
  width: 100%;
  text-align: center;
  margin-bottom: 40rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
}

.profile-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  margin-bottom: 20rpx;
  border: 4rpx solid #3498db;
  box-shadow: 0 0 20rpx rgba(52, 152, 219, 0.5);
}

.nickname {
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 40rpx;
}

.profile-stats {
  display: flex;
  justify-content: space-around;
  width: 100%;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20rpx;
  padding: 30rpx;
  box-sizing: border-box;
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
  font-size: 24rpx;
  color: #fff;
}

.no-auth {
  margin: 60rpx 0;
}
</style>
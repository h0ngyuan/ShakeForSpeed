<template>
  <view class="content">
    <text class="title">用户中心</text>
    <view class="user-info" v-if="userInfo.id">
      <image :src="userInfo.avatarUrl" class="avatar"></image>
      <text class="nickname">{{ userInfo.nickName }}</text>
    </view>
    <view class="default-user-info" v-else>
      <image src="/static/avatar/default.png" class="avatar"></image>
      <text class="nickname">默认用户</text>
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
      userInfo: {
        id: 0,
        nickName: '',
        avatarUrl: ''
      },
      totalGames: 0,
      bestScore: 0,
      ranking: 0
    };
  },
  onLoad() {
    console.log('用户页面加载成功');
    this.loadUserInfo();
    this.loadUserStats();
  },
  methods: {
    loadUserInfo() {
      try {
        // 从本地存储读取用户信息
        const storedUserInfo = uni.getStorageSync('userInfo');
        if (storedUserInfo && storedUserInfo.id) {
          this.userInfo = storedUserInfo;
        } else {
          // 使用默认用户信息
          this.userInfo = {
            id: 1,
            nickName: '默认用户',
            avatarUrl: '/static/avatar/default.png'
          };
        }
      } catch (error) {
        console.error('读取用户信息失败:', error);
        // 出错时使用默认用户信息
        this.userInfo = {
          id: 1,
          nickName: '默认用户',
          avatarUrl: '/static/avatar/default.png'
        };
      }
    },
    loadUserStats() {
      // 模拟加载用户统计数据
      // 实际项目中这里应该从服务器获取真实数据
      setTimeout(() => {
        this.totalGames = 5;
        this.bestScore = 1250;
        this.ranking = 8;
      }, 500);
    }
  }
}
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

.title {
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 40rpx;
  color: #333333;
}

.user-info, .default-user-info {
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
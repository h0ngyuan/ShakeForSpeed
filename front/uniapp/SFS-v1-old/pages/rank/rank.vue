<template>
  <view class="rank-container">
    <!-- 页面头部 -->
    <view class="rank-header">
      <text class="header-title">排行榜</text>
      <view class="header-actions">
        <button class="action-btn primary" @click="backToHome">返回首页</button>
      </view>
    </view>
    
    <!-- 排行榜列表 -->
    <view class="rank-list">
      <view 
        v-for="(item, index) in rankList" 
        :key="item.id" 
        class="rank-item"
        :class="{ 'top-three': index < 3 }"
      >
        <view class="rank-number">
          <text v-if="index < 3" class="medal">{{ getMedal(index) }}</text>
          <text v-else class="number">{{ index + 1 }}</text>
        </view>
        <view class="user-info">
          <image :src="item.avatar" class="user-avatar" mode="aspectFill"></image>
          <view class="user-details">
            <text class="user-name">{{ item.name }}</text>
            <text class="user-score">得分: {{ item.score }}</text>
          </view>
        </view>
        <view class="user-time">
          <text class="time-label">用时:</text>
          <text class="time-value">{{ item.time }}s</text>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view v-if="rankList.length === 0" class="empty-state">
        <text class="empty-text">暂无排名数据</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      rankList: [
        {
          id: 1,
          name: '张三',
          avatar: '/static/avatar1.png',
          score: 120,
          time: '8.5'
        },
        {
          id: 2,
          name: '李四',
          avatar: '/static/avatar2.png',
          score: 110,
          time: '9.2'
        },
        {
          id: 3,
          name: '王五',
          avatar: '/static/avatar3.png',
          score: 105,
          time: '9.8'
        },
        {
          id: 4,
          name: '赵六',
          avatar: '/static/avatar4.png',
          score: 98,
          time: '10.1'
        },
        {
          id: 5,
          name: '孙七',
          avatar: '/static/avatar5.png',
          score: 92,
          time: '10.5'
        }
      ]
    };
  },
  onLoad() {
    // 页面加载时可以获取真实数据
    // this.fetchRankList();
  },
  methods: {
    // 获取奖牌图标
    getMedal(index) {
      const medals = ['🥇', '🥈', '🥉'];
      return medals[index];
    },
    
    // 返回首页
    backToHome() {
      wx.switchTab({
        url: '/pages/index/index'
      });
    }
  }
};
</script>

<style scoped>
.rank-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20rpx;
  display: flex;
  flex-direction: column;
}

.rank-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 15rpx;
  margin-bottom: 30rpx;
}

.header-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
}

.header-actions {
  display: flex;
  gap: 10rpx;
}

.action-btn {
  background: #fff;
  color: #333;
  border: none;
  padding: 10rpx 20rpx;
  border-radius: 25rpx;
  font-size: 24rpx;
  font-weight: bold;
}

.action-btn.primary {
  background: #3498db;
  color: #fff;
}

.rank-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.rank-item {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 15rpx;
  box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.1);
}

.rank-item.top-three {
  background: linear-gradient(135deg, #f6d365 0%, #fda085 100%);
}

.rank-number {
  width: 80rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}

.medal {
  font-size: 40rpx;
}

.number {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.user-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.user-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  border: 2rpx solid #fff;
  box-shadow: 0 2rpx 5rpx rgba(0, 0, 0, 0.1);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 5rpx;
}

.user-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.user-score {
  font-size: 24rpx;
  color: #666;
}

.user-time {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5rpx;
}

.time-label {
  font-size: 24rpx;
  color: #666;
}

.time-value {
  font-size: 28rpx;
  font-weight: bold;
  color: #3498db;
}

.empty-state {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.empty-text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}
</style>
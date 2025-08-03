<template>
  <view class="history-container">
    <view class="header">
      <text class="title">历史记录</text>
    </view>
    
    <view class="content">
      <view class="history-list">
        <view 
          class="history-item" 
          v-for="(item, index) in historyList" 
          :key="index"
          @click="viewHistoryDetail(item)"
        >
          <view class="item-header">
            <text class="room-number">房间号: {{ item.roomNumber }}</text>
            <text class="date">{{ item.date }}</text>
          </view>
          <view class="item-content">
            <view class="score-info">
              <text class="label">得分:</text>
              <text class="score">{{ item.score }}</text>
            </view>
            <view class="rank-info">
              <text class="label">排名:</text>
              <text class="rank">{{ item.rank }}</text>
            </view>
          </view>
        </view>
      </view>
      
      <view class="empty-state" v-if="historyList.length === 0">
        <image src="/static/empty.png" class="empty-icon"></image>
        <text class="empty-text">暂无历史记录</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      historyList: [
        {
          roomNumber: '12345',
          date: '2023-01-20',
          score: 85,
          rank: 3
        },
        {
          roomNumber: '67890',
          date: '2023-01-19',
          score: 92,
          rank: 1
        }
      ]
    };
  },
  methods: {
    viewHistoryDetail(item) {
      wx.showToast({
        title: `查看房间 ${item.roomNumber} 的详细信息`,
        icon: 'none'
      });
    }
  }
}
</script>

<style scoped>
.history-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20rpx;
}

.header {
  text-align: center;
  padding: 20rpx 0;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
}

.content {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20rpx;
  padding: 30rpx;
  margin-top: 20rpx;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.history-item {
  background: #fff;
  border-radius: 15rpx;
  padding: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.history-item:active {
  transform: scale(0.98);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15rpx;
}

.room-number {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.date {
  font-size: 24rpx;
  color: #999;
}

.item-content {
  display: flex;
  justify-content: space-between;
}

.score-info, .rank-info {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.label {
  font-size: 24rpx;
  color: #666;
}

.score, .rank {
  font-size: 28rpx;
  font-weight: bold;
  color: #3498db;
}

.rank {
  color: #e74c3c;
}

.empty-state {
  text-align: center;
  padding: 100rpx 0;
}

.empty-icon {
  width: 120rpx;
  height: 120rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>
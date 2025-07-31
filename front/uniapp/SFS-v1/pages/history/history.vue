<template>
  <view class="container">
    <view class="header">
      <text class="title">历史记录</text>
    </view>
    <view class="history-list">
      <view class="empty-tip" v-if="historyList.length === 0">
        <text>暂无历史记录</text>
      </view>
      <view class="history-item" v-for="(item, index) in historyList" :key="index">
        <view class="item-info">
          <text class="item-time">{{ item.time }}</text>
          <text class="item-score">得分: {{ item.score }}</text>
        </view>
        <view class="item-rank">
          <text class="rank-text">排名: {{ item.rank }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      historyList: [] // 初始为空，后续可通过API获取数据
    };
  },
  onLoad() {
    // 模拟加载历史记录
    this.loadHistory();
  },
  methods: {
    loadHistory() {
      // 这里可以添加调用API获取历史记录的逻辑
      // 目前使用模拟数据
      setTimeout(() => {
        this.historyList = [
          { time: '2023-12-25 15:30', score: 1250, rank: 3 },
          { time: '2023-12-24 18:45', score: 980, rank: 12 },
          { time: '2023-12-23 10:15', score: 1420, rank: 1 }
        ];
      }, 500);
    }
  }
};
</script>

<style scoped>
.container {
  padding: 20rpx;
  box-sizing: border-box;
}

.header {
  text-align: center;
  margin-bottom: 30rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
}

.history-list {
  width: 100%;
}

.empty-tip {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  border-bottom: 1rpx solid #eee;
}

.item-info {
  display: flex;
  flex-direction: column;
}

.item-time {
  font-size: 28rpx;
  color: #333;
}

.item-score {
  font-size: 28rpx;
  color: #666;
  margin-top: 8rpx;
}

.item-rank {
  background-color: #f0f0f0;
  padding: 8rpx 16rpx;
  border-radius: 16rpx;
}

.rank-text {
  font-size: 28rpx;
  color: #333;
}
</style>
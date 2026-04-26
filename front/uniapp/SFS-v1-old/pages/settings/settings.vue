<template>
  <view class="settings-container">
    <!-- 页面头部 -->
    <view class="settings-header">
      <text class="header-title">设置</text>
      <button class="back-btn" @click="goBack">返回</button>
    </view>
    
    <!-- 设置项列表 -->
    <view class="settings-list">
      <!-- 用户信息 -->
      <view class="settings-item">
        <text class="item-label">用户信息</text>
        <view class="user-info" @click="editUserInfo">
          <image :src="userInfo.avatar" class="user-avatar" mode="aspectFill"></image>
          <view class="user-details">
            <text class="user-name">{{ userInfo.name }}</text>
            <text class="user-id">ID: {{ userInfo.id }}</text>
          </view>
          <text class="arrow">›</text>
        </view>
      </view>
      
      <!-- 关于 -->
      <view class="settings-item" @click="showAbout">
        <text class="item-label">关于</text>
        <text class="arrow">›</text>
      </view>
      
      <!-- 退出登录 -->
      <view class="settings-item logout" @click="logout">
        <text class="item-label">退出登录</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      userInfo: {
        id: '10001',
        name: '默认用户',
        avatar: '/static/default-avatar.png'
      }
    };
  },
  onLoad() {
    // 页面加载时可以获取真实用户信息
    // this.fetchUserInfo();
  },
  methods: {
    // 返回上一页
    goBack() {
      wx.navigateBack();
    },
    
    // 编辑用户信息
    editUserInfo() {
      wx.showToast({
        title: '编辑用户信息',
        icon: 'none'
      });
      // 这里可以跳转到用户信息编辑页面
    },
    
    // 显示关于信息
    showAbout() {
      wx.showModal({
        title: '关于摇摇乐',
        content: '摇摇乐 v1.0.0\n\n一个有趣的摇晃手机游戏\n\nCopyright © 2023',
        showCancel: false,
        confirmText: '确定'
      });
    },
    
    // 退出登录
    logout() {
      wx.showModal({
        title: '退出登录',
        content: '确定要退出当前账号吗？',
        success: (res) => {
          if (res.confirm) {
            wx.showToast({
              title: '已退出登录',
              icon: 'success'
            });
            // 这里可以清除用户信息和跳转到登录页面
            // wx.redirectTo({ url: '/pages/login/login' });
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.settings-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20rpx;
  display: flex;
  flex-direction: column;
}

.settings-header {
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

.back-btn {
  background: #fff;
  color: #333;
  border: none;
  padding: 10rpx 20rpx;
  border-radius: 25rpx;
  font-size: 24rpx;
  font-weight: bold;
}

.settings-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.settings-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 15rpx;
  box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.1);
}

.settings-item.logout {
  background: linear-gradient(135deg, #ff416c, #ff4b2b);
  color: #fff;
  justify-content: center;
}

.item-label {
  font-size: 32rpx;
  color: #333;
}

.settings-item.logout .item-label {
  color: #fff;
  font-weight: bold;
}

.user-info {
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

.user-id {
  font-size: 24rpx;
  color: #666;
}

.arrow {
  font-size: 36rpx;
  color: #999;
}
</style>
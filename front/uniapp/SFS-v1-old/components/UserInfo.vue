<template>
  <view class="user-info-container">
    <image :src="userInfo.avatarUrl" class="user-avatar"></image>
    <text class="user-name">{{userInfo.nickName}}</text>
  </view>
</template>

<script>
export default {
  name: 'UserInfo',
  props: {
    userInfo: {
      type: Object,
      required: true
    }
  }
}
</script>

<style scoped>
.user-info-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.user-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  margin-bottom: 20rpx;
  border: 4rpx solid #3498db;
}

.user-name {
  font-size: 36rpx;
  color: #fff;
  font-weight: bold;
  max-width: 200rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
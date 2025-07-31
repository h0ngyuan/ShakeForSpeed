<template>
  <view class="content">
    <text class="title">用户中心</text>
    <text>这是一个测试页面</text>
  </view>
</template>

<script>
export default {
  data() {
    return {
    };
  },
  onLoad() {
    console.log('用户页面加载成功');
  }
}
</script>

<style>
.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}
</style>
<template>
  <view class="content">
    <text class="title">账户中心</text>
    <text>这是一个全新的账户页面</text>
  </view>
</template>

<script>
export default {
  data() {
    return {
    };
  },
  onLoad() {
    console.log('账户页面加载成功');
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
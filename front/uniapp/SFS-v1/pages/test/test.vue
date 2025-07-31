<template>
  <view class="test-container">
    <text>测试页面</text>
  </view>
</template>

<script>
export default {
  data() {
    return {
    };
  },
  onLoad() {
    console.log('测试页面加载成功');
  }
};
</script>

<style scoped>
.test-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
</style>
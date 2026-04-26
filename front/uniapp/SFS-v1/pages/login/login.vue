<template>
  <view class="login-page">
    <view class="logo-section">
      <image class="logo" src="/static/logo.png" mode="aspectFit" />
      <text class="title">摇一摇拼手速</text>
      <text class="subtitle">拼手速，赢奖品！</text>
    </view>

    <view class="login-section">
      <button class="btn-wx" @click="wxLogin">
        <image class="icon" src="/static/wx-icon.png" mode="aspectFit" />
        微信一键登录
      </button>

      <view class="divider">
        <view class="line"></view>
        <text>或</text>
        <view class="line"></view>
      </view>

      <view class="phone-form">
        <input class="input" v-model="phone" type="number" maxlength="11" placeholder="请输入手机号" />
        <view class="code-row">
          <input class="input flex" v-model="code" type="number" maxlength="6" placeholder="验证码" />
          <button class="btn-code" :class="{ disabled: countdown > 0 }" @click="sendCode">
            {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
          </button>
        </view>
        <button class="btn-primary" @click="phoneLogin">登录</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import api from '../../api/index.js'

const phone = ref('')
const code = ref('')
const countdown = ref(0)

const wxLogin = () => {
  uni.login({
    provider: 'weixin',
    success: async (res) => {
      try {
        const tokenVO = await api.wxLogin(res.code)
        uni.setStorageSync('token', tokenVO.token)
        uni.switchTab({ url: '/pages/index/index' })
      } catch (e) {
        uni.showToast({ title: e.message || '登录失败', icon: 'none' })
      }
    }
  })
}

const sendCode = () => {
  if (countdown.value > 0 || !phone.value || phone.value.length !== 11) {
    return
  }
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
  uni.showToast({ title: '验证码已发送', icon: 'none' })
}

const phoneLogin = async () => {
  if (!phone.value || !code.value) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }
  try {
    const tokenVO = await api.phoneLogin(phone.value, code.value)
    uni.setStorageSync('token', tokenVO.token)
    uni.switchTab({ url: '/pages/index/index' })
  } catch (e) {
    uni.showToast({ title: e.message || '登录失败', icon: 'none' })
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #FF6B35 0%, #FFE5D9 40%, #F5F5F5 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 200rpx;
}
.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 80rpx;
}
.logo {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 20rpx;
}
.title {
  font-size: 48rpx;
  font-weight: bold;
  color: #333;
}
.subtitle {
  font-size: 28rpx;
  color: #666;
  margin-top: 10rpx;
}
.login-section {
  width: 85%;
  background: #fff;
  border-radius: 30rpx;
  padding: 40rpx;
  box-shadow: 0 10rpx 40rpx rgba(255, 107, 53, 0.15);
}
.btn-wx {
  background: #07C160;
  color: #fff;
  border-radius: 40rpx;
  height: 88rpx;
  line-height: 88rpx;
  font-size: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.icon { width: 40rpx; height: 40rpx; margin-right: 10rpx; }
.divider {
  display: flex;
  align-items: center;
  margin: 40rpx 0;
  color: #999;
  font-size: 24rpx;
}
.line { flex: 1; height: 1rpx; background: #eee; }
.phone-form .input {
  height: 88rpx;
  background: #f5f5f5;
  border-radius: 16rpx;
  padding: 0 24rpx;
  font-size: 30rpx;
  margin-bottom: 20rpx;
}
.code-row { display: flex; gap: 20rpx; }
.flex { flex: 1; margin-bottom: 0; }
.btn-code {
  width: 200rpx;
  height: 88rpx;
  line-height: 88rpx;
  background: #FF6B35;
  color: #fff;
  border-radius: 16rpx;
  font-size: 26rpx;
  text-align: center;
  border: none;
}
.btn-code.disabled { background: #ccc; }
.phone-form .btn-primary {
  margin-top: 30rpx;
}
</style>

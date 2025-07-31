<template>
  <view class="game-container">
    <!-- 个人窗口 -->
    <view class="profile-window">
      <UserInfo :userInfo="userInfo" />
    </view>
    
    <view class="game-header">
      <text class="game-title">Shake For Speed</text>
      <text class="timer">{{timer}}s</text>
    </view>
    <view class="shake-meter">
      <view class="meter-label">摇晃次数</view>
      <text class="intensity-value">{{shakeCount}}次</text>
    </view>
    <view class="game-status">{{gameStatus}}</view>
    <!-- 3秒倒计时显示 -->
    <view v-if="countdown > 0" class="countdown-overlay">
      <text class="countdown-text">{{countdown}}</text>
    </view>
    <!-- 模拟WEB端开始按钮 -->
    <view class="simulation-container">
      <button class="simulation-btn" @click="startGameByWeb" :disabled="isPlaying || countdown > 0">模拟WEB端开始</button>
    </view>
    <!-- 添加摇晃动画效果 -->
    <view class="shake-animation" :class="{ 'shake-active': isShaking }"></view>
  </view>
</template>

<script>
import UserInfo from '../../components/UserInfo.vue';
// 导入API函数
import { submitScore } from '../../api/mock.js';

// 函数节流工具
function throttle(fn, gapTime) {
  if (gapTime == null || gapTime == undefined) {
    gapTime = 1500;
  }

  let _lastTime = null;

  // 返回新的函数
  return function () {
    let _nowTime = + new Date();
    if (_nowTime - _lastTime > gapTime || !_lastTime) {
      fn.apply(this, arguments); // 将this和参数传给原函数
      _lastTime = _nowTime;
    }
  }
}

export default {
  components: {
      UserInfo
    },
  onLoad(options) {
    // 尝试从URL参数获取用户信息
    try {
      if (options && options.userInfo) {
        this.userInfo = JSON.parse(decodeURIComponent(options.userInfo));
        console.log('解析用户信息成功:', this.userInfo);
        // 保存到本地存储
        uni.setStorageSync('userInfo', this.userInfo);
      }
    } catch (e) {
      console.error('解析用户信息失败:', e);
    }
    
    // 从本地存储读取用户信息
    const storedUserInfo = uni.getStorageSync('userInfo');
    if (storedUserInfo) {
      this.userInfo = storedUserInfo;
    }
    
    // 初始化游戏状态为"等待WEB端开始..."
    this.gameStatus = '等待WEB端开始...';
    console.log('初始化游戏状态为等待WEB端开始');
  },
  data() {
      // 从本地存储读取用户信息
      const storedUserInfo = uni.getStorageSync('userInfo') || {};
      
      return {
        userInfo: storedUserInfo || {
          id: 1,
          nickName: '默认用户',
          avatarUrl: '/static/avatar/default.png'
        },
        timer: 10, // 倒计时从10开始
        shakeCount: 0, // 摇晃次数
        gameStatus: '正在摇晃...', // 游戏状态：ready(准备), waiting(等待), playing(游戏中), finished(结束)
        isPlaying: false, // 页面加载后不立即开始游戏
        isShow: false, // 页面显示状态
        countdown: 0, // 3秒倒计时
        countdownInterval: null, // 倒计时计时器
        gameTimerInterval: null, // 游戏计时器
        lastShakeTime: 0, // 上次摇晃时间
        lastX: 0, // 上次X轴数据
        lastY: 0, // 上次Y轴数据
        lastZ: 0, // 上次Z轴数据
        isShaking: false, // 用于控制摇晃动画
        shakeThreshold: 1.5, // 调整摇晃阈值，提高灵敏度
        accelerometerCallback: null // 加速度传感器回调引用
      }  },
    
    onShow() {
      console.log('游戏页面显示');
      this.isShow = true;
    },
    
    onHide() {
      console.log('游戏页面隐藏');
      this.isShow = false;
      // 页面隐藏时停止传感器监听
      this.stopSensorListener();
    },
    
  methods: {
    startGame() {
      console.log('开始游戏');
      
      // 重置游戏状态
      this.shakeCount = 0;
      this.timer = 10;
      this.isShaking = false;
      this.isPlaying = true;
      this.gameStatus = '正在摇晃...';
      
      // 启动游戏计时器
      this.gameTimerInterval = setInterval(() => {
        this.timer--;
        console.log('倒计时:', this.timer);
        
        // 检查游戏是否结束
        if (this.timer <= 0) {
          this.stopGame();
        }
      }, 1000);
      
      // 启动传感器监听
      this.startSensorListener();
    },
    
    // 启动3秒倒计时
    startCountdown() {
      this.countdown = 3;
      this.gameStatus = '准备开始...';
      
      const countdownTimer = setInterval(() => {
        this.countdown--;
        
        if (this.countdown <= 0) {
          clearInterval(countdownTimer);
          this.countdown = 0;
          this.startGame();
        }
      }, 1000);
    },
    
    // 启动传感器监听
    startSensorListener() {
      console.log('开始启动传感器监听');
      // 先请求传感器权限，再启动传感器
      this.requestSensorPermissions();
    },
    
    // 安全启动传感器（假设权限已获取）
    safeStartSensors() {
      console.log('安全启动传感器');
      // 设置为游戏模式以获得更高频率的监听
      uni.startAccelerometer({
        interval: 'game',
        success: () => {
          console.log('加速度传感器启动成功');
          // 保存回调引用以便后续移除
          this.accelerometerCallback = (res) => {
            // 检查页面是否显示且游戏是否仍在进行中
            if (this.isShow && this.isPlaying) {
              this.processSensorData(res.x, res.y, res.z);
            }
          };
          uni.onAccelerometerChange(this.accelerometerCallback);
        },
        fail: (error) => {
          console.log('加速度传感器启动失败', error);
          // 显示错误提示
          uni.showToast({
            title: '无法启动传感器，请重试',
            icon: 'none',
            duration: 2000
          });
        }
      });
    },
    
    // 停止传感器监听
    stopSensorListener() {
      uni.stopAccelerometer();
      // 使用保存的回调引用精确移除监听
      if (this.accelerometerCallback) {
        uni.offAccelerometerChange(this.accelerometerCallback);
        this.accelerometerCallback = null;
      }
      console.log('传感器监听已停止');
    },
    
    // 处理传感器数据的通用方法
    processSensorData: throttle(function(x, y, z) {
      // 计算各轴的变化量
      const deltaX = Math.abs(x - this.lastX);
      const deltaY = Math.abs(y - this.lastY);
      const deltaZ = Math.abs(z - this.lastZ);
      
      // 参考文章中的判断方式，任一轴变化超过阈值即计数
      if (deltaX > this.shakeThreshold || deltaY > this.shakeThreshold || deltaZ > this.shakeThreshold) {
        this.shakeCount++;
        
        // 触发摇晃动画
        this.triggerShakeAnimation();
        
        // 即时反馈
        uni.vibrateShort();
      }
      
      // 更新上次的值
      this.lastX = x;
      this.lastY = y;
      this.lastZ = z;
    }, 100),
    
    stopGame() {
      console.log('停止游戏');
      
      // 设置游戏状态为结束
      this.isPlaying = false;
      this.gameStatus = '游戏结束';
      
      // 清除游戏计时器
      if (this.gameTimerInterval) {
        clearInterval(this.gameTimerInterval);
        this.gameTimerInterval = null;
      }
      if (this.countdownInterval) {
        clearInterval(this.countdownInterval);
        this.countdownInterval = null;
      }
      
      // 停止传感器监听
      this.stopSensorListener();
      
      // 提交分数
      this.submitScore();
    },
    
    // 开始游戏（由WEB端触发）
    startGameByWeb() {
      if (this.isPlaying || this.countdown > 0) return;
      
      console.log('WEB端开始游戏');
      
      // 启动3秒倒计时
      this.startCountdown();
    },
    
    // 请求传感器权限
    requestSensorPermissions() {
      // 先请求加速度传感器权限
      uni.authorize({
        scope: 'scope.accelerometer',
        success: () => {
          console.log('传感器权限获取成功');
          // 权限获取成功后安全启动传感器
          this.safeStartSensors();
        },
        fail: (error) => {
          console.log('传感器权限获取失败', error);
          // 权限获取失败时显示错误提示并引导用户手动授权
          uni.showToast({
            title: '需要传感器权限才能游戏',
            icon: 'none',
            duration: 2000
          });
          // 引导用户手动授权
          setTimeout(() => {
            uni.showModal({
              title: '权限申请',
              content: '需要加速度传感器权限才能游戏，请前往设置开启',
              confirmText: '去设置',
              cancelText: '取消',
              success: (res) => {
                if (res.confirm) {
                  uni.openSetting();
                }
              }
            });
          }, 2000);
        }
      });
    },
    
    // 尝试启动传感器（已弃用，请使用requestSensorPermissions + safeStartSensors）
    tryStartSensors() {
      console.log('直接尝试启动传感器');
      // 设置为游戏模式以获得更高频率的监听
      uni.startAccelerometer({
        interval: 'game',
        success: () => {
          console.log('加速度传感器启动成功');
          // 保存回调引用以便后续移除
          this.accelerometerCallback = (res) => {
            // 添加调试日志
            // console.log('加速度数据:', res);
            // 检查页面是否显示且游戏是否仍在进行中
            if (this.isShow && this.isPlaying) {
              this.processSensorData(res.x, res.y, res.z);
            }
          };
          uni.onAccelerometerChange(this.accelerometerCallback);
        },
        fail: (error) => {
          console.log('加速度传感器启动失败', error);
          // 显示通用错误提示
          uni.showToast({
            title: '无法启动传感器，请检查权限设置',
            icon: 'none',
            duration: 2000
          });
          // 引导用户手动授权
          setTimeout(() => {
            uni.showModal({
              title: '权限申请',
              content: '需要加速度传感器权限才能游戏，请前往设置开启',
              confirmText: '去设置',
              cancelText: '取消',
              success: (res) => {
                if (res.confirm) {
                  uni.openSetting();
                }
              }
            });
          }, 2000);
        }
      });
    },
    
    // 提交分数到排行榜
    async submitScore() {
      try {
        const result = await submitScore(this.shakeCount, this.userInfo);
        
        if (result.success) {
          console.log('分数提交成功');
          // 可以在这里显示提交成功的提示
        }
      } catch (error) {
        console.error('分数提交失败', error);
        // 即使提交失败，也继续跳转到排行榜页面
      }
      
      // 跳转到排行榜页面，并传递用户信息
      uni.redirectTo({
        url: '/pages/rank/rank?userInfo=' + encodeURIComponent(JSON.stringify(this.userInfo))
      });
    },
    // 触发摇shake动画的方法
    triggerShakeAnimation() {
      this.isShaking = true;
      setTimeout(() => {
        this.isShaking = false;
      }, 300); // 缩短动画持续时间
    },
  
  onUnload() {
    // 页面卸载时确保停止所有监听
    if (this.gameTimerInterval) {
      clearInterval(this.gameTimerInterval);
      this.gameTimerInterval = null;
    }
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
      this.countdownInterval = null;
    }
    this.stopSensorListener();
  }
}
}
</script>

<style scoped>
.game-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  min-height: 100vh;
  padding: 20px;
  box-sizing: border-box;
  background-color: #f5f5f5;
  position: relative;
  overflow: hidden;
}

/* 个人窗口样式 */
.profile-window {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 10;
  background-color: rgba(255, 255, 255, 0.9);
  padding: 10px;
  border-radius: 30rpx;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.profile-window ::v-deep .user-info-container {
  flex-direction: row;
  align-items: center;
}

.profile-window ::v-deep .user-avatar {
  width: 60rpx;
  height: 60rpx;
  margin-right: 15rpx;
  margin-bottom: 0;
  border: none;
}

.profile-window ::v-deep .user-name {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
  max-width: 200rpx;
}

.game-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.user-info {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.user-avatar {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  margin-right: 20rpx;
}

.user-name {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
  max-width: 200rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.game-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.timer {
  font-size: 20px;
  color: #ff4d4f;
  font-weight: bold;
}

.shake-meter {
  width: 100%;
  margin: 50px 0;
}

.meter-label {
  font-size: 18px;
  color: #666;
  margin-bottom: 10px;
}

.meter-bar {
  height: 30px;
  width: 100%;
  background-color: #e5e5e5;
  border-radius: 15px;
  overflow: hidden;
}

.meter-progress {
  height: 100%;
  background: linear-gradient(to right, #ff4d4f, #ff7a45);
  /* 进度条过渡动画 */
  transition: width 0.3s ease;
}

.intensity-value {
  text-align: center;
  font-size: 28px;
  font-weight: bold;
  color: #ff4d4f;
  margin-top: 10px;
}

.game-status {
  font-size: 20px;
  color: #333;
  margin: 30px 0;
  text-align: center;
}

.control-btn {
  width: 100%;
  height: 50px;
  background-color: #ff4d4f;
  color: white;
  border-radius: 25px;
  font-size: 18px;
  border: none;
  margin-top: 20px;
}

.simulation-container {
  display: flex;
  justify-content: center;
  width: 100%;
  margin: 20px 0;
}

.simulation-btn {
  width: 80%;
  height: 80rpx;
  background: linear-gradient(45deg, #9c27b0, #673ab7);
  color: white;
  border-radius: 40rpx;
  font-size: 32rpx;
  border: none;
  box-shadow: 0 4px 10px rgba(156, 39, 176, 0.3);
}

.simulation-btn[disabled] {
  background: linear-gradient(45deg, #cccccc, #999999);
  color: #666;
}

/* 3秒倒计时样式 */
.countdown-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 100;
}

.countdown-text {
  font-size: 120rpx;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 0 20rpx rgba(255, 255, 255, 0.8);
}

/* 摇晃动画 */
.shake-animation {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(255,77,79,0.2) 0%, rgba(255,77,79,0) 70%);
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.3s ease;
}

.shake-animation.shake-active {
  opacity: 1;
  /* 摇晃脉冲动画 */
  animation: shake-pulse 0.3s ease-out; /* 缩短动画时间 */
}

/* 摇晃脉冲关键帧动画 */
@keyframes shake-pulse {
  0% {
    transform: scale(0.8);
    opacity: 0.7;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.3;
  }
  100% {
    transform: scale(1.3);
    opacity: 0;
  }
}
</style>
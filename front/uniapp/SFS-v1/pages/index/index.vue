<template>
  <view class="home-container">
    <!-- 顶部标题区域 -->
    <view class="header">
      <image src="/static/logo.png" class="logo"></image>
      <text class="title">ShakeForSpeed</text>
      <text class="subtitle">摇出你的速度</text>
    </view>

    <!-- 用户信息授权区域 -->
    <view class="user-auth-container" v-if="!userInfo.id">
      <view class="auth-header">
        <image src="/static/logo.png" class="auth-logo"></image>
        <text class="auth-title">ShakeForSpeed</text>
      </view>
      <view class="auth-description">
        <text>请授权获取用户信息，以便参与游戏排名</text>
      </view>
      <button class="auth-btn" open-type="getUserInfo" @getuserinfo="onGetUserInfo">
        <image class="auth-btn-icon" src="/static/avatar/default.png"></image>
        <text class="auth-btn-text">授权并登录</text>
      </button>
      <view class="auth-tip">
        <text>我们不会泄露您的个人信息</text>
      </view>
    </view>



    <!-- 主要内容区域 -->
    <view class="content" v-if="userInfo.id">
      <!-- 参与模块 -->
      <view class="module-container" v-show="currentTab === 'participate'">
        <view class="room-input-container">
          <input 
            class="room-input" 
            type="number" 
            placeholder="请输入房间号" 
            v-model="roomNumber"
          />
          <button class="join-room-btn" @click="joinRoom">进入房间</button>
        </view>
        

        
        <!-- 游戏说明 -->
        <view class="instructions">
          <view class="instructions-title">游戏说明</view>
          <view class="instructions-text">1. 输入房间号进入游戏房间</view>
          <view class="instructions-text">2. 等待WEB端开始游戏</view>
          <view class="instructions-text">3. 用力摇晃手机，次数越多排名越高</view>
          <view class="instructions-text">4. 游戏时长为10秒</view>
        </view>
      </view>
    </view>

    <!-- 权限说明弹窗 -->
    <view class="permission-popup" v-if="showPermissionPopup">
      <view class="popup-content">
        <text class="popup-title">权限说明</text>
        <view class="permission-item">
          <uni-icons type="person" size="20" color="#3498db"></uni-icons>
          <text class="permission-text">用户信息权限：用于显示您的个人信息</text>
        </view>
        <view class="permission-item">
          <uni-icons type="shake" size="20" color="#3498db"></uni-icons>
          <text class="permission-text">运动传感器权限：用于检测手机摇晃</text>
        </view>
        <view class="permission-item">
          <uni-icons type="location" size="20" color="#3498db"></uni-icons>
          <text class="permission-text">位置权限：用于区域排名功能</text>
        </view>
        <button class="confirm-btn" @click="confirmPermissions">确认并继续</button>
      </view>
    </view>

    <!-- 隐私政策弹窗 (已移除，因为组件不存在) -->

  </view>
</template>

<script>
import UserInfo from '../../components/UserInfo.vue';

// 导入API函数
import { getDefaultUser } from '../../api/mock.js';


const defaultAvatarUrl = 'https://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRna42FI242Lcia07jQodd2FJGIYQfG0LAJGFxM4FbnQP6yfMxBgJ0F3YRqJCJ1aPAK2dQagdusBZg/0'

export default {
  components: {
    UserInfo
  },
  data() {
      return {
        // 模拟数据
        currentRank: 0,
        bestScore: 0,
        userInfo: {
          id: 0, // 0表示未设置用户信息
          nickName: '',
          avatarUrl: ''
        },
        isSimulation: true, // 默认开启模拟模式
        hasUserInfoPermission: false,
        hasSensorPermission: false,
        hasLocationPermission: false,
        canStartGame: false,
        loading: false,
        roomNumber: '',
        showPermissionPopup: true, // 默认显示权限说明弹窗
        showPrivacyPopup: false, // 隐私政策弹窗已移除
        currentTab: 'participate' // 当前选中的标签页，默认为参与模块

      };
  },
  onLoad() {
    // 从本地存储读取用户信息
    const storedUserInfo = wx.getStorageSync('userInfo');
    if (storedUserInfo) {
      this.userInfo = storedUserInfo;
    }
    
    // 检查用户信息权限
    this.checkUserInfoPermission();
  },

  methods: {
    // 检查用户信息权限
    checkUserInfoPermission() {
      wx.getSetting({
        success: (res) => {
          if (res.authSetting['scope.userInfo']) {
            this.hasUserInfoPermission = true;
            this.getUserInfo();
          } else {
            // 未授权，显示授权区域
            this.showPermissionPopup = true;
          }
        }
      });
    },

    // 获取用户信息
    getUserInfo() {
      wx.getUserInfo({
        success: (res) => {
          this.userInfo = {
            id: 1, // 模拟ID
            nickName: res.userInfo.nickName,
            avatarUrl: res.userInfo.avatarUrl
          };
          wx.setStorageSync('userInfo', this.userInfo);
        }
      });
    },

    // 用户授权回调
    onGetUserInfo(e) {
      if (e.detail.userInfo) {
        // 用户同意授权
        this.hasUserInfoPermission = true;
        this.userInfo = {
          id: 1, // 模拟ID
          nickName: e.detail.userInfo.nickName,
          avatarUrl: e.detail.userInfo.avatarUrl
        };
        wx.setStorageSync('userInfo', this.userInfo);
        this.showPermissionPopup = false;
      } else {
        // 用户拒绝授权
        wx.showToast({
          title: '需要授权才能继续',
          icon: 'none'
        });
      }
    },

    // 切换标签页

      switchTab(tabName) {
        // 如果点击的是个人页面标签，则跳转到新的个人页面
      if (tabName === 'profile') {
        wx.switchTab({
          url: 'pages/myprofile/myprofile'
        });
        return;
      }

        this.currentTab = tabName;
        console.log('切换到标签:', tabName, '当前值:', this.currentTab); // 添加更详细的调试日志
        // 强制更新视图
        this.$forceUpdate();
      },

      // 直接跳转到个人页面
      directToProfile() {
        wx.navigateTo({
          url: '/pages/myprofile/myprofile'
        });
        console.log('尝试直接跳转到个人页面');
      },
      
      // 跳转到用户页面
      goToUserPage() {
        wx.navigateTo({
          url: '/pages/user/user'
        });
        console.log('尝试跳转到用户页面');
      },

      // 跳转到账户页面
      goToAccountPage() {
        wx.navigateTo({
          url: '/pages/account/account'
        });
        console.log('尝试跳转到账户页面');
      },
      
    // 确认权限说明
    confirmPermissions() {
      this.showPermissionPopup = false;
      wx.showToast({
        title: '您可以稍后在设置中授权',
        icon: 'none'
      });
    },
    
    // 模拟授权流程
    async handleSimulationAuthorization() {
      console.log('开始模拟授权流程');
      this.loading = true;
      
      // 模拟API调用获取默认用户
      try {
        // 使用绝对路径引用，避免相对路径在不同环境下的解析问题
        const result = await getDefaultUser();
        
        if (result.success) {
          this.userInfo = result.data;
          this.hasUserInfoPermission = true;
          this.hasSensorPermission = true;
          this.hasLocationPermission = true;
          this.canStartGame = true;
          console.log('模拟授权成功', this.userInfo);
          
          // 显示模拟授权成功提示
          wx.showToast({
            title: '模拟授权成功',
            icon: 'success',
            duration: 2000
          });
        } else {
          // API调用成功但返回失败状态
          console.error('模拟授权失败:', result.message);
          this.setDefaultUserInfo();
        }
      } catch (error) {
        // API调用失败
        console.error('模拟授权接口调用失败:', error);
        this.setDefaultUserInfo();
      } finally {
        this.loading = false;
      }
    },
    
    // 设置默认用户信息
    setDefaultUserInfo() {
      this.userInfo = {
        id: 1,
        nickName: '默认用户',
        avatarUrl: '/static/avatar/default.png'
      };
      this.hasUserInfoPermission = true;
      this.hasSensorPermission = true;
      this.hasLocationPermission = true;
      this.canStartGame = true;
      
      wx.showToast({
        title: '使用默认用户信息',
        icon: 'none',
        duration: 2000
      });
    },

    // 检查权限
    checkPermissions() {
      wx.getSetting({
        success: (res) => {
          // 检查用户信息权限
          this.hasUserInfoPermission = res.authSetting['scope.userInfo'] || false;
          // 检查加速度传感器权限
          this.hasSensorPermission = res.authSetting['scope.accelerometer'] || false;
          // 检查位置权限
          this.hasLocationPermission = res.authSetting['scope.userLocation'] || false;

          // 如果有未授权的权限，显示弹窗
          if (!this.hasUserInfoPermission || !this.hasSensorPermission || !this.hasLocationPermission) {
            // 保留弹窗显示，用户需要手动确认权限
            this.showPermissionPopup = true;
          } else {
            // 所有权限都已获取，显示欢迎信息
            wx.showToast({
              title: '欢迎使用ShakeForSpeed',
              icon: 'success',
              duration: 2000
            });
            
            // 隐藏权限弹窗
            this.showPermissionPopup = false;
          }
        },
        fail: (err) => {
          console.error('检查权限失败', err);
          wx.showToast({
            title: '权限检查失败',
            icon: 'none'
          });
        }
      });
    },

    // 选择头像
    onChooseAvatar(e) {
      const { avatarUrl } = e.detail;
      this.avatarUrl = avatarUrl;
    },

    // 确认用户信息
      confirmUserInfo() {
        if (!this.nickname) {
          wx.showToast({
            title: '请输入昵称',
            icon: 'none'
          });
          return;
        }

        this.userInfo = {
          id: Date.now(), // 使用时间戳作为简单ID
          avatarUrl: this.avatarUrl,
          nickName: this.nickname
        };
        // 保存用户信息到本地存储
        wx.setStorageSync('userInfo', this.userInfo);

        wx.showToast({
          title: '用户信息设置成功',
          icon: 'success'
        });

        // 继续申请其他权限
        this.checkAndApplySensorPermission();
      },

    // 确认权限
    confirmPermissions() {
      this.showPermissionPopup = false;
      // 在模拟环境下，直接开始权限申请流程
      if (this.isSimulation) {
        this.handleGetUserInfo();
      } else {
        // 检查用户是否已经设置了昵称和头像
        if (this.nickname && this.avatarUrl && this.avatarUrl !== defaultAvatarUrl) {
          // 如果已经设置了用户信息，直接开始权限申请流程
          this.handleGetUserInfo();
        } else {
          // 如果没有设置用户信息，显示提示
          wx.showToast({
            title: '请先设置头像和昵称',
            icon: 'none',
            duration: 2000
          });
          // 重新显示权限弹窗，引导用户设置信息
          this.showPermissionPopup = true;
        }
      }
    },

    // 获取用户信息
    async handleGetUserInfo() {
      // 在模拟环境下，直接获取模拟用户信息
      if (this.isSimulation) {
        await this.handleSimulationAuthorization();
      } else {
        // 直接调用确认用户信息方法
        this.confirmUserInfo();
      }
    },

    // 检查并申请运动传感器权限
    checkAndApplySensorPermission() {
      if (this.hasSensorPermission) {
        this.checkAndApplyLocationPermission();
        return;
      }

      // 先检查是否已授权加速度传感器权限
      wx.getSetting({
        success: (res) => {
          const hasAccelerometer = res.authSetting['scope.accelerometer'] || false;

          if (hasAccelerometer) {
            this.hasSensorPermission = true;
            wx.showToast({
              title: '加速度传感器权限已获取',
              icon: 'success',
              duration: 2000
            });
            this.checkAndApplyLocationPermission();
            return;
          }

          // 未授权，申请加速度传感器权限
          wx.authorize({
                scope: 'scope.accelerometer',
                success: () => {
                  this.hasSensorPermission = true;
                  wx.showToast({
                    title: '加速度传感器权限已获取',
                    icon: 'success',
                    duration: 2000
                  });
                  this.checkAndApplyLocationPermission();
                },
                fail: (err) => {
                  console.error('运动传感器权限申请失败:', err);
                  // 更全面的失败判断
                  if (err.errMsg && (err.errMsg.includes('deny') || err.errMsg.includes('fail auth deny') || err.errMsg.includes('cancel'))) {
                    wx.showModal({
                      title: '权限申请提醒',
                      content: '需要加速度传感器权限才能游戏，请前往设置开启权限',
                      confirmText: '去设置',
                      cancelText: '取消',
                      success: (modalRes) => {
                        if (modalRes.confirm) {
                          wx.openSetting({
                            success: () => {
                              // 设置页面返回后重新检查权限
                              wx.getSetting({
                                success: (newRes) => {
                                  const newAccelerometer = newRes.authSetting['scope.accelerometer'] || false;
                  if (newAccelerometer) {
                                    this.hasSensorPermission = true;
                                    wx.showToast({
                                      title: '加速度传感器权限已获取',
                                      icon: 'success',
                                      duration: 2000
                                    });
                                    this.checkAndApplyLocationPermission();
                                  } else {
                                    wx.showToast({
                                      title: '请开启加速度传感器权限',
                                      icon: 'none',
                                      duration: 2000
                                    });
                                    this.showPermissionPopup = true;
                                  }
                                },
                                fail: () => {
                                  this.showPermissionPopup = true;
                                }
                              });
                            },
                            fail: () => {
                              this.showPermissionPopup = true;
                            }
                          });
                        } else {
                          this.showPermissionPopup = true;
                        }
                      },
                      fail: () => {
                        this.showPermissionPopup = true;
                      }
                    });
                  } else {
                    wx.showToast({
                      title: '加速度传感器权限申请失败',
                      icon: 'none',
                      duration: 2000
                    });
                    this.showPermissionPopup = true;
                  }
                }
              });
        },
        fail: (err) => {
          console.error('获取设置失败:', err);
          this.showPermissionPopup = true;
        }
      });
    },

    // 检查并申请位置权限
    checkAndApplyLocationPermission() {
      if (this.hasLocationPermission) {
        this.startGame();
        return;
      }

      // 先检查是否已授权位置权限
      wx.getSetting({
        success: (res) => {
          const hasLocation = res.authSetting['scope.userLocation'] || false;

          if (hasLocation) {
            this.hasLocationPermission = true;
            wx.showToast({
              title: '位置权限已获取',
              icon: 'success',
              duration: 2000
            });
            this.startGame();
            return;
          }

          // 未授权，申请位置权限
          wx.authorize({
            scope: 'scope.userLocation',
            success: () => {
              this.hasLocationPermission = true;
              wx.showToast({
                title: '位置权限已获取',
                icon: 'success',
                duration: 2000
              });
              this.startGame();
            },
            fail: (err) => {
              console.error('位置权限申请失败:', err);
              // 更全面的失败判断
              if (err.errMsg && (err.errMsg.includes('deny') || err.errMsg.includes('fail auth deny') || err.errMsg.includes('cancel'))) {
                wx.showModal({
                  title: '权限申请提醒',
                  content: '需要位置权限才能参与区域排名，请前往设置开启权限',
                  confirmText: '去设置',
                  cancelText: '取消',
                  success: (modalRes) => {
                    if (modalRes.confirm) {
                      wx.openSetting({
                        success: () => {
                          // 设置页面返回后重新检查权限
                          wx.getSetting({
                            success: (newRes) => {
                              const newLocation = newRes.authSetting['scope.userLocation'] || false;
                              if (newLocation) {
                                this.hasLocationPermission = true;
                                wx.showToast({
                                  title: '位置权限已获取',
                                  icon: 'success',
                                  duration: 2000
                                });
                                this.startGame();
                              } else {
                                wx.showToast({
                                  title: '请开启位置权限',
                                  icon: 'none',
                                  duration: 2000
                                });
                                this.showPermissionPopup = true;
                              }
                            },
                            fail: () => {
                              this.showPermissionPopup = true;
                            }
                          });
                        },
                        fail: () => {
                          this.showPermissionPopup = true;
                        }
                      });
                    } else {
                      this.showPermissionPopup = true;
                    }
                  },
                  fail: () => {
                    this.showPermissionPopup = true;
                  }
                });
              } else {
                wx.showToast({
                  title: '位置权限申请失败',
                  icon: 'none',
                  duration: 2000
                });
                this.showPermissionPopup = true;
              }
            }
          });
        },
        fail: (err) => {
          console.error('获取设置失败:', err);
          this.showPermissionPopup = true;
        }
      });
    },

    // 进入房间
    joinRoom() {
      if (!this.roomNumber) {
        wx.showToast({
          title: '请输入房间号',
          icon: 'none'
        });
        return;
      }
      
      // 验证房间号，暂时模拟只有一个房间"111111"
      if (this.roomNumber !== '111111') {
        wx.showToast({
          title: '无此房间',
          icon: 'none'
        });
        return;
      }
      
      // 跳转到游戏页面，并传递用户信息
      wx.navigateTo({
        url: '/pages/game/game?userInfo=' + encodeURIComponent(JSON.stringify(this.userInfo))
      });
    },
    
    startGame() {
      // 检查是否有传感器权限
      if (!this.hasSensorPermission) {
        console.log('没有传感器权限，无法开始游戏');
        wx.showToast({
          title: '需要传感器权限才能开始游戏',
          icon: 'none',
          duration: 2000
        });
        // 引导用户手动授权
        setTimeout(() => {
          wx.showModal({
            title: '权限申请',
            content: '需要加速度传感器权限才能游戏，请前往设置开启',
            confirmText: '去设置',
            cancelText: '取消',
            success: (res) => {
              if (res.confirm) {
                console.log('用户前往设置页面');
                wx.openSetting();
              }
            }
          });
        }, 2000);
        return;
      }
      
      // 跳转到游戏页面，并传递用户信息
      wx.navigateTo({
        url: '/pages/game/game?userInfo=' + encodeURIComponent(JSON.stringify(this.userInfo))
      });
    },
    // 跳转到历史页面
    gotoHistory() {
      this.currentTab = 'history';
    },
    
    // 跳转到个人页面
    gotoProfile() {
      this.currentTab = 'profile';
    }
  }
};
</script>

<style scoped lang="scss">
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx 20rpx;
}

.permission-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.popup-content {
  width: 80%;
  background-color: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  box-sizing: border-box;
}

.popup-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 30rpx;
}

.permission-item {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.permission-text {
  font-size: 28rpx;
  color: #666;
  margin-left: 10rpx;
}

.confirm-btn {
  width: 100%;
  height: 80rpx;
  background: linear-gradient(90deg, #3498db 0%, #2980b9 100%);
  border-radius: 40rpx;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  margin-top: 30rpx;
}

.header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 80rpx;
}

.logo {
  width: 180rpx;
  height: 180rpx;
  border-radius: 50%;
  margin-bottom: 20rpx;
  box-shadow: 0 0 20rpx rgba(52, 152, 219, 0.5);
}

.title {
  font-size: 60rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 10rpx;
  text-shadow: 0 2rpx 10rpx rgba(52, 152, 219, 0.7);
}

.subtitle {
  font-size: 32rpx;
  color: #3498db;
  opacity: 0.9;
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.room-input-container {
  width: 80%;
  margin-bottom: 50rpx;
}

.room-input {
  width: 100%;
  height: 80rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 40rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
  margin-bottom: 20rpx;
  box-sizing: border-box;
}

.join-room-btn {
  width: 100%;
  height: 80rpx;
  background: linear-gradient(90deg, #3498db 0%, #2980b9 100%);
  border-radius: 40rpx;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
}

.rank-btn {
  width: 80%;
  height: 100rpx;
  background: linear-gradient(90deg, #e74c3c 0%, #c0392b 100%);
  border-radius: 45rpx;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  margin-top: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.rank-text {
  margin-right: 10rpx;
}

/* 模拟WEB端按钮样式 */
.simulation-btn {
  width: 80%;
  height: 80rpx;
  background: linear-gradient(90deg, #9b59b6 0%, #8e44ad 100%);
  border-radius: 40rpx;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  margin-top: 30rpx;
}

/* 个人选项样式 */
.profile-tab {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
}

.profile-tab ::v-deep .user-info-container {
  flex-direction: row;
  align-items: center;
}

.profile-tab ::v-deep .user-avatar {
  width: 40rpx;
  height: 40rpx;
  margin-right: 10rpx;
  margin-bottom: 0;
  border: none;
}

.profile-tab ::v-deep .user-name {
  font-size: 24rpx;
  color: #666;
  max-width: 120rpx;
  font-weight: normal;
}

/* 模块容器样式 */
.module-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
}

.module-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300rpx;
}

.placeholder-text {
  font-size: 32rpx;
  color: #999;
}

/* 个人模块样式 */
.profile-content {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.profile-content ::v-deep .user-info-container {
  margin-bottom: 50rpx;
}

.profile-stats {
  display: flex;
  justify-content: space-around;
  width: 80%;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 48rpx;
  font-weight: bold;
  color: #3498db;
  margin-bottom: 10rpx;
}

.stat-label {
  font-size: 28rpx;
  color: #fff;
}

/* 游戏说明样式 */
.instructions {
  width: 80%;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20rpx;
  padding: 30rpx;
  margin-top: 30rpx;
}

.instructions-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 20rpx;
  text-align: center;
}

.instructions-text {
  font-size: 26rpx;
  color: #fff;
  margin-bottom: 15rpx;
  line-height: 1.5;
}
</style>

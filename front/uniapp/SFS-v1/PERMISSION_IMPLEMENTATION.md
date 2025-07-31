# WeChat Mini Program Permission Implementation for ShakeForSpeed

## Overview
This document outlines how we've implemented permission handling in the ShakeForSpeed WeChat Mini Program, following best practices from WeChat's official documentation and industry references.

## Permission Configuration

### manifest.json
We've properly declared all required permissions in the `manifest.json` file:

```json
"mp-weixin": {
  "permission": {
    "scope.userInfo": {
      "desc": "用于展示用户昵称和头像"
    },
    "scope.userLocation": {
      "desc": "用于获取用户位置信息，提供区域排名功能"
    },
    "scope.accelerometer": {
      "desc": "用于检测手机加速度变化，实现摇一摇功能"
    }
  },
  "requiredPrivateInfos": ["motion"]
}
```

- Each permission includes a clear description explaining to users why it's needed
- Added `"requiredPrivateInfos": ["motion"]` for access to motion sensors
- This configuration ensures compliance with WeChat's privacy requirements<mcreference link="https://zhuanlan.zhihu.com/p/128858504" index="0">0</mcreference>

## Permission Request Flow

### App.vue
In the application entry file, we implement two key methods:

1. `checkSensorPermission()`: Only checks the permission status without requesting it
2. `requestSensorAuth()`: Handles the permission request process

```javascript
// 检查传感器权限状态（仅检查，不主动请求）
checkSensorPermission() {
  uni.getSetting({
    success: (res) => {
      if (!res.authSetting['scope.accelerometer']) {
        console.log('传感器权限未授权');
        // 不在这里主动请求权限，而是在game.vue中需要时再请求
      } else {
        console.log('传感器权限已授权');
      }
    }
  });
},

// 请求传感器权限（由game.vue调用）
requestSensorAuth() {
  uni.authorize({
    scope: 'scope.accelerometer',
    success: () => {
      console.log('传感器权限获取成功');
    },
    fail: (error) => {
      console.log('传感器权限获取失败', error);
      // 显示错误提示
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
          success: (resModal) => {
            if (resModal.confirm) {
              uni.openSetting();
            }
          }
        });
      }, 2000);
    }
  });
}
```

### game.vue
In the game page, we implement `requestSensorPermissions()` which is triggered when the game starts:

```javascript
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
}
```

## Best Practices Implemented

1. **Permission Declaration**:
   - Clearly declared all permissions in `manifest.json` with descriptive explanations
   - Included required private information for motion sensors

2. **On-Demand Permission Request**:
   - Requesting permissions only when they're actually needed (when the game starts)
   - Avoiding unnecessary permission requests at app launch

3. **User Guidance**:
   - Providing clear feedback when permission is denied
   - Guiding users to the settings page to manually enable permissions
   - Using appropriate timing with `setTimeout` to ensure users have time to read messages

4. **Resource Management**:
   - Properly starting and stopping the accelerometer
   - Removing listeners when they're no longer needed
   - Checking page visibility (`isShow`) and game state (`isPlaying`) before processing sensor data

5. **Error Handling**:
   - Gracefully handling permission denial
   - Providing fallback options for users who deny permissions

This implementation ensures compliance with WeChat Mini Program guidelines and provides a good user experience while protecting user privacy<mcreference link="https://zhuanlan.zhihu.com/p/128858504" index="0">0</mcreference>.
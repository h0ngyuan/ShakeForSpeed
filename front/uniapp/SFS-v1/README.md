# ShakeForSpeed

ShakeForSpeed 是一个基于 uni-app 开发的微信小程序游戏，玩家通过摇晃手机来比拼速度，决出排名。

## 项目概述

这是一个在过年时常能见到的摇一摇游戏的数字化版本。用户可以通过摇晃手机参与游戏，系统会根据摇晃的速度和频率计算得分，并提供排行榜功能。

## 技术栈

- 前端框架：uni-app (Vue 3)
- UI 组件库：Element Plus (Web端)
- 小程序端：微信小程序
- 开发语言：JavaScript/TypeScript

## 功能特性

1. **运动传感器权限管理**
   - 完善的权限申请流程
   - 用户友好的权限引导
   - 详细的权限错误处理

2. **摇一摇游戏核心功能**
   - 实时检测手机加速度变化
   - 精确计算摇晃速度和得分
   - 动画效果增强用户体验

3. **用户系统**
   - 模拟授权功能（开发/测试环境）
   - 用户信息展示

4. **排行榜系统**
   - 区域排名功能（基于位置权限）
   - 实时排名更新

## 权限说明

本项目需要以下权限：

1. **加速度传感器权限** (`scope.accelerometer`)
   - 用于检测手机加速度变化，实现摇一摇功能
   - 在游戏开始时申请

2. **用户信息权限** (`scope.userInfo`)
   - 用于展示用户昵称和头像

3. **位置信息权限** (`scope.userLocation`)
   - 用于获取用户位置信息，提供区域排名功能

## 安装与运行

1. 克隆项目代码
2. 安装依赖：`npm install`
3. 运行开发服务器：`npm run dev`
4. 使用 HBuilderX 或其他支持 uni-app 的 IDE 进行开发

## 开发注意事项

1. 传感器权限申请采用按需申请策略，在游戏开始时才申请权限
2. 提供了完善的权限失败处理机制，引导用户手动授权
3. 使用了模拟授权功能，方便开发和测试
4. 代码遵循 uni-app 最佳实践

## 项目结构

```
src/
├── pages/              # 页面文件目录
├── components/         # 公共组件目录
├── api/               # API 接口目录
├── static/            # 静态资源目录
├── utils/             # 工具函数目录
├── uni_modules/       # uni-app 模块目录
├── App.vue           # 应用入口文件
├── main.js           # 项目入口文件
├── manifest.json     # 应用配置文件
├── pages.json        # 页面配置文件
├── README.md         # 项目说明文档
├── PERMISSION_IMPLEMENTATION.md  # 权限实现说明
├── API_DOCUMENTATION.md          # API 接口文档
├── COMPONENTS_GUIDE.md           # 组件使用说明
└── PROJECT_STRUCTURE.md          # 项目结构说明
```

## 项目文档

为了更好地理解和维护项目，我们提供了详细的文档：

- [权限实现说明](PERMISSION_IMPLEMENTATION.md) - 详细说明了项目中权限处理的实现方式
- [API 接口文档](API_DOCUMENTATION.md) - 说明了项目中使用的模拟 API 接口
- [组件使用说明](COMPONENTS_GUIDE.md) - 介绍了自定义组件的使用方法
- [项目结构说明](PROJECT_STRUCTURE.md) - 详细说明了项目的目录结构和各部分作用
- [隐私政策](PRIVACY_POLICY.md) - 说明了应用如何处理用户数据

这些文档可以帮助开发者快速上手项目开发和维护。

## 后续开发计划

1. 实现完整的后端服务
2. 连通前后端接口
3. 开发 Web 端页面
4. 优化游戏算法和用户体验

## 许可证

[MIT License](LICENSE)
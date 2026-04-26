# ShakeForSpeed v2.0

> 摇一摇拼手速活动系统 - AI重构版

## 项目思路

### 项目背景

这是一个面向线下超市活动的"摇一摇拼手速"应用。参与者在活动现场通过微信小程序加入活动房间，在规定时间内疯狂摇动手机，系统实时统计摇动次数并排名，活动结束后自动发放奖励。

### 架构演进思考

#### 原来的架构思路（v1.x）
```
微服务架构:
sfs-gateway (网关) → sfs-auth (认证) → sfs-activity (活动) → sfs-rank (排行)
                            ↓
                    Spring Cloud + Netty
                            ↓
                Redis / MySQL / MongoDB / MinIO
```

**问题**：
- 服务拆分过细，维护成本高
- 本地开发需要启动多个服务
- 服务间调用增加延迟，对实时性不利

#### 现在的架构思路（v2.0）
```
单体架构:
WebSocket ←→ sfs (Spring Boot) ←→ Kafka (异步处理)
                    ↓
           Redis (实时排行) / MySQL (业务数据) / MinIO (文件)
```

**优化理由**：
- 单体架构，部署简单，一个JAR包搞定
- 本地开发只需启动一个服务
- 减少了服务间网络调用，延迟更低
- 对于中小规模活动可能够用
- 保留Kafka做异步解耦，应对摇动峰值

### 核心数据流

```
1. 用户摇动手机
   ↓
2. 小程序通过WebSocket发送摇动事件
   ↓
3. WebSocket Handler 快速将事件发送到 Kafka
   ↓
4. Kafka Consumer 异步处理:
   - 更新 Redis Sorted Set (实时排行)
   - 通过 WebSocket 推送最新排行给所有用户
   ↓
5. 活动结束:
   - 从 Redis 读取最终排行
   - 写入 MySQL leaderboards 表
   - 自动发放奖励
```

---

## 项目结构

```
ShakeForSpeed/
├── sfs/                          # 主服务模块（单体应用）
│   ├── src/main/java/com/sfs/
│   │   ├── SfsApplication.java   # 启动类
│   │   ├── config/               # 配置类
│   │   │   ├── SaTokenConfig.java
│   │   │   ├── WebSocketConfig.java
│   │   │   ├── RedissonConfig.java
│   │   │   ├── MybatisPlusConfig.java
│   │   │   ├── KafkaTopicConfig.java
│   │   │   ├── FlinkConfig.java
│   │   │   └── MinioConfig.java
│   │   ├── controller/           # REST API控制器
│   │   │   ├── AuthController.java
│   │   │   ├── ActivityController.java
│   │   │   ├── RoomController.java
│   │   │   ├── RankController.java
│   │   │   ├── RewardController.java
│   │   │   └── FileController.java
│   │   ├── service/              # 业务逻辑层
│   │   │   ├── AuthService.java
│   │   │   ├── ActivityService.java
│   │   │   ├── RoomService.java
│   │   │   ├── RankService.java
│   │   │   ├── RewardService.java
│   │   │   ├── ShakeEventService.java
│   │   │   ├── BroadcastService.java
│   │   │   └── MinioService.java
│   │   ├── mapper/               # MyBatis Mapper
│   │   ├── websocket/            # WebSocket处理
│   │   │   ├── ShakeWebSocketHandler.java
│   │   │   └── WebSocketAuthInterceptor.java
│   │   ├── kafka/                # Kafka生产者/消费者
│   │   │   ├── ShakeEventProducer.java
│   │   │   ├── ShakeEventConsumer.java
│   │   │   └── ActivityEventListener.java
│   │   ├── flink/                # Flink作业管理
│   │   ├── scheduler/            # 定时任务
│   │   └── exception/            # 异常处理
│   └── src/main/resources/
│       ├── application.yaml
│       ├── application-dev.yaml
│       └── application-prod.yaml
├── sfs-common/                   # 公共模块（实体、DTO、VO、枚举、工具类）
│   ├── entity/                   # 数据库实体
│   ├── dto/                      # 请求参数
│   ├── vo/                       # 响应数据
│   ├── enums/                    # 枚举类
│   ├── exception/                # 自定义异常
│   └── util/                     # 工具类
├── front/                        # 前端项目
│   ├── uniapp/SFS-v1/            # 微信小程序（UniApp + Vue 3）
│   │   ├── pages/
│   │   │   ├── index/index.vue   # 首页（加入房间）
│   │   │   ├── login/login.vue   # 登录页
│   │   │   ├── shake/shake.vue   # 摇一摇页面
│   │   │   ├── rank/rank.vue     # 实时排行
│   │   │   ├── profile/profile.vue # 个人中心
│   │   │   └── history/history.vue # 参与历史
│   │   ├── api/                  # API接口
│   │   └── utils/                # 工具函数
│   └── web/SFS-v1/               # Web管理后台（Vue 3 + TypeScript + Element Plus）
│       ├── src/
│       │   ├── views/
│       │   │   ├── Login.vue     # 登录页
│       │   │   ├── Layout.vue    # 布局
│       │   │   ├── Dashboard.vue # 数据看板
│       │   │   ├── Activities.vue # 活动管理
│       │   │   └── ActivityDetail.vue # 活动详情
│       │   ├── api/              # HTTP客户端
│       │   └── router/           # 路由配置
├── sfs_db_v2.sql                 # 数据库初始化脚本
├── Dockerfile                    # Docker镜像构建
├── docker-compose.yml            # Docker编排文件
└── pom.xml                       # 父POM
```

---

## 当前项目进度

### ✅ 已完成

**后端**：
- [x] Spring Boot 3.4.1 + Java 17 基础架构
- [x] Sa-Token 认证授权（微信登录、手机登录）
- [x] WebSocket 实时通信 + Kafka 异步处理
- [x] Redis Sorted Set 实时排行
- [x] 活动管理（创建、发布、结束）
- [x] 房间管理（加入、离开、状态查询）
- [x] 奖励配置与发放
- [x] 定时任务（自动开始/结束活动）
- [x] MyBatis-Plus + MySQL 持久层
- [x] MinIO 文件上传
- [x] 全局异常处理
- [x] Docker 部署配置

**前端**：
- [x] 小程序端：登录、首页、摇一摇、排行、个人中心
- [x] Web管理端：登录、数据看板、活动管理

**基础设施**：
- [x] Docker Compose 一键部署
- [x] MySQL + Redis + Kafka + MinIO
- [x] Maven 编译验证通过

### 🔨 待完善

- [ ] Flink 实时计算集成（当前为KafkaStream实现）
- [ ] WebSocket 自动重连机制
- [ ] 防作弊检测逻辑
- [ ] 压力测试
- [ ] 微信小程序真机调试
- [ ] 生产环境HTTPS配置
- [ ] 监控告警集成

### 📦 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.4.1 | 核心框架 |
| Java | 17 | 开发语言 |
| MyBatis-Plus | 3.5.9 | 持久层框架 |
| Sa-Token | 1.39.0 | 认证授权 |
| Redis + Redisson | 3.41.0 | 实时排行/会话存储 |
| Kafka | 3.6.2 | 事件流处理 |
| Flink | 1.18.1 | 实时计算(待集成) |
| MinIO | 8.5.13 | 对象存储 |
| MySQL | 8.0+ | 关系型数据库 |
| Vue 3 | 3.4+ | 前端框架 |
| Element Plus | 2.7+ | Web管理端UI |
| UniApp | 3.0+ | 小程序框架 |

---

## 快速启动

```bash
# 启动依赖服务
docker-compose up -d mysql redis kafka minio

# 编译并启动
mvn clean package -DskipTests
java -jar sfs/target/sfs-2.0.0-SNAPSHOT.jar --spring.profiles.active=dev

# Web管理端
cd front/web/SFS-v1 && npm install && npm run dev
```

详细启动说明请参考各模块的 README。

# 接口文档（通过网关）

## 总览
- 基础网关地址：`http://119.91.238.231:9999`
- 认证：所有非登录接口需在请求头携带 `token: Bearer_<JWT>`
- 服务路由：
  - 主服务：`/main/**` -> `http://119.91.238.231:8002`
  - 排名服务：`/rank/**` -> `http://119.91.238.231:8003`

## 认证模块
- 登录
  - `POST /main/auth/login`
  - 请求体：`{ "username": string, "password": string }`
  - 响应：`{ code, data: { userId, username, role, token }, msg }`
- 登出
  - `POST /main/auth/out?id=<number>`
  - 头部：`token`
  - 响应：`{ code, data, msg }`

## 活动模块
- 查询活动列表
  - `POST /main/activity/queryActivities`
  - 头部：`token`
  - 请求体：
    - `activityNameOrId?: string`
    - `rangeTimeBefore?: string(ISO_LOCAL_DATE_TIME)`
    - `rangeTimeAfter?: string(ISO_LOCAL_DATE_TIME)`
    - `role?: ADMIN|USER`
    - `pageIndex: number`
    - `pageSize: number`
  - 响应：`{ code, data: { records: Activity[], total, ... }, msg }`

- 创建活动（含奖励与图片）
  - `POST /main/activity/createActivity`
  - 头部：`token`
  - Content-Type：`multipart/form-data`
  - 表单字段：
    - `activityName: string`
    - `beginTime?: string(ISO_LOCAL_DATE_TIME)`
    - `durTime?: number`
    - `longitude?: number`
    - `latitude?: number`
    - `rewards[0].name: string`
    - `rewards[0].rankStart: number`
    - `rewards[0].rankEnd: number`
    - `rewards[0].image?: file`
    - `rewards[1]....`
  - 响应：`{ code, data, msg }`（成功时无特殊data）

- 删除活动
  - `POST /main/activity/deleteActivity?id=<number>`
  - 头部：`token`
  - 响应：`{ code, data, msg }`

- 更新活动（新增）
  - `PUT /main/activity/{id}`
  - 头部：`token`
  - 请求体（UpdateActivityDTO）：`{ "activityName"?: string, "beginTime"?: string, "durTime"?: number, "longitude"?: number, "latitude"?: number }`
  - 响应：`{ code, data, msg }`

- 获取活动详情
  - `GET /main/activity/{id}`
  - 头部：`token`
  - 响应：`{ code, data: ActivityDetailVO, msg }`

- 加入活动房间
  - `GET /main/activity/joinActivity`
  - 头部：`token`
  - 请求体：`{ activityId?: number, userId: number, roomPwd: number }`
  - 响应：`{ code, data: activityId, msg }`

- 退出活动房间
  - `GET /main/activity/exitActivity`
  - 头部：`token`
  - 请求体：`{ activityId: number, userId: number, roomPwd?: number }`
  - 响应：`{ code, data, msg }`

- 查询参与者列表（新增）
  - `GET /main/activity/{id}/participants`
  - 头部：`token`
  - 查询参数（ParticipantQueryDTO）：`pageIndex, pageSize`
  - 响应：`{ code, data: ParticipantVO[], msg }`

- 房间状态（新增）
  - `GET /main/activity/{id}/roomStatus`
  - 头部：`token`
  - 响应（RoomStatusVO）：`{ activityId, state, participantsCount }`

## 奖励模块
- 获取活动奖励列表
  - `POST /main/reward/acquireRewards?id=<number>`
  - 头部：`token`
  - 响应：`{ code, data: RewardVO[], msg }`

## 房间模块
- 开始活动（状态切换并广播）
  - `POST /main/room/start`
  - 头部：`token`
  - 请求体：`<number>`（activityId）
  - 响应：`{ code, data, msg }`

- 结束活动（新增）
  - `POST /main/room/end`
  - 头部：`token`
  - 请求体：`<number>`（activityId）
  - 响应：`{ code, data, msg }`

## 排名/实时数据模块（rank-service）
- WebSocket/实时计数说明（实现位于 rank-service，HTTP 路由前缀为 `/rank`）
  - 具体连接地址与协议由前端 `wsService.ts` 使用的路径确定；HTTP 接口如有需要，统一按 `/rank/*` 走网关。

- 获取活动 TopN（新增）
  - `GET /rank/ranking/top`
  - 查询参数：`activityId: number, n?: number`
  - 响应：`{ code, data: RankItemVO[], msg }`

- 获取活动排名分页（新增）
  - `GET /rank/ranking/page`
  - 查询参数：`activityId: number, pageIndex: number, pageSize: number`
  - 响应：`{ code, data: RankPageVO, msg }`

- 当前参与人数（新增）
  - `GET /rank/live/peopleCount`
  - 查询参数：`activityId: number`
  - 响应：`{ code, data: { activityId, people: number }, msg }`

## 数据模型摘要
- User
  - `id, role, username, password, updateTime, banFlag, deleteFlag`
- Activity
  - `id, activityName, activityType, creatorRole, beginTime, createTime, creatorId, durTime, roomPwd, longitude, latitude, state`
- Reward
  - `id, rewardName, rewardImg(url), activityId, rankRangeFront, rankRangeEnd`

- DTO/VO（新增）
  - RegisterDTO：`username, password, role`
  - UpdatePasswordDTO：`userId, oldPassword, newPassword`
  - UpdateUserRoleDTO：`userId, role`
  - BanUserDTO：`userId, banFlag`
  - UpdateActivityDTO：`id, activityName?, beginTime?, durTime?, longitude?, latitude?`
  - ParticipantQueryDTO：`activityId, pageIndex, pageSize`
  - ParticipantVO：`userId, username`
  - RoomStatusVO：`activityId, state, participantsCount`
  - RankItemVO：`rank, userId, count`
  - RankPageVO：`items: RankItemVO[], total`

## 状态码
- 统一返回
  - 成功：`code=200`
  - 登录失败：`code=2`
  - TOKEN缺失：`code=52`
  - TOKEN无效：`code=50`
  - 用户不存在：`code=1001`
  - 其他异常参考服务枚举

## 请求示例
- 登录
  - `curl -X POST http://119.91.238.231:9999/main/auth/login -H "Content-Type: application/json" -d '{"username":"u","password":"p"}'`
- 创建活动（含一个奖励）
  - `curl -X POST http://119.91.238.231:9999/main/activity/createActivity -H "token: Bearer_<jwt>" -F activityName=测试活动 -F durTime=600 -F rewards[0].name=一等奖 -F rewards[0].rankStart=1 -F rewards[0].rankEnd=1 -F rewards[0].image=@/path/to/img.png`

- 注册
  - `curl -X POST http://119.91.238.231:9999/main/auth/register -H "Content-Type: application/json" -d '{"username":"admin","password":"123456","role":"ADMIN"}'`

- 更新活动
  - `curl -X PUT http://119.91.238.231:9999/main/activity/1001 -H "token: Bearer_<jwt>" -H "Content-Type: application/json" -d '{"durTime":1200}'`

- 获取Top10排名
  - `curl -X GET 'http://119.91.238.231:9999/rank/ranking/top?activityId=1001&n=10'`
- 管理模块（新增）
  - 用户列表
    - `GET /main/admin/users`
    - 头部：`token`
    - 查询参数：`pageIndex, pageSize, username?`
    - 响应：`{ code, data: { records: User[], total }, msg }`
  - 禁用/启用用户
    - `POST /main/admin/user/ban`
    - 头部：`token`
    - 请求体（BanUserDTO）：`{ userId: number, banFlag: "0"|"1" }`
    - 响应：`{ code, data, msg }`
  - 更新用户角色
    - `POST /main/admin/user/updateRole`
    - 头部：`token`
    - 请求体（UpdateUserRoleDTO）：`{ userId: number, role: ADMIN|USER }`
    - 响应：`{ code, data, msg }`

# Gateway Service

## 概述
Gateway Service是ShakeForSpeed项目的API网关服务，作为所有微服务的统一入口，提供路由转发、身份验证、跨域处理等功能。

## 主要功能

### 1. 路由管理
- **API服务路由**: `/api/**` → `http://localhost:8001`
- **主服务路由**: `/main/**` → `http://localhost:8002`  
- **排名服务路由**: `/rank/**` → `http://localhost:8080`

### 2. 安全功能
- **JWT认证**: 统一处理所有服务的身份验证
- **CORS跨域**: 支持前端跨域请求

### 3. 监控功能
- **健康检查**: `/gateway/health` - 检查服务状态
- **服务信息**: `/gateway/info` - 获取服务详细信息
- **全局异常处理**: 统一处理网关层异常

## 架构说明

### WebSocket服务独立部署
- **rank-service**中的WebSocket服务使用纯Netty实现
- 直接监听端口`8081`，路径为`/ws`
- 前端直接连接`ws://localhost:8081/ws`，无需通过网关
- 避免了WebSocket代理的复杂性和性能损耗

### 限流功能
- 当前版本暂未实现限流功能
- 后续计划引入**Sentinel**组件进行更专业的限流和熔断

## 配置信息

### 端口配置
- 网关端口: `9999`
- rank-service HTTP: `8080`
- rank-service WebSocket: `8081`

### 不需要认证的路径
- `/api/auth/login`
- `/api/auth/register`
- `/main/auth/login`
- `/main/auth/register`
- `/rank/test`
- `/actuator/**`
- `/gateway/**`

## 技术栈
- Spring Cloud Gateway 3.1.4
- HuTool (JWT处理)
- Jackson (JSON处理)

## 过滤器说明

### 1. CorsFilter
- 处理跨域请求
- 设置CORS响应头
- 处理OPTIONS预检请求

### 2. JwtAuthenticationFilter  
- JWT token验证
- 用户信息提取
- 请求头注入(X-User-Id, X-Username, X-User-Role)

## 启动方式

1. 启动GatewayServiceApplication
2. 访问 http://localhost:9999/gateway/health 检查服务状态

## API使用示例

### 健康检查
```bash
GET http://localhost:9999/gateway/health
```

### 通过网关访问API服务
```bash
# 登录(不需要token)
POST http://localhost:9999/api/auth/login

# 访问需要认证的接口
GET http://localhost:9999/api/users
Headers: token: Bearer_your_jwt_token
```

### WebSocket连接
```javascript
// 前端直接连接WebSocket服务，无需通过网关
const ws = new WebSocket('ws://localhost:8081/ws');
```

## 注意事项
1. JWT密钥配置在application-dev.yaml中
2. WebSocket服务独立部署，不通过网关代理
3. 限流功能待后续Sentinel组件引入后实现
4. 路由配置支持动态修改，可通过GatewayRoutesConfig进行管理
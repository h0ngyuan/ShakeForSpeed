const express = require('express');
const cors = require('cors');
const app = express();
const PORT = 8080;

// 中间件
app.use(cors());
app.use(express.json());

// 登录接口
app.post('/auth/login', (req, res) => {
  const { username, password } = req.body;
  
  // 简单的验证逻辑
  if (username === 'admin' && password === '123456') {
    return res.json({
      success: true,
      role: 'admin',
      message: '登录成功'
    });
  }
  
  if (username === 'merchant' && password === '123456') {
    return res.json({
      success: true,
      role: 'merchant',
      message: '登录成功'
    });
  }
  
  if (username === 'user' && password === 'user') {
    return res.json({
      success: true,
      role: 'user',
      message: '登录成功'
    });
  }
  
  // 登录失败
  return res.status(401).json({
    success: false,
    message: '用户名或密码错误'
  });
});

// 启动服务器
app.listen(PORT, () => {
  console.log(`服务器运行在 http://localhost:${PORT}`);
});
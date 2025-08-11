import './assets/main.css'
import { createApp } from 'vue';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';
import App from './App.vue';
import router from './router';

// 动态加载百度地图API
const loadBaiduMap = () => {
  return new Promise((resolve, reject) => {
    const script = document.createElement('script');
    script.src = `https://api.map.baidu.com/api?v=3.0&ak=5dXZRbxeqBg7V6LiD8uojl6kN2cgV4sz`;
    script.type = 'text/javascript';
    script.onload = resolve;
    script.onerror = reject;
    document.head.appendChild(script);
  });
};

// 应用初始化
async function initApp() {
const app = createApp(App);
app.use(ElementPlus, { locale: zhCn });
app.use(router);

try {
    // 加载百度地图API
    await loadBaiduMap();
    console.log('百度地图API加载成功');
} catch (error) {
    console.error('百度地图API加载失败:', error);
}

// 挂载应用
app.mount('#app');
}

// 启动应用
initApp();
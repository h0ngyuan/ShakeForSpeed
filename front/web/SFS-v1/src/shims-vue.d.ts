declare module '*.vue' {
  import { DefineComponent } from 'vue';
  const component: DefineComponent<{}, {}, any>;
  export default component;
}

declare module 'element-plus/dist/locale/zh-cn.mjs'; // 添加中文语言包模块声明
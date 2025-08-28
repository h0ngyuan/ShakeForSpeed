import { ElMessage } from 'element-plus';

class WsService {
  private ws: WebSocket | null = null;
  private callbacks: Map<string, Array<(data: any) => void>> = new Map();
  private eventListeners: Map<string, Array<(event: any) => void>> = new Map();
  private reconnectInterval: number = 3000;
  private maxReconnectAttempts: number = 5;
  private reconnectAttempts: number = 0;
  private url: string;

  constructor(url: string) {
    this.url = url;
  }

  /**
   * 设置WebSocket URL
   * @param url WebSocket URL
   */
  setUrl(url: string): void {
    this.url = url;
    console.log('WsService - 设置WebSocket URL:', url);
    // 如果当前已连接，断开并重新连接
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.disconnect();
      this.connect();
    }
  }

  /**
   * 连接WebSocket
   */
  connect(): void {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      return;
    }

    console.log('WsService - 连接WebSocket:', this.url);
    this.ws = new WebSocket(this.url);

    this.ws.onopen = () => {
      console.log('WebSocket连接已建立');
      this.reconnectAttempts = 0;
      ElMessage.success('WebSocket连接已建立');
    };

    this.ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        const { type, ...payload } = data;
        if (type && this.callbacks.has(type)) {
          this.callbacks.get(type)?.forEach(callback => callback(payload));
        }
      } catch (error) {
        console.error('WebSocket消息解析错误:', error);
      }
    };

    this.ws.onclose = () => {
      console.log('WebSocket连接已关闭');
      this.reconnectAttempts++;
      if (this.reconnectAttempts <= this.maxReconnectAttempts) {
        ElMessage.warning(`WebSocket连接已关闭，${this.reconnectAttempts}秒后尝试重连...`);
        setTimeout(() => this.connect(), this.reconnectInterval);
      } else {
        ElMessage.error('WebSocket重连失败，请刷新页面');
      }
    };

    this.ws.onerror = (error) => {
      console.error('WebSocket错误:', error);
      ElMessage.error('WebSocket连接错误');
      this.ws?.close();
    };
  }

  /**
   * 发送消息
   * @param data 消息数据
   */
  send(data: any): void {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(data));
    } else {
      console.error('WebSocket未连接，无法发送消息');
      ElMessage.error('WebSocket未连接，无法发送消息');
    }
  }

  /**
   * 订阅消息
   * @param type 消息类型
   * @param callback 回调函数
   */
  subscribe(type: string, callback: (data: any) => void): void {
    if (!this.callbacks.has(type)) {
      this.callbacks.set(type, []);
    }
    this.callbacks.get(type)?.push(callback);
  }

  /**
   * 取消订阅
   * @param type 消息类型
   * @param callback 回调函数（可选，不提供则取消所有该类型的订阅）
   */
  unsubscribe(type: string, callback?: (data: any) => void): void {
    if (!this.callbacks.has(type)) {
      return;
    }

    if (callback) {
      const callbacks = this.callbacks.get(type);
      if (callbacks) {
        this.callbacks.set(type, callbacks.filter(cb => cb !== callback));
      }
    } else {
      this.callbacks.delete(type);
    }
  }

  /**
   * 关闭WebSocket连接
   */
  close(): void {
    if (this.ws) {
      this.ws.close();
      this.ws = null;
      this.callbacks.clear();
    }
  }

  /**
   * 断开WebSocket连接（别名方法）
   */
  disconnect(): void {
    this.close();
  }

  /**
   * 取消所有订阅
   */
  unsubscribeAll(): void {
    this.callbacks.clear();
  }

  /**
   * 监听WebSocket事件
   * @param event 事件名称
   * @param listener 事件监听器
   */
  on(event: string, listener: (event: any) => void): void {
    if (!this.eventListeners.has(event)) {
      this.eventListeners.set(event, []);
    }
    this.eventListeners.get(event)?.push(listener);

    // 如果WebSocket实例已存在，直接添加事件监听
    if (this.ws) {
      switch (event) {
        case 'open':
          this.ws.onopen = (e) => {
            console.log('WebSocket连接已建立');
            this.reconnectAttempts = 0;
            ElMessage.success('WebSocket连接已建立');
            this.eventListeners.get('open')?.forEach(l => l(e));
          };
          break;
        case 'error':
          this.ws.onerror = (e) => {
            console.error('WebSocket错误:', e);
            ElMessage.error('WebSocket连接错误');
            this.ws?.close();
            this.eventListeners.get('error')?.forEach(l => l(e));
          };
          break;
        case 'close':
          this.ws.onclose = (e) => {
            console.log('WebSocket连接已关闭');
            this.reconnectAttempts++;
            if (this.reconnectAttempts <= this.maxReconnectAttempts) {
              ElMessage.warning(`WebSocket连接已关闭，${this.reconnectAttempts}秒后尝试重连...`);
              setTimeout(() => this.connect(), this.reconnectInterval);
            } else {
              ElMessage.error('WebSocket重连失败，请刷新页面');
            }
            this.eventListeners.get('close')?.forEach(l => l(e));
          };
          break;
      }
    }
  }
}

// 创建并导出WebSocket服务实例
const wsService = new WsService(`ws://${window.location.host}/shake-ws`);

export default wsService;
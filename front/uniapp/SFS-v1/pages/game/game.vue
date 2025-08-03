<template>
	<view class="content">
		<image class="logo" src="/static/logo.png"></image>
		<view class="text-area">
			<text class="title">{{title}}</text>
		</view>
		<view class="shake-controls">
			<text class="count">摇一摇次数: {{shakeCount}}</text>
			<text class="count">倒计时: {{countdown}}秒</text>
		</view>
	</view>
</template>


<script>
	export default {
		data() {
			return {
				title: '摇一摇游戏',
				shakeCount: 0,
				isShaking: false,
				countdown: 10
			}
		},
		onLoad() {
			// 请求加速度传感器权限
			uni.authorize({
				scope: 'scope.accelerometer',
				success: () => {
					console.log('加速度传感器权限获取成功');
					// 自动开始摇一摇检测
					this.startShake();
				},
				fail: () => {
					console.log('加速度传感器权限获取失败');
				}
			});
			this.startShake();
			// 开始10秒倒计时
			this.startCountdown();
		},
		methods: {
			startShake() {
				if (this.isShaking) return;
				
				this.isShaking = true;
				this.shakeCount = 0;
				
				// 开始监听加速度数据
				uni.startAccelerometer({
					interval: 'normal',
					success: () => {
						console.log('开始监听加速度数据');
					}
				});
				
				// 监听加速度变化
				uni.onAccelerometerChange((res) => {
					if (this.isShaking && (Math.abs(res.x) > 1.5 || Math.abs(res.y) > 1.5 || Math.abs(res.z) > 1.5)) {
						this.shakeCount++;
					}
				});
			},
			
			stopShake() {
				if (!this.isShaking) return;
				
				this.isShaking = false;
				
				// 停止监听加速度数据
				uni.stopAccelerometer({
					success: () => {
						console.log('停止监听加速度数据');
					}
				});
			},
			
			startCountdown() {
				const timer = setInterval(() => {
					this.countdown--;
					if (this.countdown <= 0) {
						clearInterval(timer);
						// 停止摇一摇检测
						this.stopShake();
						// 跳转到页面C
						uni.redirectTo({
							url: '/pages/rank/rank.vue'
						});
					}
				}, 1000);
			}
		}
	}
</script>
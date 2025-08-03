<template>
	<view class="content">
		<image class="logo" src="/static/logo.png"></image>
		<view class="text-area">
			<text class="title">{{title}}</text>
		</view>
		
		<view class="game-container">
			<!-- 准备阶段 -->
			<view v-if="gameStatus === 'ready'" class="ready-section">
				<text class="ready-text">点击开始按钮准备游戏</text>
				<button class="start-btn" @click="startGame">开始游戏</button>
			</view>
			
			<!-- 倒计时阶段 -->
			<view v-else-if="gameStatus === 'countdown'" class="countdown-section">
				<text class="countdown-text">{{countdown}}</text>
				<text class="countdown-label">准备开始摇一摇</text>
			</view>
			
			<!-- 摇一摇阶段 -->
			<view v-else-if="gameStatus === 'shaking'" class="shake-section">
				<text class="count">摇一摇次数: {{shakeCount}}</text>
				<text class="count">倒计时: {{countdown}}秒</text>
				<text class="shake-tip">用力摇晃手机！</text>
			</view>
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
				countdown: 5,
				gameStatus: 'ready' // 'ready', 'countdown', 'shaking', 'finished'
			}
		},
			onLoad() {
				// 页面加载时请求加速度传感器权限
				uni.authorize({
					scope: 'scope.accelerometer',
					success: () => {
						console.log('加速度传感器权限获取成功');
					},
					fail: () => {
						console.log('加速度传感器权限获取失败');
					}
				});
			},
		methods: {
			startGame() {
				// 设置游戏状态为倒计时
				this.gameStatus = 'countdown';
				this.countdown = 5;
				
				// 开始5秒倒计时
				const countdownTimer = setInterval(() => {
					this.countdown--;
					if (this.countdown <= 0) {
						clearInterval(countdownTimer);
						// 倒计时结束，开始摇一摇
						this.startShake();
					}
				}, 1000);
			},
			
			startShake() {
				if (this.isShaking) return;
				
				this.isShaking = true;
				this.shakeCount = 0;
				this.gameStatus = 'shaking';
				this.countdown = 10; // 10秒摇一摇时间
				
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
				
				// 开始10秒摇一摇倒计时
				const shakeTimer = setInterval(() => {
					this.countdown--;
					if (this.countdown <= 0) {
						clearInterval(shakeTimer);
						// 停止摇一摇检测
						this.stopShake();
						// 跳转到排行榜页面
						uni.redirectTo({
							url: '/pages/rank/rank'
						});
					}
				}, 1000);
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
			}
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: flex-start;
		height: 100vh;
		padding: 20px;
		background-color: #f5f5f5;
	}
	
	.logo {
		width: 100px;
		height: 100px;
		margin: 20px 0;
	}
	
	.title {
		font-size: 24px;
		font-weight: bold;
		color: #333;
		margin-bottom: 30px;
	}
	
	.game-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		flex: 1;
		width: 100%;
	}
	
	.ready-section, .countdown-section, .shake-section {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		width: 100%;
		height: 100%;
	}
	
	.ready-text {
		font-size: 18px;
		color: #666;
		margin-bottom: 30px;
		text-align: center;
	}
	
	.start-btn {
		background-color: #3498db;
		color: white;
		border: none;
		border-radius: 30px;
		padding: 15px 40px;
		font-size: 18px;
		font-weight: bold;
		cursor: pointer;
	}
	
	.countdown-text {
		font-size: 80px;
		font-weight: bold;
		color: #3498db;
		margin-bottom: 20px;
	}
	
	.countdown-label {
		font-size: 20px;
		color: #666;
	}
	
	.count {
		font-size: 18px;
		color: #333;
		margin: 10px 0;
	}
	
	.shake-tip {
		font-size: 20px;
		color: #e74c3c;
		font-weight: bold;
		margin-top: 30px;
		animation: shake 0.5s infinite;
	}
	
	@keyframes shake {
		0% { transform: translateX(0); }
		25% { transform: translateX(-5px); }
		50% { transform: translateX(5px); }
		75% { transform: translateX(-5px); }
		100% { transform: translateX(0); }
	}
</style>
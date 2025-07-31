<script>
	export default {
		// 全局错误处理
		onError: function(err) {
			console.error('全局错误:', err);
			uni.showToast({
				title: '发生错误: ' + err,
				icon: 'none',
				duration: 3000
			});
		},

		// 页面不存在时的处理
		onPageNotFound: function(res) {
			console.error('页面不存在:', res);
			uni.showToast({
				title: '页面不存在: ' + res.path,
				icon: 'none',
				duration: 3000
			});
			// 可以重定向到首页
			setTimeout(function() {
				uni.navigateTo({
					url: '/pages/index/index'
				});
			}, 1000);
		},
		onLaunch: function() {
			console.log('App Launch');
			
			// 检查传感器权限状态（不主动请求）
			this.checkSensorPermission();
		},
		methods: {
			// 检查传感器权限状态（仅检查，不主动请求）
			checkSensorPermission() {
				uni.getSetting({
					success: (res) => {
						if (!res.authSetting['scope.accelerometer']) {
							console.log('传感器权限未授权');
							// 不在这里主动请求权限，而是在game.vue中需要时再请求
						} else {
							console.log('传感器权限已授权');
						}
					}
				});
			},
			// 请求传感器权限（由game.vue调用）
			requestSensorAuth() {
				uni.authorize({
					scope: 'scope.accelerometer',
					success: () => {
						console.log('传感器权限获取成功');
					},
					fail: (error) => {
						console.log('传感器权限获取失败', error);
						// 显示错误提示
						uni.showToast({
							title: '需要传感器权限才能游戏',
							icon: 'none',
							duration: 2000
						});
						// 引导用户手动授权
						setTimeout(() => {
							uni.showModal({
								title: '权限申请',
								content: '需要加速度传感器权限才能游戏，请前往设置开启',
								confirmText: '去设置',
								cancelText: '取消',
								success: (resModal) => {
									if (resModal.confirm) {
										uni.openSetting({
											success: (settingRes) => {
												console.log('设置页面返回结果:', settingRes.authSetting);
											}
										});
									}
								}
							});
						}, 2000);
					}
				});
			}
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style>
	/*每个页面公共css */
</style>

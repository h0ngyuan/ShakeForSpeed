<script>
	export default {
		// 全局错误处理
		onError: function(err) {
			console.error('全局错误:', err);
			wx.showToast({
				title: '发生错误: ' + err,
				icon: 'none',
				duration: 3000
			});
		},

		// 页面不存在时的处理
		onPageNotFound: function(res) {
				console.error('页面不存在:', res);
				wx.showToast({
					title: '页面不存在: ' + res.path,
					icon: 'none',
					duration: 3000
				});
				// 可以重定向到首页
				setTimeout(function() {
					wx.navigateTo({
						url: '/pages/index/index'
					});
				}, 1000);
			},
		onLaunch: function() {
			console.log('App Launch');
		},
		methods: {
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

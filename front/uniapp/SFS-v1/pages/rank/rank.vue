<template>
  <view class="rank-container">
    <!-- 顶部标题 -->
    <view class="header">
      <text class="title">排行榜</text>
    </view>
    
    <!-- 用户当前排名 -->
    <view class="user-rank" v-if="userRank">
      <view class="rank-info">
        <text class="rank-text">我的排名</text>
        <text class="rank-number">{{ userRank.rank }}</text>
      </view>
      <view class="user-info">
        <UserInfo :userInfo="userRank" />
      </view>
      <view class="score">
        <text>{{ userRank.score }}次</text>
      </view>
    </view>
    
    <!-- 排行榜列表 -->
    <view class="rank-list">
      <view 
        class="rank-item" 
        :class="{ 'top-three': item.rank <= 3, 'first': item.rank === 1, 'second': item.rank === 2, 'third': item.rank === 3 }"
        v-for="item in rankings" 
        :key="item.id"
      >
        <view class="rank-number">{{ item.rank }}</view>
        <image :src="item.avatarUrl" class="avatar"></image>
        <view class="user-info">
          <text class="nickname">{{ item.nickName }}</text>
        </view>
        <view class="score">{{ item.score }}次</view>
      </view>
    </view>
    
    <!-- 返回首页按钮 -->
    <button class="back-btn" @click="goHome">返回首页</button>
  </view>
</template>

<script>
import UserInfo from '../../components/UserInfo.vue';
// 导入API函数
import { getRankings } from '../../api/mock.js';

export default {
  components: {
    UserInfo
  },
  data() {
    return {
      rankings: [],
      userInfo: {
        id: 1,
        nickName: '默认用户',
        avatarUrl: '/static/avatar/default.png'
      },
      userRank: null
    };
  },
  
  // 页面加载时获取排行榜数据
  onLoad(options) {
    console.log('排行榜页面加载，参数:', options);
    // 解析传递过来的用户信息
    if (options && options.userInfo) {
      try {
        this.userInfo = JSON.parse(decodeURIComponent(options.userInfo));
        console.log('解析用户信息成功:', this.userInfo);
      } catch (e) {
        console.error('解析用户信息失败', e);
      }
    }
    this.fetchRankings();
  },
  
  methods: {
    // 获取排行榜数据
    async fetchRankings() {
      try {
        // 调用API获取排行榜数据
          const result = await getRankings();
        
        if (result.success) {
          this.rankings = result.data;
          
          // 查找当前用户排名
          const userRanking = this.rankings.find(item => item.id === this.userInfo.id);
          this.userRank = userRanking || null;
        }
      } catch (error) {
        console.error('获取排行榜失败', error);
        
        // 如果模拟API调用失败，使用默认数据，并确保包含当前用户信息
        this.rankings = [
          { id: this.userInfo.id, nickName: this.userInfo.nickName, avatarUrl: this.userInfo.avatarUrl, score: 100, rank: 1 },
          { id: 2, nickName: '张三', avatarUrl: '/static/avatar/avatar1.png', score: 95, rank: 2 },
          { id: 3, nickName: '李四', avatarUrl: '/static/avatar/avatar2.png', score: 88, rank: 3 },
          { id: 4, nickName: '王五', avatarUrl: '/static/avatar/avatar3.png', score: 82, rank: 4 },
          { id: 5, nickName: '赵六', avatarUrl: '/static/avatar/avatar4.png', score: 76, rank: 5 }
        ];
        
        // 查找当前用户排名
        const userRanking = this.rankings.find(item => item.id === this.userInfo.id);
        this.userRank = userRanking || null;
      }
    },
    
    // 返回首页
    goHome() {
      uni.redirectTo({
        url: '/pages/index/index'
      });
    }
  }
};
</script>

<style scoped>
.rank-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px;
  box-sizing: border-box;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.user-rank {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 15px;
  padding: 20px;
  margin-bottom: 30px;
  color: white;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.rank-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.rank-text {
  font-size: 16px;
  margin-bottom: 5px;
}

.rank-number {
  font-size: 24px;
  font-weight: bold;
}

.user-info {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .user-info ::v-deep .user-avatar {
    width: 60px;
    height: 60px;
    margin-bottom: 10px;
    border: 2px solid white;
  }
  
  .user-info ::v-deep .user-name {
    font-size: 16px;
    font-weight: bold;
  }

.score {
  font-size: 20px;
  font-weight: bold;
}

.rank-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.rank-item {
  display: flex;
  align-items: center;
  background-color: #fff;
  border-radius: 15px;
  padding: 15px;
  width: 100%;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

/* 前三名样式 */
.rank-item.first {
  flex-direction: column;
  width: 30%;
  padding: 20px 10px 15px;
  /* 第一名背景渐变 */
  background: linear-gradient(135deg, #FFD700, #FFFACD);
  color: #333;
}

.rank-item.second {
  flex-direction: column;
  width: 28%;
  padding: 15px 10px 10px;
  margin-top: 20px;
  /* 第二名背景渐变 */
  background: linear-gradient(135deg, #C0C0C0, #E8E8E8);
  color: #333;
}

.rank-item.third {
  flex-direction: column;
  width: 28%;
  padding: 15px 10px 10px;
  margin-top: 40px;
  /* 第三名背景渐变 */
  background: linear-gradient(135deg, #CD7F32, #F0E68C);
  color: #333;
}

.rank-number {
  font-size: 22px;
  font-weight: bold;
  margin-right: 15px;
  color: #888;
  min-width: 30px;
  text-align: center;
}

.rank-item.first .rank-number,
.rank-item.second .rank-number,
.rank-item.third .rank-number {
  color: #333;
  margin-right: 0;
  margin-bottom: 10px;
}

.rank-item .avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-right: 15px;
}

.rank-item.first .avatar,
.rank-item.second .avatar,
.rank-item.third .avatar {
  margin-right: 0;
  margin-bottom: 10px;
}

.rank-item .user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.rank-item .nickname {
  font-size: 16px;
  color: #333;
}

.rank-item.first .nickname,
.rank-item.second .nickname,
.rank-item.third .nickname {
  color: #333;
  margin-bottom: 5px;
}

.rank-item .score {
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f;
}

.rank-item.first .score,
.rank-item.second .score,
.rank-item.third .score {
  color: #333;
}

.back-btn {
  width: 100%;
  height: 50px;
  background-color: #333;
  color: white;
  border-radius: 25px;
  font-size: 18px;
  border: none;
  margin-top: 30px;
}
</style>
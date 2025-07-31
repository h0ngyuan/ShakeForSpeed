// 模拟后端API

// 模拟用户数据
const mockUsers = [
  { id: 1, nickName: '默认用户', avatarUrl: '/static/avatar/default.png' },
  { id: 2, nickName: '张三', avatarUrl: '/static/avatar/avatar1.png' },
  { id: 3, nickName: '李四', avatarUrl: '/static/avatar/avatar2.png' }
];

// 模拟排行榜数据
const mockRankings = [
  { id: 1, nickName: '默认用户', avatarUrl: '/static/avatar/default.png', score: 100, rank: 1 },
  { id: 2, nickName: '张三', avatarUrl: '/static/avatar/avatar1.png', score: 95, rank: 2 },
  { id: 3, nickName: '李四', avatarUrl: '/static/avatar/avatar2.png', score: 88, rank: 3 },
  { id: 4, nickName: '王五', avatarUrl: '/static/avatar/avatar3.png', score: 82, rank: 4 },
  { id: 5, nickName: '赵六', avatarUrl: '/static/avatar/avatar4.png', score: 76, rank: 5 }
];

// 获取默认用户
export function getDefaultUser() {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        success: true,
        data: mockUsers[0]
      });
    }, 500);
  });
}

// 获取排行榜
export function getRankings() {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        success: true,
        data: mockRankings
      });
    }, 500);
  });
}

// 提交游戏分数
export function submitScore(score, userInfo) {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 模拟提交分数后重新计算排名
      let newRankings = [...mockRankings];
      
      // 使用传入的用户信息或默认用户
      const user = userInfo || mockUsers[0];
      
      // 查找用户是否已经在排行榜中
      const existingUserIndex = newRankings.findIndex(item => item.id === user.id);
      
      if (existingUserIndex >= 0) {
        // 如果用户已在排行榜中，更新其分数（如果新分数更高）
        if (score > newRankings[existingUserIndex].score) {
          newRankings[existingUserIndex].score = score;
        }
      } else {
        // 如果用户不在排行榜中，添加新记录
        newRankings.push({
          id: user.id,
          nickName: user.nickName,
          avatarUrl: user.avatarUrl,
          score: score,
          rank: newRankings.length + 1
        });
      }
      
      // 简单排序
      newRankings.sort((a, b) => b.score - a.score);
      
      // 更新排名
      newRankings.forEach((item, index) => {
        item.rank = index + 1;
      });
      
      // 更新全局排行榜数据
      mockRankings.length = 0;
      mockRankings.push(...newRankings);
      
      resolve({
        success: true,
        data: newRankings
      });
    }, 500);
  });
}

// CommonJS导出
module.exports = {
  getDefaultUser,
  getRankings,
  submitScore
};
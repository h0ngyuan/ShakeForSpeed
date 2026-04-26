import http from '../utils/request.js'

export default {
  wxLogin(code) {
    return http.post('/api/auth/wx-login', { code })
  },
  phoneLogin(phone, code) {
    return http.post(`/api/auth/phone-login?phone=${phone}&code=${code}`)
  },
  logout() {
    return http.post('/api/auth/logout')
  },
  joinRoom(roomCode, roomPwd) {
    return http.post('/api/rooms/join', { roomCode, roomPwd })
  },
  leaveRoom(activityId) {
    return http.post(`/api/rooms/leave?activityId=${activityId}`)
  },
  getRoomStatus(roomCode) {
    return http.get(`/api/rooms/${roomCode}/status`)
  },
  getActivityList(params) {
    return http.get('/api/activities', params)
  },
  getActivityDetail(id) {
    return http.get(`/api/activities/${id}`)
  },
  getRealtimeRank(activityId, limit = 10) {
    return http.get(`/api/ranks/${activityId}/realtime`, { limit })
  },
  getRewardConfig(activityId) {
    return http.get(`/api/rewards/${activityId}`)
  },
  getMyRewards() {
    return http.get('/api/rewards/my')
  }
}

const BASE_URL = 'http://localhost:8080'

function request(options) {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    uni.request({
      url: `${BASE_URL}${options.url}`,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'satoken': token,
        'Content-Type': options.contentType || 'application/json',
        ...options.header
      },
      success: (res) => {
        if (res.data.code === 200) {
          resolve(res.data.data)
        } else if (res.data.code === 401) {
          uni.removeStorageSync('token')
          uni.reLaunch({ url: '/pages/login/login' })
          reject(new Error(res.data.msg))
        } else {
          reject(new Error(res.data.msg))
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

export default {
  get(url, data) { return request({ url, method: 'GET', data }) },
  post(url, data) { return request({ url, method: 'POST', data }) },
  put(url, data) { return request({ url, method: 'PUT', data }) },
  delete(url, data) { return request({ url, method: 'DELETE', data }) }
}

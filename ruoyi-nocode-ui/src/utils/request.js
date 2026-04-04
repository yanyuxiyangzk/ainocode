import axios from 'axios'

const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || '/api',
  timeout: 30000
})

// Request interceptor
service.interceptors.request.use(
  config => {
    // Add token if available
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data

    // API response format: { code: 200, data: {}, msg: '' }
    if (res.code !== 200) {
      console.error('API Error:', res.msg || 'Unknown error')
      return Promise.reject(new Error(res.msg || 'Error'))
    }

    return res
  },
  error => {
    console.error('Response error:', error)

    if (error.response) {
      switch (error.response.status) {
        case 401:
          console.error('Unauthorized')
          // Redirect to login
          break
        case 403:
          console.error('Forbidden')
          break
        case 404:
          console.error('Not Found')
          break
        case 500:
          console.error('Server Error')
          break
        default:
          console.error('Request failed')
      }
    }

    return Promise.reject(error)
  }
)

export default service
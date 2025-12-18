import request from '@/utils/request'

export const userApi = {
  // 注册：POST /api/user/register (JSON)
  register(data) {
    return request.post('/api/user/register', data)
  },

  // 登录：POST /api/user/login (@RequestParam)
  login(username, password) {
    return request.post('/api/user/login', null, { params: { username, password } })
  },

  // 当前登录用户：GET /api/user/current
  current() {
    return request.get('/api/user/current')
  },

  // 用户分页（管理员）：GET /api/user/list?pageNum=&pageSize=&username=
  page({ pageNum = 1, pageSize = 10, username = '' } = {}) {
    return request.get('/api/user/list', {
      params: { pageNum, pageSize, username: username || undefined },
    })
  },

  // 修改状态（管理员）：PUT /api/user/status/{id}?status=
  updateStatus(id, status) {
    return request.put(`/api/user/status/${id}`, null, { params: { status } })
  },
}

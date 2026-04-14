import request from './request'

// ===== 认证 =====
export const adminApi = {
  login: (data: { username: string; password: string }) =>
    request.post('/admin/auth/login', data),
  logout: () => request.post('/admin/auth/logout'),
  getMe: () => request.get('/admin/auth/me'),
}

// ===== 仪表盘 =====
export const dashboardApi = {
  getStats: () => request.get('/admin/api/dashboard/stats'),
}

// ===== 商品管理 =====
export const productAdminApi = {
  list: (params: any) => request.get('/admin/api/products', { params }),
  getDetail: (id: number) => request.get(`/admin/api/products/${id}`),
  audit: (id: number, data: { auditStatus: number; remark?: string }) =>
    request.put(`/admin/api/products/${id}/audit`, data),
  offShelf: (id: number) => request.put(`/admin/api/products/${id}/off-shelf`),
}

// ===== 用户管理 =====
export const userAdminApi = {
  list: (params: any) => request.get('/admin/api/users', { params }),
  updateStatus: (id: number, status: number) =>
    request.put(`/admin/api/users/${id}/status`, null, { params: { status } }),
}

// ===== 举报管理 =====
export const reportAdminApi = {
  list: (params: any) => request.get('/admin/api/reports', { params }),
  handle: (id: number, data: { status: number; remark?: string }) =>
    request.put(`/admin/api/reports/${id}/handle`, data),
}

// ===== 分类管理 =====
export const categoryAdminApi = {
  list: () => request.get('/admin/api/categories'),
  add: (data: any) => request.post('/admin/api/categories', data),
  update: (id: number, data: any) => request.put(`/admin/api/categories/${id}`, data),
  delete: (id: number) => request.delete(`/admin/api/categories/${id}`),
}

// ===== 学校管理 =====
export const schoolAdminApi = {
  list: (params: any) => request.get('/admin/api/schools', { params }),
  add: (data: any) => request.post('/admin/api/schools', data),
  update: (id: number, data: any) => request.put(`/admin/api/schools/${id}`, data),
  delete: (id: number) => request.delete(`/admin/api/schools/${id}`),
}

// ===== 校区管理 =====
export const campusAdminApi = {
  list: (schoolId: number) => request.get('/admin/api/campuses', { params: { schoolId } }),
  add: (data: any) => request.post('/admin/api/campuses', data),
  update: (id: number, data: any) => request.put(`/admin/api/campuses/${id}`, data),
  delete: (id: number) => request.delete(`/admin/api/campuses/${id}`),
}

// ===== 公告管理 =====
export const noticeAdminApi = {
  list: (params: any) => request.get('/admin/api/notices', { params }),
  add: (data: any) => request.post('/admin/api/notices', data),
  update: (id: number, data: any) => request.put(`/admin/api/notices/${id}`, data),
  delete: (id: number) => request.delete(`/admin/api/notices/${id}`),
}

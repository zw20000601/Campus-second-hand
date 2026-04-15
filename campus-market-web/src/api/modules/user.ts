import request from '../request'
import type { UserVO } from '@/types'

export const userApi = {
  /** 获取当前用户 */
  getMe: () =>
    request.get<any, { data: UserVO }>('/user/me'),

  /** 更新资料 */
  updateProfile: (data: Partial<UserVO>) =>
    request.put<any, { data: UserVO }>('/user/profile', data),

  /** 上传头像图片文件，返回图片 URL */
  uploadAvatarFile: (formData: FormData) =>
    request.post<any, { data: string }>('/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),

  /** 将头像 URL 持久化到用户记录 */
  updateAvatarUrl: (avatarUrl: string) =>
    request.put<any, { data: any }>(`/user/avatar?avatarUrl=${encodeURIComponent(avatarUrl)}`),
}

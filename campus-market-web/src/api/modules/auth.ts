import request from '../request'
import type { LoginDTO, LoginVO, RegisterDTO } from '@/types'

export const authApi = {
  /** 注册 */
  register: (data: RegisterDTO) =>
    request.post<any, { data: LoginVO }>('/auth/register', data),

  /** 登录 */
  login: (data: LoginDTO) =>
    request.post<any, { data: LoginVO }>('/auth/login', data),

  /** 退出 */
  logout: () =>
    request.post('/auth/logout'),
}

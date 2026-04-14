import request from '../request'
import type { MessageVO, PageDTO, PageVO } from '@/types'

export const messageApi = {
  list: (productId: number, params: PageDTO) =>
    request.get<any, { data: PageVO<MessageVO> }>(`/products/${productId}/messages`, { params }),

  publish: (productId: number, content: string, parentId?: number) =>
    request.post<any, { data: MessageVO }>(`/products/${productId}/messages`, { content, parentId }),
}

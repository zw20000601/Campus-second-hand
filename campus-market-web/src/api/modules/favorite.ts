import request from '../request'
import type { PageDTO, PageVO, ProductListVO } from '@/types'

export const favoriteApi = {
  add: (productId: number) =>
    request.post(`/favorites/${productId}`),

  remove: (productId: number) =>
    request.delete(`/favorites/${productId}`),

  myFavorites: (params: PageDTO) =>
    request.get<any, { data: PageVO<ProductListVO> }>('/my/favorites', { params }),
}

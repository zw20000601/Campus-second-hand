import request from '../request'
import type { PageVO, ProductListVO, ProductPublishDTO, ProductQueryDTO, ProductVO } from '@/types'

export const productApi = {
  /** 商品列表 */
  list: (params: ProductQueryDTO) =>
    request.get<any, { data: PageVO<ProductListVO> }>('/products', { params }),

  /** 商品详情 */
  detail: (id: number) =>
    request.get<any, { data: ProductVO }>(`/products/${id}`),

  /** 发布商品 */
  publish: (data: ProductPublishDTO) =>
    request.post<any, { data: ProductVO }>('/products', data),

  /** 更新商品 */
  update: (id: number, data: ProductPublishDTO) =>
    request.put<any, { data: ProductVO }>(`/products/${id}`, data),

  /** 删除商品 */
  delete: (id: number) =>
    request.delete(`/products/${id}`),

  /** 下架 */
  offShelf: (id: number) =>
    request.put(`/products/${id}/off-shelf`),

  /** 标记售出 */
  markSold: (id: number) =>
    request.put(`/products/${id}/sold`),

  /** 我的发布 */
  myProducts: (params: ProductQueryDTO) =>
    request.get<any, { data: PageVO<ProductListVO> }>('/my/products', { params }),

  /** 上传图片 */
  uploadImage: (formData: FormData) =>
    request.post<any, { data: string }>('/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
}

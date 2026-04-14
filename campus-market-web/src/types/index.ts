// =====================================================
// 通用类型定义
// =====================================================

/** 统一响应体 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

/** 分页结果 */
export interface PageVO<T> {
  total: number
  pages: number
  pageNum: number
  pageSize: number
  list: T[]
}

/** 分页查询参数 */
export interface PageDTO {
  pageNum?: number
  pageSize?: number
}

// =====================================================
// 用户相关
// =====================================================

export interface UserVO {
  id: number
  username: string
  nickname: string
  avatar: string
  gender: number
  bio: string
  schoolId: number
  schoolName: string
  campusId: number
  campusName: string
  verified: number
  createdAt: string
}

export interface LoginVO {
  token: string
  user: UserVO
}

export interface RegisterDTO {
  username: string
  password: string
  nickname?: string
  schoolId?: number
  campusId?: number
}

export interface LoginDTO {
  username: string
  password: string
}

// =====================================================
// 学校/校区
// =====================================================

export interface School {
  id: number
  name: string
  logo: string
  city: string
  province: string
}

export interface Campus {
  id: number
  schoolId: number
  name: string
  address: string
}

// =====================================================
// 分类
// =====================================================

export interface ProductCategory {
  id: number
  name: string
  icon: string
  parentId: number
  level: number
  sort: number
}

// =====================================================
// 商品相关
// =====================================================

/** 商品状态 */
export enum ProductStatus {
  PENDING = 0,   // 待审核
  ON_SHELF = 1,  // 已上架
  OFF_SHELF = 2, // 已下架
  SOLD = 3,      // 已售出
  REJECTED = 4,  // 审核拒绝
}

/** 成色 */
export const CONDITION_LABELS = ['', '全新', '几乎全新', '轻微使用', '明显使用', '有瑕疵']

/** 交易方式 */
export const TRADE_TYPE_LABELS = ['', '线下交易', '快递邮寄', '均可']

export interface ProductListVO {
  id: number
  title: string
  price: number
  originalPrice: number
  coverImage: string
  conditionLevel: number
  conditionDesc: string
  tradeType: number
  schoolName: string
  campusName: string
  viewCount: number
  favoriteCount: number
  status: number
  createdAt: string
  userNickname: string
  userAvatar: string
}

export interface ProductVO extends ProductListVO {
  userId: number
  categoryId: number
  categoryName: string
  description: string
  images: string[]
  tradeTypeDesc: string
  tradeLocation: string
  messageCount: number
  favorited: boolean
}

export interface ProductPublishDTO {
  title: string
  description?: string
  price: number
  originalPrice?: number
  categoryId: number
  schoolId: number
  campusId?: number
  coverImage: string
  images?: string[]
  conditionLevel: number
  tradeType: number
  tradeLocation?: string
}

export interface ProductQueryDTO extends PageDTO {
  keyword?: string
  categoryId?: number
  schoolId?: number
  campusId?: number
  minPrice?: number
  maxPrice?: number
  conditionLevel?: number
  tradeType?: number
  sortBy?: 'newest' | 'price_asc' | 'price_desc'
}

// =====================================================
// 留言
// =====================================================

export interface MessageVO {
  id: number
  productId: number
  parentId: number
  userId: number
  userNickname: string
  userAvatar: string
  content: string
  createdAt: string
}

// =====================================================
// 公告
// =====================================================

export interface Notice {
  id: number
  title: string
  content: string
  cover: string
  type: number
  isTop: number
  viewCount: number
  createdAt: string
}

package com.campus.market.module.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.market.common.page.PageVO;
import com.campus.market.module.product.dto.ProductPublishDTO;
import com.campus.market.module.product.dto.ProductQueryDTO;
import com.campus.market.module.product.entity.Product;
import com.campus.market.module.product.vo.ProductListVO;
import com.campus.market.module.product.vo.ProductVO;

/**
 * 商品服务接口
 */
public interface ProductService extends IService<Product> {

    /** 发布商品 */
    ProductVO publish(ProductPublishDTO dto);

    /** 分页查询商品列表（公开，只展示已上架） */
    PageVO<ProductListVO> pageList(ProductQueryDTO dto);

    /** 获取商品详情 */
    ProductVO getDetail(Long id);

    /** 更新商品 */
    ProductVO update(Long id, ProductPublishDTO dto);

    /** 删除商品（软删） */
    void delete(Long id);

    /** 下架商品 */
    void offShelf(Long id);

    /** 标记已售出 */
    void markSold(Long id);

    /** 获取我的发布列表 */
    PageVO<ProductListVO> myProducts(ProductQueryDTO dto);

    /** 将 Product 转为 ListVO */
    ProductListVO toListVO(Product product);

    /** 将 Product 转为 DetailVO */
    ProductVO toDetailVO(Product product);
}

package com.campus.market.module.favorite.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.common.exception.BusinessException;
import com.campus.market.common.page.PageDTO;
import com.campus.market.common.page.PageVO;
import com.campus.market.common.result.R;
import com.campus.market.common.result.ResultCode;
import com.campus.market.module.favorite.entity.Favorite;
import com.campus.market.module.favorite.mapper.FavoriteMapper;
import com.campus.market.module.product.entity.Product;
import com.campus.market.module.product.mapper.ProductMapper;
import com.campus.market.module.product.service.ProductService;
import com.campus.market.module.product.vo.ProductListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏接口（需登录）
 */
@Tag(name = "收藏", description = "收藏/取消收藏/收藏列表")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;
    private final ProductService productService;

    @Operation(summary = "收藏商品")
    @PostMapping("/favorites/{productId}")
    public R<Void> add(@PathVariable Long productId) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        // 检查是否已收藏
        long count = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));
        if (count > 0) {
            throw new BusinessException(ResultCode.FAVORITE_EXIST);
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteMapper.insert(favorite);

        // 更新收藏计数
        Product update = new Product();
        update.setId(productId);
        update.setFavoriteCount(product.getFavoriteCount() + 1);
        productMapper.updateById(update);

        return R.ok();
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/favorites/{productId}")
    public R<Void> remove(@PathVariable Long productId) {
        Long userId = StpUtil.getLoginIdAsLong();

        int deleted = favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));
        if (deleted == 0) {
            throw new BusinessException(ResultCode.FAVORITE_NOT_FOUND);
        }

        // 更新收藏计数
        Product product = productMapper.selectById(productId);
        if (product != null && product.getFavoriteCount() > 0) {
            Product update = new Product();
            update.setId(productId);
            update.setFavoriteCount(product.getFavoriteCount() - 1);
            productMapper.updateById(update);
        }

        return R.ok();
    }

    @Operation(summary = "我的收藏列表")
    @GetMapping("/my/favorites")
    public R<PageVO<ProductListVO>> myFavorites(PageDTO pageDTO) {
        Long userId = StpUtil.getLoginIdAsLong();

        IPage<Favorite> favPage = favoriteMapper.selectPage(
                new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()),
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreatedAt));

        List<ProductListVO> voList = favPage.getRecords().stream()
                .map(fav -> {
                    Product product = productMapper.selectById(fav.getProductId());
                    if (product == null) return null;
                    return productService.toListVO(product);
                })
                .filter(vo -> vo != null)
                .collect(Collectors.toList());

        return R.ok(PageVO.of(favPage, voList));
    }
}

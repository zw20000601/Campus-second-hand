package com.campus.market.module.product.controller;

import com.campus.market.common.page.PageVO;
import com.campus.market.common.result.R;
import com.campus.market.module.product.dto.ProductPublishDTO;
import com.campus.market.module.product.dto.ProductQueryDTO;
import com.campus.market.module.product.service.ProductService;
import com.campus.market.module.product.vo.ProductListVO;
import com.campus.market.module.product.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品接口
 */
@Tag(name = "商品", description = "商品发布/列表/详情")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "商品列表（公开）")
    @GetMapping("/products")
    public R<PageVO<ProductListVO>> list(ProductQueryDTO dto) {
        return R.ok(productService.pageList(dto));
    }

    @Operation(summary = "商品详情（公开）")
    @GetMapping("/products/{id}")
    public R<ProductVO> detail(@PathVariable Long id) {
        return R.ok(productService.getDetail(id));
    }

    @Operation(summary = "发布商品（需登录）")
    @PostMapping("/products")
    public R<ProductVO> publish(@RequestBody @Valid ProductPublishDTO dto) {
        return R.ok(productService.publish(dto));
    }

    @Operation(summary = "更新商品（需登录）")
    @PutMapping("/products/{id}")
    public R<ProductVO> update(@PathVariable Long id, @RequestBody @Valid ProductPublishDTO dto) {
        return R.ok(productService.update(id, dto));
    }

    @Operation(summary = "删除商品（需登录）")
    @DeleteMapping("/products/{id}")
    public R<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return R.ok();
    }

    @Operation(summary = "下架商品（需登录）")
    @PutMapping("/products/{id}/off-shelf")
    public R<Void> offShelf(@PathVariable Long id) {
        productService.offShelf(id);
        return R.ok();
    }

    @Operation(summary = "标记已售出（需登录）")
    @PutMapping("/products/{id}/sold")
    public R<Void> sold(@PathVariable Long id) {
        productService.markSold(id);
        return R.ok();
    }

    @Operation(summary = "我的发布列表（需登录）")
    @GetMapping("/my/products")
    public R<PageVO<ProductListVO>> myProducts(ProductQueryDTO dto) {
        return R.ok(productService.myProducts(dto));
    }
}

package com.campus.market.module.category.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.market.common.result.R;
import com.campus.market.module.category.entity.ProductCategory;
import com.campus.market.module.category.mapper.ProductCategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品分类接口（公开）
 */
@Tag(name = "商品分类", description = "分类列表")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ProductCategoryMapper categoryMapper;

    @Operation(summary = "获取所有分类列表")
    @GetMapping
    public R<List<ProductCategory>> list() {
        List<ProductCategory> list = categoryMapper.selectList(
                new LambdaQueryWrapper<ProductCategory>()
                        .eq(ProductCategory::getStatus, 1)
                        .orderByAsc(ProductCategory::getSort));
        return R.ok(list);
    }
}

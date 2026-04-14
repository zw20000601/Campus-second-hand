package com.campus.market.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.market.common.result.R;
import com.campus.market.module.category.entity.ProductCategory;
import com.campus.market.module.category.mapper.ProductCategoryMapper;
import com.campus.market.common.satoken.StpAdminUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台分类管理
 */
@Tag(name = "后台-分类管理", description = "商品分类 CRUD")
@RestController
@RequestMapping("/admin/api/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final ProductCategoryMapper categoryMapper;

    @Operation(summary = "分类列表")
    @GetMapping
    public R<List<ProductCategory>> list() {
        return R.ok(categoryMapper.selectList(
                new LambdaQueryWrapper<ProductCategory>()
                        .orderByAsc(ProductCategory::getSort)));
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public R<ProductCategory> add(@RequestBody @Valid CategoryRequest req) {
        StpAdminUtil.checkSuperAdmin();
        ProductCategory category = new ProductCategory();
        category.setName(req.getName());
        category.setIcon(req.getIcon());
        category.setParentId(req.getParentId() != null ? req.getParentId() : 0L);
        category.setLevel(req.getParentId() != null && req.getParentId() > 0 ? 2 : 1);
        category.setSort(req.getSort() != null ? req.getSort() : 0);
        category.setStatus(1);
        categoryMapper.insert(category);
        return R.ok(category);
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody CategoryRequest req) {
        StpAdminUtil.checkSuperAdmin();
        ProductCategory category = categoryMapper.selectById(id);
        if (category == null) return R.fail("分类不存在");
        if (req.getName() != null) category.setName(req.getName());
        if (req.getIcon() != null) category.setIcon(req.getIcon());
        if (req.getSort() != null) category.setSort(req.getSort());
        if (req.getStatus() != null) category.setStatus(req.getStatus());
        categoryMapper.updateById(category);
        return R.ok();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        StpAdminUtil.checkSuperAdmin();
        categoryMapper.deleteById(id);
        return R.ok();
    }

    @Data
    public static class CategoryRequest {
        @NotBlank private String name;
        private String icon;
        private Long parentId;
        private Integer sort;
        private Integer status;
    }
}

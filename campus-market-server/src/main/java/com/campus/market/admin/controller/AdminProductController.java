package com.campus.market.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.market.common.satoken.StpAdminUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.common.page.PageVO;
import com.campus.market.common.result.R;
import com.campus.market.module.product.entity.Product;
import com.campus.market.module.product.mapper.ProductMapper;
import com.campus.market.module.product.service.ProductService;
import com.campus.market.module.product.vo.ProductListVO;
import com.campus.market.module.product.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台商品管理接口
 */
@Tag(name = "后台-商品管理", description = "商品审核/管理")
@RestController
@RequestMapping("/admin/api/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductMapper productMapper;
    private final ProductService productService;

    @Operation(summary = "商品详情（后台）")
    @GetMapping("/{id}")
    public R<ProductVO> detail(@PathVariable Long id) {
        ProductVO vo = productService.getDetail(id);
        if (vo == null) return R.fail("商品不存在");
        return R.ok(vo);
    }

    @Operation(summary = "商品列表（含所有状态）")
    @GetMapping
    public R<PageVO<ProductListVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long schoolId) {

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .orderByDesc(Product::getCreatedAt);

        if (StringUtils.hasText(keyword)) wrapper.like(Product::getTitle, keyword);
        if (status != null) wrapper.eq(Product::getStatus, status);
        if (auditStatus != null) wrapper.eq(Product::getAuditStatus, auditStatus);
        if (categoryId != null) wrapper.eq(Product::getCategoryId, categoryId);
        if (schoolId != null) wrapper.eq(Product::getSchoolId, schoolId);

        IPage<Product> page = productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<ProductListVO> voList = page.getRecords().stream()
                .map(productService::toListVO)
                .collect(Collectors.toList());
        return R.ok(PageVO.of(page, voList));
    }

    @Operation(summary = "审核商品")
    @PutMapping("/{id}/audit")
    public R<Void> audit(@PathVariable Long id, @RequestBody @Valid AuditRequest req) {
        Long adminId = StpAdminUtil.getLoginIdAsLong();

        Product product = productMapper.selectById(id);
        if (product == null) return R.fail("商品不存在");

        Product update = new Product();
        update.setId(id);
        update.setAuditStatus(req.getAuditStatus());
        update.setAuditRemark(req.getRemark());
        update.setAuditedBy(adminId);
        update.setAuditedAt(LocalDateTime.now());

        // 审核通过 -> 上架；审核拒绝 -> 状态4
        if (req.getAuditStatus() == 1) {
            update.setStatus(1); // 已上架
        } else if (req.getAuditStatus() == 2) {
            update.setStatus(4); // 审核拒绝
        }

        productMapper.updateById(update);
        return R.ok();
    }

    @Operation(summary = "后台下架商品")
    @PutMapping("/{id}/off-shelf")
    public R<Void> offShelf(@PathVariable Long id) {
        Product update = new Product();
        update.setId(id);
        update.setStatus(2);
        productMapper.updateById(update);
        return R.ok();
    }

    @Data
    public static class AuditRequest {
        @NotNull @Min(1) @Max(2)
        private Integer auditStatus; // 1=通过 2=拒绝
        @Size(max = 200)
        private String remark;
    }
}

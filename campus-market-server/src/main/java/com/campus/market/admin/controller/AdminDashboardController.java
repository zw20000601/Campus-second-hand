package com.campus.market.admin.controller;

import com.campus.market.common.result.R;
import com.campus.market.module.product.mapper.ProductMapper;
import com.campus.market.module.report.mapper.ReportMapper;
import com.campus.market.module.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台数据统计看板
 */
@Tag(name = "后台-数据看板", description = "平台数据统计")
@RestController
@RequestMapping("/admin/api/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final ReportMapper reportMapper;

    @Operation(summary = "获取统计数据")
    @GetMapping("/stats")
    public R<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();

        // 用户统计
        data.put("totalUsers", userMapper.selectCount(null));

        // 商品统计
        data.put("totalProducts", productMapper.selectCount(null));

        // 待审核商品
        data.put("pendingAuditProducts", productMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<
                        com.campus.market.module.product.entity.Product>()
                        .eq(com.campus.market.module.product.entity.Product::getAuditStatus, 0)));

        // 待处理举报
        data.put("pendingReports", reportMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<
                        com.campus.market.module.report.entity.Report>()
                        .eq(com.campus.market.module.report.entity.Report::getStatus, 0)));

        // 已上架商品
        data.put("onShelfProducts", productMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<
                        com.campus.market.module.product.entity.Product>()
                        .eq(com.campus.market.module.product.entity.Product::getStatus, 1)));

        return R.ok(data);
    }
}

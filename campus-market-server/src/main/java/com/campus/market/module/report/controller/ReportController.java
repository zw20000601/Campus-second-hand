package com.campus.market.module.report.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.market.common.exception.BusinessException;
import com.campus.market.common.result.R;
import com.campus.market.common.result.ResultCode;
import com.campus.market.module.report.entity.Report;
import com.campus.market.module.report.mapper.ReportMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 举报接口（需登录）
 */
@Tag(name = "举报", description = "商品举报接口")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportMapper reportMapper;

    @Operation(summary = "举报商品")
    @PostMapping
    public R<Void> report(@RequestBody @Valid ReportRequest req) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 检查是否已举报
        long count = reportMapper.selectCount(new LambdaQueryWrapper<Report>()
                .eq(Report::getReporterId, userId)
                .eq(Report::getProductId, req.getProductId()));
        if (count > 0) {
            throw new BusinessException(ResultCode.REPORT_EXIST);
        }

        Report report = new Report();
        report.setReporterId(userId);
        report.setProductId(req.getProductId());
        report.setReason(req.getReason());
        report.setDescription(req.getDescription());
        report.setStatus(0); // 待处理
        reportMapper.insert(report);

        return R.ok();
    }

    @Data
    public static class ReportRequest {
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        @NotNull(message = "举报原因不能为空")
        @Min(1) @Max(5)
        private Integer reason;

        @Size(max = 200, message = "举报说明最多200字")
        private String description;
    }
}

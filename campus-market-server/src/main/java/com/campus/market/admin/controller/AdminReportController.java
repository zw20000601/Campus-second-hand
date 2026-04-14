package com.campus.market.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.market.common.satoken.StpAdminUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.common.page.PageVO;
import com.campus.market.common.result.R;
import com.campus.market.module.report.entity.Report;
import com.campus.market.module.report.mapper.ReportMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 后台举报管理接口
 */
@Tag(name = "后台-举报管理", description = "举报处理")
@RestController
@RequestMapping("/admin/api/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportMapper reportMapper;

    @Operation(summary = "举报列表")
    @GetMapping
    public R<PageVO<Report>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<Report>()
                .orderByDesc(Report::getCreatedAt);
        if (status != null) wrapper.eq(Report::getStatus, status);

        IPage<Report> page = reportMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return R.ok(PageVO.of(page));
    }

    @Operation(summary = "处理举报")
    @PutMapping("/{id}/handle")
    public R<Void> handle(@PathVariable Long id, @RequestBody @Valid HandleRequest req) {
        Long adminId = StpAdminUtil.getLoginIdAsLong();

        Report update = new Report();
        update.setId(id);
        update.setStatus(req.getStatus());
        update.setHandleRemark(req.getRemark());
        update.setHandledBy(adminId);
        update.setHandledAt(LocalDateTime.now());
        reportMapper.updateById(update);
        return R.ok();
    }

    @Data
    public static class HandleRequest {
        @NotNull(message = "处理状态不能为空")
        private Integer status; // 1=已处理 2=已忽略
        private String remark;
    }
}

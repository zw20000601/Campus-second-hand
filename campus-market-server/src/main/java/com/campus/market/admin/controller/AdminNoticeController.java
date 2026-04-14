package com.campus.market.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.common.page.PageVO;
import com.campus.market.common.result.R;
import com.campus.market.module.notice.entity.Notice;
import com.campus.market.module.notice.mapper.NoticeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 后台公告管理
 */
@Tag(name = "后台-公告管理", description = "公告 CRUD")
@RestController
@RequestMapping("/admin/api/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeMapper noticeMapper;

    @Operation(summary = "公告列表")
    @GetMapping
    public R<PageVO<Notice>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<Notice>()
                .orderByDesc(Notice::getCreatedAt);
        if (status != null) wrapper.eq(Notice::getStatus, status);

        IPage<Notice> page = noticeMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return R.ok(PageVO.of(page));
    }

    @Operation(summary = "新增公告")
    @PostMapping
    public R<Notice> add(@RequestBody @Valid NoticeRequest req) {
        String loginId = StpUtil.getLoginId().toString();
        Long adminId = Long.parseLong(loginId.replace("admin:", ""));

        Notice notice = new Notice();
        notice.setTitle(req.getTitle());
        notice.setContent(req.getContent());
        notice.setCover(req.getCover());
        notice.setType(req.getType() != null ? req.getType() : 1);
        notice.setIsTop(req.getIsTop() != null ? req.getIsTop() : 0);
        notice.setStatus(req.getStatus() != null ? req.getStatus() : 1);
        notice.setCreatedBy(adminId);
        notice.setViewCount(0);
        if (notice.getStatus() == 1) {
            notice.setPublishedAt(LocalDateTime.now());
        }
        noticeMapper.insert(notice);
        return R.ok(notice);
    }

    @Operation(summary = "更新公告")
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody NoticeRequest req) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) return R.fail("公告不存在");
        if (req.getTitle() != null) notice.setTitle(req.getTitle());
        if (req.getContent() != null) notice.setContent(req.getContent());
        if (req.getCover() != null) notice.setCover(req.getCover());
        if (req.getType() != null) notice.setType(req.getType());
        if (req.getIsTop() != null) notice.setIsTop(req.getIsTop());
        if (req.getStatus() != null) notice.setStatus(req.getStatus());
        noticeMapper.updateById(notice);
        return R.ok();
    }

    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        noticeMapper.deleteById(id);
        return R.ok();
    }

    @Data
    public static class NoticeRequest {
        @NotBlank private String title;
        @NotBlank private String content;
        private String cover;
        private Integer type;
        private Integer isTop;
        private Integer status;
    }
}

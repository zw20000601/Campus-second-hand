package com.campus.market.module.notice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.market.common.page.PageDTO;
import com.campus.market.common.page.PageVO;
import com.campus.market.common.result.R;
import com.campus.market.module.notice.entity.Notice;
import com.campus.market.module.notice.mapper.NoticeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 公告接口（公开）
 */
@Tag(name = "公告", description = "平台公告接口")
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeMapper noticeMapper;

    @Operation(summary = "公告列表（公开）")
    @GetMapping
    public R<PageVO<Notice>> list(PageDTO pageDTO) {
        IPage<Notice> page = noticeMapper.selectPage(
                new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()),
                new LambdaQueryWrapper<Notice>()
                        .eq(Notice::getStatus, 1)
                        .orderByDesc(Notice::getIsTop)
                        .orderByDesc(Notice::getCreatedAt));
        return R.ok(PageVO.of(page));
    }

    @Operation(summary = "公告详情（公开）")
    @GetMapping("/{id}")
    public R<Notice> detail(@PathVariable Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice != null) {
            // 增加阅读次数
            Notice update = new Notice();
            update.setId(id);
            update.setViewCount(notice.getViewCount() + 1);
            noticeMapper.updateById(update);
        }
        return R.ok(notice);
    }
}

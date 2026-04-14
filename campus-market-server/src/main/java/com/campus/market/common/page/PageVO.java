package com.campus.market.common.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页响应体
 */
@Data
@Schema(description = "分页结果")
public class PageVO<T> {

    @Schema(description = "总记录数")
    private long total;

    @Schema(description = "总页数")
    private long pages;

    @Schema(description = "当前页码")
    private long pageNum;

    @Schema(description = "每页条数")
    private long pageSize;

    @Schema(description = "数据列表")
    private List<T> list;

    public static <T> PageVO<T> of(IPage<T> page) {
        PageVO<T> vo = new PageVO<>();
        vo.total = page.getTotal();
        vo.pages = page.getPages();
        vo.pageNum = page.getCurrent();
        vo.pageSize = page.getSize();
        vo.list = page.getRecords();
        return vo;
    }

    public static <T> PageVO<T> of(IPage<?> page, List<T> list) {
        PageVO<T> vo = new PageVO<>();
        vo.total = page.getTotal();
        vo.pages = page.getPages();
        vo.pageNum = page.getCurrent();
        vo.pageSize = page.getSize();
        vo.list = list;
        return vo;
    }
}

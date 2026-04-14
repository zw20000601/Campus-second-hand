package com.campus.market.common.page;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页请求参数基类
 */
@Data
@Schema(description = "分页参数")
public class PageDTO {

    @Schema(description = "页码（从1开始）", defaultValue = "1")
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", defaultValue = "12")
    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 100, message = "每页条数最大为100")
    private Integer pageSize = 12;

    /**
     * 计算偏移量（MyBatis-Plus 分页插件使用）
     */
    public long getOffset() {
        return (long) (pageNum - 1) * pageSize;
    }
}

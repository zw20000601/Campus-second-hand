package com.campus.market.module.product.dto;

import com.campus.market.common.page.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品查询参数 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品查询参数")
public class ProductQueryDTO extends PageDTO {

    @Schema(description = "关键词（搜索标题）")
    private String keyword;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "学校ID")
    private Long schoolId;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "最低价格")
    private java.math.BigDecimal minPrice;

    @Schema(description = "最高价格")
    private java.math.BigDecimal maxPrice;

    @Schema(description = "成色")
    private Integer conditionLevel;

    @Schema(description = "交易方式：1=线下 2=快递 3=均可")
    private Integer tradeType;

    @Schema(description = "排序：newest=最新 price_asc=价格升序 price_desc=价格降序")
    private String sortBy = "newest";

    @Schema(description = "商品状态过滤（仅用于我的发布）：0=待审核 1=已上架 2=已下架 3=已售出 4=审核拒绝")
    private Integer status;
}

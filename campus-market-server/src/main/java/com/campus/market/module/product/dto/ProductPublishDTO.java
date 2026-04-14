package com.campus.market.module.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 发布商品 DTO
 */
@Data
@Schema(description = "发布商品请求")
public class ProductPublishDTO {

    @Schema(description = "商品标题", required = true)
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题最多100字")
    private String title;

    @Schema(description = "商品描述")
    @Size(max = 2000, message = "描述最多2000字")
    private String description;

    @Schema(description = "价格（元）", required = true)
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格最低0.01元")
    @DecimalMax(value = "99999.99", message = "价格最高99999.99元")
    private BigDecimal price;

    @Schema(description = "原价（元）")
    private BigDecimal originalPrice;

    @Schema(description = "分类ID", required = true)
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @Schema(description = "所在学校ID", required = true)
    @NotNull(message = "学校不能为空")
    private Long schoolId;

    @Schema(description = "所在校区ID")
    private Long campusId;

    @Schema(description = "封面图片URL", required = true)
    @NotBlank(message = "封面图片不能为空")
    private String coverImage;

    @Schema(description = "商品图片URL列表（最多9张）")
    @Size(max = 9, message = "最多上传9张图片")
    private List<String> images;

    @Schema(description = "成色：1=全新 2=几乎全新 3=轻微使用 4=明显使用 5=有瑕疵", required = true)
    @NotNull(message = "成色不能为空")
    @Min(value = 1) @Max(value = 5)
    private Integer conditionLevel;

    @Schema(description = "交易方式：1=线下 2=快递 3=均可", required = true)
    @NotNull(message = "交易方式不能为空")
    @Min(value = 1) @Max(value = 3)
    private Integer tradeType;

    @Schema(description = "交易地点")
    @Size(max = 100, message = "交易地点最多100字")
    private String tradeLocation;
}

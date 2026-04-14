package com.campus.market.module.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品列表项 VO（精简版，用于列表展示）
 */
@Data
@Schema(description = "商品列表项")
public class ProductListVO {

    private Long id;
    private String title;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String coverImage;
    private Integer conditionLevel;
    private String conditionDesc;
    private Integer tradeType;
    private String schoolName;
    private String campusName;
    private Integer viewCount;
    private Integer favoriteCount;
    private Integer status;
    private LocalDateTime createdAt;

    @Schema(description = "发布者昵称")
    private String userNickname;

    @Schema(description = "发布者头像")
    private String userAvatar;
}

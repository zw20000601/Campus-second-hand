package com.campus.market.module.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品详情 VO
 */
@Data
@Schema(description = "商品详情")
public class ProductVO {

    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "发布用户ID")
    private Long userId;

    @Schema(description = "发布用户昵称")
    private String userNickname;

    @Schema(description = "发布用户头像")
    private String userAvatar;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "学校ID")
    private Long schoolId;

    @Schema(description = "学校名称")
    private String schoolName;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "校区名称")
    private String campusName;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "封面图")
    private String coverImage;

    @Schema(description = "商品图片列表")
    private List<String> images;

    @Schema(description = "成色：1=全新 2=几乎全新 3=轻微使用 4=明显使用 5=有瑕疵")
    private Integer conditionLevel;

    @Schema(description = "成色描述")
    private String conditionDesc;

    @Schema(description = "交易方式：1=线下 2=快递 3=均可")
    private Integer tradeType;

    @Schema(description = "交易方式描述")
    private String tradeTypeDesc;

    @Schema(description = "交易地点")
    private String tradeLocation;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "收藏次数")
    private Integer favoriteCount;

    @Schema(description = "留言次数")
    private Integer messageCount;

    @Schema(description = "商品状态：0=待审核 1=已上架 2=已下架 3=已售出 4=拒绝")
    private Integer status;

    @Schema(description = "是否已收藏（需登录）")
    private Boolean favorited;

    @Schema(description = "发布时间")
    private LocalDateTime createdAt;
}

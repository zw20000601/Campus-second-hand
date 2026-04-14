package com.campus.market.module.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long categoryId;
    private Long schoolId;
    private Long campusId;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String coverImage;

    /** 成色：1=全新 2=几乎全新 3=轻微使用 4=明显使用 5=有瑕疵 */
    private Integer conditionLevel;

    /** 交易方式：1=线下交易 2=快递邮寄 3=均可 */
    private Integer tradeType;
    private String tradeLocation;
    private String tags;
    private Integer viewCount;
    private Integer favoriteCount;
    private Integer messageCount;

    /** 状态：0=待审核 1=已上架 2=已下架 3=已售出 4=审核拒绝 */
    private Integer status;

    /** 审核状态：0=待审核 1=通过 2=拒绝 */
    private Integer auditStatus;
    private String auditRemark;
    private Long auditedBy;
    private LocalDateTime auditedAt;
    private Integer isTop;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}

package com.campus.market.module.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("product_message")
public class ProductMessage {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Long userId;
    private Long parentId;
    private String content;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}

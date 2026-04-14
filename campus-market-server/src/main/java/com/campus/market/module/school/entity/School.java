package com.campus.market.module.school.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("school")
public class School {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String logo;
    private String city;
    private String province;
    private Integer sort;
    private Integer status;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}

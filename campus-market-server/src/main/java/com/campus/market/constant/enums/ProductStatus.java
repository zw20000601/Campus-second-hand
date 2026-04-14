package com.campus.market.constant.enums;

import lombok.Getter;

/**
 * 商品状态枚举
 */
@Getter
public enum ProductStatus {

    PENDING(0, "待审核"),
    ON_SHELF(1, "已上架"),
    OFF_SHELF(2, "已下架"),
    SOLD(3, "已售出"),
    REJECTED(4, "审核拒绝");

    private final int code;
    private final String desc;

    ProductStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ProductStatus of(int code) {
        for (ProductStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}

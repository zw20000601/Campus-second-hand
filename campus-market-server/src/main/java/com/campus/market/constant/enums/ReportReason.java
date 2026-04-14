package com.campus.market.constant.enums;

import lombok.Getter;

/**
 * 举报原因枚举
 */
@Getter
public enum ReportReason {

    FAKE_INFO(1, "虚假信息"),
    PRODUCT_VIOLATION(2, "商品违规"),
    PRICE_FRAUD(3, "价格欺诈"),
    DUPLICATE(4, "重复发布"),
    OTHER(5, "其他");

    private final int code;
    private final String desc;

    ReportReason(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

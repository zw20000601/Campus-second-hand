package com.campus.market.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {

    // ===== 通用 =====
    SUCCESS(200, "操作成功"),
    ERROR(500, "系统内部错误"),
    PARAM_ERROR(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),

    // ===== 用户相关 =====
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_DISABLED(1003, "账号已被封禁"),
    USER_EXIST(1004, "用户名已存在"),
    OLD_PASSWORD_ERROR(1005, "原密码错误"),
    USER_NOT_LOGIN(1006, "请先登录"),

    // ===== 商品相关 =====
    PRODUCT_NOT_FOUND(2001, "商品不存在"),
    PRODUCT_ALREADY_SOLD(2002, "商品已售出"),
    PRODUCT_OFF_SHELF(2003, "商品已下架"),
    PRODUCT_AUDIT_PENDING(2004, "商品正在审核中"),
    PRODUCT_NOT_OWNER(2005, "无权操作他人商品"),

    // ===== 文件上传 =====
    FILE_UPLOAD_ERROR(3001, "文件上传失败"),
    FILE_TYPE_ERROR(3002, "不支持的文件类型"),
    FILE_SIZE_ERROR(3003, "文件大小超出限制"),

    // ===== 收藏相关 =====
    FAVORITE_EXIST(4001, "已收藏该商品"),
    FAVORITE_NOT_FOUND(4002, "未收藏该商品"),

    // ===== 举报相关 =====
    REPORT_EXIST(5001, "您已举报过该商品"),

    // ===== 分类相关 =====
    CATEGORY_NOT_FOUND(6001, "分类不存在"),
    CATEGORY_HAS_CHILDREN(6002, "该分类下存在子分类，无法删除"),
    CATEGORY_HAS_PRODUCTS(6003, "该分类下存在商品，无法删除"),

    // ===== 学校/校区 =====
    SCHOOL_NOT_FOUND(7001, "学校不存在"),
    CAMPUS_NOT_FOUND(7002, "校区不存在");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

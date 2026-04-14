package com.campus.market.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应体
 * <pre>
 * {
 *   "code": 200,
 *   "message": "操作成功",
 *   "data": {}
 * }
 * </pre>
 */
@Data
public class R<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    private R() {}

    // ===== 成功响应 =====

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.message = ResultCode.SUCCESS.getMessage();
        r.data = data;
        return r;
    }

    public static <T> R<T> ok(String message, T data) {
        R<T> r = new R<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.message = message;
        r.data = data;
        return r;
    }

    // ===== 失败响应 =====

    public static <T> R<T> fail(String message) {
        R<T> r = new R<>();
        r.code = ResultCode.ERROR.getCode();
        r.message = message;
        return r;
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        R<T> r = new R<>();
        r.code = resultCode.getCode();
        r.message = resultCode.getMessage();
        return r;
    }

    public static <T> R<T> fail(int code, String message) {
        R<T> r = new R<>();
        r.code = code;
        r.message = message;
        return r;
    }

    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }
}

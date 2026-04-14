package com.campus.market.module.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录成功响应 VO
 */
@Data
@Schema(description = "登录结果")
public class LoginVO {

    @Schema(description = "Token")
    private String token;

    @Schema(description = "用户信息")
    private UserVO user;
}

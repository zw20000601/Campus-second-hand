package com.campus.market.module.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息 VO（对外展示，不含敏感字段）
 */
@Data
@Schema(description = "用户信息")
public class UserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "性别：0=未知 1=男 2=女")
    private Integer gender;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "所在学校ID")
    private Long schoolId;

    @Schema(description = "所在学校名称")
    private String schoolName;

    @Schema(description = "所在校区ID")
    private Long campusId;

    @Schema(description = "所在校区名称")
    private String campusName;

    @Schema(description = "是否校园认证")
    private Integer verified;

    @Schema(description = "注册时间")
    private LocalDateTime createdAt;
}

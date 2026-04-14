package com.campus.market.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册 DTO
 */
@Data
@Schema(description = "用户注册请求")
public class RegisterDTO {

    @Schema(description = "用户名（4-20位字母数字下划线）", required = true)
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名为4-20位字母、数字或下划线")
    private String username;

    @Schema(description = "密码（6-20位）", required = true)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20位")
    private String password;

    @Schema(description = "昵称")
    @Size(max = 20, message = "昵称最长20位")
    private String nickname;

    @Schema(description = "所在学校ID")
    private Long schoolId;

    @Schema(description = "所在校区ID")
    private Long campusId;
}

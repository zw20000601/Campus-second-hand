package com.campus.market.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户资料 DTO
 */
@Data
@Schema(description = "更新用户资料")
public class UpdateProfileDTO {

    @Schema(description = "昵称")
    @Size(max = 20, message = "昵称最长20位")
    private String nickname;

    @Schema(description = "性别：0=未知 1=男 2=女")
    private Integer gender;

    @Schema(description = "个人简介")
    @Size(max = 100, message = "简介最长100字")
    private String bio;

    @Schema(description = "所在学校ID")
    private Long schoolId;

    @Schema(description = "所在校区ID")
    private Long campusId;
}

package com.campus.market.module.user.controller;

import com.campus.market.common.result.R;
import com.campus.market.module.user.dto.UpdateProfileDTO;
import com.campus.market.module.user.service.UserService;
import com.campus.market.module.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息接口
 */
@Tag(name = "用户信息", description = "用户资料相关接口（需登录）")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public R<UserVO> me() {
        return R.ok(userService.getCurrentUser());
    }

    @Operation(summary = "更新用户资料")
    @PutMapping("/profile")
    public R<UserVO> updateProfile(@RequestBody @Valid UpdateProfileDTO dto) {
        return R.ok(userService.updateProfile(dto));
    }

    @Operation(summary = "更新头像")
    @PutMapping("/avatar")
    public R<UserVO> updateAvatar(@RequestParam String avatarUrl) {
        return R.ok(userService.updateAvatar(avatarUrl));
    }
}

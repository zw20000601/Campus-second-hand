package com.campus.market.admin.controller;

import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.market.admin.entity.AdminUser;
import com.campus.market.admin.mapper.AdminUserMapper;
import com.campus.market.common.exception.BusinessException;
import com.campus.market.common.result.R;
import com.campus.market.common.result.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台管理员认证接口
 */
@Tag(name = "后台认证", description = "管理员登录/退出")
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminUserMapper adminUserMapper;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody @Valid AdminLoginRequest req,
                                         HttpServletRequest request) {
        // 查找管理员
        AdminUser admin = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, req.getUsername()));
        if (admin == null) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 验证密码
        if (!BCrypt.checkpw(req.getPassword(), admin.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        if (admin.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 更新登录信息
        AdminUser update = new AdminUser();
        update.setId(admin.getId());
        update.setLastLoginAt(LocalDateTime.now());
        update.setLastLoginIp(getClientIp(request));
        adminUserMapper.updateById(update);

        // 使用独立的 admin 登录 token（前缀区分用户和管理员）
        // 此处复用 StpUtil，使用 extra 区分角色
        StpUtil.login("admin:" + admin.getId(),
                SaLoginConfig.setExtra("role", admin.getRole())
                             .setExtra("type", "admin"));

        Map<String, Object> result = new HashMap<>();
        result.put("token", StpUtil.getTokenValue());
        result.put("adminId", admin.getId());
        result.put("username", admin.getUsername());
        result.put("nickname", admin.getNickname());
        result.put("role", admin.getRole());
        return R.ok(result);
    }

    @Operation(summary = "管理员退出")
    @PostMapping("/logout")
    public R<Void> logout() {
        StpUtil.logout();
        return R.ok();
    }

    @Operation(summary = "获取当前管理员信息")
    @GetMapping("/me")
    public R<AdminUser> me() {
        String loginId = StpUtil.getLoginId().toString();
        Long adminId = Long.parseLong(loginId.replace("admin:", ""));
        AdminUser admin = adminUserMapper.selectById(adminId);
        return R.ok(admin);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Data
    public static class AdminLoginRequest {
        @NotBlank(message = "账号不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
    }
}

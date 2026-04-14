package com.campus.market.admin.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.market.admin.entity.AdminUser;
import com.campus.market.admin.mapper.AdminUserMapper;
import com.campus.market.common.exception.BusinessException;
import com.campus.market.common.result.R;
import com.campus.market.common.result.ResultCode;
import com.campus.market.common.satoken.StpAdminUtil;
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
 *
 * <p>使用 {@link StpAdminUtil}（type="admin"）进行登录，
 * 与用户端 {@code StpUtil}（type="login"）完全隔离。
 */
@Tag(name = "后台认证", description = "管理员登录/退出/获取当前信息")
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
        AdminUser admin = adminUserMapper.selectOne(
                new LambdaQueryWrapper<AdminUser>()
                        .eq(AdminUser::getUsername, req.getUsername()));
        if (admin == null) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 验证密码
        if (!BCrypt.checkpw(req.getPassword(), admin.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 检查账号状态
        if (admin.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 更新最后登录信息
        AdminUser update = new AdminUser();
        update.setId(admin.getId());
        update.setLastLoginAt(LocalDateTime.now());
        update.setLastLoginIp(getClientIp(request));
        adminUserMapper.updateById(update);

        // 使用 StpAdminUtil（type="admin"）登录，与用户端 token 空间完全隔离
        StpAdminUtil.login(admin.getId());

        // 将角色写入 Session，用于后续权限判断
        StpAdminUtil.getSession().set("role", admin.getRole());
        StpAdminUtil.getSession().set("adminId", admin.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", StpAdminUtil.getTokenValue());
        result.put("adminId", admin.getId());
        result.put("username", admin.getUsername());
        result.put("nickname", admin.getNickname());
        result.put("role", admin.getRole());
        result.put("isSuperAdmin", "super_admin".equals(admin.getRole()));
        return R.ok(result);
    }

    @Operation(summary = "管理员退出登录")
    @PostMapping("/logout")
    public R<Void> logout() {
        if (StpAdminUtil.isLogin()) {
            StpAdminUtil.logout();
        }
        return R.ok();
    }

    @Operation(summary = "获取当前管理员信息（需登录）")
    @GetMapping("/me")
    public R<Map<String, Object>> me() {
        Long adminId = StpAdminUtil.getLoginIdAsLong();
        AdminUser admin = adminUserMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 不返回密码字段（password 字段已设 select = false）
        Map<String, Object> info = new HashMap<>();
        info.put("adminId", admin.getId());
        info.put("username", admin.getUsername());
        info.put("nickname", admin.getNickname());
        info.put("role", admin.getRole());
        info.put("isSuperAdmin", "super_admin".equals(admin.getRole()));
        return R.ok(info);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
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

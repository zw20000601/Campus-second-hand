package com.campus.market.common.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.campus.market.common.satoken.StpAdminUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 路由鉴权拦截器
 *
 * <p>认证边界：
 * <ul>
 *   <li>用户端（/api/**）：使用默认 StpUtil（type="login"）</li>
 *   <li>管理后台（/admin/api/**）：使用 StpAdminUtil（type="admin"），与用户端完全隔离</li>
 * </ul>
 * 用户登录后持有的 token 无法通过管理后台的 checkLogin()，反之亦然。
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /** 公开的用户端接口（无需登录） */
    private static final String[] USER_PUBLIC_PATHS = {
            "/api/auth/**",       // 注册、登录
            "/api/schools",       // 学校列表
            "/api/campuses",      // 校区列表
            "/api/categories",    // 分类列表
            "/api/notices",       // 公告列表
            "/api/notices/*",     // 公告详情
    };

    /** 公开的商品接口（GET 不需要登录，PUT/POST/DELETE 需要在 Service 层自行 checkLogin） */
    private static final String[] PRODUCT_PUBLIC_PATHS = {
            "/api/products",      // 列表（GET）
            "/api/products/*",    // 详情（GET）
            "/api/products/*/messages", // 留言列表（GET）
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {

            // ——————————————————————————————————————————
            // 管理后台：所有 /admin/api/** 和 /admin/auth/me
            // 使用 StpAdminUtil（type="admin"）独立校验，用户 token 无效
            // ——————————————————————————————————————————
            SaRouter.match("/admin/api/**", "/admin/auth/me")
                    .check(r -> StpAdminUtil.checkLogin());

            // ——————————————————————————————————————————
            // 用户端：需要登录的写操作 / 个人数据接口
            // ——————————————————————————————————————————
            SaRouter.match("/api/user/**")
                    .check(r -> StpUtil.checkLogin());

            SaRouter.match("/api/my/**")
                    .check(r -> StpUtil.checkLogin());

            SaRouter.match("/api/favorites/**")
                    .check(r -> StpUtil.checkLogin());

            SaRouter.match("/api/upload/**")
                    .check(r -> StpUtil.checkLogin());

            // 举报（POST /api/reports）
            SaRouter.match("/api/reports")
                    .notMatch(SaHttpMethod.GET)
                    .check(r -> StpUtil.checkLogin());

            // 商品写操作（POST/PUT/DELETE /api/products/**）
            SaRouter.match("/api/products", "/api/products/**")
                    .notMatch(SaHttpMethod.GET)
                    .check(r -> StpUtil.checkLogin());

            // 留言发布（POST /api/products/*/messages）
            SaRouter.match("/api/products/*/messages")
                    .notMatch(SaHttpMethod.GET)
                    .check(r -> StpUtil.checkLogin());

        })).addPathPatterns("/**")
                .excludePathPatterns(
                        "/admin/auth/login",  // 管理员登录（公开）
                        "/uploads/**",        // 上传文件静态资源
                        "/doc.html",          // Knife4j 文档
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/favicon.ico"
                );
    }
}

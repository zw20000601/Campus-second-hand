package com.campus.market.common.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 拦截器配置
 * 路由鉴权在 Controller 层使用注解 @SaCheckLogin 或手动调用
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 用户端需要登录的接口
            SaInterceptor.matchCheck("/api/user/me", () -> StpUtil.checkLogin());
            SaInterceptor.matchCheck("/api/user/profile", () -> StpUtil.checkLogin());
            SaInterceptor.matchCheck("/api/user/avatar", () -> StpUtil.checkLogin());
            SaInterceptor.matchCheck("/api/products", "POST", () -> StpUtil.checkLogin());
            SaInterceptor.matchCheck("/api/products/**", "PUT", () -> StpUtil.checkLogin());
            SaInterceptor.matchCheck("/api/products/**", "DELETE", () -> StpUtil.checkLogin());
            SaInterceptor.matchCheck("/api/my/**", () -> StpUtil.checkLogin());
            SaInterceptor.matchCheck("/api/favorites/**", () -> StpUtil.checkLogin());
            SaInterceptor.matchCheck("/api/reports", () -> StpUtil.checkLogin());
            SaInterceptor.matchCheck("/api/products/*/messages", "POST", () -> StpUtil.checkLogin());
            SaInterceptor.matchCheck("/api/upload/**", () -> StpUtil.checkLogin());

            // 后台接口需要管理员登录
            SaInterceptor.matchCheck("/admin/api/**", () ->
                    cn.dev33.satoken.stp.StpKit.DEFAULT.checkLogin()
            );
        })).addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/auth/**",
                        "/admin/auth/login",
                        "/api/schools",
                        "/api/campuses",
                        "/api/categories",
                        "/api/products",
                        "/api/products/*",
                        "/api/products/*/messages",
                        "/api/notices",
                        "/uploads/**",
                        "/doc.html",
                        "/webjars/**",
                        "/v3/api-docs/**"
                );
    }
}

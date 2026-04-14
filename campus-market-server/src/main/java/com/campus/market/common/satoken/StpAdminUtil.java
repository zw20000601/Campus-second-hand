package com.campus.market.common.satoken;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpLogic;

/**
 * 管理员专属 StpLogic（与用户端 StpUtil 隔离的独立 token 空间）
 *
 * <p>Sa-Token 多账号体系：
 * <ul>
 *   <li>用户端使用默认 {@code StpUtil}（type="login"）</li>
 *   <li>管理员使用本类（type="admin"），token 存储 key 前缀不同，完全隔离</li>
 * </ul>
 *
 * <p>安全边界：即使用户端 token 值相同，
 * {@code StpAdminUtil.checkLogin()} 也会拒绝，因为在 "admin" 命名空间下查不到该 token。
 */
public class StpAdminUtil {

    /** 管理员专属 StpLogic，type="admin" 注册到 SaManager */
    public static final StpLogic stpLogic = new StpLogic("admin");

    // ===== 登录 / 退出 =====

    public static void login(Object id) {
        stpLogic.login(id);
    }

    public static void login(Object id, SaLoginModel loginModel) {
        stpLogic.login(id, loginModel);
    }

    public static void logout() {
        stpLogic.logout();
    }

    public static boolean isLogin() {
        return stpLogic.isLogin();
    }

    public static void checkLogin() {
        stpLogic.checkLogin();
    }

    // ===== Token =====

    public static String getTokenValue() {
        return stpLogic.getTokenValue();
    }

    // ===== 登录 ID =====

    public static Object getLoginId() {
        return stpLogic.getLoginId();
    }

    public static long getLoginIdAsLong() {
        return stpLogic.getLoginIdAsLong();
    }

    // ===== Session（用于存储角色等额外信息）=====

    public static SaSession getSession() {
        return stpLogic.getSession();
    }

    /**
     * 获取当前管理员角色（从 session 中读取，登录时写入）
     *
     * @return "super_admin" 或 "admin"
     */
    public static String getCurrentRole() {
        SaSession session = stpLogic.getSession();
        return session != null ? (String) session.get("role") : null;
    }

    /**
     * 校验当前管理员是否为超级管理员，否则抛 403
     */
    public static void checkSuperAdmin() {
        String role = getCurrentRole();
        if (!"super_admin".equals(role)) {
            throw new cn.dev33.satoken.exception.NotPermissionException("super_admin");
        }
    }

    /**
     * 是否为超级管理员
     */
    public static boolean isSuperAdmin() {
        return "super_admin".equals(getCurrentRole());
    }
}

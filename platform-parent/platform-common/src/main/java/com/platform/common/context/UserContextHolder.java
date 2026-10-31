package com.platform.common.context;

public class UserContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    public static void setContext(UserContext context) {
        CONTEXT.set(context);
    }

    public static UserContext getContext() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    public static Long getUserId() {
        UserContext context = getContext();
        return context != null ? context.getUserId() : null;
    }

    public static String getUserCode() {
        UserContext context = getContext();
        return context != null ? context.getUserCode() : null;
    }

    public static Long getTenantId() {
        UserContext context = getContext();
        return context != null ? context.getTenantId() : null;
    }

    public static String getTenantCode() {
        UserContext context = getContext();
        return context != null ? context.getTenantCode() : null;
    }

    public static Long getDeptId() {
        UserContext context = getContext();
        return context != null ? context.getDeptId() : null;
    }

    public static Integer getDataScope() {
        UserContext context = getContext();
        return context != null ? context.getDataScope() : null;
    }

    public static String getDeptIds() {
        UserContext context = getContext();
        return context != null ? context.getDeptIds() : null;
    }

    public static Boolean isPlatformRole() {
        UserContext context = getContext();
        return context != null && context.isPlatformRole();
    }

    public static Long getRoleId() {
        UserContext context = getContext();
        return context != null ? context.getRoleId() : null;
    }

    /**
     * 获取当前用户角色编码
     *
     * @return
     */
    public static String getRoleCode() {
        UserContext context = getContext();
        return context != null ? context.getRoleCode() : null;
    }
}
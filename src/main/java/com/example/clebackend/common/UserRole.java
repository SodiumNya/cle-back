package com.example.clebackend.common;

public enum  UserRole {
    ADMIN("管理员"),
    SHOP_OWNER("商家"),
    CUSTOMER("用户");

    private final String roleName;
    UserRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    // 根据角色名称获取对应的枚举值
    public static UserRole fromRoleName(String roleName) {
        for (UserRole role : UserRole.values()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("无效的角色名称: " + roleName);
    }
}

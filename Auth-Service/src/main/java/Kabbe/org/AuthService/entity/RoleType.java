package Kabbe.org.AuthService.entity;

public enum RoleType {
    ADMIN,
    CLIENT,
    USER;

    public static RoleType fromString(String role) {
        return RoleType.valueOf(role.toUpperCase());
    }
}

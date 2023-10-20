package cc.bearvalley.badminton.util;

import java.util.Arrays;

/**
 * 权限的枚举值
 */
public enum PrivilegeEnum {
    VIEW_EVENT("查看活动", "view_event"),
    ADD_EVENT("添加活动", "add_event"),
    EDIT_EVENT("修改活动", "edit_event"),
    EDIT_EVENT_STATUS("修改活动状态", "edit_event_status"),
    VIEW_ENROLL("查看报名", "view_enroll"),
    CANCEL_ENROLL("取消报名", "cancel_enroll"),
    CHECK_ENROLL("确认报名", "check_enroll"),
    VIEW_CHALLENGE("查看挑战", "view_challenge"),
    VIEW_USER("查看用户列表", "view_user"),
    VIEW_ITEM("查看积分兑换商品", "view_item"),
    ADD_ITEM("添加积分兑换商品", "add_item"),
    EDIT_ITEM("修改积分兑换商品", "edit_item"),
    EDIT_ITEM_STATUS("修改积分兑换商品状态", "edit_item_status"),
    VIEW_ITEM_PICTURE("查看积分兑换商品图片", "view_item_picture"),
    ADD_ITEM_PICTURE("添加积分兑换商品图片", "add_item_picture"),
    DELETE_ITEM_PICTURE("删除积分兑换商品图片", "delete_item_picture"),
    SET_ITEM_COVER("配置积分兑换商品的封面", "set_item_cover"),
    VIEW_ORDER("查看积分兑换记录", "view_order"),
    VIEW_ADMIN("查看账号", "view_admin"),
    ADD_ADMIN("添加账号", "add_admin"),
    EDIT_ADMIN("修改账号", "edit_admin"),
    EDIT_ADMIN_STATUS("修改账号状态", "edit_admin_status"),
    VIEW_ROLE("查看角色", "view_role"),
    EDIT_ROLE("修改角色", "edit_role"),
    DELETE_ROLE("删除角色", "delete_role"),
    RESET_PASSWORD("重置密码", "reset_password"),
    ADMIN_BIND_ROLE("给账号绑定校色", "admin_bind_role"),
    ROLE_BIND_PRIVILEGE("给校色绑定权限", "role_bind_privilege");

    private final String name;
    private final String value;

    PrivilegeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "PrivilegeEnum{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public static PrivilegeEnum findPrivilegeByValue(String value) {
        return Arrays.stream(PrivilegeEnum.values()).filter(privilegeEnum -> privilegeEnum.getValue().equals(value)).findFirst().orElse(null);
    }
}

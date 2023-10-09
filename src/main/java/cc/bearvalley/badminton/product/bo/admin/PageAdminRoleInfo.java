package cc.bearvalley.badminton.product.bo.admin;

import cc.bearvalley.badminton.entity.admin.Admin;
import cc.bearvalley.badminton.entity.admin.Role;

/**
 * 用于页面上显示角色和权限关系的信息对象
 */
public class PageAdminRoleInfo {
    private int admin_id;      // 管理账号id
    private int role_id;       // 管理角色id
    private String admin_name; // 账号名称
    private String role_name;  // 角色名称
    private boolean contained; // 是否包含

    /**
     * 生成一个角色和权限关系的信息对象
     *
     * @param admin 账号对象
     * @param role  角色对象
     * @return 新对象
     */
    public static PageAdminRoleInfo of(Admin admin, Role role) {
        PageAdminRoleInfo pageAdminRoleInfo = new PageAdminRoleInfo();
        pageAdminRoleInfo.setAdmin_id(admin.getId());
        pageAdminRoleInfo.setRole_id(role.getId());
        pageAdminRoleInfo.setAdmin_name(admin.getUsername());
        pageAdminRoleInfo.setRole_name(role.getName());
        pageAdminRoleInfo.setContained(admin.getRoles().stream().anyMatch(r -> r.getId() == role.getId()));
        return pageAdminRoleInfo;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getAdmin_name() {
        return admin_name;
    }

    public void setAdmin_name(String admin_name) {
        this.admin_name = admin_name;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public boolean isContained() {
        return contained;
    }

    public void setContained(boolean contained) {
        this.contained = contained;
    }

    @Override
    public String toString() {
        return "PageAdminRole{" +
                "admin_id=" + admin_id +
                ", role_id=" + role_id +
                ", admin_name='" + admin_name + '\'' +
                ", role_name='" + role_name + '\'' +
                ", contained=" + contained +
                '}';
    }
}

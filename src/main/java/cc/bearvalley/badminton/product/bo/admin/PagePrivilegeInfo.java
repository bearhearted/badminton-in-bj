package cc.bearvalley.badminton.product.bo.admin;

import cc.bearvalley.badminton.util.PrivilegeEnum;

import java.util.Collection;

/**
 * 用于页面上显示角色和权限关系的信息对象
 */
public class PagePrivilegeInfo {
    private String name;        // 权限名称
    private String value;       // 权限值
    private boolean contained;  // 该权限是否被角色拥有

    /**
     * 生成一个角色权限对应管理的对象
     *
     * @param privilegeEnum    权限
     * @param rolePrivilegeSet 该角色包含的权限集合
     * @return 一个包含角色权限对应管理的对象
     */
    public static PagePrivilegeInfo of(PrivilegeEnum privilegeEnum, Collection<String> rolePrivilegeSet) {
        PagePrivilegeInfo pagePrivilegeInfo = new PagePrivilegeInfo();
        pagePrivilegeInfo.setName(privilegeEnum.getName());
        pagePrivilegeInfo.setValue(privilegeEnum.getValue());
        pagePrivilegeInfo.setContained(rolePrivilegeSet.stream().anyMatch(p -> privilegeEnum.getValue().equals(p)));
        return pagePrivilegeInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isContained() {
        return contained;
    }

    public void setContained(boolean contained) {
        this.contained = contained;
    }

    @Override
    public String toString() {
        return "PagePrivilege{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", contained=" + contained +
                '}';
    }
}

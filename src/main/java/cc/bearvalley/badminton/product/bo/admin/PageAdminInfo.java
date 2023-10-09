package cc.bearvalley.badminton.product.bo.admin;

import cc.bearvalley.badminton.entity.admin.Role;

/**
 * 页面上账号的信息对象
 */
public class PageAdminInfo {
    private int    id;        // 主键
    private String username;  // 登录名
    private String realname;  // 真实姓名
    private String mobile;    // 手机号
    private int    status;    // 状态值
    private String statusStr; // 状态字符
    private String color;     // 页面显示颜色

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "PageAdminInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", realname='" + realname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", status=" + status +
                ", statusStr='" + statusStr + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}

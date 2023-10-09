package cc.bearvalley.badminton.product.bo.admin;

/**
 * 后台显示参与的信息对象
 */
public class PageEnrollInfo {
    private int id;            // 参与的主键
    private String username;   // 参与的用户名
    private String avatar;     // 参与的用户头像url
    private String enrollTime; // 参加时间
    private String point;      // 可获取的积分

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEnrollTime() {
        return enrollTime;
    }

    public void setEnrollTime(String enrollTime) {
        this.enrollTime = enrollTime;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "PageEnrollInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", enrollTime='" + enrollTime + '\'' +
                ", point='" + point + '\'' +
                '}';
    }
}

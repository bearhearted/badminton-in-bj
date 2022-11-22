package cc.bearvalley.badminton.product.bo;

/**
 * 羽毛球活动的报名点business object
 */
public class BadmintonEnroll {
    private String username; // 报名的用户名
    private String avatar;   // 报名的用户头像url
    private boolean delete;  // 是否可以删除该条数据

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

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @Override
    public String toString() {
        return "BadmintonEnroll{" +
                "username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", delete=" + delete +
                '}';
    }
}

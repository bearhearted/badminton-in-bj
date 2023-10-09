package cc.bearvalley.badminton.product.bo.admin;

/**
 * 用于页面上显示用户关联的信息对象
 */
public class PageRelationInfo {
    private int id;                  // 用户id
    private String username;         // 用户昵称
    private String avatar;           // 用户头像url
    private int relatedUserid;       // 关联的用户id
    private String relatedUsername;  // 关联的用户昵称
    private int attended;            // 是否参加过活动

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

    public int getRelatedUserid() {
        return relatedUserid;
    }

    public void setRelatedUserid(int relatedUserid) {
        this.relatedUserid = relatedUserid;
    }

    public String getRelatedUsername() {
        return relatedUsername;
    }

    public void setRelatedUsername(String relatedUsername) {
        this.relatedUsername = relatedUsername;
    }

    public int getAttended() {
        return attended;
    }

    public void setAttended(int attended) {
        this.attended = attended;
    }

    @Override
    public String toString() {
        return "PageRelationInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", relatedUserid=" + relatedUserid +
                ", relatedUsername='" + relatedUsername + '\'' +
                ", attended=" + attended +
                '}';
    }
}

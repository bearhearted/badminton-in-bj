package cc.bearvalley.badminton.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 用户信息的实体数据类
 */
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    id;       // 用户的主键
    private String  nickname; // 用户的昵称
    private String  avatar;   // 用户的头像
    private String  openid;   // 关联的openid
    private Integer status;   // 用户的状态

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", openid='" + openid + '\'' +
                ", status=" + status +
                '}';
    }

    /**
     * 描述活动状态的枚举类
     */
    public enum STATUS {
        COMPLETED(1, "设置了昵称和图标"),
        INITIAL(0, "初始化");

        private final int value;
        private final String desc;

        STATUS(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public int getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }
    }
}

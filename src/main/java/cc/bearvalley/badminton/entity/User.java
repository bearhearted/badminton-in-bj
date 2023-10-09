package cc.bearvalley.badminton.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户信息的实体数据类
 */
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;           // 用户的主键
    private String nickname;  // 用户的昵称
    private String avatar;    // 用户的头像
    private String openid;    // 关联的openid
    private Integer status;   // 用户的状态
    private int win;          // 胜利场次
    private int lose;         // 失败场次
    private BigDecimal ratio; // 胜率
    private int point;        // 积分
    private int attended;     // 是否参加过活动

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @CreationTimestamp
    private Date createTime;  // 创建时间

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updateTime;  // 更新时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getAttended() {
        return attended;
    }

    public void setAttended(int attended) {
        this.attended = attended;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", openid='" + openid + '\'' +
                ", status=" + status +
                ", win=" + win +
                ", lose=" + lose +
                ", ratio=" + ratio +
                ", point=" + point +
                ", attended=" + attended +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    /**
     * 描述用户状态的枚举类
     */
    public enum StatusEnum {
        COMPLETED(1, "资料已完整"),
        INCOMPLETE(0, "资料未完整");
        private final int value;
        private final String desc;

        StatusEnum(int value, String desc) {
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

    /**
     * 描述用户是否参加过活动状态的枚举类
     */
    public enum AttendEnum {
        ON(0, "参加过活动"),
        OFF(1, "还未参加活动");
        private final int value;
        private final String desc;

        AttendEnum(int value, String desc) {
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

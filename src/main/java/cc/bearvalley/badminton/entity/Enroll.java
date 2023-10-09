package cc.bearvalley.badminton.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 羽毛球报名的实体类
 */
@Entity
public class Enroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;          // 自增主键id

    @ManyToOne
    @JoinColumn(name = "event_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Event event;     // 要报名的活动

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private User user;       // 要报名的用户

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @CreationTimestamp
    private Date enrollTime; // 报名时间

    private int attended;    // 实际是否参加此次报名

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @CreationTimestamp
    private Date createTime; // 创建时间

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updateTime; // 更新时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getEnrollTime() {
        return enrollTime;
    }

    public void setEnrollTime(Date enrollTime) {
        this.enrollTime = enrollTime;
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
        return "Enroll{" +
                "id=" + id +
                ", event=" + event +
                ", user=" + user +
                ", enrollTime=" + enrollTime +
                ", attended=" + attended +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    /**
     * 描述活动是否参加状态的枚举类
     */
    public enum AttendedEnum {
        YES(1, "已参加"),
        NO(0, "未参加");
        private final int value;
        private final String desc;

        AttendedEnum(int value, String desc) {
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

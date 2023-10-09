package cc.bearvalley.badminton.entity.point;

import cc.bearvalley.badminton.entity.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 积分变化记录的实体类
 */
@Entity
public class PointRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;          // 自增主键id

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private User user;       // 积分变化的用户

    private int point;       // 单次增加的积分

    private String scene;    // 增加积分的场景

    private int day;         // 积分增加的日期

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int expireDay) {
        this.day = expireDay;
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
        return "Point{" +
                "id=" + id +
                ", user=" + user +
                ", point=" + point +
                ", scene='" + scene + '\'' +
                ", day=" + day +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}

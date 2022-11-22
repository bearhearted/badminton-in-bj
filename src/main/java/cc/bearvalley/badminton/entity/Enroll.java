package cc.bearvalley.badminton.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 羽毛球报名的实体类
 */
@Entity
public class Enroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;         // 自增主键id

    @ManyToOne
    @JoinColumn(name = "event_id", foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))
    private Event event;     // 要报名的活动

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))
    private User user;       // 要报名的用户

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @CreationTimestamp
    private Date enrollTime; // 报名时间

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    @Override
    public String toString() {
        return "Enroll{" +
                "id=" + id +
                ", event=" + event +
                ", user=" + user +
                ", enrollTime=" + enrollTime +
                '}';
    }
}

package cc.bearvalley.badminton.entity.challenge;

import cc.bearvalley.badminton.entity.Event;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * 羽毛球约战的实体类
 */
@Entity
public class Battle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;          // 自增主键id

    @ManyToOne
    @JoinColumn(name = "event_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Event event;     // 要约战的活动

    private int type;        // 该约战的类型

    private int result;      // 该约战的结果

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
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
        return "Battle{" +
                "id=" + id +
                ", event=" + event +
                ", type=" + type +
                ", result=" + result +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Battle battle = (Battle) o;
        return id == battle.id && type == battle.type && result == battle.result && event.equals(battle.event) && createTime.equals(battle.createTime) && updateTime.equals(battle.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, event, type, result, createTime, updateTime);
    }

    /**
     * 描述挑战类型的枚举类
     */
    public enum TypeEnum {
        SINGLE(0, "单打"),
        DOUBLE(1, "双打");
        private final int value;
        private final String desc;

        TypeEnum(int value, String desc) {
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
     * 描述挑战结果的枚举类
     */
    public enum ResultEnum {
        UN_DONE(0, "未完赛"),
        LEFT_WON(1, "左队胜"),
        RIGHT_WON(2, "右队胜");
        private final int value;
        private final String desc;

        ResultEnum(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public int getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }

        public static ResultEnum findResultByValue(int result) {
            return Arrays.stream(ResultEnum.values()).filter(r -> r.getValue() == result).findFirst().orElse(null);
        }
    }
}

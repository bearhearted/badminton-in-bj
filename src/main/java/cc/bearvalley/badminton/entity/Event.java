package cc.bearvalley.badminton.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * 羽毛球活动的实体类
 */
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;         // 自增主键id
    private String stadium; // 球馆
    private String fields;  // 场地
    private int fee;        // 人均费用
    private int amount;     // 人数
    private Date startTime; // 开始时间
    private Date endTime;   // 结束时间
    private String creator; // 创建者
    private int status;     // 该活动的状态

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", stadium='" + stadium + '\'' +
                ", fields='" + fields + '\'' +
                ", fee=" + fee +
                ", amount=" + amount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", creator='" + creator + '\'' +
                ", status=" + status +
                '}';
    }

    /**
     * 描述活动状态的枚举类
     */
    public enum STATUS {
        ON(1, "显示"),
        OFF(0, "不显示");

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

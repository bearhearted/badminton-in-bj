package cc.bearvalley.badminton.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;

/**
 * 羽毛球活动的实体类
 */
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;          // 自增主键id
    private String stadium;  // 球馆
    private String fields;   // 场地
    private int fee;         // 人均费用
    private int amount;      // 人数
    private Date startTime;  // 开始时间
    private Date endTime;    // 结束时间
    private String creator;  // 创建者
    private int status;      // 该活动的状态
    private int checked;     // 该次活动是否确认过

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

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
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
                ", checked=" + checked +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    /**
     * 描述活动状态的枚举类
     */
    public enum StatusEnum {
        ON(1, "显示"),
        OFF(0, "不显示");
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

        public static StatusEnum findStatusByValue(int value) {
            return Arrays.stream(StatusEnum.values()).filter(statusEnum -> statusEnum.getValue() == value).findFirst().orElse(OFF);
        }
    }

    /**
     * 描述活动确认状态的枚举类
     */
    public enum CheckEnum {
        YES(1, "已确认"),
        NO(0, "未确认");
        private final int value;
        private final String desc;

        CheckEnum(int value, String desc) {
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

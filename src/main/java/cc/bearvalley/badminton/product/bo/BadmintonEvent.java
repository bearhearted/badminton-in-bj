package cc.bearvalley.badminton.product.bo;

/**
 * 羽毛球活动的信息对象
 */
public class BadmintonEvent {
    private int id;           // 活动的id，页面操作时需要
    private String day;       // 活动日期，格式为yyyyMMdd
    private String weekday;   // 活动在周几
    private String day2;      // 活动日期2，格式为yyyy-MM-dd
    private String time;      // 活动时间
    private long startTime;   // 开始时间的毫秒数
    private String stadium;   // 活动场馆
    private String fields;    // 活动场地
    private int fee;          // 人均费用
    private int totalNumber;  // 可以参加的人数
    private int enrollNumber; // 报名的人数
    private boolean enrolled; // 是否报名

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getDay2() {
        return day2;
    }

    public void setDay2(String day2) {
        this.day2 = day2;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
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

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public int getEnrollNumber() {
        return enrollNumber;
    }

    public void setEnrollNumber(int enrollNumber) {
        this.enrollNumber = enrollNumber;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    @Override
    public String toString() {
        return "BadmintonEvent{" +
                "id=" + id +
                ", day='" + day + '\'' +
                ", weekday='" + weekday + '\'' +
                ", day2='" + day2 + '\'' +
                ", time='" + time + '\'' +
                ", startTime=" + startTime +
                ", stadium='" + stadium + '\'' +
                ", fields='" + fields + '\'' +
                ", fee=" + fee +
                ", totalNumber=" + totalNumber +
                ", enrollNumber=" + enrollNumber +
                ", enrolled=" + enrolled +
                '}';
    }
}

package cc.bearvalley.badminton.product.bo.admin;

/**
 * 页面上活动的信息对象
 */
public class PageEventInfo {
    private String color;        // 页面要显示的文字颜色
    private int index;           // 开始序号
    private String time;         // 活动时间
    private String stadium;      // 活动场所
    private String fields;       // 活动场地
    private int fee;             // 活动费用
    private int amount;          // 活动人数
    private int enrollNum;       // 参加人数
    private int id;              // 活动id
    private String checkedStr;   // 是否参与的显示字符串
    private String statusStr;    // 状态的显示字符串
    private String statusToggle; // 状态的转换字符串
    private String toggleUrl;    // 转换状态的url

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public int getEnrollNum() {
        return enrollNum;
    }

    public void setEnrollNum(int enrollNum) {
        this.enrollNum = enrollNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCheckedStr() {
        return checkedStr;
    }

    public void setCheckedStr(String checkedStr) {
        this.checkedStr = checkedStr;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getStatusToggle() {
        return statusToggle;
    }

    public void setStatusToggle(String statusToggle) {
        this.statusToggle = statusToggle;
    }

    public String getToggleUrl() {
        return toggleUrl;
    }

    public void setToggleUrl(String toggleUrl) {
        this.toggleUrl = toggleUrl;
    }

    @Override
    public String toString() {
        return "PageEventInfo{" +
                "color='" + color + '\'' +
                ", index=" + index +
                ", time='" + time + '\'' +
                ", stadium='" + stadium + '\'' +
                ", fields='" + fields + '\'' +
                ", fee=" + fee +
                ", amount=" + amount +
                ", enrollNum=" + enrollNum +
                ", id=" + id +
                ", checkedStr='" + checkedStr + '\'' +
                ", statusStr='" + statusStr + '\'' +
                ", statusToggle='" + statusToggle + '\'' +
                ", toggleUrl='" + toggleUrl + '\'' +
                '}';
    }
}

package cc.bearvalley.badminton.product.bo.admin;

/**
 * 页面上商品的信息对象
 */
public class PageItemInfo {
    private String color;        // 页面要显示的文字颜色
    private int index;           // 开始序号
    private String name;         // 商品名称
    private String intro;        // 商品简介
    private int point;           // 兑换商品所需积分
    private int stock;           // 商品库存
    private int sold;            // 商品兑换出的数量
    private int amount;          // 目前商品的剩余数量
    private int id;              // 商品id
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return "PageItemInfo{" +
                "color='" + color + '\'' +
                ", index=" + index +
                ", name='" + name + '\'' +
                ", intro='" + intro + '\'' +
                ", point=" + point +
                ", stock=" + stock +
                ", sold=" + sold +
                ", amount=" + amount +
                ", id=" + id +
                ", statusStr='" + statusStr + '\'' +
                ", statusToggle='" + statusToggle + '\'' +
                ", toggleUrl='" + toggleUrl + '\'' +
                '}';
    }
}

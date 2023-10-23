package cc.bearvalley.badminton.product.bo;

import java.util.List;

/**
 * 积分商城商品的信息对象
 */
public class StoreItemEntity {
    private int id;              // 商品id
    private String name;         // 商品名称
    private int point;           // 兑换商品所需积分
    private int stock;           // 商品库存
    private int sold;            // 商品兑换
    private int left;            // 商品剩余
    private boolean afford;      // 能否购买
    private String introduction; // 商品描述
    private List<String> pics;   // 商品图片列表

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public boolean isAfford() {
        return afford;
    }

    public void setAfford(boolean afford) {
        this.afford = afford;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    @Override
    public String toString() {
        return "StoreItemEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", point=" + point +
                ", stock=" + stock +
                ", sold=" + sold +
                ", left=" + left +
                ", afford=" + afford +
                ", introduction='" + introduction + '\'' +
                ", pics=" + pics +
                '}';
    }
}

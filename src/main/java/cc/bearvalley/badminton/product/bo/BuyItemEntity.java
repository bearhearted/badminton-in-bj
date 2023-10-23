package cc.bearvalley.badminton.product.bo;

import java.util.List;

/**
 * 积分商城商品的信息对象
 */
public class BuyItemEntity {
    private int id;              // 商品id
    private String name;         // 商品名称
    private int point;           // 兑换商品所需积分
    private int left;            // 商品剩余

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

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    @Override
    public String toString() {
        return "StoreItemEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", point=" + point +
                ", left=" + left +
                '}';
    }
}

package cc.bearvalley.badminton.product.bo;

/**
 * 积分商城商品的兑换确认信息对象
 */
public class ItemConfirmInfoEntity {
    private String pic;          // 商品图片
    private String name;         // 商品名称
    private String introduction; // 商品介绍
    private int point;           // 兑换商品所需积分
    private int left;            // 用户剩余积分

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
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
        return "BuyItemEntity{" +
                "pic='" + pic + '\'' +
                ", name='" + name + '\'' +
                ", introduction='" + introduction + '\'' +
                ", point=" + point +
                ", left=" + left +
                '}';
    }
}

package cc.bearvalley.badminton.product.bo;

import java.util.List;

/**
 * 积分商城首页的信息对象
 */
public class StoreItemList {
    private boolean last;    // 是否为最后一页

    private List<Info> list; // 包含商品信息的列表

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public List<Info> getList() {
        return list;
    }

    public void setList(List<Info> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "StoreItemList{" +
                "last=" + last +
                ", list=" + list +
                '}';
    }

    /**
     * 积分商城首页的商品信息对象
     */
    public static class Info {
        private int id;              // 商品id
        private String name;         // 商品名称
        private int point;           // 兑换商品所需积分
        private int left;            // 商品剩余
        private String picture;      // 商品图片

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

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", point=" + point +
                    ", left=" + left +
                    ", picture='" + picture + '\'' +
                    '}';
        }
    }
}

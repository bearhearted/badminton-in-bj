package cc.bearvalley.badminton.product.bo.admin;

import java.util.List;

/**
 * 我的订单页面的信息对象
 */
public class MyOrder {
    private boolean last;    // 是否是最后一页
    private List<Info> list; // 包含订单信息的列表

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
        return "MyBattle{" +
                "last=" + last +
                ", list=" + list +
                '}';
    }

    /**
     * 我的订单页面的订单信息
     */
    public static class Info {
        private String date;     // 下单日期
        private String name;     // 商品名称
        private int point;       // 商品积分
        private String result;   // 订单结果

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
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

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "date='" + date + '\'' +
                    ", name='" + name + '\'' +
                    ", point=" + point +
                    ", result='" + result + '\'' +
                    '}';
        }
    }
}


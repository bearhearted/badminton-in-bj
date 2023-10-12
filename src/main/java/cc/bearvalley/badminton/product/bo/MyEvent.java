package cc.bearvalley.badminton.product.bo;

import java.util.List;

/**
 * 我的报名页面的信息对象
 */
public class MyEvent {
    private boolean last = true;       // 是否为最后一页
    private List<BadmintonEvent> list; // 包含报名信息的列表

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public List<BadmintonEvent> getList() {
        return list;
    }

    public void setList(List<BadmintonEvent> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "MyEvent{" +
                "last=" + last +
                ", list=" + list +
                '}';
    }
}

package cc.bearvalley.badminton.product.bo;

import java.util.List;

/**
 * 用于显示挑战列表的信息对象
 */
public class BattleEvent {
    private int id;                // 对战所属活动的唯一标示id
    private String date;           // 活动的日期
    private boolean enrolled;      // 是否已经报名该活动
    private boolean added;         // 是否已经在某个对战中报过名
    private List<BattleInfo> list; // 该活动下的所有对战列表

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public List<BattleInfo> getList() {
        return list;
    }

    public void setList(List<BattleInfo> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "BattleEvent{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", enrolled=" + enrolled +
                ", added=" + added +
                ", list=" + list +
                '}';
    }
}

package cc.bearvalley.badminton.product.bo;

import java.util.List;

/**
 * 我的对战页面的信息对象
 */
public class MyBattle {
    private boolean last;    // 是否是最后一页
    private List<Info> list; // 包含对战信息的列表

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
     * 我的对战页面的对战信息
     */
    public static class Info {
        private String date;     // 对战日期
        private String teammate; // 对战队友
        private String opponent; // 对战对手
        private String result;   // 对战结果

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTeammate() {
            return teammate;
        }

        public void setTeammate(String teammate) {
            this.teammate = teammate;
        }

        public String getOpponent() {
            return opponent;
        }

        public void setOpponent(String opponent) {
            this.opponent = opponent;
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
                    ", teammate='" + teammate + '\'' +
                    ", opponent='" + opponent + '\'' +
                    ", result='" + result + '\'' +
                    '}';
        }
    }
}


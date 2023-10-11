package cc.bearvalley.badminton.product.vo;

public class MyPointListVo {
    private String sid;
    private int page;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        if (page < 0) {
            page = 0;
        }
        this.page = page;
    }

    @Override
    public String toString() {
        return "MyPointListVo{" +
                "sid='" + sid + '\'' +
                ", page=" + page +
                '}';
    }
}

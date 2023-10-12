package cc.bearvalley.badminton.product.vo;

/**
 * 接受用户sid和页码page参数的vo
 */
public class UserSidAndPageVo {
    private String sid; // 用户的sid
    private int page;   // 页码

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

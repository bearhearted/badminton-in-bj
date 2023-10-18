package cc.bearvalley.badminton.product.vo;

/**
 * 接受页码page参数的vo
 */
public class PageVo {
    private int page;   // 页码

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
        return "PageVo{" +
                "page=" + page +
                '}';
    }
}

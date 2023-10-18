package cc.bearvalley.badminton.product.vo;

/**
 * 接受用户sid参数的vo
 */
public class UserSidVo {
    private String sid; // 用户的sid

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @Override
    public String toString() {
        return "UserSidVo{" +
                "sid='" + sid + '\'' +
                '}';
    }
}

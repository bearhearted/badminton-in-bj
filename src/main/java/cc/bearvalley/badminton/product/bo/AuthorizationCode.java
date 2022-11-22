package cc.bearvalley.badminton.product.bo;

/**
 * 微信小程序的登录验证实体类
 * 文档参考 https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html
 */
public class AuthorizationCode {
    private String session_key; // 会话密钥
    private String openid;      // 用户唯一标识

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Override
    public String toString() {
        return "AuthorizationCode{" +
                "session_key='" + session_key + '\'' +
                ", openid='" + openid + '\'' +
                '}';
    }
}

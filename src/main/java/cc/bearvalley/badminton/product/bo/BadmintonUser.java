package cc.bearvalley.badminton.product.bo;

/**
 * 羽毛球活动的用户的信息对象
 */
public class BadmintonUser {
    private String sessionId;  // 用户缓存的session id
    private long time;         // 该session id缓存的天数
    private boolean completed; // 用户资料是否完善

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "BadmintonUser{" +
                "sessionId='" + sessionId + '\'' +
                ", time=" + time +
                ", completed=" + completed +
                '}';
    }
}

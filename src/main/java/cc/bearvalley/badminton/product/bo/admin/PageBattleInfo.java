package cc.bearvalley.badminton.product.bo.admin;

import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.challenge.Battle;

import java.util.List;

/**
 * 后台显示对战的信息对象
 */
public class PageBattleInfo {
    private int id;                      // 对战主键id
    private int result;                  // 对战结果
    private String resultStr;            // 对战结果字符串
    private List<User> leftChallengers;  // 左队的参加者
    private List<User> rightChallengers; // 右队的参加者
    private List<User> leftVoters;       // 左队的投票者
    private List<User> rightVoters;      // 右队的投票者

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getResultStr() {
        return resultStr;
    }

    public void setResultStr(String resultStr) {
        this.resultStr = resultStr;
    }

    public List<User> getLeftChallengers() {
        return leftChallengers;
    }

    public void setLeftChallengers(List<User> leftChallengers) {
        this.leftChallengers = leftChallengers;
    }

    public List<User> getRightChallengers() {
        return rightChallengers;
    }

    public void setRightChallengers(List<User> rightChallengers) {
        this.rightChallengers = rightChallengers;
    }

    public List<User> getLeftVoters() {
        return leftVoters;
    }

    public void setLeftVoters(List<User> leftVoters) {
        this.leftVoters = leftVoters;
    }

    public List<User> getRightVoters() {
        return rightVoters;
    }

    public void setRightVoters(List<User> rightVoters) {
        this.rightVoters = rightVoters;
    }

    @Override
    public String toString() {
        return "PageBattleInfo{" +
                "id=" + id +
                ", result=" + result +
                ", resultStr='" + resultStr + '\'' +
                ", leftChallengers=" + leftChallengers +
                ", rightChallengers=" + rightChallengers +
                ", leftVoters=" + leftVoters +
                ", rightVoters=" + rightVoters +
                '}';
    }
}

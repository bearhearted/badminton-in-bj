package cc.bearvalley.badminton.product.bo;

import java.util.List;

/**
 * 单条挑战的信息对象
 */
public class BattleInfo {
    private int id;                    // 该对战的唯一标示id
    private boolean added;             // 是否已经报名改对战
    private boolean enrolled;          // 是否已经报名改次的活动
    private boolean voted;             // 是否已经为该次对战投过票
    private int leftChallengerCount;   // 左队的参赛人数
    private int rightChallengerCount;  // 右队的参赛人数
    private int leftVoterCount;        // 左队的投票人数
    private int rightVoterCount;       // 右队的投票人数
    private List<BadmintonEnroll> leftChallengers;   // 左队的参赛者列表
    private List<BadmintonEnroll> rightChallengers;  // 右队的参赛者列表
    private List<List<BadmintonEnroll>> leftVoters;  // 左队的投票者列表
    private List<List<BadmintonEnroll>> rightVoters; // 右队的投票者列表

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public boolean isVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    public int getLeftChallengerCount() {
        return leftChallengerCount;
    }

    public void setLeftChallengerCount(int leftChallengerCount) {
        this.leftChallengerCount = leftChallengerCount;
    }

    public int getRightChallengerCount() {
        return rightChallengerCount;
    }

    public void setRightChallengerCount(int rightChallengerCount) {
        this.rightChallengerCount = rightChallengerCount;
    }

    public int getLeftVoterCount() {
        return leftVoterCount;
    }

    public void setLeftVoterCount(int leftVoterCount) {
        this.leftVoterCount = leftVoterCount;
    }

    public int getRightVoterCount() {
        return rightVoterCount;
    }

    public void setRightVoterCount(int rightVoterCount) {
        this.rightVoterCount = rightVoterCount;
    }

    public List<BadmintonEnroll> getLeftChallengers() {
        return leftChallengers;
    }

    public void setLeftChallengers(List<BadmintonEnroll> leftChallengers) {
        this.leftChallengers = leftChallengers;
    }

    public List<BadmintonEnroll> getRightChallengers() {
        return rightChallengers;
    }

    public void setRightChallengers(List<BadmintonEnroll> rightChallengers) {
        this.rightChallengers = rightChallengers;
    }

    public List<List<BadmintonEnroll>> getLeftVoters() {
        return leftVoters;
    }

    public void setLeftVoters(List<List<BadmintonEnroll>> leftVoters) {
        this.leftVoters = leftVoters;
    }

    public List<List<BadmintonEnroll>> getRightVoters() {
        return rightVoters;
    }

    public void setRightVoters(List<List<BadmintonEnroll>> rightVoters) {
        this.rightVoters = rightVoters;
    }

    @Override
    public String toString() {
        return "BattleInfo{" +
                "id=" + id +
                ", added=" + added +
                ", enrolled=" + enrolled +
                ", voted=" + voted +
                ", leftChallengerCount=" + leftChallengerCount +
                ", rightChallengerCount=" + rightChallengerCount +
                ", leftVoterCount=" + leftVoterCount +
                ", rightVoterCount=" + rightVoterCount +
                ", leftChallengers=" + leftChallengers +
                ", rightChallengers=" + rightChallengers +
                ", leftVoters=" + leftVoters +
                ", rightVoters=" + rightVoters +
                '}';
    }
}

package cc.bearvalley.badminton.util;

/**
 *
 */
public enum RuleEnum {
    ENROLL_BEFORE_72_HOURS(3, "ENROLL_72_HOURS", "活动开始72小时前报名并参加"),
    ENROLL_IN_72_HOURS(2, "ENROLL_IN_72_HOURS", "活动开始72小时内报名并参加"),
    ADD_GROUP_CHAT(1, "ADD_GROUP_CHAT", "拉新人入群"),
    INTRODUCER_FIRST_ENROLL(2, "INTRODUCER_FIRST_ENROLL", "拉的新人来打球"),
    NEWCOMER__FIRST_ENROLL(2, "NEWCOMER__FIRST_ENROLL", "第一次来打球"),
    BATTLE_WIN(3, "BATTLE_WIN", "挑战胜利"),
    BATTLE_LOSE(-3, "BATTLE_LOSE", "挑战失败");
    private final int point;
    private final String name;
    private final String desc;

    RuleEnum(int point, String name, String desc) {
        this.point = point;
        this.name = name;
        this.desc = desc;
    }

    public int getPoint() {
        return point;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}

package cc.bearvalley.badminton.common;

/**
 * redis的key值常量类
 */
public interface RedisKey {

    /**
     * 前台用户登录的锁名称
     */
    String USER_LOGIN_ACTION_LOCK_KEY = "USER_LOGIN_ACTION_LOCK_KEY";

    /**
     * 用户参加活动的锁名称
     */
    String USER_ENROLL_LOCK_KEY = "USER_ENROLL_LOCK_KEY";

    /**
     * 用户购买商品的锁名称
     */
    String USER_BUY_LOCK_KEY = "USER_BUY_LOCK_KEY";

    /**
     * 后台修改挑战的锁名称
     */
    String ADMIN_EDIT_EVENT_LOCK_KEY = "ADMIN_EDIT_EVENT_LOCK_KEY";

    /**
     * 后台修改挑战结果的锁名称
     */
    String EDIT_BATTLE_RESULT_LOCK_KEY = "EDIT_BATTLE_RESULT_LOCK_KEY";

    /**
     * 添加挑战的锁名称
     */
    String ADD_BATTLE_LOCK_KEY = "ADD_BATTLE_LOCK_KEY";

    /**
     * 删除挑战的锁名称
     */
    String DELETE_BATTLE_LOCK_KEY = "DELETE_BATTLE_LOCK_KEY";

    /**
     * 给挑战投票的锁名称
     */
    String VOTE_BATTLE_LOCK_KEY = "VOTE_BATTLE_LOCK_KEY";

    /**
     * 取消挑战投票的锁名称
     */
    String CANCEL_VOTE_BATTLE_LOCK_KEY = "CANCEL_VOTE_BATTLE_LOCK_KEY";

    /**
     * 后台修改用户的锁名称
     */
    String ADMIN_EDIT_ADMIN_LOCK_KEY = "ADMIN_EDIT_ADMIN_LOCK_KEY";

    /**
     * 后台修改角色的锁名称
     */
    String ADMIN_EDIT_ROLE_LOCK_KEY = "ADMIN_EDIT_ROLE_LOCK_KEY";

    /**
     * 修改用户密码的锁名称
     */
    String PASSWORD_EDIT_LOCK_KEY = "PASSWORD_EDIT_LOCK_KEY";

    /**
     * 后台修改商品的锁名称
     */
    String ADMIN_EDIT_ITEM_LOCK_KEY = "ADMIN_EDIT_ITEM_LOCK_KEY";

    /**
     * 后台设置用户关联的锁名称
     */
    String ADMIN_SET_USER_RELATION_LOCK_KEY = "ADMIN_SET_USER_RELATION_LOCK_KEY";

    /**
     * 后台设置用户参加活动的锁名称
     */
    String ADMIN_SET_USER_ATTEND_LOCK_KEY = "ADMIN_SET_USER_ATTEND_LOCK_KEY";

    /**
     * 后台设置用户参加活动的锁名称
     */
    String ADMIN_UPDATE_USER_POINT_LOCK_KEY = "ADMIN_UPDATE_USER_POINT_LOCK_KEY";
}

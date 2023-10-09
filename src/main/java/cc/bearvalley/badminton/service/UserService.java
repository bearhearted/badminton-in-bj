package cc.bearvalley.badminton.service;

import cc.bearvalley.badminton.common.RedisKey;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.dao.UserDao;
import cc.bearvalley.badminton.dao.point.UserRelationDao;
import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.admin.Log;
import cc.bearvalley.badminton.entity.point.UserRelation;
import cc.bearvalley.badminton.util.RuleEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户相关的数据服务类
 */
@Service
public class UserService {

    /**
     * 根据用户id获取用户
     *
     * @param id 用户id
     * @return 该id对应的用户对象，没有的话返回<code>null</code>
     */
    public User getUserById(int id) {
        return userDao.findById(id).orElse(null);
    }

    /**
     * 根据用户的openid获取用户
     *
     * @param openid 用户的openid
     * @return 该openid对应的用户对象，没有的话返回<code>null</code>
     */
    public User getUserByOpenid(String openid) {
        return userDao.findByOpenid(openid);
    }

    /**
     * 创建一个新的用户
     *
     * @param openid 用户的openid
     * @return 创建后的新用户
     */
    public User saveUser(String openid) {
        User user = new User();
        user.setOpenid(openid);
        user.setStatus(User.StatusEnum.INCOMPLETE.getValue());
        user.setWin(0);
        user.setLose(0);
        user.setRatio(new BigDecimal(0));
        user.setPoint(0);
        user.setAttended(User.AttendEnum.OFF.getValue());
        return userDao.save(user);
    }

    /**
     * 更新一个用户的昵称和头像
     *
     * @param user     要修改的用户
     * @param nickname 要修改的昵称
     * @param avatar   要修改的头像
     * @return 修改后的用户
     */
    public User updateUser(User user, String nickname, String avatar) {
        if (!ObjectUtils.isEmpty(nickname)) {
            user.setNickname(nickname);
        }
        if (!ObjectUtils.isEmpty(avatar)) {
            user.setAvatar(avatar);
        }
        user.setAvatar(avatar);
        user.setStatus(User.StatusEnum.COMPLETED.getValue());
        return userDao.save(user);
    }

    /**
     * 获取所有的用户列表
     *
     * @param pageable 翻页参数
     * @return 所有的用户列表
     */
    public Page<User> listAllUser(Pageable pageable) {
        return userDao.findAll(pageable);
    }


    /**
     * 根据昵称搜索用户
     *
     * @param name     要搜索的字符
     * @param pageable 分页信息
     * @return 搜索结果
     */
    public Page<User> listUserByName(String name, Pageable pageable) {
        return userDao.findAllByNicknameContains(name, pageable);
    }

    /**
     * 获取完善资料的用户列表
     *
     * @param pageable 翻页参数
     * @return 该页码的完善资料的用户列表
     */
    public Page<User> listUserCompleted(Pageable pageable) {
        return userDao.findAllByStatus(User.StatusEnum.COMPLETED.getValue(), pageable);
    }

    /**
     * 获取所有完善资料的用户列表
     *
     * @return 所有的完善资料的用户列表
     */
    public List<User> listAllCompletedUser() {
        return userDao.findAllByStatus(User.StatusEnum.COMPLETED.getValue());
    }

    /**
     * 获取某个用户的推荐人信息
     *
     * @param user 要获取信息的用户
     * @return 该用户的推荐人信息
     */
    public UserRelation findUserRelationByUser(User user) {
        return userRelationDao.findByUser(user);
    }

    /**
     * 创建一个用户的推荐人信息
     *
     * @param user       要创建的用户
     * @param introducer 该用户的推荐用户
     * @return 创建结果
     */
    public RespBody<?> createUserRelation(User user, User introducer) {
        logger.info("start to create user relation with user = {}, introducer = {}", user, introducer);
        String lockName = RedisKey.ADMIN_SET_USER_RELATION_LOCK_KEY + user.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, check data");
        int count = userRelationDao.countByUser(user);
        logger.info("there is {} introducer of user {}", count, user);
        if (count > 0) {
            logger.info("data already existed, turn");
            return RespBody.isFail().msg("该用户已经有介绍人了");
        }
        logger.info("start to save relation");
        UserRelation userRelation = new UserRelation();
        userRelation.setUser(user);
        userRelation.setIntroducer(introducer);
        userRelationDao.save(userRelation);
        logger.info("relation {} saved", userRelation);
        logService.recordAdminLog(Log.ActionEnum.SET_USER_INTRODUCER,
                "", introducer.getNickname() + "介绍了" + user.getNickname());
        pointOperationService.updatePoint(introducer, RuleEnum.ADD_GROUP_CHAT, user.getNickname());
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 删除一个用户的推荐人信息
     *
     * @param user 要删除的用户
     * @return 删除结果
     */
    public RespBody<?> cancelUserRelation(User user) {
        logger.info("start to cancel user relation with user = {}", user);
        String lockName = RedisKey.ADMIN_SET_USER_RELATION_LOCK_KEY + user.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, check data");
        UserRelation relation = userRelationDao.findByUser(user);
        logger.info("relation of user {} is {}", user, relation);
        if (relation == null) {
            logger.info("data does not exist, turn");
            return RespBody.isFail().msg("该用户还没有介绍人");
        }
        logger.info("start to delete relation");
        userRelationDao.delete(relation);
        logger.info("relation {} saved", relation);
        logService.recordAdminLog(Log.ActionEnum.UNSET_USER_INTRODUCER,
                relation.getIntroducer().getNickname() + "介绍了" + user.getNickname(), "");
        pointOperationService.cancelPoint(relation.getIntroducer(), RuleEnum.ADD_GROUP_CHAT, user.getNickname());
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 设置用户参与过活动
     *
     * @param user 要设置的用户
     * @return 设置结果
     */
    public RespBody<?> setUserAttend(User user) {
        logger.info("start to create user attend with user = {}", user);
        String lockName = RedisKey.ADMIN_SET_USER_ATTEND_LOCK_KEY + user.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to set");
        logger.info("start to save attend");
        user.setAttended(User.AttendEnum.ON.getValue());
        userDao.save(user);
        logger.info("user {} attended saved", user);
        logService.recordAdminLog(Log.ActionEnum.SET_USER_ATTEND,
                "", user.getNickname());
        pointOperationService.updatePoint(user, RuleEnum.NEWCOMER__FIRST_ENROLL);
        // 看看他有没有介绍人
        UserRelation relation = userRelationDao.findByUser(user);
        logger.info("relation of introducer of user is {}", relation);
        if (relation != null) {
            logService.recordAdminLog(Log.ActionEnum.INTRODUCER_SET_ATTEND,
                    "", user.getNickname() + "介绍人" + relation.getIntroducer().getNickname());
            pointOperationService.updatePoint(relation.getIntroducer(), RuleEnum.INTRODUCER_FIRST_ENROLL);
        }

        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 取消用户参与过活动记录
     *
     * @param user 要取消的用户
     * @return 取消结果
     */
    public RespBody<?> cancelUserAttend(User user) {
        logger.info("start to cancel user attend with user = {}", user);
        String lockName = RedisKey.ADMIN_SET_USER_ATTEND_LOCK_KEY + user.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to set");
        logger.info("start to cancel attend");
        user.setAttended(User.AttendEnum.OFF.getValue());
        userDao.save(user);
        logger.info("user {} attended saved", user);
        logService.recordAdminLog(Log.ActionEnum.UNSET_USER_ATTEND,
                "", user.getNickname());
        pointOperationService.cancelPoint(user, RuleEnum.NEWCOMER__FIRST_ENROLL);
        // 看看他有没有介绍人
        UserRelation relation = userRelationDao.findByUser(user);
        logger.info("relation of introducer of user is {}", relation);
        if (relation != null) {
            logService.recordAdminLog(Log.ActionEnum.INTRODUCER_UNSET_ATTEND,
                    user.getNickname() + "介绍人" + relation.getIntroducer().getNickname(), "");
            pointOperationService.cancelPoint(relation.getIntroducer(), RuleEnum.INTRODUCER_FIRST_ENROLL);
        }
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param redisTemplate         操作redis的工具类
     * @param logService            操作日志的数据服务类
     * @param pointOperationService 对积分进行操作的服务类
     * @param userDao               用户信息的数据操作类
     * @param userRelationDao       用户推荐关系的数据操作类
     */
    public UserService(RedisTemplate<String, String> redisTemplate, LogService logService,
                       PointOperationService pointOperationService, UserDao userDao, UserRelationDao userRelationDao) {
        this.redisTemplate = redisTemplate;
        this.logService = logService;
        this.pointOperationService = pointOperationService;
        this.userDao = userDao;
        this.userRelationDao = userRelationDao;
    }

    private final RedisTemplate<String, String> redisTemplate;
    private final LogService logService;
    private final PointOperationService pointOperationService;
    private final UserDao userDao;
    private final UserRelationDao userRelationDao;
    private final Logger logger = LogManager.getLogger("serviceLogger");
}

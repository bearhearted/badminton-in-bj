package cc.bearvalley.badminton.service;

import cc.bearvalley.badminton.common.RedisKey;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.dao.EnrollDao;
import cc.bearvalley.badminton.entity.Enroll;
import cc.bearvalley.badminton.entity.Event;
import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.admin.Log;
import cc.bearvalley.badminton.util.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 羽毛球报名的数据服务类
 */
@Service
public class EnrollService {

    /**
     * 检查某个报名是否存在
     *
     * @param event 要报名的活动
     * @param user  要报名的用户
     * @return 如果存在返回<code>true</code>，不存在返回<code>false</code>
     */
    public boolean checkEnroll(Event event, User user) {
        return enrollDao.countByEventAndUser(event, user) > 0;
    }

    /**
     * 获取某个活动的报名人数
     *
     * @param event 要查询的活动
     * @return 该活动的报名人数
     */
    public int countEnrollByEvent(Event event) {
        return enrollDao.countByEvent(event);
    }

    /**
     * 创建一个报名
     *
     * @param event 要报名的活动
     * @param user  要报名的用户
     * @return 该用户对该活动的报名
     */
    public RespBody<?> createEnroll(Event event, User user) {

        logger.info("start to enroll event {} with user = {}", event, user);
        String lockName = RedisKey.USER_ENROLL_LOCK_KEY + event.getId() + "_" + user.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, check constriction");
        int count = enrollDao.countByEventAndUser(event, user);
        logger.info("there is {} with event and user", count);
        if (count > 0) {
            logger.info("enroll already existed");
            return RespBody.isFail().msg("已经参加");
        }
        logger.info("start to save enroll");
        Enroll enroll = new Enroll();
        enroll.setEvent(event);
        enroll.setUser(user);
        enroll.setAttended(Enroll.AttendedEnum.NO.getValue());
        enroll.setEnrollTime(new Date(System.currentTimeMillis()));
        enrollDao.save(enroll);
        logger.info("enroll {} saved", enroll);
        logService.recordApiLog(Log.ActionEnum.API_USER_ENROLL_EVENT, "", user.getNickname() + "->" + DateUtil.getDateStr(event.getStartTime()));
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 获取一个活动的所有报名
     *
     * @param event 要查询的活动
     * @return 该活动的所有报名
     */
    public List<Enroll> getEnrollListByEvent(Event event) {
        return enrollDao.findAllByEventOrderByEnrollTime(event);
    }

    /**
     * 获取一个用户的最新10个报名
     *
     * @param event    要查询的用户
     * @param pageable 分页参数
     * @return 该用户的所有报名在当前页的
     */
    public Page<Enroll> getEnrollListByEvent(User user) {
        // 默认一页显示多少个报名
        int PAGE_SIZE = 10;
        // 排序的列名
        String COLUMN_NAME = "enrollTime";
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE, Sort.Direction.DESC, COLUMN_NAME);
        return enrollDao.findAllByUser(user, pageRequest);
    }

    /**
     * 删除一个报名
     *
     * @param id 要删除的报名id
     */
    public RespBody<?> deleteEnrollByEventAndUser(Event event, User user) {
        logger.info("start to delete user {} enroll in event {}", user, event);
        enrollDao.deleteByEventAndUser(event, user);
        logger.info("enroll deleted");
        battleService.deleteBattleByEventAndUser(event, user);
        return RespBody.isOk();
    }

    /**
     * 删除某个报名
     *
     * @param id 要删除的报名id
     * @return 删除成功
     */
    public RespBody<?> deleteEnrollById(int id) {
        logger.info("start to enroll id = {}", id);
        enrollDao.deleteById(id);
        logger.info("enroll which id = {} is deleted", id);
        return RespBody.isOk();
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param enrollDao     羽毛球报名的数据操作类
     * @param battleService 羽毛球挑战的数据服务类
     * @param logService    操作日志的数据服务类
     * @param redisTemplate 操作redis的工具类
     */
    public EnrollService(EnrollDao enrollDao, BattleService battleService, LogService logService, RedisTemplate<String, String> redisTemplate) {
        this.enrollDao = enrollDao;
        this.battleService = battleService;
        this.redisTemplate = redisTemplate;
        this.logService = logService;
    }

    private final RedisTemplate<String, String> redisTemplate;
    private final EnrollDao enrollDao;
    private final BattleService battleService;
    private final LogService logService;
    private final Logger logger = LogManager.getLogger("serviceLogger");
}

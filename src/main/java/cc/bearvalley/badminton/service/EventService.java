package cc.bearvalley.badminton.service;

import cc.bearvalley.badminton.common.ErrorEnum;
import cc.bearvalley.badminton.common.RedisKey;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.dao.EnrollDao;
import cc.bearvalley.badminton.dao.EventDao;
import cc.bearvalley.badminton.entity.Enroll;
import cc.bearvalley.badminton.entity.Event;
import cc.bearvalley.badminton.entity.admin.Log;
import cc.bearvalley.badminton.util.DateUtil;
import cc.bearvalley.badminton.util.RuleEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 羽毛球活动的数据服务类
 */
@Service
public class EventService {

    /**
     * 创建一个羽毛球活动
     *
     * @param stadium   活动场馆
     * @param fields    活动场地
     * @param fee       人均费用
     * @param amount    活动人数
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 创建的羽毛球活动
     */
    public RespBody<?> createEvent(String stadium, String fields, int fee, int amount, Date startTime, Date endTime) {
        logger.info("start to create event with stadium = {}, fields = {}, fee = {}, amount = {}, startTime = {}, endTime = {}",
                stadium, fields, fee, amount, startTime, endTime);
        String lockName = RedisKey.ADMIN_EDIT_EVENT_LOCK_KEY;
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to save");
        Event event = new Event();
        event.setStadium(stadium);
        event.setFields(fields);
        event.setFee(fee);
        event.setAmount(amount);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setStatus(Event.StatusEnum.ON.getValue());
        event.setChecked(Event.CheckEnum.NO.getValue());
        event.setCreator(null);
        eventDao.save(event);
        logger.info("event {} is saved", event);
        logService.recordAdminLog(Log.ActionEnum.ADD_EVENT, "", event.toString());
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 根据id获取某次活动
     *
     * @param eventId 要查询的活动id
     * @return 改id对应的活动，没有返回<code>null</code>
     */
    public Event getEventById(int eventId) {
        return eventDao.findById(eventId).orElse(null);
    }

    /**
     * 获取所有可以显示的活动
     *
     * @return 所有可以显示的活动
     */
    public List<Event> listAllAvailableEvents() {
        return eventDao.findAllByStatusOrderByStartTimeAsc(Event.StatusEnum.ON.getValue());
    }

    /**
     * 分页获取活动列表
     *
     * @param pageable 翻页参数
     * @return 该页码下的活动列表
     */
    public Page<Event> listEvents(Pageable pageable) {
        return eventDao.findAll(pageable);
    }

    /**
     * 获取最新的一次活动
     *
     * @return 最新的一次活动
     */
    public Event listLeastRecentEvent() {
        return eventDao.findFirstByOrderByCreateTimeDesc();
    }

    /**
     * 修改一个羽毛球活动
     *
     * @param stadium   活动场馆
     * @param fields    活动场地
     * @param fee       人均费用
     * @param amount    活动人数
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 修改成功
     */
    public RespBody<?> editEvent(Event event, String stadium, String fields, int fee, int amount, Date startTime, Date endTime) {
        logger.info("start to edit event with event ={}, stadium = {}, fields = {}, fee = {}, amount = {}, startTime = {}, endTime = {}",
                event, stadium, fields, fee, amount, startTime, endTime);
        String lockName = RedisKey.ADMIN_EDIT_EVENT_LOCK_KEY + event.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to save");
        String old = event.toString();
        event.setStadium(stadium);
        event.setFields(fields);
        event.setFee(fee);
        event.setAmount(amount);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setCreator(null);
        eventDao.save(event);
        logger.info("event {} is saved", event);
        logService.recordAdminLog(Log.ActionEnum.EDIT_EVENT, old, event.toString());
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 将活动的状态进行转换，显示<->隐藏
     *
     * @param event 要修改的活动
     * @return 保存成功
     */
    public RespBody<?> editEventStatus(Event event, Event.StatusEnum statusEnum) {
        logger.info("start to edit status of event {} to {}", event, statusEnum);
        int oldStatus = event.getStatus();
        String lockName = RedisKey.ADMIN_EDIT_EVENT_LOCK_KEY + event.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to save");
        event.setStatus(statusEnum.getValue());
        eventDao.save(event);
        logger.info("event {} is saved", event);
        logService.recordAdminLog(Log.ActionEnum.EDIT_EVENT_STATUS, Event.StatusEnum.findStatusByValue(oldStatus).getDesc(),
                statusEnum.getDesc());
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 确认某次活动的参与人员
     *
     * @param event   要确认的活动
     * @param enrolls 参与的人员id列表
     * @return 确认结果
     */
    public RespBody<?> checkEnroll(Event event, int[] enrolls) {
        logger.info("start to checkEvent with event = {}, enroll = {}", event, enrolls);
        String lockName = RedisKey.ADMIN_EDIT_EVENT_LOCK_KEY + event.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        List<Enroll> list = Arrays.stream(enrolls).mapToObj(id -> enrollDao.findById(id).orElse(null)).collect(Collectors.toList());
        if (list.size() != enrolls.length) {
            return RespBody.isFail().msg(ErrorEnum.DATA_ERROR);
        }
        list.forEach(this::checkEnroll);
        event.setChecked(Event.CheckEnum.YES.getValue());
        eventDao.save(event);
        logger.info("event = {} is checked", event);
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 确认某次参与
     *
     * @param enroll 要确认的参与
     */
    private void checkEnroll(Enroll enroll) {
        enroll.setAttended(Enroll.AttendedEnum.YES.getValue());
        enrollDao.save(enroll);
        if (enroll.getEnrollTime().getTime() - enroll.getEvent().getStartTime().getTime() > 72 * 60 * 60 * 1000) {
            pointOperationService.updatePoint(enroll.getUser(), RuleEnum.ENROLL_BEFORE_72_HOURS, DateUtil.getDateStr(enroll.getEvent().getStartTime()));
        } else {
            pointOperationService.updatePoint(enroll.getUser(), RuleEnum.ENROLL_IN_72_HOURS, DateUtil.getDateStr(enroll.getEvent().getStartTime()));
        }
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param redisTemplate         操作redis的工具类
     * @param eventDao              羽毛球活动的数据操作类
     * @param enrollDao             羽毛球报名的数据操作类
     * @param logService            操作日志的数据服务类
     * @param pointOperationService 对积分进行操作的服务类
     */
    public EventService(RedisTemplate<String, String> redisTemplate, EventDao eventDao,
                        EnrollDao enrollDao, LogService logService,
                        PointOperationService pointOperationService) {
        this.redisTemplate = redisTemplate;
        this.eventDao = eventDao;
        this.enrollDao = enrollDao;
        this.logService = logService;
        this.pointOperationService = pointOperationService;
    }

    private final RedisTemplate<String, String> redisTemplate;
    private final EventDao eventDao;
    private final EnrollDao enrollDao;
    private final LogService logService;
    private final PointOperationService pointOperationService;
    private final Logger logger = LogManager.getLogger("serviceLogger");
}

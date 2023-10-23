package cc.bearvalley.badminton.service;

import cc.bearvalley.badminton.dao.UserDao;
import cc.bearvalley.badminton.dao.point.PointRecordDao;
import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.admin.Log;
import cc.bearvalley.badminton.entity.point.Item;
import cc.bearvalley.badminton.entity.point.PointRecord;
import cc.bearvalley.badminton.util.DateUtil;
import cc.bearvalley.badminton.util.RuleEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 对积分进行操作的服务类
 */
@Service
public class PointOperationService {

    /**
     * 用户积分
     * @param user
     * @param item
     */
    public void buyPoint(User user, Item item) {
        logger.info("start to update point with user = {}, buying item = {}", user, item);
        int oldPoint = user.getPoint();
        user.setPoint(oldPoint - item.getPoint());
        userDao.save(user);
        logger.info("user {} saved with new point = {}", user, user.getPoint());
        logService.recordApiLog(Log.ActionEnum.USER_BUY_ITEM, "", "消耗" + item.getPoint() + "积分购买" + item);
        savePointRecord(user, -item.getPoint(), "购买商品", item.getName());
    }

    /**
     * 更新一个用户的积分
     *
     * @param user 要变化积分的用户
     * @param rule 更新的规则
     */
    public void updatePoint(User user, RuleEnum rule) {
        updatePoint(user, rule, null);
    }

    /**
     * 更新一个用户的积分
     *
     * @param user 要变化积分的用户
     * @param rule 更新的规则
     * @param note 备注
     */
    public void updatePoint(User user, RuleEnum rule, String note) {
        logger.info("start to update point with user = {}, rule = {}", user, rule);
        int oldPoint = user.getPoint();
        user.setPoint(oldPoint + rule.getPoint());
        userDao.save(user);
        logger.info("user {} saved with new point = {}", user, user.getPoint());
        logService.recordAdminLog(Log.ActionEnum.ADD_POINT, user.getNickname() + "积分" + oldPoint, user.getNickname() + "积分" + user.getPoint());
        savePointRecord(user, rule.getPoint(), rule.getDesc(), note);
    }

    /**
     * 取消一个用户的积分
     *
     * @param user 要取消积分的用户
     * @param rule 更新的规则
     */
    public void cancelPoint(User user, RuleEnum rule) {
        updatePoint(user, rule, null);
    }

    /**
     * 取消一个用户的积分
     *
     * @param user 要取消积分的用户
     * @param rule 更新的规则
     * @param note 备注
     */
    public void cancelPoint(User user, RuleEnum rule, String note) {
        logger.info("start to cancel point with user = {}, rule = {}", user, rule);
        int oldPoint = user.getPoint();
        user.setPoint(oldPoint - rule.getPoint());
        userDao.save(user);
        logger.info("user {} saved with new point = {}", user, user.getPoint());
        logService.recordAdminLog(Log.ActionEnum.CANCEL_POINT, user.getNickname() + "积分" + oldPoint, user.getNickname() + "积分" + user.getPoint());
        savePointRecord(user, -rule.getPoint(), "撤销" + rule.getDesc(), note);
    }

    /**
     * 记录这次用户积分的变化
     *
     * @param user  要记录的用户
     * @param point 本次变化的积分值
     * @param scene 积分变化场景
     * @param note  备注
     */
    private void savePointRecord(User user, int point, String scene, String note) {
        logger.info("start to save point record with user id = {}, point = {}, scene = {}, note = {}",
                user.getId(), point, scene, note);
        if (StringUtils.hasLength(note)) {
            scene = scene + "(" + note + ")";
        }
        PointRecord record = new PointRecord();
        record.setUser(user);
        record.setPoint(point);
        record.setScene(scene);
        record.setDay(DateUtil.getDayInt(new Date(System.currentTimeMillis())));
        pointRecordDao.save(record);
        logger.info("log record {} saved", record);
    }

    /**
     * 检查过期的积分
     */
    public void checkOverduePoint() {

    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param logService     操作日志的数据服务类
     * @param pointRecordDao 积分变化记录的数据操作类
     * @param userDao        用户信息的数据操作类
     */
    public PointOperationService(LogService logService, PointRecordDao pointRecordDao, UserDao userDao) {
        this.logService = logService;
        this.pointRecordDao = pointRecordDao;
        this.userDao = userDao;
    }

    private final LogService logService;
    private final PointRecordDao pointRecordDao;
    private final UserDao userDao;
    private final Logger logger = LogManager.getLogger("serviceLogger");

}

package cc.bearvalley.badminton.service;

import cc.bearvalley.badminton.common.Const;
import cc.bearvalley.badminton.common.RedisKey;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.dao.EnrollDao;
import cc.bearvalley.badminton.dao.UserDao;
import cc.bearvalley.badminton.dao.challenge.BattleDao;
import cc.bearvalley.badminton.dao.challenge.ChallengerDao;
import cc.bearvalley.badminton.dao.challenge.VoteDao;
import cc.bearvalley.badminton.entity.Event;
import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.admin.Log;
import cc.bearvalley.badminton.entity.challenge.Battle;
import cc.bearvalley.badminton.entity.challenge.Challenger;
import cc.bearvalley.badminton.entity.challenge.Vote;
import cc.bearvalley.badminton.product.bo.BadmintonEnroll;
import cc.bearvalley.badminton.product.bo.BattleEvent;
import cc.bearvalley.badminton.product.bo.BattleInfo;
import cc.bearvalley.badminton.product.bo.MyBattle;
import cc.bearvalley.badminton.product.bo.admin.PageBattleInfo;
import cc.bearvalley.badminton.util.DateUtil;
import cc.bearvalley.badminton.util.RuleEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 羽毛球挑战的数据服务类
 */
@Service
public class BattleService {

    /**
     * 根据id获取约战
     *
     * @param id 要查询的约战id
     * @return 该id对应的约战，没找到返回<code>null</code>
     */
    public Battle getBattleById(int id) {
        return battleDao.findById(id).orElse(null);
    }

    /**
     * 获取某些活动所包含的所有挑战
     *
     * @param eventList 要查询的活动列表
     * @param user      访问用户
     * @return 该列表中活动所包含的所有挑战
     */
    public RespBody<?> listBattlesByEvents(List<Event> eventList, User user) {
        List<BattleEvent> list = eventList.stream().map(event -> listBattlesByEvent(event, user)).collect(Collectors.toList());
        return RespBody.isOk().data(list);
    }

    /**
     * 获取某个活动所包含的所有挑战
     *
     * @param event 要查询的活动
     * @param user  访问用户
     * @return 该活动所包含的所有挑战
     */
    public BattleEvent listBattlesByEvent(Event event, User user) {
        BattleEvent battleEvent = new BattleEvent();
        battleEvent.setId(event.getId());
        String date = DateUtil.getDateStr(event.getStartTime());
        String[] weekdays = new String[]{"", "周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(event.getStartTime());
        String day = weekdays[calendar.get(Calendar.DAY_OF_WEEK)];
        String startTime = DateUtil.getTimeStr(event.getStartTime());
        String endTime = DateUtil.getTimeStr(event.getEndTime());

        battleEvent.setDate(date + "  " + day + "  " + startTime + "-" + endTime);
        List<Battle> battleList = battleDao.findAllByEvent(event);
        boolean enrolled;
        if (user != null) {
            enrolled = enrollDao.findAllByEventOrderByEnrollTime(event).stream().
                    anyMatch(enroll -> enroll.getUser().getId() == user.getId());
        } else {
            enrolled = false;
        }
        battleEvent.setEnrolled(enrolled);
        battleEvent.setList(battleList.stream().map(challenge ->
                getBattleInfo(challenge, user, enrolled)).collect(Collectors.toList()));
        battleEvent.setAdded(battleEvent.getList().stream().anyMatch(BattleInfo::isAdded));
        return battleEvent;
    }

    /**
     * 获取某个对战对某个用户的展示信息
     *
     * @param battle   对战信息对象
     * @param user     访问用户
     * @param enrolled 该用户是否已经参加该对战
     * @return 该对战对该用户的展示信息
     */
    private BattleInfo getBattleInfo(Battle battle, User user, boolean enrolled) {
        BattleInfo battleInfo = new BattleInfo();
        battleInfo.setId(battle.getId());
        // 获取所有挑战者
        List<Challenger> challengerList = challengerDao.findAllByBattle(battle);
        // 获取所有的投票者
        List<Vote> voteList = voteDao.findAllByBattle(battle);
        battleInfo.setEnrolled(enrolled);
        if (user == null) {
            battleInfo.setAdded(false);
            battleInfo.setVoted(false);
        } else {
            battleInfo.setAdded(challengerList.stream().anyMatch(challenger -> challenger.getUser().getId() == user.getId()));
            battleInfo.setVoted(voteList.stream().anyMatch(vote -> vote.getUser().getId() == user.getId()));
        }
        // 分别找出左边队伍，右边队伍，左边投票者，右边投票者
        List<BadmintonEnroll> leftChallengers = challengerList.stream().filter(challenger ->
                challenger.getPosition() == Challenger.PositionEnum.LEFT_1.getValue()
                        || challenger.getPosition() == Challenger.PositionEnum.LEFT_2.getValue()).map(challenger ->
                getBattleEnrollInfo(challenger.getUser(), user)).collect(Collectors.toList());
        List<BadmintonEnroll> rightChallengers = challengerList.stream().filter(challenger ->
                challenger.getPosition() == Challenger.PositionEnum.RIGHT_1.getValue()
                        || challenger.getPosition() == Challenger.PositionEnum.RIGHT_2.getValue()).map(challenger ->
                getBattleEnrollInfo(challenger.getUser(), user)).collect(Collectors.toList());
        List<BadmintonEnroll> leftVoters = voteList.stream().filter(vote
                -> vote.getPosition() == Vote.PositionEnum.LEFT.getValue()).map(vote
                -> getBattleEnrollInfo(vote.getUser(), user)).collect(Collectors.toList());
        List<BadmintonEnroll> rightVoters = voteList.stream().filter(vote
                -> vote.getPosition() == Vote.PositionEnum.RIGHT.getValue()).map(vote
                -> getBattleEnrollInfo(vote.getUser(), user)).collect(Collectors.toList());
        battleInfo.setLeftChallengers(leftChallengers);
        battleInfo.setLeftChallengerCount(leftChallengers.size());
        battleInfo.setRightChallengers(rightChallengers);
        battleInfo.setRightChallengerCount(rightChallengers.size());
        battleInfo.setLeftVoters(partitionVoter(leftVoters, Const.DEFAULT_VOTER_NUMBER_IN_LINE));
        battleInfo.setLeftVoterCount(leftVoters.size());
        battleInfo.setRightVoters(partitionVoter(rightVoters, Const.DEFAULT_VOTER_NUMBER_IN_LINE));
        battleInfo.setRightVoterCount(rightVoters.size());
        return battleInfo;
    }

    /**
     * 把投票用户列表按比例分隔成多个小列表
     *
     * @param list 投票用户列表
     * @param size 每个小列表的元素数量
     * @return 分隔后的列表的列表
     */
    private List<List<BadmintonEnroll>> partitionVoter(List<BadmintonEnroll> list, int size) {
        int round = list.size() / size + (list.size() % size == 0 ? 0 : 1);
        List<List<BadmintonEnroll>> ret = new ArrayList<>(round);
        for (int i = 0; i < round; i++) {
            int start = i * size;
            int end = Math.min(start + size, list.size());
            ret.add(list.subList(start, end));
        }
        return ret;
    }

    /**
     * 获取参加用户用于展示的信息
     *
     * @param user    参加用户
     * @param visitor 访问用户
     * @return 参加用户的信息
     */
    private BadmintonEnroll getBattleEnrollInfo(User user, User visitor) {
        BadmintonEnroll badmintonEnroll = new BadmintonEnroll();
        badmintonEnroll.setUsername(user.getNickname());
        badmintonEnroll.setAvatar(user.getAvatar());
        if (visitor == null) {
            badmintonEnroll.setDelete(false);
        } else {
            badmintonEnroll.setDelete(user.getId() == visitor.getId());
        }
        return badmintonEnroll;
    }

    /**
     * 检查一个用户是否在某次活动中报名过挑战
     *
     * @param event 要查询的活动
     * @param user  要查询的用户
     * @return 如果有报名过挑战返回<code>true</code>，否则返回<code>false</code>
     */
    public RespBody<?> checkUserEnrollInAnyBattle(Event event, User user) {
        List<Battle> list = battleDao.findAllByEvent(event);
        boolean hasEnrolled = list.stream().anyMatch(challenge -> checkUserEnrollInBattle(challenge, user));
        return RespBody.isOk().data(hasEnrolled);
    }

    /**
     * 检查一个用户是否在某次挑战报过名
     *
     * @param battle 要查询的挑战
     * @param user   要查询的用户
     * @return 如果有报名过挑战返回<code>true</code>，否则返回<code>false</code>
     */
    private boolean checkUserEnrollInBattle(Battle battle, User user) {
        List<Challenger> challengers = challengerDao.findAllByBattle(battle);
        return challengers.stream().anyMatch(challenger -> user.getId() == challenger.getUser().getId());
    }

    /**
     * 创建一次挑战
     *
     * @param user  挑战用户
     * @param event 所属活动
     * @param type  挑战类型
     * @return 创建结果
     */
    public RespBody<?> createBattle(User user, Event event, int type) {
        logger.info("start to create battle with user = {}, event = {}, type = {}", user, event, type);
        if (user == null) {
            logger.info("user is null, return fail");
            return RespBody.isFail().msg("未获取到用户信息");
        }
        if (event == null) {
            logger.info("event is null, return fail");
            return RespBody.isFail().msg("还没报名");
        }
        logger.info("check lock in redis");
        String lockName = RedisKey.ADD_BATTLE_LOCK_KEY + user.getId();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockName, "0", 10, TimeUnit.SECONDS);
        if (lock == null || !lock) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("点击过快，请待会重试");
        }
        logger.info("check passed, start to save");
        Battle battle = new Battle();
        battle.setEvent(event);
        battle.setType(type);
        battle.setResult(Battle.ResultEnum.UN_DONE.getValue());
        battleDao.save(battle);
        logger.info("battle {} is saved", battle);
        logService.recordApiLog(Log.ActionEnum.ADD_BATTLE, "", battle);
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return createChallenger(user, battle, Challenger.PositionEnum.LEFT_1.getValue());
    }

    /**
     * 加入某个挑战的左边第一位
     *
     * @param user   参加用户
     * @param battle 要参加的挑战
     * @return 参加结果
     */
    public RespBody<?> rejoinBattle(User user, Battle battle) {
        return createChallenger(user, battle, Challenger.PositionEnum.LEFT_1.getValue());
    }

    /**
     * 加入某个挑战的左边第二位
     *
     * @param user   参加用户
     * @param battle 要参加的挑战
     * @return 参加结果
     */
    public RespBody<?> joinBattle(User user, Battle battle) {
        return createChallenger(user, battle, Challenger.PositionEnum.LEFT_2.getValue());
    }

    /**
     * 加入某个挑战的右边队伍第一位
     *
     * @param user   参加用户
     * @param battle 要参加的挑战
     * @return 参加结果
     */
    public RespBody<?> againstBattle(User user, Battle battle) {
        return createChallenger(user, battle, Challenger.PositionEnum.RIGHT_1.getValue());
    }

    /**
     * 加入某个挑战的右边队伍第二位
     *
     * @param user   参加用户
     * @param battle 要参加的挑战
     * @return 参加结果
     */
    public RespBody<?> joinAgainstBattle(User user, Battle battle) {
        return createChallenger(user, battle, Challenger.PositionEnum.RIGHT_2.getValue());
    }

    /**
     * 创建对战的挑战用户
     *
     * @param user     参加用户
     * @param battle   要加入的对战
     * @param position 要加入的位置
     * @return 创建用户
     */
    private RespBody<?> createChallenger(User user, Battle battle, int position) {
        logger.info("start to joint battle with user = {}, battle = {}, position = {}", user, battle, position);
        if (user == null) {
            logger.info("user is null, return fail");
            return RespBody.isFail().msg("未获取到用户信息");
        }
        if (battle == null) {
            logger.info("battle is null, return fail");
            return RespBody.isFail().msg("未获取到挑战");
        }
        logger.info("check lock in redis");
        String lockName = RedisKey.ADD_BATTLE_LOCK_KEY + user.getId();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockName, "0", 10, TimeUnit.SECONDS);
        if (lock == null || !lock) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("点击过快，请待会重试");
        }
        logger.info("check passed, start to save");
        Challenger challenger = new Challenger();
        challenger.setBattle(battle);
        challenger.setUser(user);
        challenger.setPosition(position);
        challenger.setEnrollTime(new Date(System.currentTimeMillis()));
        challengerDao.save(challenger);
        logger.info("challenger {} is saved", challenger);
        logger.info("delete user vote in battle");
        voteDao.deleteByUserAndBattle(user, battle);
        logger.info("user vote deleted");
        logService.recordApiLog(Log.ActionEnum.JOIN_BATTLE, battle, user);
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 取消参与某次对战
     *
     * @param user   要取消的用户
     * @param battle 要取消的对战
     * @return 取消结果
     */
    public RespBody<?> cancelBattleEnrollment(User user, Battle battle) {
        logger.info("start to cancel battle with user = {}, battle = {}", user, battle);
        if (user == null) {
            logger.info("user is null, return fail");
            return RespBody.isFail().msg("未获取到用户信息");
        }
        if (battle == null) {
            logger.info("battle is null, return fail");
            return RespBody.isFail().msg("未获取到挑战");
        }
        logger.info("check lock in redis");
        String lockName = RedisKey.DELETE_BATTLE_LOCK_KEY + user.getId();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockName, "0", 10, TimeUnit.SECONDS);
        if (lock == null || !lock) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("点击过快，请待会重试");
        }

        logger.info("check passed, start to cancel");
        int countJoin = challengerDao.countByUserAndBattle(user, battle);
        logger.info("count join = {}", countJoin);
        if (countJoin == 0) {
            logger.info("enroll is null, return");
            return RespBody.isFail().msg("未找到挑战用户");
        }
        logger.info("delete user enroll");
        challengerDao.deleteByUserAndBattle(user, battle);
        logger.info("user enroll deleted");
        logService.recordApiLog(Log.ActionEnum.LEAVE_BATTLE, battle, user);
        int challengerCount = challengerDao.countByBattle(battle);
        logger.info("there is {} challengers in battle", challengerCount);
        String deleteBattle = "";
        if (challengerCount == 0) {
            logger.info("there is no challengers in battle, start to delete battle {}", battle);
            battleDao.delete(battle);
            logger.info("battle {} deleted", battle);
            logger.info("delete vote in battle");
            voteDao.deleteByBattle(battle);
            logger.info("vote of battle deleted");
            logService.recordApiLog(Log.ActionEnum.DELETE_BATTLE, battle, user);
        }
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 为一次对战的某一方投票
     *
     * @param user     要投票的用户
     * @param battle   要投票的对战
     * @param position 要投票的位置
     * @return 投票结果
     */
    public RespBody<?> voteBattle(User user, Battle battle, int position) {
        logger.info("start to vote battle with user = {}, battle = {}, position = {}", user, battle, position);
        if (user == null) {
            logger.info("user is null, return fail");
            return RespBody.isFail().msg("未获取到用户信息");
        }
        if (battle == null) {
            logger.info("battle is null, return fail");
            return RespBody.isFail().msg("未获取到挑战");
        }
        logger.info("check lock in redis");
        String lockName = RedisKey.VOTE_BATTLE_LOCK_KEY + user.getId();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockName, "0", 10, TimeUnit.SECONDS);
        if (lock == null || !lock) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("点击过快，请待会重试");
        }
        if (challengerDao.countByUserAndBattle(user, battle) > 0) {
            logger.info("user already enrolled");
            return RespBody.isFail().msg("已经加入挑战，无法再投票");
        }
        if (voteDao.countByUserAndBattle(user, battle) > 0) {
            logger.info("user already voted");
            return RespBody.isFail().msg("已经投过票，无法再投票");
        }
        logger.info("check passed, start to save");
        Vote vote = new Vote();
        vote.setBattle(battle);
        vote.setUser(user);
        vote.setPosition(position);
        voteDao.save(vote);
        logger.info("vote {} is saved", vote);
        logService.recordApiLog(Log.ActionEnum.VOTE_BATTLE, "", vote);
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 取消对对战的投票
     *
     * @param user   要取消投票的用户
     * @param battle 要取消投票的对战
     * @return 取消结果
     */
    public RespBody<?> cancelVote(User user, Battle battle) {
        logger.info("start to cancel battle vote with user = {}, battle = {}", user, battle);
        if (user == null) {
            logger.info("user is null, return fail");
            return RespBody.isFail().msg("未获取到用户信息");
        }
        if (battle == null) {
            logger.info("battle is null, return fail");
            return RespBody.isFail().msg("未获取到挑战");
        }
        logger.info("check lock in redis");
        String lockName = RedisKey.CANCEL_VOTE_BATTLE_LOCK_KEY + user.getId();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockName, "0", 10, TimeUnit.SECONDS);
        if (lock == null || !lock) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("点击过快，请待会重试");
        }
        logger.info("check passed, start to cancel");
        if (voteDao.countByUserAndBattle(user, battle) > 0) {
            logger.info("delete vote");
            voteDao.deleteByUserAndBattle(user, battle);
            logService.recordApiLog(Log.ActionEnum.CANCEL_VOTE_BATTLE, battle, user);
        }
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 修改约战的比赛结果
     *
     * @param battle 要修改的约战
     * @param result 确认的比赛结果
     * @return 修改结果
     */
    public RespBody<?> editBattleResult(Battle battle, Battle.ResultEnum result) {
        logger.info("start to edit battle result with battle = {}, result = {}", battle, result);
        if (battle == null) {
            logger.info("battle is null, return fail");
            return RespBody.isFail().msg("未找到对战");
        }
        if (result == null) {
            logger.info("result is null, return fail");
            return RespBody.isFail().msg("未找到结果");
        }
        logger.info("check lock in redis");
        String lockName = RedisKey.EDIT_BATTLE_RESULT_LOCK_KEY + battle.getId();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockName, "0", 10, TimeUnit.SECONDS);
        if (lock == null || !lock) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("点击过快，请待会重试");
        }
        int oldResult = battle.getResult();
        logger.info("check passed, start to edit");
        battle.setResult(result.getValue());
        battleDao.save(battle);
        logger.info("edit battle {} saved", battle);
        logService.recordAdminLog(Log.ActionEnum.CONFIRM_BATTLE_RESULT, battle, result.getDesc());
        logger.info("start to add point");
        List<Challenger> challengers = challengerDao.findAllByBattle(battle);
        logger.info("challengers in battle = {}", challengers);
        challengers.forEach(challenger -> updateChallengeResult(challenger, result.getValue(), oldResult));
        logger.info("delete lock in redis");
        Boolean deleteResult = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", deleteResult);
        return RespBody.isOk();
    }

    /**
     * 更新参与约战的用户的相关信息
     *
     * @param challenger 约战参加者
     * @param newResult  最新确认的对战状态
     * @param oldResult  之前的对战状态
     */
    private void updateChallengeResult(Challenger challenger, int newResult, int oldResult) {
        logger.info("start to update challenger record with challenger = {}, new = {}, old = {}", challenger, newResult, oldResult);
        User user = challenger.getUser();
        int oldWin = user.getWin();
        int oldLose = user.getLose();
        BigDecimal oldRatio = user.getRatio();
        // 开始判断本次更新逻辑
        if (oldResult == Battle.ResultEnum.UN_DONE.getValue() && newResult == Battle.ResultEnum.LEFT_WON.getValue()) {
            // 旧结果是未完赛，左队胜利的情况下
            if (challenger.getPosition() == Challenger.PositionEnum.LEFT_1.getValue() ||
                    challenger.getPosition() == Challenger.PositionEnum.LEFT_2.getValue()) {
                user.setWin(user.getWin() + 1);
                pointOperationService.updatePoint(user, RuleEnum.BATTLE_WIN, DateUtil.getDateStr(challenger.getBattle().getEvent().getStartTime()));
            } else {
                user.setLose(user.getLose() + 1);
                pointOperationService.updatePoint(user, RuleEnum.BATTLE_LOSE, DateUtil.getDateStr(challenger.getBattle().getEvent().getStartTime()));
            }
        } else if (oldResult == Battle.ResultEnum.UN_DONE.getValue() && newResult == Battle.ResultEnum.RIGHT_WON.getValue()) {
            // 旧结果是未完赛，右队胜利的情况下
            if (challenger.getPosition() == Challenger.PositionEnum.RIGHT_1.getValue() ||
                    challenger.getPosition() == Challenger.PositionEnum.RIGHT_2.getValue()) {
                user.setWin(user.getWin() + 1);
                pointOperationService.updatePoint(user, RuleEnum.BATTLE_WIN, DateUtil.getDateStr(challenger.getBattle().getEvent().getStartTime()));
            } else {
                user.setLose(user.getLose() + 1);
                pointOperationService.updatePoint(user, RuleEnum.BATTLE_LOSE, DateUtil.getDateStr(challenger.getBattle().getEvent().getStartTime()));
            }
        } else if (oldResult == Battle.ResultEnum.LEFT_WON.getValue() && newResult == Battle.ResultEnum.UN_DONE.getValue()) {
            // 左队胜利要改成未完赛，这种是撤销
            if (challenger.getPosition() == Challenger.PositionEnum.LEFT_1.getValue() ||
                    challenger.getPosition() == Challenger.PositionEnum.LEFT_2.getValue()) {
                user.setWin(user.getWin() - 1);
                pointOperationService.cancelPoint(user, RuleEnum.BATTLE_WIN, DateUtil.getDateStr(challenger.getBattle().getEvent().getStartTime()));
            } else {
                user.setLose(user.getLose() - 1);
                pointOperationService.cancelPoint(user, RuleEnum.BATTLE_LOSE, DateUtil.getDateStr(challenger.getBattle().getEvent().getStartTime()));
            }
        } else if (oldResult == Battle.ResultEnum.RIGHT_WON.getValue() && newResult == Battle.ResultEnum.UN_DONE.getValue()) {
            // 右队胜利要改成未完赛，这种是撤销
            if (challenger.getPosition() == Challenger.PositionEnum.RIGHT_1.getValue() ||
                    challenger.getPosition() == Challenger.PositionEnum.RIGHT_2.getValue()) {
                user.setWin(user.getWin() - 1);
                pointOperationService.cancelPoint(user, RuleEnum.BATTLE_WIN, DateUtil.getDateStr(challenger.getBattle().getEvent().getStartTime()));
            } else {
                user.setLose(user.getLose() - 1);
                pointOperationService.cancelPoint(user, RuleEnum.BATTLE_LOSE, DateUtil.getDateStr(challenger.getBattle().getEvent().getStartTime()));
            }
        }
        BigDecimal win = new BigDecimal(user.getWin());
        BigDecimal total = new BigDecimal(user.getWin() + user.getLose());
        BigDecimal ratio = total.compareTo(BigDecimal.ZERO)==0?total:win.divide(total, 5, RoundingMode.HALF_UP);
        logger.info("start to save ");
        user.setRatio(ratio);
        userDao.save(user);
        logger.info("user {} saved", user);
        logService.recordAdminLog(Log.ActionEnum.CONFIRM_USER_BATTLE_RESULT, oldWin + "胜" + oldLose + "负,胜率" + oldRatio,
                user.getWin() + "胜" + user.getLose() + "负,胜率" + user.getRatio());
    }

    /**
     * 获取一个活动的全部对战信息
     *
     * @param event 要获取信息的对战
     * @return 该活动的全部对战信息
     */
    public List<PageBattleInfo> listBattlesByEvent(Event event) {
        return battleDao.findAllByEvent(event).stream().map(battle -> {
            List<Challenger> challengerList = challengerDao.findAllByBattle(battle);
            List<Vote> voteList = voteDao.findAllByBattle(battle);
            List<User> leftChallengerList = challengerList.stream().filter(challenger ->
                            challenger.getPosition() == Challenger.PositionEnum.LEFT_1.getValue()
                                    || challenger.getPosition() == Challenger.PositionEnum.LEFT_2.getValue())
                    .map(Challenger::getUser).collect(Collectors.toList());
            List<User> rightChallengerList = challengerList.stream().filter(challenger ->
                            challenger.getPosition() == Challenger.PositionEnum.RIGHT_1.getValue()
                                    || challenger.getPosition() == Challenger.PositionEnum.RIGHT_2.getValue())
                    .map(Challenger::getUser).collect(Collectors.toList());
            List<User> leftVoterList = voteList.stream().filter(vote -> vote.getPosition() == Vote.PositionEnum.LEFT.getValue())
                    .map(Vote::getUser).collect(Collectors.toList());
            List<User> rightVoterList = voteList.stream().filter(vote -> vote.getPosition() == Vote.PositionEnum.RIGHT.getValue())
                    .map(Vote::getUser).collect(Collectors.toList());
            PageBattleInfo pageBattleInfo = new PageBattleInfo();
            pageBattleInfo.setId(battle.getId());
            pageBattleInfo.setResult(battle.getResult());
            pageBattleInfo.setResultStr(Battle.ResultEnum.findResultByValue(battle.getResult()).getDesc());
            pageBattleInfo.setLeftChallengers(leftChallengerList);
            pageBattleInfo.setRightChallengers(rightChallengerList);
            pageBattleInfo.setLeftVoters(leftVoterList);
            pageBattleInfo.setRightVoters(rightVoterList);
            return pageBattleInfo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取对战记录
     *
     * @return 对战记录列表
     */
    public RespBody<List<User>> getBattleRecord() {
        return RespBody.isOk().data(userDao.findAllByOrderByRatioDesc());
    }

    /**
     * 删除一个用户在某个活动的全部对战
     *
     * @param event 要删除的活动
     * @param user  要删除的用户
     */
    public void deleteBattleByEventAndUser(Event event, User user) {
        logger.info("start to delete user {} in battle of event {}", user, event);
        battleDao.findAllByEvent(event).forEach(challenge -> cancelBattleEnrollment(user, challenge));
        logger.info("user in event deleted");
    }

    /**
     * 获取某个用户参加的活动列表
     * @param user 要查询的用户
     * @param pageable 分页参数
     * @return 该用户参加的活动列表
     */
    public MyBattle listChallengerByUser(User user, Pageable pageable) {
        Page<Challenger> page = challengerDao.findAllByUser(user, pageable);
        List<MyBattle.Info> list = page.stream().map(challenger -> {
            boolean isLeft = challenger.getPosition() == Challenger.PositionEnum.LEFT_1.getValue()
                    || challenger.getPosition() == Challenger.PositionEnum.LEFT_2.getValue();
            Battle battle = challenger.getBattle();
            MyBattle.Info info = new MyBattle.Info();
            info.setDate(DateUtil.getDateStr(battle.getEvent().getStartTime()));
            List<Challenger> challengers = challengerDao.findAllByBattle(battle);
            AtomicReference<String> teammate = new AtomicReference<>("");
            AtomicReference<String> opponent = new AtomicReference<>("");
            challengers.stream().filter(challenger1 -> challenger1.getId()!= challenger.getId()).forEach(challenger1 -> {
                if (isLeft) {
                    if (challenger1.getPosition() == Challenger.PositionEnum.LEFT_1.getValue() || challenger1.getPosition() == Challenger.PositionEnum.LEFT_2.getValue()) {
                        teammate.set(challenger1.getUser().getNickname());
                    } else {
                        if (StringUtils.hasLength(opponent.get())) {
                            opponent.set(opponent.get() + " & " + challenger1.getUser().getNickname());
                        } else {
                            opponent.set(challenger1.getUser().getNickname());
                        }
                    }
                } else {
                    if (challenger1.getPosition() == Challenger.PositionEnum.LEFT_1.getValue() || challenger1.getPosition() == Challenger.PositionEnum.LEFT_2.getValue()) {
                        if (StringUtils.hasLength(opponent.get())) {
                            opponent.set(opponent.get() + " & " + challenger1.getUser().getNickname());
                        } else {
                            opponent.set(challenger1.getUser().getNickname());
                        }
                    } else {
                        teammate.set(challenger1.getUser().getNickname());
                    }
                }
            });
            info.setTeammate(teammate.get());
            info.setOpponent(opponent.get());
            if (battle.getResult() == Battle.ResultEnum.LEFT_WON.getValue()) {
                if (isLeft) {
                    info.setResult("胜利");
                } else {
                    info.setResult("失败");
                }
            } else if (battle.getResult() == Battle.ResultEnum.RIGHT_WON.getValue()) {
                if (isLeft) {
                    info.setResult("失败");
                } else {
                    info.setResult("胜利");
                }
            } else {
                info.setResult("--");
            }
            return info;
        }).collect(Collectors.toList());
        MyBattle myBattle = new MyBattle();
        myBattle.setLast(page.isLast());
        myBattle.setList(list);
        return myBattle;
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param redisTemplate         操作redis的工具类
     * @param battleDao             角色和权限绑定的数据操作类
     * @param challengerDao         羽毛球约战的参与者的数据操作类
     * @param enrollDao             羽毛球报名的数据操作类
     * @param logService            操作日志的数据服务类
     * @param pointOperationService 对积分进行操作的服务类
     * @param voteDao               羽毛球约战投票的数据操作类
     * @param userDao               用户信息的数据操作类
     */
    public BattleService(RedisTemplate<String, String> redisTemplate, BattleDao battleDao,
                         ChallengerDao challengerDao, EnrollDao enrollDao,
                         LogService logService, PointOperationService pointOperationService,
                         VoteDao voteDao, UserDao userDao) {
        this.redisTemplate = redisTemplate;
        this.battleDao = battleDao;
        this.challengerDao = challengerDao;
        this.enrollDao = enrollDao;
        this.logService = logService;
        this.pointOperationService = pointOperationService;
        this.voteDao = voteDao;
        this.userDao = userDao;
    }

    private final RedisTemplate<String, String> redisTemplate;
    private final BattleDao battleDao;
    private final ChallengerDao challengerDao;
    private final EnrollDao enrollDao;
    private final LogService logService;
    private final PointOperationService pointOperationService;
    private final VoteDao voteDao;
    private final UserDao userDao;
    private final Logger logger = LogManager.getLogger("serviceLogger");

}

package cc.bearvalley.badminton.product.controller.api;

import cc.bearvalley.badminton.common.Const;
import cc.bearvalley.badminton.common.ErrorEnum;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.entity.Event;
import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.challenge.Battle;
import cc.bearvalley.badminton.product.bo.MyBattle;
import cc.bearvalley.badminton.product.service.MiniProgramService;
import cc.bearvalley.badminton.product.vo.UserSidAndPageVo;
import cc.bearvalley.badminton.service.BattleService;
import cc.bearvalley.badminton.service.EventService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 羽毛球挑战相关的控制类
 */
@RestController
@RequestMapping("/api/battle/")
public class BattleController {

    /**
     * 返回当前的所有挑战
     *
     * @param sid 要参与的用户session id
     * @return 当前的所有挑战
     */
    @RequestMapping("list")
    public RespBody<?> listChallenges(@RequestParam String sid) {
        User user = miniProgramService.getUserFromSessionId(sid);
        List<Event> eventList = eventService.listAllAvailableEvents();
        return battleService.listBattlesByEvents(eventList, user);
    }

    /**
     * 返回当前的所有挑战
     *
     * @param sid 要参与的用户session id
     * @return 当前的所有挑战
     */
    @RequestMapping("check")
    public RespBody<?> checkUserHasChallengeInEvent(@RequestParam int eventId, @RequestParam String sid) {
        User user = miniProgramService.getUserFromSessionId(sid);
        Event event = eventService.getEventById(eventId);
        return battleService.checkUserEnrollInAnyBattle(event, user);
    }

    /**
     * 用户发起一个挑战
     *
     * @param eventId 要参与的活动id
     * @param sid     要参与的用户session id
     * @return 发起一个挑战结果
     */
    @RequestMapping("start")
    public RespBody<?> startChallenge(@RequestParam int eventId, @RequestParam String sid, @RequestParam int type) {
        Event event = eventService.getEventById(eventId);
        User user = miniProgramService.getUserFromSessionId(sid);
        return battleService.createBattle(user, event, type);
    }

    /**
     * 用户取消一个挑战
     *
     * @param battleId 要取消挑战的挑战id
     * @param sid      要取消挑战的用户session id
     * @return 取消挑战结果
     */
    @RequestMapping("cancel")
    public RespBody<?> cancelChallenge(@RequestParam int battleId, @RequestParam String sid) {
        Battle battle = battleService.getBattleById(battleId);
        User user = miniProgramService.getUserFromSessionId(sid);
        return battleService.cancelBattleEnrollment(user, battle);
    }

    /**
     * 用户加入一个挑战
     *
     * @param battleId 要加入的挑战id
     * @param sid      要参与的用户session id
     * @return 加入挑战结果
     */
    @RequestMapping("join")
    public RespBody<?> joinChallenge(@RequestParam int battleId, @RequestParam String sid) {
        logger.info("join battleId = {}, sid = {}", battleId, sid);
        Battle battle = battleService.getBattleById(battleId);
        logger.info("battleId = {}, find battle = {}", battleId, battle);
        User user = miniProgramService.getUserFromSessionId(sid);
        logger.info("sid = {} find user = {}", sid, user);
        return battleService.joinBattle(user, battle);
    }

    /**
     * 用户应答一个挑战
     *
     * @param battleId 要应答的挑战id
     * @param sid      要应答的用户session id
     * @return 应答挑战结果
     */
    @RequestMapping("against")
    public RespBody<?> againstChallenge(@RequestParam int battleId, @RequestParam String sid) {
        Battle battle = battleService.getBattleById(battleId);
        User user = miniProgramService.getUserFromSessionId(sid);
        return battleService.againstBattle(user, battle);
    }

    /**
     * 用户加入一个挑战应答
     *
     * @param battleId 要加入的挑战id
     * @param sid      要加入挑战应答的用户session id
     * @return 加入挑战应答结果
     */
    @RequestMapping("join/against")
    public RespBody<?> joinAgainstChallenge(@RequestParam int battleId, @RequestParam String sid) {
        Battle battle = battleService.getBattleById(battleId);
        User user = miniProgramService.getUserFromSessionId(sid);
        return battleService.joinAgainstBattle(user, battle);
    }

    /**
     * 用户投票
     *
     * @param battleId 要投票的挑战id
     * @param sid      要投票的用户session id
     * @param position 投票的位置
     * @return 投票结果
     */
    @RequestMapping("vote")
    public RespBody<?> voteChallenge(@RequestParam int battleId, @RequestParam String sid, @RequestParam int position) {
        logger.info("join battleId = {}, sid = {}", battleId, sid);
        Battle battle = battleService.getBattleById(battleId);
        logger.info("battleId = {}, find battle = {}", battleId, battle);
        User user = miniProgramService.getUserFromSessionId(sid);
        logger.info("jsid = {} find user = {}", sid, user);
        return battleService.voteBattle(user, battle, position);
    }

    /**
     * 用户取消一个投票
     *
     * @param battleId 要取消投票的挑战id
     * @param sid      要取消投票的用户session id
     * @return 取消投票结果
     */
    @RequestMapping("vote/cancel")
    public RespBody<?> cancelVote(@RequestParam int battleId, @RequestParam String sid) {
        Battle battle = battleService.getBattleById(battleId);
        User user = miniProgramService.getUserFromSessionId(sid);
        return battleService.cancelVote(user, battle);
    }

    /**
     * 获取用户挑战的战绩
     *
     * @return 用户挑战的战绩
     */
    @RequestMapping("record")
    public RespBody<?> getChallengeRecord() {
        return battleService.getBattleRecord();
    }

    /**
     * 获取用户挑战的战绩
     *
     * @return 用户挑战的战绩
     */
    @RequestMapping("my/list")
    public RespBody<?> getMyBattleList(@RequestBody UserSidAndPageVo vo) {
        User user = miniProgramService.getUserFromSessionId(vo.getSid());
        if (user == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(vo.getPage(), Const.DEFAULT_ADMIN_PAGE_SIZE, Sort.Direction.DESC, "enrollTime");
        MyBattle myBattle = battleService.listChallengerByUser(user, pageable);
        return RespBody.isOk().data(myBattle);
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param battleService      羽毛球挑战的数据服务类
     * @param eventService       羽毛球活动的数据服务类
     * @param miniProgramService 微信小程序的服务类
     */
    public BattleController(BattleService battleService,
                            EventService eventService,
                            MiniProgramService miniProgramService) {
        this.battleService = battleService;
        this.eventService = eventService;
        this.miniProgramService = miniProgramService;
    }

    private final BattleService battleService;
    private final EventService eventService;
    private final MiniProgramService miniProgramService;
    private final Logger logger = LogManager.getLogger("apiLogger");
}

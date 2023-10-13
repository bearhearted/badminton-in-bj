package cc.bearvalley.badminton.product.controller;

import cc.bearvalley.badminton.common.Const;
import cc.bearvalley.badminton.common.ErrorEnum;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.entity.Enroll;
import cc.bearvalley.badminton.entity.Event;
import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.product.bo.BadmintonEnroll;
import cc.bearvalley.badminton.product.bo.BadmintonEvent;
import cc.bearvalley.badminton.product.bo.BadmintonUser;
import cc.bearvalley.badminton.product.bo.MyEvent;
import cc.bearvalley.badminton.product.service.CosService;
import cc.bearvalley.badminton.product.service.MiniProgramService;
import cc.bearvalley.badminton.product.vo.UserSidAndPageVo;
import cc.bearvalley.badminton.service.EnrollService;
import cc.bearvalley.badminton.service.EventService;
import cc.bearvalley.badminton.service.PointService;
import cc.bearvalley.badminton.service.UserService;
import cc.bearvalley.badminton.util.DateUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 羽毛球活动相关的控制器
 */
@RestController
@RequestMapping("/api/")
public class BadmintonController {

    /**
     * 微信用户的登录操作
     *
     * @param code 用户在小程序中通过wx.login()获取的临时令牌
     * @return 登录成功状态
     */
    @RequestMapping("login")
    public RespBody<?> login(@RequestParam String code) {
        BadmintonUser user = miniProgramService.userLogin(code);
        if (user != null) {
            return RespBody.isOk().data(user);
        }
        return RespBody.isFail();
    }

    /**
     * 将用户上传的头像url保存到本地，并返回本地的访问url
     *
     * @param file 用户上传的文件
     * @return 保存结果
     */
    @RequestMapping("upload")
    public RespBody<?> saveUrlToLocal(@RequestParam("file") MultipartFile file) {
        String url = cosService.uploadUserPic(file);
        if (StringUtils.hasLength(url)) {
            return RespBody.isOk().data(url);
        }
        return RespBody.isFail();
    }

    /**
     * 修改用户的资料
     *
     * @param sid      用户的session id
     * @param nickname 用户要修改的昵称
     * @param avatar   用户要修改的头像
     * @return 修改成功状态
     */
    @RequestMapping("user/update")
    public RespBody<?> updateUser(@RequestParam String sid, @RequestParam String nickname, @RequestParam String avatar) {
        User user = miniProgramService.getUserFromSessionId(sid);
        if (user != null) {
            userService.updateUser(user, nickname, avatar);
            return RespBody.isOk();
        }
        return RespBody.isFail();
    }

    /**
     * 获取用户
     *
     * @param sid 用户的session id
     * @return 用户对象
     */
    @RequestMapping("user/get")
    public RespBody<?> getUser(@RequestParam String sid) {
        User user = miniProgramService.getUserFromSessionId(sid);
        if (user != null && user.getStatus() == User.StatusEnum.COMPLETED.getValue()) {
            User nowUser = userService.getUserById(user.getId());
            return RespBody.isOk().data(nowUser);
        }
        return RespBody.isFail();
    }

    /**
     * 获取活动列表
     *
     * @param sid 用户session id
     * @return 活动列表
     */
    @RequestMapping("event/list")
    public RespBody<?> getEventList(@RequestParam String sid) {
        List<Event> eventList = eventService.listAllAvailableEvents();
        List<BadmintonEvent> list = new ArrayList<>(eventList.size());
        User user = miniProgramService.getUserFromSessionId(sid);
        for (Event event : eventList) {
            list.add(getBadmintonEvent(event, user));
        }
        return RespBody.isOk().data(list);
    }

    /**
     * 获取活动列表
     *
     * @param vo 页面参数
     * @return 活动列表
     */
    @PostMapping("my/event/list")
    public RespBody<?> getMyEvent(@RequestBody UserSidAndPageVo vo) {
        User user = miniProgramService.getUserFromSessionId(vo.getSid());
        if (user == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(vo.getPage(), Const.DEFAULT_ADMIN_PAGE_SIZE, Sort.Direction.DESC, "enrollTime");
        Page<Enroll> enrollList = enrollService.getEnrollListByUser(user, pageable);
        MyEvent myEvent = new MyEvent();
        myEvent.setLast(enrollList.isLast());
        myEvent.setList(enrollList.stream().map(enroll -> getBadmintonEvent(enroll.getEvent(), user)).collect(Collectors.toList()));
        return RespBody.isOk().data(myEvent);
    }

    /**
     * 获取某用户的积分记录列表
     *
     * @param vo 页面参数
     * @return 该用户的积分记录列表
     */
    @PostMapping("/my/point/list")
    public RespBody<?> listMyPoint(@RequestBody UserSidAndPageVo vo) {
        User user = miniProgramService.getUserFromSessionId(vo.getSid());
        Pageable pageable = PageRequest.of(vo.getPage(), Const.DEFAULT_ADMIN_PAGE_SIZE, Sort.Direction.DESC, "createTime");
        return RespBody.isOk().data(pointService.listPointRecordByUser(user, pageable));
    }

    /**
     * 将一次活动数据格式化成展示数据
     *
     * @param event 一次活动数据
     * @param user  微信用户
     * @return 格式化后的数据
     */
    private BadmintonEvent getBadmintonEvent(Event event, User user) {
        BadmintonEvent badmintonEvent = new BadmintonEvent();
        badmintonEvent.setId(event.getId());
        Date eventStartTime = event.getStartTime();
        badmintonEvent.setStartTime(eventStartTime.getTime());
        badmintonEvent.setDay(DateUtil.getDateStr(eventStartTime));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(eventStartTime);
        String[] weekdays = new String[]{"", "周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        badmintonEvent.setWeekday(weekdays[calendar.get(Calendar.DAY_OF_WEEK)]);
        badmintonEvent.setDay2(DateUtil.getDateStr(eventStartTime));
        String startTime = DateUtil.getTimeStr(eventStartTime);
        String endTime = DateUtil.getTimeStr(event.getEndTime());
        badmintonEvent.setTime(startTime + "-" + endTime);
        badmintonEvent.setStadium(event.getStadium());
        badmintonEvent.setFields(event.getFields());
        badmintonEvent.setFee(event.getFee());
        badmintonEvent.setTotalNumber(event.getAmount());
        badmintonEvent.setEnrollNumber(enrollService.countEnrollByEvent(event));
        if (user != null) {
            badmintonEvent.setEnrolled(enrollService.checkEnroll(event, user));
        }
        return badmintonEvent;
    }

    /**
     * 获取某次要参加的活动
     *
     * @param id  活动id
     * @param sid 用户session id
     * @return 该id对应的活动
     */
    @RequestMapping("enroll/event")
    public RespBody<?> getEventById(@RequestParam int id, @RequestParam String sid) {
        Event event = eventService.getEventById(id);
        User user = miniProgramService.getUserFromSessionId(sid);
        if (event != null) {
            return RespBody.isOk().data(getBadmintonEvent(event, user));
        }
        return RespBody.isFail();
    }

    /**
     * 获取某次活动的参加列表
     *
     * @param id  活动id
     * @param sid 用户session id
     * @return 该活动的参加列表
     */
    @RequestMapping("enroll/list")
    public RespBody<?> getEnrollListByEvent(@RequestParam int id, @RequestParam String sid) {
        Event event = eventService.getEventById(id);
        User user = miniProgramService.getUserFromSessionId(sid);
        if (event != null) {
            return RespBody.isOk().data(getBadmintonEnroll(event, user));
        }
        return RespBody.isFail();
    }

    /**
     * 获取某次活动的参与列表
     *
     * @param event 活动对象
     * @param user  微信用户
     * @return 该活动的参与列表
     */
    private List<BadmintonEnroll> getBadmintonEnroll(Event event, User user) {
        List<Enroll> enrollList = enrollService.getEnrollListByEvent(event);
        List<BadmintonEnroll> list = new ArrayList<>(enrollList.size());
        for (Enroll enroll : enrollList) {
            BadmintonEnroll badmintonEnroll = new BadmintonEnroll();
            badmintonEnroll.setUsername(enroll.getUser().getNickname());
            badmintonEnroll.setAvatar(enroll.getUser().getAvatar());
            badmintonEnroll.setDelete(user != null && user.getOpenid().equals(enroll.getUser().getOpenid()));
            list.add(badmintonEnroll);
        }
        return list;
    }

    /**
     * 用户参与活动
     *
     * @param eventId 要参与的活动id
     * @param sid     要参与的用户session id
     * @return 参与结果
     */
    @RequestMapping("user/enroll")
    public RespBody<?> userEnroll(@RequestParam int eventId, @RequestParam String sid) {
        Event event = eventService.getEventById(eventId);
        User user = miniProgramService.getUserFromSessionId(sid);
        if (event == null) {
            return RespBody.isFail().msg(ErrorEnum.EVENT_NOT_FOUND);
        }
        if (user == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        return enrollService.createEnroll(event, user);
    }

    /**
     * 用户撤销参与活动
     *
     * @param eventId 要撤销的活动id
     * @param sid     要撤销的用户session id
     * @return 撤销结果
     */
    @RequestMapping("user/cancel")
    public RespBody<?> userCancel(@RequestParam int eventId, @RequestParam String sid) {
        Event event = eventService.getEventById(eventId);
        if (event == null) {
            return RespBody.isFail().msg(ErrorEnum.EVENT_NOT_FOUND);
        }
        User user = miniProgramService.getUserFromSessionId(sid);
        if (user == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        return enrollService.deleteEnrollByEventAndUser(event, user);
    }

    /**
     * 获取用户的积分列表
     *
     * @param p 页码
     * @return 用户的积分列表
     */
    @PostMapping("/point/list")
    public RespBody<?> listUserPoint(@RequestParam(required = false, defaultValue = "0") int p) {
        if (p < 0) {
            p = 0;
        }
        Pageable pageable = PageRequest.of(p, Const.DEFAULT_ADMIN_PAGE_SIZE, Sort.Direction.DESC, "point", "id");
        return RespBody.isOk().data(userService.listUserCompleted(pageable));
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param cosService         腾讯COS相关的服务类
     * @param eventService       羽毛球活动的数据服务类
     * @param enrollService      羽毛球报名的数据服务类
     * @param pointService       用户积分的数据服务类
     * @param userService        用户相关的数据服务类
     * @param miniProgramService 微信小程序的服务类å
     */
    public BadmintonController(CosService cosService, EventService eventService,
                               EnrollService enrollService, PointService pointService,
                               UserService userService, MiniProgramService miniProgramService) {
        this.cosService = cosService;
        this.eventService = eventService;
        this.enrollService = enrollService;
        this.pointService = pointService;
        this.userService = userService;
        this.miniProgramService = miniProgramService;
    }

    private final CosService cosService;
    private final EventService eventService;
    private final EnrollService enrollService;
    private final PointService pointService;
    private final UserService userService;
    private final MiniProgramService miniProgramService;
}

package cc.bearvalley.badminton.product.controller;

import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.entity.Enroll;
import cc.bearvalley.badminton.entity.Event;
import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.product.bo.BadmintonEnroll;
import cc.bearvalley.badminton.product.bo.BadmintonEvent;
import cc.bearvalley.badminton.product.bo.BadmintonUser;
import cc.bearvalley.badminton.product.service.MiniProgramService;
import cc.bearvalley.badminton.service.EnrollService;
import cc.bearvalley.badminton.service.EventService;
import cc.bearvalley.badminton.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 羽毛球活动相关的控制器
 */
@RestController
@RequestMapping("/api/")
public class BadmintonController {

    /**
     * 微信用户的登录操作
     * @param code 用户在小程序中通过wx.login()获取的临时令牌
     * @return 登录成功状态
     */
    @RequestMapping("login")
    public RespBody login(@RequestParam String code) {
        BadmintonUser user = miniProgramService.userLogin(code);
        if (user != null) {
            return RespBody.isOk().data(user);
        }
        return RespBody.isFail();
    }

    /**
     * 将用户上传的头像url保存到本地，并返回本地的访问url
     * @param url 用户上传头像后微信返回的临时url
     * @return 保存后的本地url
     */
    @RequestMapping("upload")
    public RespBody saveUrlToLocal(@RequestParam("file") MultipartFile file) {
        logger.info("update url : {}", file);
        try {
            String dir = "/data/image/head/badminton/";
            String website = "https://images.bearvalley.cc/head/badminton/";
            String pref = "";
            if (file.getOriginalFilename().contains(".")) {
                pref = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            }
            String outputFileName = UUID.randomUUID() + pref;
            logger.info("output file name is {}", outputFileName);
            file.transferTo(new File(dir + outputFileName));
            return RespBody.isOk().data(website + outputFileName);
        } catch (Exception ex) {
            logger.error("upload head error", ex);
            ex.printStackTrace();
        }
        return RespBody.isFail();
    }

    /**
     * 修改用户的资料
     * @param sid 用户的session id
     * @param nickname 用户要修改的昵称
     * @param avatar 用户要修改的头像
     * @return 修改成功状态
     */
    @RequestMapping("user/update")
    public RespBody updateUser(@RequestParam String sid, @RequestParam String nickname, @RequestParam String avatar) {
        User user = miniProgramService.getUserFromSessionId(sid);
        if (user != null) {
            userService.updateUser(user, nickname, avatar);
            return RespBody.isOk();
        }
        return RespBody.isFail();
    }

    /**
     * 获取用户
     * @param sid 用户的session id
     * @return 用户对象
     */
    @RequestMapping("user/get")
    public RespBody getUser(@RequestParam String sid) {
        User user = miniProgramService.getUserFromSessionId(sid);
        if (user != null && user.getStatus() == User.STATUS.COMPLETED.getValue()) {
            return RespBody.isOk().data(user);
        }
        return RespBody.isFail();
    }

    /**
     * 获取活动列表
     * @param sid 用户session id
     * @return 活动列表
     */
    @RequestMapping("event/list")
    public RespBody getEventList(@RequestParam String sid) {
        List<Event> eventList = eventService.listAllAvailableEvents();
        List<BadmintonEvent> list = new ArrayList<>(eventList.size());
        User user = miniProgramService.getUserFromSessionId(sid);
        for(Event event : eventList) {
            list.add(getBadmintonEvent(event, user));
        }
        return RespBody.isOk().data(list);
    }

    /**
     * 获取活动列表
     * @param sid 用户session id
     * @return 活动列表
     */
    @RequestMapping("my/event")
    public RespBody getMyEventList(@RequestParam String sid) {
        User user = miniProgramService.getUserFromSessionId(sid);
        List<BadmintonEvent> list = new ArrayList<>();
        if (user != null) {
            Page<Enroll> enrollList = enrollService.getEnrollListByEvent(user);
            for (Enroll enroll : enrollList) {
                list.add(getBadmintonEvent(enroll.getEvent(), user));
            }
        }
        return RespBody.isOk().data(list);
    }

    /**
     * 将一次活动数据格式化成展示数据
     * @param event 一次活动数据
     * @param user 微信用户
     * @return 格式化后的数据
     */
    private BadmintonEvent getBadmintonEvent(Event event, User user) {
        BadmintonEvent badmintonEvent = new BadmintonEvent();
        badmintonEvent.setId(event.getId());
        Date eventStartTime = event.getStartTime();
        badmintonEvent.setStartTime(eventStartTime.getTime());
        badmintonEvent.setDay(new SimpleDateFormat("yyyyMMdd").format(eventStartTime));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(eventStartTime);
        String[] weekdays = new String[]{"", "周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        badmintonEvent.setWeekday(weekdays[calendar.get(Calendar.DAY_OF_WEEK)]);
        badmintonEvent.setDay2(new SimpleDateFormat("yyyy-MM-dd").format(eventStartTime));
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String startTime = timeFormat.format(eventStartTime);
        String endTime = timeFormat.format(event.getEndTime());
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
     * @param id 活动id
     * @param sid 用户session id
     * @return 该id对应的活动
     */
    @RequestMapping("enroll/event")
    public RespBody getEventById(@RequestParam int id, @RequestParam String sid) {
        Event event = eventService.getEventById(id);
        User user = miniProgramService.getUserFromSessionId(sid);
        if (event != null) {
            return RespBody.isOk().data(getBadmintonEvent(event, user));
        }
        return RespBody.isFail();
    }

    /**
     * 获取某次活动的参加列表
     * @param id 活动id
     * @param sid 用户session id
     * @return 该活动的参加列表
     */
    @RequestMapping("enroll/list")
    public RespBody getEnrollListByEvent(@RequestParam int id, @RequestParam String sid) {
        Event event = eventService.getEventById(id);
        User user = miniProgramService.getUserFromSessionId(sid);
        if (event != null) {
            return RespBody.isOk().data(getBadmintonEnroll(event, user));
        }
        return RespBody.isFail();
    }

    /**
     * 获取某次活动的参与列表
     * @param event 活动对象
     * @param user 微信用户
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
     * @param eventId 要参与的活动id
     * @param sid 要参与的用户session id
     * @return 参与结果
     */
    @RequestMapping("user/enroll")
    public RespBody<?> userEnroll(@RequestParam int eventId, @RequestParam String sid) {
        Event event = eventService.getEventById(eventId);
        User user = miniProgramService.getUserFromSessionId(sid);
        if (event != null && user != null) {
            enrollService.createEnroll(event, user);
            return RespBody.isOk();
        }
        return RespBody.isFail();
    }

    /**
     * 用户撤销参与活动
     * @param eventId 要撤销的活动id
     * @param sid 要撤销的用户session id
     * @return 撤销结果
     */
    @RequestMapping("user/cancel")
    public RespBody<?> userCancel(@RequestParam int eventId, @RequestParam String sid) {
        Event event = eventService.getEventById(eventId);
        User user = miniProgramService.getUserFromSessionId(sid);
        if (event != null && user != null) {
            if (enrollService.checkEnroll(event, user)) {
                enrollService.deleteEnrollByEventAndUser(event, user);
                return RespBody.isOk();
            } else {
                return RespBody.isFail();
            }
        }
        return RespBody.isFail();
    }

    /**
     * 构造器注入需要组件
     * @param eventService 活动服务类
     * @param enrollService 报名服务类
     * @param userService 用户服务类
     * @param miniProgramService 小程序的服务类
     */
    public BadmintonController(EventService eventService, EnrollService enrollService,
                               UserService userService, MiniProgramService miniProgramService) {
        this.eventService = eventService;
        this.enrollService = enrollService;
        this.userService = userService;
        this.miniProgramService = miniProgramService;
    }

    private final EventService eventService;
    private final EnrollService enrollService;
    private final UserService userService;
    private final MiniProgramService miniProgramService;
    private static final Logger logger = LoggerFactory.getLogger(BadmintonController.class);
}

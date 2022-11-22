package cc.bearvalley.badminton.product.controller;

import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.entity.Enroll;
import cc.bearvalley.badminton.entity.Event;
import cc.bearvalley.badminton.service.EnrollService;
import cc.bearvalley.badminton.service.EventService;
import cc.bearvalley.badminton.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 后台相关的Controller
 */
@Controller
@RequestMapping("/manage")
public class AdminController {

    /**
     * 根据页码获取活动列表
     * @param page 页码
     * @return 该页码的活动列表
     */
    @RequestMapping("/event/list")
    public ModelAndView getBadmintonListPage(@RequestParam(required = false, defaultValue = "1") int page) {
        Map<String, Object> model = new HashMap<>();
        Page<Event> list = eventService.listAllEvents(page-1);
        StringBuilder enrollInfo = new StringBuilder();
        for (Event event : list.getContent()) {
            enrollInfo.append("enroll").append(event.getId())
                    .append(":").append(enrollService.countEnrollByEvent(event))
                    .append(":").append(event.getId())
                    .append("|");
        }
        enrollInfo.deleteCharAt(enrollInfo.length()-1);
        model.put("enroll", enrollInfo.toString());
        model.put("list", list);
        model.put("page", page);
        return new ModelAndView("badminton/list", model);
    }

    /**
     * 添加活动的页面
     * @return 添加活动的页面
     */
    @RequestMapping("/event/add")
    public ModelAndView getBadmintonAddPage() {
        Event event = new Event();
        Page<Event> list = eventService.listAllEvents(0);
        if (list.getContent().size() > 0) {
            event = list.getContent().get(0);
        }
        return new ModelAndView("badminton/add", "event", event);
    }

    /**
     * 添加活动的操作
     * @param stadium     活动场馆
     * @param fields      活动场地
     * @param fee         人均费用
     * @param amount      参加人数
     * @param day         活动日期
     * @param startHour   活动开始小时
     * @param startMinute 活动开始分钟
     * @param endHour     活动结束小时
     * @param endMinute   活动结束分钟
     * @return 添加活动的结果
     */
    @ResponseBody
    @RequestMapping("/event/add/action")
    public RespBody addBadmintonEvent(@RequestParam String stadium, @RequestParam String fields,
                               @RequestParam int fee, @RequestParam int amount, @RequestParam String day,
                               @RequestParam int startHour, @RequestParam int startMinute,
                               @RequestParam int endHour, @RequestParam int endMinute) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(day);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, startHour);
            calendar.set(Calendar.MINUTE, startMinute);
            Date startTime = new Date(calendar.getTimeInMillis());
            calendar.set(Calendar.HOUR_OF_DAY, endHour);
            calendar.set(Calendar.MINUTE, endMinute);
            Date endTime = new Date(calendar.getTimeInMillis());
            Event event = eventService.createEvent(stadium, fields, fee, amount, startTime, endTime);
            if (event != null) {
                return RespBody.isOk();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RespBody.isFail();
    }

    /**
     * 修改活动的页面
     * @param id 活动id
     * @return 该id对应的活动的修改页面
     */
    @RequestMapping("/event/edit")
    public ModelAndView getBadmintonList(@RequestParam int id) {
        Map<String, Object> model = new HashMap<>();
        Event event = eventService.getEventById(id);
        model.put("event", event);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(event.getStartTime());
        int startHour = calendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = calendar.get(Calendar.MINUTE);
        model.put("startHour", startHour);
        model.put("startMinute", startMinute);
        calendar.setTime(event.getEndTime());
        int endHour = calendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = calendar.get(Calendar.MINUTE);
        model.put("endHour", endHour);
        model.put("endMinute", endMinute);
        return new ModelAndView("badminton/edit", model);
    }

    /**
     * 修改活动的操作
     * @param id          要修改的活动id
     * @param stadium     活动场馆
     * @param fields      活动场地
     * @param fee         人均费用
     * @param amount      参加人数
     * @param day         活动日期
     * @param startHour   活动开始小时
     * @param startMinute 活动开始分钟
     * @param endHour     活动结束小时
     * @param endMinute   活动结束分钟
     * @return 修改活动的结果
     */
    @ResponseBody
    @RequestMapping("/event/edit/action")
    public RespBody editBadmintonEvent(@RequestParam int id, @RequestParam String stadium, @RequestParam String fields,
                                @RequestParam int fee, @RequestParam int amount, @RequestParam String day,
                                @RequestParam int startHour, @RequestParam int startMinute,
                                @RequestParam int endHour, @RequestParam int endMinute) {
        try {
            Event event = eventService.getEventById(id);
            if (event != null) {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(day);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, startHour);
                calendar.set(Calendar.MINUTE, startMinute);
                Date startTime = new Date(calendar.getTimeInMillis());
                calendar.set(Calendar.HOUR_OF_DAY, endHour);
                calendar.set(Calendar.MINUTE, endMinute);
                Date endTime = new Date(calendar.getTimeInMillis());
                eventService.editEvent(event, stadium, fields, fee, amount, startTime, endTime);
                return RespBody.isOk();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RespBody.isFail();
    }

    /**
     * 对活动的状态进行反转操作
     * @param id 要修改的活动id
     * @return 状态反转操作操作结果
     */
    @ResponseBody
    @RequestMapping("/event/status/edit")
    public RespBody editEventStatus(@RequestParam int id) {
        if (eventService.toggleEventStatusById(id)) {
            return RespBody.isOk();
        }
        return RespBody.isFail();
    }

    /**
     * 获取某个活动的报名用户列表
     * @param id 要查询的活动id
     * @return 该活动id对应的所有报名的用户
     */
    @ResponseBody
    @RequestMapping("/event/enroll/list")
    public RespBody getEnrollListByEventId(@RequestParam int id) {
        List<Enroll> enrollList = enrollService.getEnrollListByEvent(eventService.getEventById(id));
        return RespBody.isOk().data(enrollList);
    }

    /**
     * 取消某个的报名
     * @param id 要取消的报名id
     * @return 取消的结果
     */
    @ResponseBody
    @RequestMapping("/event/enroll/cancel")
    public RespBody cancelEnrollById(@RequestParam int id) {
        enrollService.deleteEnrollById(id);
        return RespBody.isOk();
    }

    /**
     * 展示所有的用户
     * @return 用户页面
     */
    @ResponseBody
    @RequestMapping("/user/list")
    public ModelAndView getAllUserList() {
        return new ModelAndView("badminton/user", "list", userService.listAllUser());
    }

    /**
     * 构造器注入需要组件
     * @param eventService 活动服务类
     * @param enrollService 报名服务类
     * @param userService 用户服务类
     */
    public AdminController(EventService eventService, EnrollService enrollService, UserService userService) {
        this.eventService = eventService;
        this.enrollService = enrollService;
        this.userService = userService;
    }

    private final EventService eventService;
    private final EnrollService enrollService;
    private final UserService userService;
}

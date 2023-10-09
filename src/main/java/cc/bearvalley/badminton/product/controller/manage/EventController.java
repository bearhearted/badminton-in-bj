package cc.bearvalley.badminton.product.controller.manage;

import cc.bearvalley.badminton.common.Const;
import cc.bearvalley.badminton.common.ErrorEnum;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.entity.Event;
import cc.bearvalley.badminton.entity.challenge.Battle;
import cc.bearvalley.badminton.product.bo.admin.PageEnrollInfo;
import cc.bearvalley.badminton.product.bo.admin.PageEventInfo;
import cc.bearvalley.badminton.service.BattleService;
import cc.bearvalley.badminton.service.EnrollService;
import cc.bearvalley.badminton.service.EventService;
import cc.bearvalley.badminton.util.DateUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理后台活动相关的控制器
 */
@Controller
@RequestMapping("/manage/event/")
public class EventController {

    /**
     * 显示活动列表第一页
     *
     * @return 第一页活动列表
     */
    @GetMapping("list")
    public ModelAndView getBadmintonListPage() {
        return getBadmintonListPage(1);
    }

    /**
     * 根据页码显示活动列表
     *
     * @param page 页码
     * @return 该页码的活动列表
     */
    @GetMapping("list/{page}")
    public ModelAndView getBadmintonListPage(@PathVariable int page) {
        if (page < 1) {
            page = 1;
        }
        String sortColumnName = "startTime";
        // 根据传入的页面，按默认的每页数量获取该页包含的活动列表
        Pageable pageable = PageRequest.of(page - 1, Const.DEFAULT_ADMIN_PAGE_SIZE,
                Sort.Direction.DESC, sortColumnName);
        Page<Event> list = eventService.listEvents(pageable);
        List<PageEventInfo> pageList = list.stream().map(event -> {
            PageEventInfo pageEventInfo = new PageEventInfo();
            pageEventInfo.setIndex(pageable.getPageNumber() * pageable.getPageSize());
            pageEventInfo.setTime(DateUtil.getPageTimeStr(event.getStartTime(), event.getEndTime()));
            pageEventInfo.setStadium(event.getStadium());
            pageEventInfo.setFields(event.getFields());
            pageEventInfo.setFee(event.getFee());
            pageEventInfo.setAmount(event.getAmount());
            pageEventInfo.setEnrollNum(enrollService.countEnrollByEvent(event));
            pageEventInfo.setId(event.getId());
            pageEventInfo.setCheckedStr(event.getChecked() == Event.CheckEnum.NO.getValue() ? Event.CheckEnum.NO.getDesc() : Event.CheckEnum.YES.getDesc());
            if (event.getStatus() == Event.StatusEnum.OFF.getValue()) {
                // 状态为隐藏的时候
                pageEventInfo.setColor(Const.COLOR_GREY);
                pageEventInfo.setStatusStr(Event.StatusEnum.OFF.getDesc());
                pageEventInfo.setStatusToggle("恢复显示");
                pageEventInfo.setToggleUrl("show");
            } else {
                // 状态为显示的时候
                pageEventInfo.setColor(Const.COLOR_BLACK);
                pageEventInfo.setStatusStr(Event.StatusEnum.ON.getDesc());
                pageEventInfo.setStatusToggle("隐藏");
                pageEventInfo.setToggleUrl("hide");
            }
            return pageEventInfo;
        }).collect(Collectors.toList());
        Map<String, Object> model = new HashMap<>(2);
        model.put("list", list);
        model.put("pageList", pageList);
        return new ModelAndView("badminton/list", model);
    }

    /**
     * 添加活动的页面
     *
     * @return 添加活动的页面
     */
    @GetMapping("create")
    public ModelAndView createBadmintonEventPage() {
        // 按时间倒序找到最新的活动
        Event event = eventService.listLeastRecentEvent();
        if (event == null) {
            // 如果是空，说明还没有添加过活动，这个时候为了页面显示，需要创建个新的对象
            event = new Event();
        }
        return new ModelAndView("badminton/add", "event", event);
    }

    /**
     * 添加活动的操作
     *
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
    @PostMapping("create/action")
    public RespBody<?> createBadmintonEventAction(@RequestParam String stadium,
                                                  @RequestParam String fields, @RequestParam int fee,
                                                  @RequestParam int amount, @RequestParam String day,
                                                  @RequestParam int startHour, @RequestParam int startMinute,
                                                  @RequestParam int endHour, @RequestParam int endMinute) {
        // 先把传入的日期字符串转化为日期对象，如果错误直接返回错误页面
        Date date = DateUtil.getDateFromStr(day);
        if (date == null) {
            return RespBody.isFail().msg(ErrorEnum.DATE_FORMAT_ERROR);
        }
        // 根据填写的时间，得到开始时间和结束时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, startMinute);
        Date startTime = new Date(calendar.getTimeInMillis());
        calendar.set(Calendar.HOUR_OF_DAY, endHour);
        calendar.set(Calendar.MINUTE, endMinute);
        Date endTime = new Date(calendar.getTimeInMillis());
        // 创建活动
        return eventService.createEvent(stadium, fields, fee, amount, startTime, endTime);
    }

    /**
     * 修改活动的页面
     *
     * @param id 活动id
     * @return 该id对应的活动的修改页面
     */
    @GetMapping("{id}/edit")
    public ModelAndView getBadmintonList(@PathVariable int id) {
        // 先检查传入的是否为合法的活动id，不合法直接跳转错误页面
        Event event = eventService.getEventById(id);
        if (event == null) {
            return new ModelAndView(Const.ERROR_PAGE, Const.ERROR_PAGE_MESSAGE, ErrorEnum.EVENT_NOT_FOUND.getMessage());
        }
        // 从开始时间和结束时间里获取开始的小时和分钟数，结束的小时和分钟数
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(event.getStartTime());
        int startHour = calendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = calendar.get(Calendar.MINUTE);
        calendar.setTime(event.getEndTime());
        int endHour = calendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = calendar.get(Calendar.MINUTE);
        // 将上面的信息传入页面
        Map<String, Object> model = new HashMap<>(5);
        model.put("event", event);
        model.put("startHour", startHour);
        model.put("startMinute", startMinute);
        model.put("endHour", endHour);
        model.put("endMinute", endMinute);
        return new ModelAndView("badminton/edit", model);
    }

    /**
     * 修改活动的操作
     *
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
    @PostMapping("{id}/edit/action")
    public RespBody<?> editBadmintonEvent(@PathVariable int id, @RequestParam String stadium,
                                          @RequestParam String fields, @RequestParam int fee,
                                          @RequestParam int amount, @RequestParam String day,
                                          @RequestParam int startHour, @RequestParam int startMinute,
                                          @RequestParam int endHour, @RequestParam int endMinute) {
        // 先检查传入的是否为合法的活动id，不合法直接返回错误
        Event event = eventService.getEventById(id);
        if (event == null) {
            return RespBody.isFail().msg(ErrorEnum.EVENT_NOT_FOUND);
        }
        // 先把传入的日期字符串转化为日期对象，不合法直接返回错误
        Date date = DateUtil.getDateFromStr(day);
        if (date == null) {
            return RespBody.isFail().msg(ErrorEnum.DATE_FORMAT_ERROR);
        }
        // 根据填写的时间，得到新的开始时间和结束时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, startMinute);
        Date startTime = new Date(calendar.getTimeInMillis());
        calendar.set(Calendar.HOUR_OF_DAY, endHour);
        calendar.set(Calendar.MINUTE, endMinute);
        Date endTime = new Date(calendar.getTimeInMillis());
        // 修改活动内容
        return eventService.editEvent(event, stadium, fields, fee, amount, startTime, endTime);
    }

    /**
     * 设置活动的状态为隐藏
     *
     * @param id 要修改的活动id
     * @return 状态反转操作操作结果
     */
    @ResponseBody
    @PostMapping("{id}/status/hide")
    public RespBody<?> hideEvent(@PathVariable int id) {
        // 先检查传入的是否为合法的活动id，不合法直接返回错误
        Event event = eventService.getEventById(id);
        if (event == null) {
            return RespBody.isFail().msg(ErrorEnum.EVENT_NOT_FOUND);
        }
        // 对状态进行转换操作
        return eventService.editEventStatus(event, Event.StatusEnum.OFF);
    }

    /**
     * 设置活动的状态为显示
     *
     * @param id 要修改的活动id
     * @return 状态反转操作操作结果
     */
    @ResponseBody
    @PostMapping("{id}/status/show")
    public RespBody<?> showEventStatus(@PathVariable int id) {
        // 先检查传入的是否为合法的活动id，不合法直接返回错误
        Event event = eventService.getEventById(id);
        if (event == null) {
            return RespBody.isFail().msg(ErrorEnum.EVENT_NOT_FOUND);
        }
        // 对状态进行转换操作
        return eventService.editEventStatus(event, Event.StatusEnum.ON);
    }

    /**
     * 获取某个活动的报名用户列表
     *
     * @param id 要查询的活动id
     * @return 该活动id对应的所有报名的用户
     */
    @ResponseBody
    @PostMapping("{id}/enroll/list")
    public RespBody<?> getEnrollListByEventId(@PathVariable int id) {
        return RespBody.isOk().data(enrollService.getEnrollListByEvent(eventService.getEventById(id)));
    }

    /**
     * 取消某个的报名
     *
     * @param id 要取消的报名id
     * @return 取消的结果
     */
    @ResponseBody
    @PostMapping("/enroll/{id}/cancel")
    public RespBody<?> cancelEnrollById(@PathVariable int id) {
        return enrollService.deleteEnrollById(id);
    }

    /**
     * 获取某个活动的报名用户列表
     *
     * @param id 要查询的活动id
     * @return 该活动id对应的所有报名的用户
     */
    @ResponseBody
    @GetMapping("{id}/enroll")
    public ModelAndView getEnrollListPageByEventId(@PathVariable int id) {
        // 先检查传入的是否为合法的活动id，不合法直接跳转错误页面
        Event event = eventService.getEventById(id);
        if (event == null) {
            return new ModelAndView(Const.ERROR_PAGE, Const.ERROR_PAGE_MESSAGE, ErrorEnum.EVENT_NOT_FOUND.getMessage());
        }
        // 获取活动下的所有报名信息
        List<PageEnrollInfo> enrollList = enrollService.getEnrollListByEvent(eventService.getEventById(id)).stream().map(enroll -> {
            PageEnrollInfo pageEnrollInfo = new PageEnrollInfo();
            pageEnrollInfo.setId(enroll.getId());
            pageEnrollInfo.setUsername(enroll.getUser().getNickname());
            pageEnrollInfo.setAvatar(enroll.getUser().getAvatar());
            Date startTime = enroll.getEvent().getStartTime();
            Date enrollTime = enroll.getEnrollTime();
            if (startTime.getTime() - enrollTime.getTime() > 72 * 60 * 60 * 1000) {
                pageEnrollInfo.setEnrollTime(DateUtil.getDateTimeStr(enrollTime) + "(>72小时)");
                pageEnrollInfo.setPoint("3分");
            } else {
                pageEnrollInfo.setEnrollTime(DateUtil.getDateTimeStr(enrollTime) + "(<72小时)");
                pageEnrollInfo.setPoint("2分");
            }
            return pageEnrollInfo;
        }).collect(Collectors.toList());
        Map<String, Object> model = new HashMap<>(2);
        model.put("event", event);
        model.put("list", enrollList);
        return new ModelAndView("badminton/check", model);
    }

    /**
     * 获取某个活动的报名用户列表
     *
     * @param id 要查询的活动id
     * @return 该活动的报名用户列表
     */
    @ResponseBody
    @PostMapping("{id}/enroll/check")
    public RespBody<?> checkEnrollListPageByEventId(@PathVariable int id, @RequestParam(value = "enrolls[]") int[] enrolls) {
        // 先检查传入的是否为合法的活动id，不合法直接跳转错误页面
        Event event = eventService.getEventById(id);
        if (event == null) {
            return RespBody.isFail().msg(ErrorEnum.EVENT_NOT_FOUND.getMessage());
        }
        if (ObjectUtils.isEmpty(enrolls)) {
            return RespBody.isFail().msg(ErrorEnum.DATA_ERROR.getMessage());
        }
        // 开始进行确认
        return eventService.checkEnroll(event, enrolls);
    }

    /**
     * 展示某次活动下的所有的对战
     *
     * @param id 活动id
     * @return 该次活动下的所有的对战
     */
    @GetMapping("{id}/battle")
    public ModelAndView getBattleList(@PathVariable int id) {
        // 先检查传入的是否为合法的活动id，不合法直接跳转错误页面
        Event event = eventService.getEventById(id);
        if (event == null) {
            return new ModelAndView(Const.ERROR_PAGE, Const.ERROR_PAGE_MESSAGE, ErrorEnum.EVENT_NOT_FOUND.getMessage());
        }
        // 获取活动下的所有对战信息
        Map<String, Object> model = new HashMap<>(3);
        model.put("event", event);
        model.put("list", battleService.listBattlesByEvent(event));
        model.put("results", Battle.ResultEnum.values());
        return new ModelAndView("badminton/battle", model);
    }

    /**
     * 展示某次活动下的所有的对战
     *
     * @param id 活动id
     * @return 该次活动下的所有的对战
     */
    @ResponseBody
    @PostMapping("battle/result")
    public RespBody<?> setBattleResult(@RequestParam int id, @RequestParam int result) {
        // 先检查传入的是否为合法的活动id，不合法直接跳转错误页面
        Battle battle = battleService.getBattleById(id);
        if (battle == null) {
            return RespBody.isFail().msg(ErrorEnum.DATA_NOT_FOUND);
        }
        Battle.ResultEnum resultEnum = Battle.ResultEnum.findResultByValue(result);
        if (resultEnum == null) {
            return RespBody.isFail().msg(ErrorEnum.DATA_NOT_FOUND);
        }
        return battleService.editBattleResult(battle, resultEnum);
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param battleService 羽毛球挑战的数据服务类
     * @param enrollService 羽毛球报名的数据服务类
     * @param eventService  羽毛球活动的数据服务类
     */
    public EventController(BattleService battleService, EnrollService enrollService, EventService eventService) {
        this.battleService = battleService;
        this.enrollService = enrollService;
        this.eventService = eventService;
    }

    private final BattleService battleService;
    private final EnrollService enrollService;
    private final EventService eventService;
}

package cc.bearvalley.badminton.service;

import cc.bearvalley.badminton.dao.EventDao;
import cc.bearvalley.badminton.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 羽毛球活动的服务类
 */
@Service
public class EventService {

    /**
     * 创建一个羽毛球活动
     * @param stadium   活动场馆
     * @param fields    活动场地
     * @param fee       人均费用
     * @param amount    活动人数
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 创建的羽毛球活动
     */
    public Event createEvent(String stadium, String fields, int fee, int amount, Date startTime, Date endTime) {
        Event event = new Event();
        event.setStadium(stadium);
        event.setFields(fields);
        event.setFee(fee);
        event.setAmount(amount);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setStatus(Event.STATUS.ON.getValue());
        event.setCreator(null);
        return eventDao.save(event);
    }

    public Event getEventById(int eventId) {
        return eventDao.findById(eventId).orElse(null);
    }

    /**
     * 获取所有可以显示的活动
     * @return 所有可以显示的活动
     */
    public List<Event> listAllAvailableEvents() {
        return eventDao.findAllByStatusOrderByStartTimeAsc(Event.STATUS.ON.getValue());
    }

    /**
     * 分页获取所有活动
     * @param page 要获取的页码
     * @return 改页码下的活动列表
     */
    public Page<Event> listAllEvents(int page) {
        // 默认一页显示多少个羽毛球活动
        int PAGE_SIZE = 10;
        // 排序的列名
        String COLUMN_NAME = "startTime";
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, Sort.Direction.DESC, COLUMN_NAME);
        return eventDao.findAll(pageRequest);
    }

    /**
     * 修改一个羽毛球活动
     * @param stadium   活动场馆
     * @param fields    活动场地
     * @param fee       人均费用
     * @param amount    活动人数
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 修改后的羽毛球活动
     */
    public Event editEvent(Event event, String stadium, String fields, int fee, int amount, Date startTime, Date endTime) {
        event.setStadium(stadium);
        event.setFields(fields);
        event.setFee(fee);
        event.setAmount(amount);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setCreator(null);
        return eventDao.save(event);
    }

    /**
     * 将活动的状态进行反转，显示<->隐藏
     * @param id 要修改的活动
     * @return 修改成功返回<code>true</code>，否则返回<code>false</code>
     */
    public boolean toggleEventStatusById(int id) {
        Event event = eventDao.findById(id).orElse(null);
        if (event != null) {
            if (event.getStatus() == Event.STATUS.ON.getValue()) {
                event.setStatus(Event.STATUS.OFF.getValue());
            } else {
                event.setStatus(Event.STATUS.ON.getValue());
            }
            eventDao.save(event);
            return true;
        }
        return false;
    }

    /**
     * 构造器注入需要组件
     * @param eventDao 活动的数据操作类
     */
    public EventService(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    private final EventDao eventDao;
}

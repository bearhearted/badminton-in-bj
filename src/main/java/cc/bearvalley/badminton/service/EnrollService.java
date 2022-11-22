package cc.bearvalley.badminton.service;

import cc.bearvalley.badminton.dao.EnrollDao;
import cc.bearvalley.badminton.entity.Enroll;
import cc.bearvalley.badminton.entity.Event;
import cc.bearvalley.badminton.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 羽毛球报名的服务类
 */
@Service
public class EnrollService {

    /**
     * 检查某个报名是否存在
     * @param event 要报名的活动
     * @param user 要报名的用户
     * @return 如果存在返回<code>true</code>，不存在返回<code>false</code>
     */
    public boolean checkEnroll(Event event, User user) {
        return enrollDao.countByEventAndUser(event, user) > 0;
    }

    /**
     * 获取某个活动的报名人数
     * @param event 要查询的活动
     * @return 该活动的报名人数
     */
    public int countEnrollByEvent(Event event) {
        return enrollDao.countByEvent(event);
    }

    /**
     * 创建一个报名
     * @param event 要报名的活动
     * @param user 要报名的用户
     * @return 该用户对该活动的报名
     */
    public Enroll createEnroll(Event event, User user) {
        String key = "enroll" + event.getId() + ":" + user;
        if (redisTemplate.opsForHash().hasKey("createEnroll", key)) {
            return null;
        }
        redisTemplate.opsForHash().put("createEnroll", key, "1");
        Enroll enroll = null;
        if (enrollDao.countByEventAndUser(event, user) == 0) {
            enroll = new Enroll();
            enroll.setEvent(event);
            enroll.setUser(user);
            enroll.setEnrollTime(new Date(System.currentTimeMillis()));
            enrollDao.save(enroll);
        }
        redisTemplate.opsForHash().delete("createEnroll", key);
        return enroll;
    }

    /**
     * 获取一个活动的所有报名
     * @param event 要查询的活动
     * @return 该活动的所有报名
     */
    public List<Enroll> getEnrollListByEvent(Event event) {
        return enrollDao.findAllByEventOrderByEnrollTime(event);
    }

    /**
     * 获取一个用户的最新10个报名
     * @param event 要查询的用户
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
     * @param id 要删除的报名id
     */
    public void deleteEnrollByEventAndUser(Event event, User user) {
        enrollDao.deleteByEventAndUser(event, user);
    }

    /**
     * 删除某个报名
     * @param id 要删除的报名id
     */
    public void deleteEnrollById(long id) {
        enrollDao.deleteById(id);
    }

    /**
     * 构造器注入需要组件
     * @param enrollDao 报名的数据操作类
     */
    public EnrollService(EnrollDao enrollDao, RedisTemplate<String, String> redisTemplate) {
        this.enrollDao = enrollDao;
        this.redisTemplate = redisTemplate;
    }

    private final RedisTemplate<String, String> redisTemplate;
    private final EnrollDao enrollDao;
}

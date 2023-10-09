package cc.bearvalley.badminton.dao;

import cc.bearvalley.badminton.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 羽毛球活动的数据操作类
 */
public interface EventDao extends JpaRepository<Event, Integer> {
    List<Event> findAllByStatusOrderByStartTimeAsc(int status);

    Event findFirstByOrderByCreateTimeDesc();
}

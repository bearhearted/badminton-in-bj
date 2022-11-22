package cc.bearvalley.badminton.dao;

import cc.bearvalley.badminton.entity.Enroll;
import cc.bearvalley.badminton.entity.Event;
import cc.bearvalley.badminton.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 羽毛球报名的数据操作类
 */
public interface EnrollDao extends JpaRepository<Enroll, Long> {
    List<Enroll> findAllByEventOrderByEnrollTime(Event event);

    Page<Enroll> findAllByUser(User user, Pageable pageable);
    int countByEventAndUser(Event event, User user);
    int countByEvent(Event event);
    @Transactional
    void deleteByEventAndUser(Event event, User user);
}

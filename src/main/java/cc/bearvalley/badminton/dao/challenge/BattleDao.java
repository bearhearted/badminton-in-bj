package cc.bearvalley.badminton.dao.challenge;

import cc.bearvalley.badminton.entity.Event;
import cc.bearvalley.badminton.entity.challenge.Battle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 羽毛球约战的数据操作类
 */
public interface BattleDao extends JpaRepository<Battle, Integer> {
    List<Battle> findAllByEvent(Event event);
}

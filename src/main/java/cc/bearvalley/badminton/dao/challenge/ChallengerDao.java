package cc.bearvalley.badminton.dao.challenge;

import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.challenge.Battle;
import cc.bearvalley.badminton.entity.challenge.Challenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 羽毛球约战的参与者的数据操作类
 */
public interface ChallengerDao extends JpaRepository<Challenger, Integer> {
    int countByUserAndBattle(User user, Battle battle);

    int countByBattle(Battle battle);

    List<Challenger> findAllByBattle(Battle battle);

    @Modifying
    @Transactional
    void deleteByUserAndBattle(User user, Battle battle);
}

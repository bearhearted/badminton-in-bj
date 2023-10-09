package cc.bearvalley.badminton.dao.challenge;

import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.challenge.Battle;
import cc.bearvalley.badminton.entity.challenge.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 羽毛球约战投票的数据操作类
 */
public interface VoteDao extends JpaRepository<Vote, Integer> {
    List<Vote> findAllByBattle(Battle battle);

    int countByUserAndBattle(User user, Battle battle);

    @Modifying
    @Transactional
    void deleteByUserAndBattle(User user, Battle battle);

    @Modifying
    @Transactional
    void deleteByBattle(Battle battle);
}

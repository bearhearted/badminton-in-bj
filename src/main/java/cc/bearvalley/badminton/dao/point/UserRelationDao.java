package cc.bearvalley.badminton.dao.point;

import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.point.UserRelation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户推荐关系的数据操作类
 */
public interface UserRelationDao extends JpaRepository<UserRelation, Integer> {
    UserRelation findByUser(User user);

    int countByUser(User user);
}

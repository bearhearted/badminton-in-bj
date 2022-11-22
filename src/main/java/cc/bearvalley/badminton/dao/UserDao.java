package cc.bearvalley.badminton.dao;

import cc.bearvalley.badminton.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户信息的数据操作类
 */
public interface UserDao extends JpaRepository<User, Long> {
    User findByOpenid(String openid);
}

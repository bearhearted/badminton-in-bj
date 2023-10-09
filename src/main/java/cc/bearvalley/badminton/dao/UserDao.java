package cc.bearvalley.badminton.dao;

import cc.bearvalley.badminton.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 用户信息的数据操作类
 */
public interface UserDao extends JpaRepository<User, Integer> {
    User findByOpenid(String openid);

    List<User> findAllByOrderByRatioDesc();

    List<User> findAllByStatus(int status);

    Page<User> findAllByStatus(int status, Pageable pageable);

    Page<User> findAllByNicknameContains(String name, Pageable pageable);
}

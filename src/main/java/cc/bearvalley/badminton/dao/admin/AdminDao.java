package cc.bearvalley.badminton.dao.admin;

import cc.bearvalley.badminton.entity.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 管理后台登录账号的数据操作类
 */
public interface AdminDao extends JpaRepository<Admin, Integer> {
    List<Admin> findAllByOrderById();

    Admin findByUsername(String username);

    Admin findByMobile(String mobile);

    int countByMobile(String mobile);

    int countByUsername(String username);

    int countByUsernameAndIdNot(String username, int id);

    int countByMobileAndIdNot(String mobile, int id);
}

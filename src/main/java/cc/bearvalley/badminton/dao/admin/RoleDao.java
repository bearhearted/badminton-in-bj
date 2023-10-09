package cc.bearvalley.badminton.dao.admin;

import cc.bearvalley.badminton.entity.admin.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 角色的数据操作类
 */
public interface RoleDao extends JpaRepository<Role, Integer> {
    int countByName(String name);

    int countByNameAndIdNot(String name, int id);
}

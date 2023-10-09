package cc.bearvalley.badminton.dao.admin;

import cc.bearvalley.badminton.entity.admin.Role;
import cc.bearvalley.badminton.entity.admin.RolePrivilege;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 角色和权限绑定的数据操作类
 */
public interface RolePrivilegeDao extends JpaRepository<RolePrivilege, Integer> {
    int countByRoleAndAndPrivilege(Role role, String privilege);

    List<RolePrivilege> findAllByRole(Role role);

    @Transactional
    void deleteAllByRoleAndPrivilege(Role role, String value);
}

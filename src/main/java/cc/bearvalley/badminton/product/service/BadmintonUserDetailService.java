package cc.bearvalley.badminton.product.service;

import cc.bearvalley.badminton.dao.admin.AdminDao;
import cc.bearvalley.badminton.dao.admin.RolePrivilegeDao;
import cc.bearvalley.badminton.entity.admin.Admin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 加载后台用户的服务类
 */
@Service
public class BadmintonUserDetailService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("start to load admin user with name = {}", username);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // 从数据库中取出用户信息
        Admin admin = adminDao.findByUsername(username);
        if (admin == null) {
            admin = adminDao.findByMobile(username);
        }
        logger.info("admin user with name = {} is {}", username, admin);
        // 判断用户是否存在
        if (admin == null) {
            logger.info("admin is null, return exception");
            throw new UsernameNotFoundException("用户名不存在");
        }
        if (admin.getStatus() != Admin.StatusEnum.ON.getValue()) {
            logger.info("admin status is {}, return exception", admin.getStatus());
            throw new UsernameNotFoundException("用户名不存在");
        }
        logger.info("start to load privileges from roles of this admin user");
        // 添加权限
        admin.getRoles().forEach(role ->
                rolePrivilegeDao.findAllByRole(role).forEach(rolePrivilege ->
                        authorities.add(new SimpleGrantedAuthority(rolePrivilege.getPrivilege()))
                )
        );
        logger.info("admin user {} has privilege {}", admin, authorities);
        // 返回UserDetails实现类
        return new User(admin.getUsername(), admin.getPassword(), authorities);
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param adminDao         管理后台登录账号的数据操作类
     * @param rolePrivilegeDao 角色和权限绑定的数据操作类
     */
    public BadmintonUserDetailService(AdminDao adminDao, RolePrivilegeDao rolePrivilegeDao) {
        this.adminDao = adminDao;
        this.rolePrivilegeDao = rolePrivilegeDao;
    }

    private final AdminDao adminDao;
    private final RolePrivilegeDao rolePrivilegeDao;
    private final Logger logger = LogManager.getLogger("serviceLogger");
}

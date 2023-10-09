package cc.bearvalley.badminton.service;

import cc.bearvalley.badminton.common.ErrorEnum;
import cc.bearvalley.badminton.common.RedisKey;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.dao.admin.AdminDao;
import cc.bearvalley.badminton.dao.admin.RoleDao;
import cc.bearvalley.badminton.dao.admin.RolePrivilegeDao;
import cc.bearvalley.badminton.entity.admin.Admin;
import cc.bearvalley.badminton.entity.admin.Log;
import cc.bearvalley.badminton.entity.admin.Role;
import cc.bearvalley.badminton.entity.admin.RolePrivilege;
import cc.bearvalley.badminton.util.PrivilegeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 后台账号管理相关的数据服务类
 */
@Service
public class AdminService {

    /**
     * 根据id查找登录账号
     *
     * @param id 登录账号id
     * @return 对应的登录账号对象，没找到返回<code>null</code>
     */
    public Admin getAdminById(Integer id) {
        return adminDao.findById(id).orElse(null);
    }

    /**
     * 根据登录名查找管理员账号
     *
     * @param username 登录名
     * @return 查找到的管理员账号，没找到返回<code>null</code>
     */
    public Admin findByUsername(String username) {
        return adminDao.findByUsername(username);
    }

    /**
     * 查询所有的登录账号
     *
     * @return 登录账号的列表
     */
    public List<Admin> listAllAdmin() {
        return adminDao.findAllByOrderById();
    }

    /**
     * 创建一个登录账号
     *
     * @param username 登录id
     * @param realname 真实姓名
     * @param mobile   手机号
     */
    public RespBody<?> saveAdmin(String username, String realname, String mobile) {
        logger.info("start to create admin with username = {}, realname = {}, mobile = {}", username, realname, mobile);
        String lockName = RedisKey.ADMIN_EDIT_ADMIN_LOCK_KEY;
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, check constriction");
        int count = adminDao.countByUsername(username);
        logger.info("admin with username = {} count = {}", username, count);
        if (count > 0) {
            logger.info("username {} is not unique", username);
            return RespBody.isFail().msg("该登录名已经存在");
        }
        count = adminDao.countByMobile(mobile);
        logger.info("admin with mobile = {} count = {}", mobile, count);
        if (count > 0) {
            logger.info("mobile {} is not unique", mobile);
            return RespBody.isFail().msg("该手机号已经存在");
        }
        logger.info("check passed, start to save");
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setRealname(realname);
        admin.setMobile(mobile);
        admin.setPassword(passwordEncoder.encode(getDefaultPassword(admin)));
        admin.setStatus(Admin.StatusEnum.ON.getValue());
        adminDao.save(admin);
        logger.info("admin {} is saved", admin);
        logService.recordAdminLog(Log.ActionEnum.ADD_ADMIN, "", admin);
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 获取默认的密码，使用用户名的前四位和手机号的前6位
     *
     * @param admin 要获取默认密码的用户
     * @return 默认的密码
     */
    private String getDefaultPassword(Admin admin) {
        return admin.getUsername().substring(0, Math.min(admin.getUsername().length(), 4)) + "#" +
                admin.getMobile().substring(0, Math.min(admin.getMobile().length(), 6));
    }

    /**
     * 修改登录账号信息
     *
     * @param admin    登录账号
     * @param username 登录id
     * @param realname 真实姓名
     * @param mobile   手机号
     */
    public RespBody<?> updateAdmin(Admin admin, String username, String realname, String mobile) {
        logger.info("start to update admin {} with username = {}, realname = {}, mobile = {}",
                admin, username, realname, mobile);
        String lockName = RedisKey.ADMIN_EDIT_ADMIN_LOCK_KEY + admin.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, check constriction");
        int count = adminDao.countByUsernameAndIdNot(username, admin.getId());
        logger.info("admin with username = {} count = {}", username, count);
        if (count > 0) {
            logger.info("username {} is not unique", username);
            return RespBody.isFail().msg("该登录名已经存在");
        }
        count = adminDao.countByMobileAndIdNot(mobile, admin.getId());
        logger.info("admin with mobile = {} count = {}", mobile, count);
        if (count > 0) {
            logger.info("mobile {} is not unique", mobile);
            return RespBody.isFail().msg("该手机号已经存在");
        }
        logger.info("check passed, start to save");
        String oldAdmin = admin.toString();
        admin.setUsername(username);
        admin.setRealname(realname);
        admin.setMobile(mobile);
        adminDao.save(admin);
        logger.info("admin {} is saved", admin);
        logService.recordAdminLog(Log.ActionEnum.EDIT_ADMIN, oldAdmin, admin);
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 设置账号的状态为启用
     *
     * @param admin 要启用的账号
     */
    public RespBody<?> enableAdmin(Admin admin) {
        logger.info("start to enable admin {}", admin);
        String lockName = RedisKey.ADMIN_EDIT_ADMIN_LOCK_KEY + admin.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("check passed, start to enable");
        String oldStatus = Admin.StatusEnum.findStatusByValue(admin.getStatus()).getName();
        admin.setStatus(Admin.StatusEnum.ON.getValue());
        adminDao.save(admin);
        logger.info("admin {} is saved", admin);
        logService.recordAdminLog(Log.ActionEnum.ADD_ADMIN, oldStatus, Admin.StatusEnum.ON.getName());
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 设置账号的状态为禁用
     *
     * @param admin 要禁用的账号
     */
    public RespBody<?> disableAdmin(Admin admin) {
        logger.info("start to enable admin {}", admin);
        String lockName = RedisKey.ADMIN_EDIT_ADMIN_LOCK_KEY + admin.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("check passed, start to enable");
        String oldStatus = Admin.StatusEnum.findStatusByValue(admin.getStatus()).getName();
        admin.setStatus(Admin.StatusEnum.OFF.getValue());
        adminDao.save(admin);
        logger.info("admin {} is saved", admin);
        logService.recordAdminLog(Log.ActionEnum.ADD_ADMIN, oldStatus, Admin.StatusEnum.OFF.getName());
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 根据角色id查询角色
     *
     * @param id 角色id
     * @return 角色对象，没找到返回<code>null</code>
     */
    public Role getRoleById(Integer id) {
        return roleDao.findById(id).orElse(null);
    }

    /**
     * 创建一个角色
     *
     * @param name 角色名
     */
    public RespBody<?> saveRole(String name) {
        logger.info("start to create role with name = {}", name);
        String lockName = RedisKey.ADMIN_EDIT_ROLE_LOCK_KEY;
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, check constriction");
        int count = roleDao.countByName(name);
        logger.info("role with name = {} count = {}", name, count);
        if (count > 0) {
            logger.info("name {} is not unique", name);
            return RespBody.isFail().msg("该角色名已经存在");
        }
        logger.info("check passed, start to save");
        Role role = new Role();
        role.setName(name);
        roleDao.save(role);
        logger.info("role {} is saved", role);
        logService.recordAdminLog(Log.ActionEnum.ADD_ROLE, "", role);
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 更新一个角色名称
     *
     * @param role 要更新的角色
     * @param name 角色名称
     */
    public RespBody<?> updateRole(Role role, String name) {
        logger.info("start to edit role {} with name = {}", role, name);
        String lockName = RedisKey.ADMIN_EDIT_ROLE_LOCK_KEY + role.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, check constriction");
        int count = roleDao.countByNameAndIdNot(name, role.getId());
        logger.info("role with name = {} count = {}", name, count);
        if (count > 0) {
            logger.info("name {} is not unique", name);
            return RespBody.isFail().msg("该角色名已经存在");
        }
        logger.info("check passed, start to save");
        String oldRole = role.toString();
        role.setName(name);
        roleDao.save(role);
        logger.info("role {} is saved", role);
        logService.recordAdminLog(Log.ActionEnum.EDIT_ROLE, oldRole, role);
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 获取所有的角色列表
     *
     * @return 角色列表
     */
    public List<Role> listAllRoles() {
        return roleDao.findAll();
    }

    /**
     * 获取某个登录账号和所有角色的关系
     *
     * @param admin 登录账号
     * @return 账号和角色的绑定关系列表
     */
    public List<Role> listAllRoles(Admin admin) {
        return roleDao.findAll();
    }

    /**
     * 将角色绑定/解绑到登录账号上
     *
     * @param admin 登录账号
     * @param role  角色
     */
    public RespBody<?> toggleAdminRole(Admin admin, Role role) {
        logger.info("start to toggle role {} with admin = {}", role, admin);
        String lockName = RedisKey.ADMIN_EDIT_ROLE_LOCK_KEY + admin.getId() + ":" + role.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to check this action is add or delete");
        boolean contains = admin.getRoles().stream().anyMatch(r -> r.getId() == role.getId());
        logger.info("admin {} contains role {} is {}", admin, role, contains);
        if (contains) {
            logger.info("contains to delete");
            logger.info("before delete, admin has {} roles", admin.getRoles().size());
            admin.setRoles(admin.getRoles().stream().filter(r -> r.getId() != role.getId()).collect(Collectors.toSet()));
            logger.info("after delete. admin has {} roles", admin.getRoles().size());
        } else {
            logger.info("not contains to add");
            logger.info("before add, admin has {} roles", admin.getRoles().size());
            admin.getRoles().add(role);
            logger.info("after add. admin has {} roles", admin.getRoles().size());
        }
        logger.info("start to save");
        adminDao.save(admin);
        logger.info("admin {} is saved", admin);
        if (contains) {
            logService.recordAdminLog(Log.ActionEnum.UNBIND_ROLE, role, "");
        } else {
            logService.recordAdminLog(Log.ActionEnum.BIND_ROLE, "", role);
        }
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 查找权限绑定的数量
     *
     * @param role      角色
     * @param privilege 权限
     * @return 角色权限绑定的数量
     */
    public int countRolePrivilegeByRoleAndPrivilege(Role role, String privilege) {
        return rolePrivilegeDao.countByRoleAndAndPrivilege(role, privilege);
    }


    /**
     * 获取某个角色和所有权限的关系
     *
     * @param role 角色
     * @return 角色和权限的绑定关系列表
     */
    public List<RolePrivilege> listPrivilegeByRole(Role role) {
        return rolePrivilegeDao.findAllByRole(role);
    }

    /**
     * 将权限绑定/解绑到角色里
     *
     * @param role          角色
     * @param privilegeEnum 权限
     */
    public RespBody<?> toggleRolePrivilege(Role role, PrivilegeEnum privilegeEnum) {
        logger.info("start to toggle role {} with privilegeEnum = {}", role, privilegeEnum);
        String lockName = RedisKey.ADMIN_EDIT_ROLE_LOCK_KEY + role.getId() + ":" + privilegeEnum.getValue();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to check this action is add or delete");
        int count = countRolePrivilegeByRoleAndPrivilege(role, privilegeEnum.getValue());
        logger.info("role {} contains privilegeEnum {} is {}", role, privilegeEnum, count);
        if (count == 0) {
            logger.info("not contains to add");
            logger.info("start to add");
            RolePrivilege rolePrivilege = new RolePrivilege();
            rolePrivilege.setRole(role);
            rolePrivilege.setPrivilege(privilegeEnum.getValue());
            rolePrivilegeDao.save(rolePrivilege);
            logger.info("rolePrivilege {} is saved", rolePrivilege);
            logService.recordAdminLog(Log.ActionEnum.BIND_PRIVILEGE, "", privilegeEnum + "->" + role);
        } else {
            logger.info("contains to delete");
            rolePrivilegeDao.deleteAllByRoleAndPrivilege(role, privilegeEnum.getValue());
            logger.info("delete done");
            logService.recordAdminLog(Log.ActionEnum.UNBIND_PRIVILEGE, privilegeEnum + "->" + role, "");
        }
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 修改用户密码
     *
     * @param admin       要修改密码的用户对象
     * @param oldPassword 用户原密码
     * @param newPassword 要修改的新密码
     * @return 修改结果
     */
    public RespBody<?> editPassword(Admin admin, String oldPassword, String newPassword) {
        logger.info("start to edit password of admin {}", admin);
        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            logger.info("old password error, return");
            return RespBody.isFail().msg(ErrorEnum.ORIGINAL_PASSWORD_ERROR);
        }
        String lockName = RedisKey.PASSWORD_EDIT_LOCK_KEY + admin.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to edit");
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminDao.save(admin);
        logger.info("edit finished");
        logService.recordAdminLog(Log.ActionEnum.EDIT_PASSWORD, admin, admin);
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 重置用户密码
     *
     * @param admin 要重置密码的用户
     * @return 重置结果
     */
    public RespBody<?> resetPassword(Admin admin) {
        logger.info("start to reset password of admin {}", admin);
        String lockName = RedisKey.PASSWORD_EDIT_LOCK_KEY + admin.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to reset");
        admin.setPassword(passwordEncoder.encode(getDefaultPassword(admin)));
        adminDao.save(admin);
        logger.info("reset finished");
        logService.recordAdminLog(Log.ActionEnum.RESET_PASSWORD, admin, admin);
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 判断一个用户的密码是否为默认密码
     *
     * @param admin 要判断的用户
     * @return 是默认密码返回<code>true</code>，否则返回<code>false</code>
     */
    public boolean checkDefaultPassword(Admin admin) {
        return passwordEncoder.matches(getDefaultPassword(admin), admin.getPassword());
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param redisTemplate    操作redis的工具类
     * @param adminDao         管理后台登录账号的数据操作类
     * @param roleDao          角色的数据操作类
     * @param rolePrivilegeDao 角色和权限绑定的数据操作类
     * @param logService       操作日志的数据服务类
     * @param passwordEncoder  密码加密器
     */
    public AdminService(RedisTemplate<String, String> redisTemplate,
                        AdminDao adminDao,
                        RoleDao roleDao,
                        RolePrivilegeDao rolePrivilegeDao,
                        LogService logService,
                        PasswordEncoder passwordEncoder) {
        this.redisTemplate = redisTemplate;
        this.adminDao = adminDao;
        this.roleDao = roleDao;
        this.rolePrivilegeDao = rolePrivilegeDao;
        this.logService = logService;
        this.passwordEncoder = passwordEncoder;
    }

    private final RedisTemplate<String, String> redisTemplate;
    private final AdminDao adminDao;
    private final RoleDao roleDao;
    private final RolePrivilegeDao rolePrivilegeDao;
    private final LogService logService;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LogManager.getLogger("serviceLogger");
}

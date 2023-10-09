package cc.bearvalley.badminton.product.controller.manage;

import cc.bearvalley.badminton.common.Const;
import cc.bearvalley.badminton.common.ErrorEnum;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.entity.admin.Admin;
import cc.bearvalley.badminton.entity.admin.Role;
import cc.bearvalley.badminton.entity.admin.RolePrivilege;
import cc.bearvalley.badminton.product.bo.admin.PageAdminInfo;
import cc.bearvalley.badminton.product.bo.admin.PageAdminRoleInfo;
import cc.bearvalley.badminton.product.bo.admin.PagePrivilegeInfo;
import cc.bearvalley.badminton.service.AdminService;
import cc.bearvalley.badminton.service.LogService;
import cc.bearvalley.badminton.util.PrivilegeEnum;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 后台账号管理的控制器
 */
@Controller
@RequestMapping("/manage/account/")
public class AdminController {

    /**
     * 用户修改密码的页面
     *
     * @return 修改密码的页面地址
     */
    @GetMapping("/password/edit")
    public String resetPassword() {
        return "admin/edit_password";
    }

    /**
     * 用户修改密码的操作
     *
     * @param user 登录用户的信息
     * @param op   old password 用户填写的旧密码
     * @param np   new password 用户要修改的新密码
     * @return 修改密码的结果
     */
    @ResponseBody
    @PostMapping("/password/edit/action")
    public RespBody<?> resetPasswordAction(@AuthenticationPrincipal UserDetails user, @RequestParam String op, @RequestParam String np) {
        if (user == null) {
            return RespBody.isFail().msg(ErrorEnum.LOGIN_STATUS_ERROR);
        }
        Admin admin = adminService.findByUsername(user.getUsername());
        if (admin == null) {
            return RespBody.isFail().msg(ErrorEnum.ADMIN_ACCOUNT_NOT_FOUND);
        }
        return adminService.editPassword(admin, op, np);
    }

    /**
     * 获取用户列表的界面
     *
     * @return 跳转View信息
     */
    @GetMapping("list")
    @PreAuthorize("hasAuthority('edit_admin')")
    public ModelAndView list() {
        List<PageAdminInfo> list = adminService.listAllAdmin().stream().map(admin -> {
            PageAdminInfo pageAdminInfo = new PageAdminInfo();
            pageAdminInfo.setId(admin.getId());
            pageAdminInfo.setUsername(admin.getUsername());
            pageAdminInfo.setRealname(admin.getRealname());
            pageAdminInfo.setMobile(admin.getMobile());
            pageAdminInfo.setStatus(admin.getStatus());
            pageAdminInfo.setStatusStr(Admin.StatusEnum.findStatusByValue(admin.getStatus()).getName());
            pageAdminInfo.setColor(Admin.StatusEnum.OFF.getValue() == admin.getStatus() ? Const.COLOR_GREY : Const.COLOR_BLACK);
            return pageAdminInfo;
        }).collect(Collectors.toList());
        return new ModelAndView("admin/admin_list", "list", list);
    }

    /**
     * 添加用户的界面
     *
     * @return 添加用户的页面地址
     */
    @GetMapping("create")
    @PreAuthorize("hasAuthority('edit_admin')")
    public String addAdmin() {
        return "admin/add_admin";
    }

    /**
     * 添加用户的操作
     *
     * @param username 用户名
     * @param realname 真实姓名
     * @param mobile   用户手机号
     * @return 创建成功返回<code>0</code>
     */
    @PostMapping("create/action")
    @PreAuthorize("hasAuthority('edit_admin')")
    @ResponseBody
    public RespBody<?> addAdminAction(@RequestParam String username, @RequestParam String realname, @RequestParam String mobile) {
        return adminService.saveAdmin(username, realname, mobile);
    }

    /**
     * 修改用户的界面
     *
     * @param id 用户id
     * @return 跳转View信息
     */
    @GetMapping("{id}/edit")
    @PreAuthorize("hasAuthority('edit_admin')")
    public ModelAndView editAdmin(@PathVariable Integer id) {
        Admin admin = adminService.getAdminById(id);
        if (admin == null) {
            return new ModelAndView(Const.ERROR_PAGE, Const.ERROR_PAGE_MESSAGE, ErrorEnum.ADMIN_ACCOUNT_NOT_FOUND.getMessage());
        }
        return new ModelAndView("admin/edit_admin", "admin", admin);
    }

    /**
     * 修改用户的操作
     *
     * @param id       要修改的用户id
     * @param username 要修改的用户名
     * @param realname 要修改的真实姓名
     * @param mobile   要修改的用户手机号
     * @return 修改成功返回<code>0</code>
     */
    @PostMapping("{id}/edit/action")
    @PreAuthorize("hasAuthority('edit_admin')")
    @ResponseBody
    public RespBody<?> editAdminAction(@PathVariable Integer id, @RequestParam String username, @RequestParam String realname, @RequestParam String mobile) {
        Admin admin = adminService.getAdminById(id);
        if (admin == null) {
            return RespBody.isFail().msg(ErrorEnum.ADMIN_ACCOUNT_NOT_FOUND);
        }
        return adminService.updateAdmin(admin, username, realname, mobile);
    }

    /**
     * 启用用户
     *
     * @param id 要启用的用户id
     * @return 启用结果
     */
    @PostMapping("{id}/enable")
    @PreAuthorize("hasAuthority('edit_admin')")
    @ResponseBody
    public RespBody<?> enableAdmin(@PathVariable Integer id) {
        Admin admin = adminService.getAdminById(id);
        if (admin == null) {
            return RespBody.isFail().msg(ErrorEnum.ADMIN_ACCOUNT_NOT_FOUND);
        }
        return adminService.enableAdmin(admin);
    }

    /**
     * 禁用用户
     *
     * @param id 要禁用的用户id
     * @return 禁用结果
     */
    @PostMapping("{id}/disable")
    @PreAuthorize("hasAuthority('edit_admin')")
    @ResponseBody
    public RespBody<?> disableAdmin(@PathVariable Integer id) {
        Admin admin = adminService.getAdminById(id);
        if (admin == null) {
            return RespBody.isFail().msg(ErrorEnum.ADMIN_ACCOUNT_NOT_FOUND);
        }
        return adminService.disableAdmin(admin);
    }

    /**
     * 用户重置密码的操作
     *
     * @param id 要重置密码的用户id
     * @return 重置密码的结果
     */
    @ResponseBody
    @PostMapping("{id}/password/reset")
    @PreAuthorize("hasAuthority('reset_password')")
    public RespBody<?> resetPasswordAction(@PathVariable int id) {
        Admin admin = adminService.getAdminById(id);
        if (admin == null) {
            return RespBody.isFail().msg(ErrorEnum.ADMIN_ACCOUNT_NOT_FOUND);
        }
        return adminService.resetPassword(admin);
    }

    /**
     * 账号绑定角色的界面
     *
     * @param id 要绑定角色的账号id
     * @return 跳转View信息
     */
    @GetMapping("{id}/role/bind")
    @PreAuthorize("hasAuthority('edit_admin')")
    public ModelAndView addRole(@PathVariable Integer id) {
        Admin admin = adminService.getAdminById(id);
        if (admin == null) {
            return new ModelAndView(Const.ERROR_PAGE, Const.ERROR_PAGE_MESSAGE, ErrorEnum.ADMIN_ACCOUNT_NOT_FOUND.getMessage());
        }
        Map<String, Object> model = new HashMap<>(2);
        model.put("admin", admin);
        model.put("list", adminService.listAllRoles(admin).stream().map(role -> PageAdminRoleInfo.of(admin, role)).collect(Collectors.toList()));
        return new ModelAndView("admin/admin_role", model);
    }

    /**
     * 账号绑定/解绑角色的操作
     *
     * @param id     要操作的账号id
     * @param roleId 要操作的角色id
     * @return 绑定/解绑的操作结果
     */
    @PostMapping("{id}/role/{roleId}/bind")
    @PreAuthorize("hasAuthority('edit_admin')")
    @ResponseBody
    public RespBody<?> toggleAdminRole(@PathVariable Integer id, @PathVariable Integer roleId) {
        Admin admin = adminService.getAdminById(id);
        if (admin == null) {
            return RespBody.isFail().msg(ErrorEnum.ADMIN_ACCOUNT_NOT_FOUND);
        }
        Role role = adminService.getRoleById(roleId);
        if (role == null) {
            return RespBody.isFail().msg(ErrorEnum.ROLE_NOT_FOUND);
        }
        return adminService.toggleAdminRole(admin, role);
    }

    /**
     * 添加角色的界面
     *
     * @return 添加角色的页面地址
     */
    @GetMapping("role/create")
    @PreAuthorize("hasAuthority('edit_admin')")
    public String addRole() {
        return "admin/add_role";
    }

    /**
     * 添加角色的操作
     *
     * @param name 添加角色的名称
     * @return 添加角色的操作结果
     */
    @PostMapping("role/create/action")
    @PreAuthorize("hasAuthority('edit_admin')")
    @ResponseBody
    public RespBody<?> addRoleAction(@RequestParam String name) {
        return adminService.saveRole(name);
    }

    /**
     * 获取角色列表界面
     *
     * @return 跳转View信息
     */
    @GetMapping("role/list")
    @PreAuthorize("hasAuthority('edit_admin')")
    public ModelAndView getRoleList() {
        return new ModelAndView("admin/role_list", "list", adminService.listAllRoles());
    }

    /**
     * 修改角色的界面
     *
     * @param id 要修改的角色id
     * @return 跳转View信息
     */
    @GetMapping("role/{id}/edit")
    @PreAuthorize("hasAuthority('edit_admin')")
    public ModelAndView editRole(@PathVariable Integer id) {
        Role role = adminService.getRoleById(id);
        if (role == null) {
            return new ModelAndView(Const.ERROR_PAGE, Const.ERROR_PAGE_MESSAGE, ErrorEnum.ROLE_NOT_FOUND.getMessage());
        }
        return new ModelAndView("admin/edit_role", "role", role);
    }

    /**
     * 修改角色的操作
     *
     * @param id   要修改的角色id
     * @param name 要修改的角色名称
     * @return 成功返回<code>0</code>
     */
    @PostMapping("role/{id}/edit/action")
    @PreAuthorize("hasAuthority('edit_admin')")
    @ResponseBody
    public RespBody<?> editRoleAction(@PathVariable Integer id, @RequestParam String name) {
        Role role = adminService.getRoleById(id);
        if (role == null) {
            return RespBody.isFail().msg(ErrorEnum.ROLE_NOT_FOUND);
        }
        return adminService.updateRole(role, name);
    }

    /**
     * 给角色添加权限的页面
     *
     * @param id 要修改权限的角色id
     * @return 跳转View信息
     */
    @GetMapping("role/{id}/privilege/bind")
    @PreAuthorize("hasAuthority('edit_admin')")
    public ModelAndView listPrivilege(@PathVariable Integer id) {
        Role role = adminService.getRoleById(id);
        if (role == null) {
            return new ModelAndView(Const.ERROR_PAGE, Const.ERROR_PAGE_MESSAGE, ErrorEnum.ROLE_NOT_FOUND.getMessage());
        }
        Set<String> privilegeSet = adminService.listPrivilegeByRole(role).stream().map(RolePrivilege::getPrivilege).collect(Collectors.toSet());
        List<PagePrivilegeInfo> list = Arrays.stream(PrivilegeEnum.values()).map(p -> PagePrivilegeInfo.of(p, privilegeSet)).collect(Collectors.toList());
        Map<String, Object> model = new HashMap<>(2);
        model.put("role", role);
        model.put("list", list);
        return new ModelAndView("admin/privilege_list", model);
    }

    /**
     * 将权限和角色的绑定/解绑操作
     *
     * @param id    要修改权限的角色id
     * @param value 要修改的权限值
     * @return 绑定/解绑的操作结果
     */
    @PostMapping("role/{id}/privilege/{value}/bind")
    @PreAuthorize("hasAuthority('edit_admin')")
    @ResponseBody
    public RespBody<?> privilegeAction(@PathVariable Integer id, @PathVariable String value) {
        Role role = adminService.getRoleById(id);
        PrivilegeEnum privilegeEnum = PrivilegeEnum.findPrivilegeByValue(value);
        if (role == null || privilegeEnum == null) {
            return RespBody.isFail().msg(ErrorEnum.ROLE_NOT_FOUND);
        }
        return adminService.toggleRolePrivilege(role, privilegeEnum);
    }

    /**
     * 查看操作记录的页面
     *
     * @return 跳转View信息
     */
    @GetMapping("log/list")
    public ModelAndView listPrivilege() {
        Pageable pageable = PageRequest.of(0, 20,
                Sort.Direction.DESC, "createTime");
        Map<String, Object> model = new HashMap<>(1);
        model.put("list", logService.listAllLog(pageable).toList());
        return new ModelAndView("admin/log_list", model);
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param adminService 后台账号管理相关的数据服务类
     * @param logService   操作日志的数据服务类
     */
    public AdminController(AdminService adminService, LogService logService) {
        this.adminService = adminService;
        this.logService = logService;
    }

    private final AdminService adminService;
    private final LogService logService;
}

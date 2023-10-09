package cc.bearvalley.badminton.product.controller;

import cc.bearvalley.badminton.common.Const;
import cc.bearvalley.badminton.common.ErrorEnum;
import cc.bearvalley.badminton.entity.admin.Admin;
import cc.bearvalley.badminton.service.AdminService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 默认的控制器
 */
@Controller
public class IndexController {

    /**
     * 首页
     *
     * @return 首页
     */
    @RequestMapping("/")
    public ModelAndView index(@AuthenticationPrincipal UserDetails user) {
        if (user == null) {
            return new ModelAndView(Const.ERROR_PAGE, Const.ERROR_PAGE_MESSAGE, ErrorEnum.LOGIN_STATUS_ERROR.getMessage());
        }
        Admin admin = adminService.findByUsername(user.getUsername());
        if (admin == null) {
            return new ModelAndView(Const.ERROR_PAGE, Const.ERROR_PAGE_MESSAGE, ErrorEnum.ADMIN_ACCOUNT_NOT_FOUND.getMessage());
        }
        String path = "manage/event/list";
        if (adminService.checkDefaultPassword(admin)) {
            path = "manage/account/password/edit";
        }
        return new ModelAndView("index", "path", path);
    }

    /**
     * 登录页
     *
     * @return 登录页
     */
    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param adminService 后台账号管理相关的数据服务类
     */
    public IndexController(AdminService adminService) {
        this.adminService = adminService;
    }

    private final AdminService adminService;
}

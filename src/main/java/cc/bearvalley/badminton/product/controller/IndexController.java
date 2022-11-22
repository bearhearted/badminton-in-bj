package cc.bearvalley.badminton.product.controller;

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
     * @return 首页
     */
    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    /**
     * 登录页
     * @return 登录页
     */
    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }
}

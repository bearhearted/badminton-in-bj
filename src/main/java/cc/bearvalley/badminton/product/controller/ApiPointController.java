package cc.bearvalley.badminton.product.controller;

import cc.bearvalley.badminton.common.Const;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台积分相关的api
 */
@RestController
@RequestMapping("/api/point")
public class ApiPointController {

    /**
     * 获取用户的积分列表
     *
     * @param p 页码
     * @return 用户的积分列表
     */
    @PostMapping("/user/list")
    public RespBody<?> listUserPoint(@RequestParam(required = false, defaultValue = "0") int p) {
        if (p < 0) {
            p = 0;
        }
        Pageable pageable = PageRequest.of(p, Const.DEFAULT_ADMIN_PAGE_SIZE, Sort.Direction.DESC, "point", "id");
        return RespBody.isOk().data(userService.listUserCompleted(pageable));
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param userService  用户相关的数据服务类
     */
    public ApiPointController(UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;
}

package cc.bearvalley.badminton.product.controller.api;

import cc.bearvalley.badminton.common.Const;
import cc.bearvalley.badminton.common.ErrorEnum;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.point.Item;
import cc.bearvalley.badminton.product.service.MiniProgramService;
import cc.bearvalley.badminton.product.vo.PageVo;
import cc.bearvalley.badminton.product.vo.UserSidVo;
import cc.bearvalley.badminton.service.PointService;
import cc.bearvalley.badminton.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * 积分商城相关的控制类
 */
@RestController
@RequestMapping("/api/store/")
public class StoreController {

    /**
     * 获取首页的积分商品列表信息
     *
     * @param vo 页码参数
     * @return 获取首页的积分商品列表信息
     */
    @PostMapping("list")
    public RespBody<?> showItemList(@RequestBody PageVo vo) {
        Pageable pageable = PageRequest.of(vo.getPage(), Const.DEFAULT_ADMIN_PAGE_SIZE, Sort.Direction.ASC, "sequence");
        return RespBody.isOk().data(pointService.listItem(pageable));
    }

    /**
     * 获取某个积分商品的信息
     *
     * @param id 要获取的积分商品信息
     * @return 该id对应的积分商品信息
     */
    @PostMapping("item/{id}")
    public RespBody<?> showItem(@PathVariable int id, @RequestBody UserSidVo vo) {
        Item item = pointService.findItemById(id);
        if (item == null) {
            return RespBody.isFail().msg(ErrorEnum.ITEM_NOT_FOUND);
        }
        User user = miniProgramService.getUserFromSessionId(vo.getSid());
        if (user == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        User nowUser = userService.getUserById(user.getId());
        if (nowUser == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        return RespBody.isOk().data(pointService.getItemEntity(item, nowUser));
    }

    /**
     * 获取某个积分商品的信息
     *
     * @param id 要获取的积分商品信息
     * @return 该id对应的积分商品信息
     */
    @PostMapping("item/{id}/confirm")
    public RespBody<?> confirmBuyItem(@PathVariable int id, @RequestBody UserSidVo vo) {
        Item item = pointService.findItemById(id);
        if (item == null) {
            return RespBody.isFail().msg(ErrorEnum.ITEM_NOT_FOUND);
        }
        User user = miniProgramService.getUserFromSessionId(vo.getSid());
        if (user == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        User nowUser = userService.getUserById(user.getId());
        if (nowUser == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        return RespBody.isOk().data(pointService.getItemConfirmInfo(item, nowUser));
    }

    /**
     * 获取某个积分商品的信息
     *
     * @param id 要获取的积分商品信息
     * @return 该id对应的积分商品信息
     */
    @PostMapping("item/{id}/order")
    public RespBody<?> orderItem(@PathVariable int id, @RequestBody UserSidVo vo) {
        Item item = pointService.findItemById(id);
        if (item == null) {
            return RespBody.isFail().msg(ErrorEnum.ITEM_NOT_FOUND);
        }
        User user = miniProgramService.getUserFromSessionId(vo.getSid());
        if (user == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        User nowUser = userService.getUserById(user.getId());
        if (nowUser == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        return RespBody.isOk().data(pointService.orderItem(nowUser, item));
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param pointService       用户积分的数据服务类
     * @param userService        用户相关的数据服务类
     * @param miniProgramService 微信小程序的服务类
     */
    public StoreController(PointService pointService, UserService userService, MiniProgramService miniProgramService) {
        this.pointService = pointService;
        this.userService = userService;
        this.miniProgramService = miniProgramService;
    }

    private final PointService pointService;
    private final UserService userService;
    private final MiniProgramService miniProgramService;
}

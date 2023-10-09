package cc.bearvalley.badminton.product.controller.manage;

import cc.bearvalley.badminton.common.Const;
import cc.bearvalley.badminton.common.ErrorEnum;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.point.Item;
import cc.bearvalley.badminton.entity.point.ItemPicture;
import cc.bearvalley.badminton.product.bo.admin.PageItemInfo;
import cc.bearvalley.badminton.service.PointService;
import cc.bearvalley.badminton.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理积分相关的控制器
 */
@Controller
@RequestMapping("/manage/point/")
public class PointController {

    /**
     * 显示在线的积分商品
     *
     * @return 在线的积分商品页面
     */
    @GetMapping("item/list/online")
    public ModelAndView getOnlineItemList() {
        return getOnlineItemList(1);
    }

    /**
     * 显示离线的积分商品
     *
     * @return 离线的积分商品页面
     */
    @GetMapping("item/list/offline")
    public ModelAndView getOfflineItemList() {
        return getOfflineItemList(1);
    }

    /**
     * 显示全部的积分商品
     *
     * @return 全部的积分商品页面
     */
    @GetMapping("item/list/all")
    public ModelAndView getAllItemList() {
        return getAllItemList(1);
    }

    /**
     * 显示在线的积分商品
     *
     * @param page 页码
     * @return 在线的积分商品
     */
    @GetMapping("item/list/online/{page}")
    public ModelAndView getOnlineItemList(@PathVariable int page) {
        return getItemByPageAndStatus(page, Item.StatusEnum.ON);
    }

    /**
     * 显示离线的积分商品
     *
     * @param page 页码
     * @return 离线的积分商品页面
     */
    @GetMapping("item/list/offline/{page}")
    public ModelAndView getOfflineItemList(@PathVariable int page) {
        return getItemByPageAndStatus(page, Item.StatusEnum.OFF);
    }

    /**
     * 显示全部的积分商品
     *
     * @param page 页码
     * @return 用户页面
     */
    @GetMapping("item/list/all/{page}")
    public ModelAndView getAllItemList(@PathVariable int page) {
        return getItemByPageAndStatus(page, null);
    }

    /**
     * 根据商品的状态返回对应的积分商品列表页面
     *
     * @param page   页码
     * @param status 商品状态
     * @return 对应的积分商品列表页面
     */
    private ModelAndView getItemByPageAndStatus(int page, Item.StatusEnum status) {
        if (page < 1) {
            page = 1;
        }
        String sortColumnName = "updateTime";
        Pageable pageable = PageRequest.of(page - 1, Const.DEFAULT_ADMIN_PAGE_SIZE,
                Sort.Direction.DESC, sortColumnName);
        Page<Item> list = pointService.listItems(pageable, status);
        List<PageItemInfo> pageList = list.stream().map(item -> {
            PageItemInfo pageItemInfo = new PageItemInfo();
            pageItemInfo.setIndex(pageable.getPageNumber() * pageable.getPageSize());
            pageItemInfo.setId(item.getId());
            pageItemInfo.setName(item.getName());
            pageItemInfo.setIntro(item.getIntroduction());
            pageItemInfo.setPoint(item.getPoint());
            pageItemInfo.setAmount(item.getStock() - item.getSold());
            pageItemInfo.setSold(item.getSold());
            pageItemInfo.setStock(item.getStock());
            if (item.getStatus() == Item.StatusEnum.OFF.getValue()) {
                // 状态为下线的时候
                pageItemInfo.setColor(status == null ? Const.COLOR_GREY : Const.COLOR_BLACK);
                pageItemInfo.setStatusStr(Item.StatusEnum.OFF.getName());
                pageItemInfo.setStatusToggle("上线");
                pageItemInfo.setToggleUrl("show");
            } else {
                // 状态为在线的时候
                pageItemInfo.setColor(Const.COLOR_BLACK);
                pageItemInfo.setStatusStr(Item.StatusEnum.ON.getName());
                pageItemInfo.setStatusToggle("下线");
                pageItemInfo.setToggleUrl("hide");
            }
            return pageItemInfo;
        }).collect(Collectors.toList());
        Map<String, Object> model = new HashMap<>(3);
        model.put("list", list);
        model.put("pageList", pageList);
        String path = "all";
        if (status != null) {
            if (status.getValue() == Item.StatusEnum.ON.getValue()) {
                path = "online";
            } else if (status.getValue() == Item.StatusEnum.OFF.getValue()) {
                path = "offline";
            }
        }
        model.put("path", path);
        return new ModelAndView("point/item_list", model);
    }

    /**
     * 创建积分商品的页面
     *
     * @return 创建积分商品的页面地址
     */
    @GetMapping("item/create")
    public String createItem() {
        return "point/add_item";
    }

    /**
     * 创建一个积分商品
     *
     * @param name  商品名称
     * @param point 兑换商品所需积分
     * @param stock 商品库存
     * @param intro 商品说明
     * @return 创建结果
     */
    @ResponseBody
    @PostMapping("item/create/action")
    public RespBody<?> createItemAction(@RequestParam String name, @RequestParam int point,
                                        @RequestParam int stock, @RequestParam String intro) {
        return pointService.createItem(name, point, stock, intro);
    }

    /**
     * 修改一个积分商品的页面
     *
     * @param id 要修改的积分商品id
     * @return 修改积分商品的页面
     */
    @GetMapping("item/{id}/edit")
    public ModelAndView editItem(@PathVariable int id) {
        Item item = pointService.findItemById(id);
        if (item == null) {
            return new ModelAndView(Const.ERROR_PAGE, Const.ERROR_PAGE_MESSAGE, ErrorEnum.ITEM_NOT_FOUND.getMessage());
        }
        return new ModelAndView("point/edit_item", "item", item);
    }

    /**
     * 修改一个积分商品
     *
     * @param id    要修改的积分商品id
     * @param name  要修改的积分商品名称
     * @param point 要修改的积分商品兑换积分
     * @param stock 要修改的积分商品库存
     * @param intro 要修改的积分商品说明
     * @return 修改结果
     */
    @ResponseBody
    @PostMapping("item/{id}/edit/action")
    public RespBody<?> editItemAction(@PathVariable int id, @RequestParam String name, @RequestParam int point,
                                      @RequestParam int stock, String intro) {
        Item item = pointService.findItemById(id);
        if (item == null) {
            return RespBody.isFail().msg(ErrorEnum.ITEM_NOT_FOUND);
        }
        return pointService.editItem(item, name, point, stock, intro);
    }

    /**
     * 显示一个积分商品
     *
     * @param id 要显示的积分商品id
     * @return 修改结果
     */
    @ResponseBody
    @PostMapping("item/{id}/status/show")
    public RespBody<?> showItemAction(@PathVariable int id) {
        Item item = pointService.findItemById(id);
        if (item == null) {
            return RespBody.isFail().msg(ErrorEnum.ITEM_NOT_FOUND);
        }
        return pointService.editItemStatus(item, Item.StatusEnum.ON);
    }

    /**
     * 隐藏一个积分商品
     *
     * @param id 要隐藏的积分商品id
     * @return 隐藏结果
     */
    @ResponseBody
    @PostMapping("item/{id}/status/hide")
    public RespBody<?> hideItemAction(@PathVariable int id) {
        Item item = pointService.findItemById(id);
        if (item == null) {
            return RespBody.isFail().msg(ErrorEnum.ITEM_NOT_FOUND);
        }
        return pointService.editItemStatus(item, Item.StatusEnum.OFF);
    }

    /**
     * 获取积分商品图片列表的页面
     *
     * @param id 要获取图片列表的积分商品id
     * @return 图片列表的页面
     */
    @GetMapping("item/{id}/picture/list")
    @PreAuthorize("hasAuthority('edit_picture')")
    @ResponseBody
    public ModelAndView getItemPictureList(@PathVariable int id) {
        Item item = pointService.findItemById(id);
        if (item == null) {
            return new ModelAndView(Const.ERROR_PAGE, Const.ERROR_PAGE_MESSAGE, ErrorEnum.ITEM_NOT_FOUND.getMessage());
        }
        List<ItemPicture> list = pointService.listItemPictures(item);
        Map<String, Object> model = new HashMap<>(3);
        model.put("item", item);
        model.put("plist", list);
        int size = list.size();
        int rows = size % 4 == 0 ? size / 4 : (size / 4 + 1);
        if (rows > 0) {
            rows--;
        }
        model.put("prows", rows);
        return new ModelAndView("point/picture_list", model);
    }

    /**
     * 上传积分商品图片的操作
     *
     * @return 上传结果
     */
    @PostMapping("item/{id}/image/upload")
    @PreAuthorize("hasAuthority('edit_picture')")
    @ResponseBody
    public RespBody<?> uploadPicture(@PathVariable int id, @RequestParam("file") MultipartFile[] files) {
        if (ObjectUtils.isEmpty(files)) {
            return RespBody.isFail().msg(ErrorEnum.DATA_ERROR);
        }
        if (Arrays.stream(files).anyMatch(f -> f.getSize() > 1024 * 1024 * 10)) {
            return RespBody.isFail().msg(ErrorEnum.FILE_SIZE_EXCEED);
        }
        Item item = pointService.findItemById(id);
        if (item == null) {
            return RespBody.isFail().msg(ErrorEnum.ITEM_NOT_FOUND);
        }
        return pointService.createItemPicture(item, files);
    }

    /**
     * 删除积分商品的图片
     *
     * @param id 要删除的图片id
     * @return 删除结果
     */
    @PostMapping("item/picture/{id}/delete")
    @ResponseBody
    public RespBody<?> deleteItemPicture(@PathVariable int id) {
        return pointService.deleteItemPicture(id);
    }

    /**
     * 批量删除积分商品的图片
     *
     * @param id 多个图片id的用,链接的字符串，如123,456,789
     * @return 删除结果
     */
    @PostMapping("item/picture/delete")
    @ResponseBody
    public RespBody<?> deleteItemPictures(@RequestParam String id) {
        List<Integer> list = Arrays.stream(id.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        return pointService.deleteItemPictures(list);
    }

    /**
     * 获取用户的积分列表页面
     *
     * @return 用户的积分列表页面
     */
    @GetMapping("user/list")
    public ModelAndView getUserList() {
        return getUserList(1);
    }

    /**
     * 搜索用户的积分列表页面
     *
     * @param name 要搜索的字符串
     * @return 搜索的结果
     */
    @GetMapping("user/search/{name}")
    public ModelAndView searchUser(@PathVariable String name) {
        Map<String, Object> model = new HashMap<>();
        model.put("name", name);
        Pageable pageable = PageRequest.of(0, 1000,
                Sort.Direction.DESC, "point");
        model.put("list", userService.listUserByName(name, pageable));
        return new ModelAndView("point/user_list", model);
    }

    /**
     * 获取用户的积分列表页面
     *
     * @param page 页码
     * @return 该页码的用户的积分列表页面
     */
    @GetMapping("user/list/{page}")
    public ModelAndView getUserList(@PathVariable int page) {
        if (page < 1) {
            page = 1;
        }
        Map<String, Object> model = new HashMap<>();
        model.put("name", "");
        Pageable pageable = PageRequest.of(page - 1, Const.DEFAULT_ADMIN_PAGE_SIZE,
                Sort.Direction.DESC, "point");
        model.put("list", userService.listUserCompleted(pageable));
        return new ModelAndView("point/user_list", model);
    }

    /**
     * 修改用户积分
     *
     * @param id         要修改的用户id
     * @param point      要修改的积分
     * @param note       该次修改的备注
     * @param needRecord 是否需要记录
     * @return 修改结果
     */
    @PostMapping("edit/{id}")
    @ResponseBody
    public RespBody<?> editPoint(@PathVariable int id, @RequestParam int point, @RequestParam String note, @RequestParam boolean needRecord) {
        User user = userService.getUserById(id);
        if (user == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        return pointService.updateUserPoint(user, point, note, needRecord);
    }

    /**
     * 获取用户积分记录列表
     *
     * @return 用户积分记录列表的页面
     */
    @GetMapping("record/list")
    public ModelAndView getRecordList() {
        return getRecordList(1);
    }

    /**
     * 获取用户积分记录列表
     *
     * @param page 页码
     * @return 该页的用户积分记录列表页面
     */
    @GetMapping("record/list/{page}")
    public ModelAndView getRecordList(@PathVariable int page) {
        if (page < 1) {
            page = 1;
        }
        String sortColumnName = "updateTime";
        Pageable pageable = PageRequest.of(page - 1, Const.DEFAULT_ADMIN_PAGE_SIZE,
                Sort.Direction.DESC, sortColumnName);
        return new ModelAndView("point/record_list", "list", pointService.listPointRecord(pageable));
    }

    /**
     * 获取用户积分记录详情列表
     *
     * @param id 要查询的用户id
     * @return 该用户的积分记录详情列表页面
     */
    @GetMapping("record/{id}/detail")
    public ModelAndView getRecordDetail(@PathVariable int id) {
        return getRecordDetail(id, 1);
    }

    /**
     * 获取用户积分记录详情列表
     *
     * @param id   要查询的用户id
     * @param page 页面
     * @return 该用户的积分记录详情列表页面
     */
    @GetMapping("record/{id}/detail/{page}")
    public ModelAndView getRecordDetail(@PathVariable int id, @PathVariable int page) {
        User user = userService.getUserById(id);
        if (user == null) {
            return new ModelAndView(Const.ERROR_PAGE, Const.ERROR_PAGE_MESSAGE, ErrorEnum.USER_NOT_FOUND.getMessage());
        }
        if (page < 1) {
            page = 1;
        }
        String sortColumnName = "updateTime";
        Pageable pageable = PageRequest.of(page - 1, Const.DEFAULT_ADMIN_PAGE_SIZE,
                Sort.Direction.DESC, sortColumnName);
        Map<String, Object> model = new HashMap<>(2);
        model.put("list", pointService.listPointRecordByUser(user, pageable));
        model.put("user", user);
        return new ModelAndView("point/record_detail", model);
    }

    /**
     * 用户的订单列表
     *
     * @return 订单列表页面
     */
    @GetMapping("order/list")
    public ModelAndView getItemOrderList() {
        return getItemOrderList(1);
    }

    /**
     * 用户的订单列表
     *
     * @param page 页码
     * @return 订单列表页面
     */
    @GetMapping("order/list/{page}")
    public ModelAndView getItemOrderList(@PathVariable int page) {
        if (page < 1) {
            page = 1;
        }
        String sortColumnName = "updateTime";
        Pageable pageable = PageRequest.of(page - 1, Const.DEFAULT_ADMIN_PAGE_SIZE,
                Sort.Direction.DESC, sortColumnName);
        return new ModelAndView("point/order_list", "list", pointService.listItemOrder(pageable));
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param pointService 用户积分的数据服务类
     * @param userService  用户相关的数据服务类
     */
    public PointController(PointService pointService, UserService userService) {
        this.pointService = pointService;
        this.userService = userService;
    }

    private final PointService pointService;

    private final UserService userService;
}

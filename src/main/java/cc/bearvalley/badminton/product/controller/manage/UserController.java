package cc.bearvalley.badminton.product.controller.manage;

import cc.bearvalley.badminton.common.Const;
import cc.bearvalley.badminton.common.ErrorEnum;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.point.UserRelation;
import cc.bearvalley.badminton.product.bo.admin.PageRelationInfo;
import cc.bearvalley.badminton.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理后台用户相关的控制器
 */
@Controller
@RequestMapping("/manage/user/")
public class UserController {

    /**
     * 显示全部用户列表第一页
     *
     * @return 第一页用户列表
     */
    @GetMapping("list")
    public ModelAndView getUserList() {
        return getUserList(1);
    }

    /**
     * 根据页码显示全部用户列表
     *
     * @param page 页码
     * @return 该页码的用户列表
     */
    @GetMapping("list/{page}")
    public ModelAndView getUserList(@PathVariable int page) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, Const.DEFAULT_ADMIN_PAGE_SIZE);
        Map<String, Object> model = new HashMap<>(4);
        model.put("list", userService.listAllUser(pageable));
        model.put("status", -1);
        model.put("url", "");
        model.put("startIndex", (page - 1) * Const.DEFAULT_ADMIN_PAGE_SIZE);
        return new ModelAndView("badminton/user", model);
    }

    /**
     * 显示完整资料用户列表第一页
     *
     * @return 第一页用户列表
     */
    @GetMapping("completed/list")
    public ModelAndView getCompletedUserList() {
        return getCompletedUserList(1);
    }

    /**
     * 根据页码显示完整资料用户列表
     *
     * @param page 页码
     * @return 该页码的用户列表
     */
    @GetMapping("completed/list/{page}")
    public ModelAndView getCompletedUserList(@PathVariable int page) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, Const.DEFAULT_ADMIN_PAGE_SIZE);
        Map<String, Object> model = new HashMap<>(5);
        model.put("list", userService.listUserCompleted(pageable));
        model.put("status", User.StatusEnum.COMPLETED.getValue());
        model.put("url", "completed/");
        model.put("startIndex", (page - 1) * Const.DEFAULT_ADMIN_PAGE_SIZE);
        return new ModelAndView("badminton/user", model);
    }

    /**
     * 显示用户关联列表第一页
     *
     * @return 第一页用户列表
     */
    @GetMapping("relate/list")
    public ModelAndView getRelateUserList() {
        return getRelateUserList(1);
    }

    /**
     * 根据页码显示用户关联列表
     *
     * @param page 页码
     * @return 该页码的用户关联列表
     */
    @GetMapping("relate/list/{page}")
    public ModelAndView getRelateUserList(@PathVariable int page) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, Const.DEFAULT_ADMIN_PAGE_SIZE, Sort.Direction.DESC, "id");
        Map<String, Object> model = new HashMap<>(2);
        Page<User> userList = userService.listUserCompleted(pageable);
        List<PageRelationInfo> list = userList.stream().map(user -> {
            PageRelationInfo pageRelationInfo = new PageRelationInfo();
            pageRelationInfo.setId(user.getId());
            pageRelationInfo.setUsername(user.getNickname());
            pageRelationInfo.setAvatar(user.getAvatar());
            pageRelationInfo.setAttended(user.getAttended());
            UserRelation relation = userService.findUserRelationByUser(user);
            if (relation == null) {
                pageRelationInfo.setRelatedUserid(11);
                pageRelationInfo.setRelatedUsername(null);
            } else {
                pageRelationInfo.setRelatedUserid(relation.getIntroducer().getId());
                pageRelationInfo.setRelatedUsername(relation.getIntroducer().getNickname());
            }
            return pageRelationInfo;
        }).collect(Collectors.toList());
        model.put("list", userList);
        model.put("pageList", list);
        model.put("users", userService.listAllCompletedUser());
        model.put("startIndex", (page - 1) * Const.DEFAULT_ADMIN_PAGE_SIZE);
        return new ModelAndView("badminton/relate", model);
    }

    /**
     * 根据页码显示用户关联列表
     *
     * @param page 页码
     * @return 该页码的用户关联列表
     */
    @GetMapping("relate/search/{name}")
    public ModelAndView getRelateUserList(@PathVariable String name) {
        int page = 1;
        Pageable pageable = PageRequest.of(page - 1, Const.DEFAULT_ADMIN_PAGE_SIZE, Sort.Direction.DESC, "id");
        Map<String, Object> model = new HashMap<>(2);
        Page<User> userList = userService.listUserByName(name, pageable);
        List<PageRelationInfo> list = userList.stream().map(user -> {
            PageRelationInfo pageRelationInfo = new PageRelationInfo();
            pageRelationInfo.setId(user.getId());
            pageRelationInfo.setUsername(user.getNickname());
            pageRelationInfo.setAvatar(user.getAvatar());
            pageRelationInfo.setAttended(user.getAttended());
            UserRelation relation = userService.findUserRelationByUser(user);
            if (relation == null) {
                pageRelationInfo.setRelatedUserid(11);
                pageRelationInfo.setRelatedUsername(null);
            } else {
                pageRelationInfo.setRelatedUserid(relation.getIntroducer().getId());
                pageRelationInfo.setRelatedUsername(relation.getIntroducer().getNickname());
            }
            return pageRelationInfo;
        }).collect(Collectors.toList());
        model.put("list", userList);
        model.put("pageList", list);
        model.put("users", userService.listAllCompletedUser());
        model.put("startIndex", (page - 1) * Const.DEFAULT_ADMIN_PAGE_SIZE);
        return new ModelAndView("badminton/relate", model);
    }

    /**
     * 对用户进行关联操作
     *
     * @param id           关联的用户id
     * @param introducerId 对应的介绍人用户id
     * @return 关联操作结果
     */
    @ResponseBody
    @PostMapping("{id}/relate/{introducerId}")
    public RespBody<?> setUserRelation(@PathVariable int id, @PathVariable int introducerId) {
        User user = userService.getUserById(id);
        if (user == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        User introducer = userService.getUserById(introducerId);
        if (introducer == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        return userService.createUserRelation(user, introducer);
    }

    /**
     * 取消用户的关联操作
     *
     * @param id 要取消的用户id
     * @return 取消关联操作结果
     */
    @ResponseBody
    @PostMapping("{id}/relate/cancel")
    public RespBody<?> cancelUserRelation(@PathVariable int id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        return userService.cancelUserRelation(user);
    }

    /**
     * 确认用户参与活动
     *
     * @param id 要确认的用户id
     * @return 确认结果
     */
    @ResponseBody
    @PostMapping("{id}/relate/attend")
    public RespBody<?> setUserAttend(@PathVariable int id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        return userService.setUserAttend(user);
    }

    /**
     * 取消确认用户参与活动
     *
     * @param id 要取消的用户id
     * @return 取消结果
     */
    @ResponseBody
    @PostMapping("{id}/relate/attend/cancel")
    public RespBody<?> cancelUserAttend(@PathVariable int id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return RespBody.isFail().msg(ErrorEnum.USER_NOT_FOUND);
        }
        return userService.cancelUserAttend(user);
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param userService 用户相关的数据服务类
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;
}

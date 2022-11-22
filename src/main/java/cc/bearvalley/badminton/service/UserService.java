package cc.bearvalley.badminton.service;

import cc.bearvalley.badminton.dao.UserDao;
import cc.bearvalley.badminton.entity.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户相关的数据服务类
 */
@Service
public class UserService {

    /**
     * 根据用户id获取用户
     * @param id 用户id
     * @return 该id对应的用户对象，没有的话返回<code>null</code>
     */
    public User getUserById(Long id) {
        return userDao.findById(id).orElse(null);
    }

    /**
     * 根据用户的openid获取用户
     * @param openid 用户的openid
     * @return 该openid对应的用户对象，没有的话返回<code>null</code>
     */
    public User getUserByOpenid(String openid) {
        return userDao.findByOpenid(openid);
    }

    /**
     * 创建一个新的用户
     * @param openid 用户的openid
     * @return 创建后的新用户
     */
    public User saveUser(String openid) {
        User user = new User();
        user.setOpenid(openid);
        user.setStatus(User.STATUS.INITIAL.getValue());
        return userDao.save(user);
    }

    /**
     * 更新一个用户的昵称和头像
     * @param user 要修改的用户
     * @param nickname 要修改的昵称
     * @param avatar 要修改的头像
     * @return 修改后的用户
     */
    public User updateUser(User user, String nickname, String avatar) {
        if (!ObjectUtils.isEmpty(nickname)) {
            user.setNickname(nickname);
        }
        if (!ObjectUtils.isEmpty(avatar)) {
            user.setAvatar(avatar);
        }
        user.setAvatar(avatar);
        user.setStatus(User.STATUS.COMPLETED.getValue());
        return userDao.save(user);
    }

    /**
     * 获取所有的用户列表
     * @return 所有的用户列表
     */
    public List<User> listAllUser() {
        return userDao.findAll();
    }

    /**
     * 构造器注入需要组件
     * @param userDao 用户的数据操作类
     */
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    private final  UserDao userDao;
}

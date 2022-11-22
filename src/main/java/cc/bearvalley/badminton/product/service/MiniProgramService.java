package cc.bearvalley.badminton.product.service;

import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.product.bo.AuthorizationCode;
import cc.bearvalley.badminton.product.bo.BadmintonUser;
import cc.bearvalley.badminton.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 微信小程序的服务类
 */
@Service
public class MiniProgramService {

    /**
     * 使用微信传回来的code获取用户openid，判断该openid的用户是否存在，存在返回已有用户，不存在则创建用户后返回
     * 用户信息以bu_uuid(badminton_user)为key放在缓存中，并设置过期时间，将key作为session id和过期时间一起返回
     * @param code 小程序通过wx.login()方法获取的js code
     * @return 该用户的信息
     */
    public BadmintonUser userLogin(@RequestParam String code) {
        logger.info("js code = {}", code);
        // 根据code去wx接口获取用户的open id
        AuthorizationCode authorizationCode = getAuthorizationCodeFromCode(code);
        // 如果正常返回，且带有用户的open id
        if (authorizationCode != null && !ObjectUtils.isEmpty(authorizationCode.getOpenid())) {
            // 查询这个open id是否已经存在
            User user = userService.getUserByOpenid(authorizationCode.getOpenid());
            if (user == null) {
                // 如果不存在，则创建一个新用户
                user = userService.saveUser(authorizationCode.getOpenid());
            }
            String sessionId = "bu_" + UUID.randomUUID().toString().replace("-", "");
            long time = System.currentTimeMillis() + RESERVE_SESSION_TIME_IN_DAY * 24 * 60 * 60 * 1000;
            try {
                redisTemplate.opsForValue().set(sessionId, objectMapper.writeValueAsString(user),
                        RESERVE_SESSION_TIME_IN_DAY, TimeUnit.DAYS);
                BadmintonUser badmintonUser = new BadmintonUser();
                badmintonUser.setSessionId(sessionId);
                badmintonUser.setTime(time);
                badmintonUser.setCompleted(user.getStatus() == User.STATUS.COMPLETED.getValue());
                logger.info("return object = {}", badmintonUser);
                return badmintonUser;
            } catch (Exception ex) {
                logger.error("save user to redis error", ex);
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据code获取包括用户openId的AccessToken
     * @param code 用户通过wx.login()返回的code
     * @return 小程序的AccessToken
     */
    private AuthorizationCode getAuthorizationCodeFromCode(String code) {
        logger.info("start to fetch wechat session contains user token");
        String requestUrl = GET_SESSION_URL + code;
        logger.info("request url = {}", requestUrl);
        String sessionStr = null;
        try {
            ResponseBody body = new OkHttpClient().newCall(new Request.Builder().url(requestUrl).build()).execute().body();
            logger.info("response body = {}", body);
            if (body != null) {
                sessionStr = body.string();
                logger.info("response string = {}", sessionStr);
                body.close();
                AuthorizationCode authorizationCode = objectMapper.readValue(sessionStr, AuthorizationCode.class);
                logger.info("read object = {}", authorizationCode);
                return authorizationCode;
            }
        } catch (Exception ex) {
            logger.error("fetch wechat session error", ex);
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 根据session获取用户
     * @param sessionId sessionId
     * @return 该sessionId对应的用户，未找到返回<code>null</code>
     */
    public User getUserFromSessionId(String sessionId) {
        String userSession = redisTemplate.opsForValue().get(sessionId);
        if (!ObjectUtils.isEmpty(userSession)) {
            try {
                return objectMapper.readValue(userSession, User.class);
            } catch (Exception ex) {
                logger.error("get user from session error", ex);
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 构造器注入需要组件
     * @param redisTemplate redis操作类
     * @param objectMapper json对象操作类
     * @param userService 用户的服务类
     */
    public MiniProgramService(RedisTemplate<String, String> redisTemplate,
                              ObjectMapper objectMapper,
                              UserService userService) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    // 用户的信息在缓存中存放的时间
    private static final int RESERVE_SESSION_TIME_IN_DAY = 7;
    // 获取用户信息的url，https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html
    private static final String GET_SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?";
    private static final Logger logger = LoggerFactory.getLogger(MiniProgramService.class);
}

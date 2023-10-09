package cc.bearvalley.badminton.product.service;

import cc.bearvalley.badminton.common.RedisKey;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.product.bo.AuthorizationCode;
import cc.bearvalley.badminton.product.bo.BadmintonUser;
import cc.bearvalley.badminton.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
     *
     * @param code 小程序通过wx.login()方法获取的js code
     * @return 该用户的信息
     */
    public BadmintonUser userLogin(@RequestParam String code) {
        logger.info("user login code = {}", code);
        String lockName = RedisKey.USER_LOGIN_ACTION_LOCK_KEY + code;
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 10, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return null;
        }
        // 根据code去wx接口获取用户的open id
        AuthorizationCode authorizationCode = getAuthorizationCodeFromCode(code);
        logger.info("user authorization code = {}", authorizationCode);
        // 如果正常返回，且带有用户的open id
        if (authorizationCode != null && !ObjectUtils.isEmpty(authorizationCode.getOpenid())) {
            // 查询这个open id是否已经存在
            User user = userService.getUserByOpenid(authorizationCode.getOpenid());
            logger.info("get user from open id {} = {}", authorizationCode.getOpenid(), user);
            if (user == null) {
                logger.info("create a new user");
                // 如果不存在，则创建一个新用户
                user = userService.saveUser(authorizationCode.getOpenid());
                logger.info("new user {} has been created", user);
            }
            String sessionId = "bu_" + UUID.randomUUID().toString().replace("-", "");
            long time = System.currentTimeMillis() + sessionTime * 24 * 60 * 60 * 1000L;
            try {
                redisTemplate.opsForValue().set(sessionId, objectMapper.writeValueAsString(user),
                        sessionTime, TimeUnit.DAYS);
                logger.info("save session info to redis");
                BadmintonUser badmintonUser = new BadmintonUser();
                badmintonUser.setSessionId(sessionId);
                badmintonUser.setTime(time);
                badmintonUser.setCompleted(user.getStatus() == User.StatusEnum.COMPLETED.getValue());
                logger.info("return object = {}", badmintonUser);
                return badmintonUser;
            } catch (Exception ex) {
                logger.error("save user to redis error", ex);
                ex.printStackTrace();
            } finally {
                redisTemplate.delete(lockName);
            }
        }
        redisTemplate.delete(lockName);
        return null;
    }

    /**
     * 根据code获取包括用户openId的AccessToken
     *
     * @param code 用户通过wx.login()返回的code
     * @return 小程序的AccessToken
     */
    private AuthorizationCode getAuthorizationCodeFromCode(String code) {
        logger.info("start to fetch wechat session contains user token");
        String requestUrl = getSessionRequestUrl(code);
        logger.info("request url = {}", requestUrl);
        try {
            ResponseBody body = new OkHttpClient().newCall(new Request.Builder().url(requestUrl).build()).execute().body();
            logger.info("response body = {}", body);
            if (body != null) {
                String sessionStr = body.string();
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
     *
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
     * 获取微信用户的auth.code2Session接口地址
     *
     * @param code 用户通过 wx.login 接口获得临时登录凭证
     * @return 获取该用户信息的接口地址
     * @link <a href="https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html"></a>
     */
    private String getSessionRequestUrl(String code) {
        return getSessionUrl + "?appid=" + appId + "&secret=" + secretId + "&grant_type=" + grantType + "&js_code=" + code;
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param redisTemplate 操作redis的工具类
     * @param objectMapper  操作json的工具类
     * @param userService   用户相关的数据服务类
     * @param getSessionUrl 获取session的url
     * @param appId         小程序的app id
     * @param secretId      小程序的secret id
     * @param grantType     接口访问的app grant type
     * @param sessionTime   用户资料保存在session的时间
     */
    public MiniProgramService(RedisTemplate<String, String> redisTemplate,
                              ObjectMapper objectMapper,
                              UserService userService,
                              @Value("${wx.get-session-url}") String getSessionUrl,
                              @Value("${wx.app-id}") String appId,
                              @Value("${wx.secret-id}") String secretId,
                              @Value("${wx.grant-type}") String grantType,
                              @Value("${wx.session-time}") int sessionTime) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.getSessionUrl = getSessionUrl;
        this.appId = appId;
        this.secretId = secretId;
        this.grantType = grantType;
        this.sessionTime = sessionTime;
    }

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final String getSessionUrl;
    private final String appId;
    private final String secretId;
    private final String grantType;
    private final int sessionTime;

    private final Logger logger = LogManager.getLogger("apiLogger");
}

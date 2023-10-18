package cc.bearvalley.badminton.service;

import cc.bearvalley.badminton.dao.admin.LogDao;
import cc.bearvalley.badminton.entity.admin.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

/**
 * 操作日志的数据服务类
 */
@Service
public class LogService {

    /**
     * 记录后台的操作日志
     * @param operation 操作行为
     * @param oldContent 操作前内容
     * @param newContent 操作后内容
     */
    public void recordAdminLog(Log.ActionEnum operation, Object oldContent, Object newContent) {
        recordLog(operation, oldContent, newContent, Log.TypeEnum.ADMIN);
    }

    /**
     * 记录前端的操作日志
     * @param operation 操作行为
     * @param oldContent 操作前内容
     * @param newContent 操作后内容
     */
    public void recordApiLog(Log.ActionEnum operation, Object oldContent, Object newContent) {
        recordLog(operation, oldContent, newContent, Log.TypeEnum.API);
    }

    /**
     * 记录操作日志
     *
     * @param operation 操作行为
     * @param oldContent 操作前内容
     * @param newContent 操作后内容
     * @param type 操作类型
     */
    private void recordLog(Log.ActionEnum operation, Object oldContent, Object newContent, Log.TypeEnum type) {
        logger.info("start to record log");
        Log log = new Log();
        log.setAccount(getCurrentUsername());
        log.setOperation(operation.getName());
        log.setOldContent(oldContent.toString());
        log.setNewContent(newContent.toString());
        log.setType(type.getValue());
        logDao.save(log);
        logger.info("log (id={})is saved", log.getId());
    }

    /**
     * 获取系统的当前登录用户
     *
     * @return 登录的用户名
     */
    private String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return "NO USER";
        }
        if (authentication.getPrincipal() instanceof User) {
            User userDetails = (User) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        return authentication.getPrincipal().toString();
    }

    /**
     * 获取某页的日志列表
     *
     * @param pageable 翻页信息
     * @return 该页面的日志列表
     */
    public Page<Log> listAllLog(Pageable pageable) {
        return logDao.findAll(pageable);
    }

    /**
     * 获取某页的某种类型的日志列表
     *
     * @param type 日志类型
     * @param pageable 翻页信息
     * @return 该类型日志在某页面的列表
     */
    public Page<Log> listLogByType(int type, Pageable pageable) {
        return logDao.findAllByType(type, pageable);
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param logDao 操作日志的数据操作类
     */
    public LogService(LogDao logDao) {
        this.logDao = logDao;
    }

    private final LogDao logDao;
    private final Logger logger = LogManager.getLogger("serviceLogger");
}

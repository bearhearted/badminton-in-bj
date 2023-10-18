package cc.bearvalley.badminton.job;

import cc.bearvalley.badminton.service.PointOperationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 检查积分的定时任务
 */
@Component
public class PointCheckJob {
    /**
     * 每天0点10分开始进行过期积分检查
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void checkPoint() {
        pointOperationService.checkOverduePoint();
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param pointOperationService 对积分进行操作的服务类
     */
    public PointCheckJob(PointOperationService pointOperationService) {
        this.pointOperationService = pointOperationService;
    }

    private PointOperationService pointOperationService;
}

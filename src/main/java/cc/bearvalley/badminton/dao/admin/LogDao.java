package cc.bearvalley.badminton.dao.admin;

import cc.bearvalley.badminton.entity.admin.Log;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 操作日志的数据操作类
 */
public interface LogDao extends JpaRepository<Log, Integer> {
}

package cc.bearvalley.badminton.dao.point;

import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.point.PointRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 积分变化记录的数据操作类
 */
public interface PointRecordDao extends JpaRepository<PointRecord, Integer> {
    Page<PointRecord> getAllPointByUserOrderByCreateTimeDesc(User user, Pageable pageable);
}

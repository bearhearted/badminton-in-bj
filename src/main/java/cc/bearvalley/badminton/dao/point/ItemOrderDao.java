package cc.bearvalley.badminton.dao.point;

import cc.bearvalley.badminton.entity.point.ItemOrder;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 订单的数据操作类
 */
public interface ItemOrderDao extends JpaRepository<ItemOrder, Integer> {
}

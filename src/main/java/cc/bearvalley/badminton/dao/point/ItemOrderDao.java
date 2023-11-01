package cc.bearvalley.badminton.dao.point;

import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.point.Item;
import cc.bearvalley.badminton.entity.point.ItemOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 订单的数据操作类
 */
public interface ItemOrderDao extends JpaRepository<ItemOrder, Integer> {
    Page<ItemOrder> findAllByItem(Item item, Pageable pageable);
    Page<ItemOrder> findAllByUser(User user, Pageable pageable);
}

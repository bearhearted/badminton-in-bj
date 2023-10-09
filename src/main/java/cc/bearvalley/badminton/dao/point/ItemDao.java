package cc.bearvalley.badminton.dao.point;

import cc.bearvalley.badminton.entity.point.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 积分商品的数据操作类
 */
public interface ItemDao extends JpaRepository<Item, Integer> {
    Page<Item> findAllByStatus(int value, Pageable pageable);
}

package cc.bearvalley.badminton.dao.point;

import cc.bearvalley.badminton.entity.point.Item;
import cc.bearvalley.badminton.entity.point.ItemPicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 积分商品图片的数据操作类
 */
public interface ItemPictureDao extends JpaRepository<ItemPicture, Integer> {
    List<ItemPicture> findAllByItem(Item item);
    List<ItemPicture> findAllByItemAndPosition(Item item, int position);
    ItemPicture findByItemAndPosition(Item item, int position);
    int countByItemAndPosition(Item item, int position);
}

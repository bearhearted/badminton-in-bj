package cc.bearvalley.badminton.service;

import cc.bearvalley.badminton.common.ErrorEnum;
import cc.bearvalley.badminton.common.RedisKey;
import cc.bearvalley.badminton.common.RespBody;
import cc.bearvalley.badminton.dao.UserDao;
import cc.bearvalley.badminton.dao.point.ItemDao;
import cc.bearvalley.badminton.dao.point.ItemOrderDao;
import cc.bearvalley.badminton.dao.point.ItemPictureDao;
import cc.bearvalley.badminton.dao.point.PointRecordDao;
import cc.bearvalley.badminton.entity.Event;
import cc.bearvalley.badminton.entity.User;
import cc.bearvalley.badminton.entity.admin.Log;
import cc.bearvalley.badminton.entity.point.Item;
import cc.bearvalley.badminton.entity.point.ItemOrder;
import cc.bearvalley.badminton.entity.point.ItemPicture;
import cc.bearvalley.badminton.entity.point.PointRecord;
import cc.bearvalley.badminton.product.bo.ItemConfirmInfoEntity;
import cc.bearvalley.badminton.product.bo.StoreItemEntity;
import cc.bearvalley.badminton.product.bo.StoreItemList;
import cc.bearvalley.badminton.product.bo.admin.MyOrder;
import cc.bearvalley.badminton.product.service.CosService;
import cc.bearvalley.badminton.util.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 用户积分的数据服务类
 */
@Service
public class PointService {

    /**
     * 根据id获取积分商品
     *
     * @param id 要获取的积分商品id
     * @return 该id对应的积分商品
     */
    public Item findItemById(int id) {
        return itemDao.findById(id).orElse(null);
    }

    /**
     * 根据id获取积分商品图片
     *
     * @param id 要获取的积分商品图片id
     * @return 该id对应的积分商品图片
     */
    public ItemPicture getItemPicture(int id) {
        return itemPictureDao.findById(id).orElse(null);
    }

    /**
     * 获取某种状态在某个页面的积分商品列表
     *
     * @param pageable 要查询的页码
     * @param status   要查询的状态
     * @return 该状态在该页面的积分商品列表
     */
    public Page<Item> listItems(Pageable pageable, Item.StatusEnum status) {
        if (status == null) {
            return itemDao.findAll(pageable);
        }
        return itemDao.findAllByStatus(status.getValue(), pageable);
    }

    /**
     * 创建一个积分商品
     *
     * @param name         商品名称
     * @param point        兑换商品所需积分
     * @param stock        商品状态
     * @param introduction 商品介绍
     * @param order        商品排序
     * @return 创建结果
     */
    public RespBody<?> createItem(String name, int point, int stock, String introduction, int order) {
        logger.info("start to create item with name = {}, point = {}, stock = {}, introduction = {}, order = {}",
                name, point, stock, introduction, order);
        String lockName = RedisKey.ADMIN_EDIT_ITEM_LOCK_KEY;
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to save");
        Item item = new Item();
        item.setName(name);
        item.setPoint(point);
        item.setStock(stock);
        item.setSold(0);
        item.setIntroduction(introduction);
        item.setSequence(order);
        item.setStatus(Item.StatusEnum.OFF.getValue());
        itemDao.save(item);
        logger.info("item {} is saved", item);
        logService.recordAdminLog(Log.ActionEnum.ADD_ITEM, "", item.toString());
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 修改一个积分商品
     *
     * @param item         要修改的积分商品的id
     * @param name         商品名称
     * @param point        兑换商品所需积分
     * @param stock        商品状态
     * @param introduction 商品介绍
     * @return 修改结果
     */
    public RespBody<?> editItem(Item item, String name, int point, int stock, String introduction, int order) {
        logger.info("start to edit item {} with name = {}, point = {}, amount = {}, introduction = {}, order = {}",
                item, name, point, stock, introduction, order);
        String lockName = RedisKey.ADMIN_EDIT_ITEM_LOCK_KEY + item.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to save");
        String old = item.toString();
        item.setName(name);
        item.setPoint(point);
        item.setStock(stock);
        item.setIntroduction(introduction);
        item.setSequence(order);
        itemDao.save(item);
        logger.info("item {} is saved", item);
        logService.recordAdminLog(Log.ActionEnum.EDIT_EVENT, old, item.toString());
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 修改一个积分商品的状态
     *
     * @param item       要修改的积分商品
     * @param statusEnum 要修改的状态
     * @return 修改结果
     */
    public RespBody<?> editItemStatus(Item item, Item.StatusEnum statusEnum) {
        logger.info("start to edit status of item {} to {}", item, statusEnum);
        int oldStatus = item.getStatus();
        String lockName = RedisKey.ADMIN_EDIT_ITEM_LOCK_KEY + item.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to save");
        item.setStatus(statusEnum.getValue());
        itemDao.save(item);
        logger.info("item {} is saved", item);
        logService.recordAdminLog(Log.ActionEnum.EDIT_ITEM_STATUS, Event.StatusEnum.findStatusByValue(oldStatus).getDesc(),
                statusEnum.getName());
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 获取一个用户的积分记录
     *
     * @param user     要获取记录的用户
     * @param pageable 页码
     * @return 该用户的积分记录列表
     */
    public Page<PointRecord> listPointRecordByUser(User user, Pageable pageable) {
        return pointRecordDao.getAllPointByUserOrderByCreateTimeDesc(user, pageable);
    }

    /**
     * 获取一个积分商品对应的图片列表
     *
     * @param item 要获取的积分商品
     * @return 该积分商品对应的图片列表
     */
    public List<ItemPicture> listItemPictures(Item item) {
        return itemPictureDao.findAllByItem(item);
    }

    /**
     * 获取一个积分商品对应的图片列表
     *
     * @param item     要获取的积分商品
     * @param position 图片位置
     * @return 该积分商品对应的图片列表
     */
    public List<ItemPicture> listItemPicturesByPosition(Item item, int position) {
        return itemPictureDao.findAllByItemAndPosition(item, position);
    }

    /**
     * 创建积分商品对应的图片
     *
     * @param item  要创建图片所属的积分商品
     * @param files 要上传的图片
     * @return 创建结果
     */
    public RespBody<?> createItemPicture(Item item, MultipartFile[] files) {
        logger.info("start to create item picture to item = {}", item);
        String lockName = RedisKey.ADMIN_EDIT_ITEM_LOCK_KEY + item.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to save");
        AtomicInteger success = new AtomicInteger();
        Arrays.stream(files).forEach(file -> {
            String path = cosService.uploadItemPic(file, item.getId());
            if (path != null) {
                ItemPicture itemPicture = new ItemPicture();
                itemPicture.setItem(item);
                itemPicture.setPath(path);
                itemPicture.setPosition(ItemPicture.PositionEnum.OTHER.getValue());
                itemPictureDao.save(itemPicture);
                logger.info("picture {} is saved", itemPicture);
                success.getAndIncrement();
            }
        });
        logger.info("{} file upload, {} saved", files.length, success);
        logService.recordAdminLog(Log.ActionEnum.ADD_PICTURE,
                "上传商品[" + item.getName() + "]的" + files.length + "张图片", success + "张上传成功");
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        if (files.length == success.intValue()) {
            return RespBody.isOk();
        } else {
            return RespBody.isFail();
        }
    }

    /**
     * 删除一个积分商品的图片
     *
     * @param id 要删除的图片id
     * @return 删除结果
     */
    public RespBody<?> deleteItemPicture(int id) {
        List<Integer> list = new ArrayList<>(1);
        list.add(id);
        return deleteItemPictures(list);
    }

    /**
     * 设置某张图片位积分商品的封面图
     *
     * @param item        要设置的积分商品
     * @param itemPicture 要设计的积分商品图片
     * @return 设置结果
     */
    public RespBody<?> setItemPictureCover(Item item, ItemPicture itemPicture) {
        logger.info("start to set item {}cover to = {}", item, itemPicture);
        String lockName = RedisKey.ADMIN_EDIT_ITEM_LOCK_KEY + item.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to save");

        List<ItemPicture> list = itemPictureDao.findAllByItemAndPosition(item, ItemPicture.PositionEnum.COVER.getValue());
        list.forEach(picture -> {
            picture.setPosition(ItemPicture.PositionEnum.OTHER.getValue());
            itemPictureDao.save(picture);
        });
        itemPicture.setPosition(ItemPicture.PositionEnum.COVER.getValue());
        itemPictureDao.save(itemPicture);
        logger.info("picture {} is saved", itemPicture);
        logService.recordAdminLog(Log.ActionEnum.SET_COVER, "", "设置" + item.getName() + "的封面图");
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 删除一批积分商品图片
     *
     * @param list 要删除的图片id列表
     * @return 删除结果
     */
    public RespBody<?> deleteItemPictures(List<Integer> list) {
        logger.info("start to delete item picture list = {}", list);
        if (CollectionUtils.isEmpty(list)) {
            return RespBody.isFail().msg(ErrorEnum.DATA_NOT_FOUND);
        }
        ItemPicture picture = itemPictureDao.findById(list.get(0)).orElse(null);
        logger.info("picture = {}", picture);
        if (picture == null || picture.getItem() == null) {
            return RespBody.isFail().msg(ErrorEnum.DATA_NOT_FOUND);
        }
        String name = picture.getItem().getName();
        AtomicInteger success = new AtomicInteger();
        list.forEach(id -> {
            logger.info("delete id = {}", id);
            itemPictureDao.deleteById(id);
            success.getAndIncrement();
            logger.info("{} deleted", id);
        });
        logService.recordAdminLog(Log.ActionEnum.DELETE_PICTURE,
                "删除商品[" + name + "]的" + list.size() + "张图片", success + "张删除成功");
        return RespBody.isOk();
    }

    /**
     * 更新用户积分
     *
     * @param user       要更新积分的用户
     * @param point      要更新的积分值
     * @param note       备注
     * @param needRecord 是否需要记录
     * @return 更新结果
     */
    public RespBody<?> updateUserPoint(User user, int point, String note, boolean needRecord) {
        logger.info("start to update user {} point to {} with note = {} needRecord = {}", user, point, note, needRecord);
        if (user == null) {
            logger.info("user is null, return fail");
            return RespBody.isFail().msg("未获取到用户信息");
        }
        String lockName = RedisKey.ADMIN_UPDATE_USER_POINT_LOCK_KEY + user.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to save");
        int oldPoint = user.getPoint();
        user.setPoint(point);
        userDao.save(user);
        logger.info("user {} saved", user);
        logService.recordAdminLog(Log.ActionEnum.ADJUST_POINT, user.getNickname() + "积分" + oldPoint, user.getNickname() + "积分" + user.getPoint());
        if (needRecord) {
            logger.info("start to save point record");
            String scene = Log.ActionEnum.ADJUST_POINT.getName();
            if (StringUtils.hasLength(note)) {
                scene = scene + "(" + note + ")";
            }
            PointRecord record = new PointRecord();
            record.setUser(user);
            record.setPoint(point);
            record.setScene(scene);
            record.setDay(DateUtil.getDayInt(new Date(System.currentTimeMillis())));
            pointRecordDao.save(record);
            logger.info("log record {} saved", record);
        }
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 获取积分记录的列表
     *
     * @param pageable 页码
     * @return 该页的积分记录列表
     */
    public Page<PointRecord> listPointRecord(Pageable pageable) {
        return pointRecordDao.findAll(pageable);
    }

    /**
     * 获取所有的积分商品订单信息
     *
     * @param pageable 页码
     * @return 该页的积分商品订单列表
     */
    public Page<ItemOrder> listItemOrder(Pageable pageable) {
        return itemOrderDao.findAll(pageable);
    }

    /**
     * 获取某个积分商品的订单信息
     *
     * @param item     要查询的积分商品
     * @param pageable 页码
     * @return 该页的积分商品订单列表
     */
    public Page<ItemOrder> listItemOrderByItem(Item item, Pageable pageable) {
        return itemOrderDao.findAllByItem(item, pageable);
    }

    /**
     * 获取积分商城首页的信息对象
     *
     * @param pageable 分页信息
     * @return 改页积分商城首页的信息对象
     */
    public StoreItemList listItem(Pageable pageable) {
        StoreItemList storeItemList = new StoreItemList();
        Page<Item> page = itemDao.findAllByStatus(Item.StatusEnum.ON.getValue(), pageable);
        storeItemList.setLast(page.isLast());
        storeItemList.setList(page.getContent().stream().map(item -> {
                    StoreItemList.Info info = new StoreItemList.Info();
                    info.setId(item.getId());
                    info.setName(item.getName());
                    info.setPoint(item.getPoint());
                    info.setLeft(item.getStock() - item.getSold());
                    ItemPicture p = itemPictureDao.findByItemAndPosition(item, ItemPicture.PositionEnum.COVER.getValue());
                    if (p != null) {
                        info.setPicture(p.getPath());
                    }
                    return info;
                }
        ).collect(Collectors.toList()));
        return storeItemList;
    }

    /**
     * 获取一个商品页面展示信息
     *
     * @param item 要展示的商品
     * @return 该商品在页面要展示的信息
     */
    public StoreItemEntity getItemEntity(Item item, User user) {
        StoreItemEntity storeItem = new StoreItemEntity();
        storeItem.setId(item.getId());
        storeItem.setName(item.getName());
        storeItem.setPoint(item.getPoint());
        storeItem.setIntroduction(item.getIntroduction());
        storeItem.setStock(item.getStock());
        storeItem.setSold(item.getSold());
        storeItem.setLeft(item.getStock() - item.getSold());
        storeItem.setAfford(user.getPoint() >= item.getPoint());
        storeItem.setPics(itemPictureDao.findAllByItem(item).stream().map(ItemPicture::getPath).collect(Collectors.toList()));
        return storeItem;
    }

    /**
     * 获取一个积分商品购买确认页面展示信息
     *
     * @param item 要展示的商品
     * @param user 要购买的用户
     * @return 购买确认页面要展示的信息
     */
    public ItemConfirmInfoEntity getItemConfirmInfo(Item item, User user) {
        ItemConfirmInfoEntity itemConfirmInfoEntity = new ItemConfirmInfoEntity();
        ItemPicture itemPicture = itemPictureDao.findByItemAndPosition(item, ItemPicture.PositionEnum.COVER.getValue());
        if (itemPicture != null) {
            itemConfirmInfoEntity.setPic(itemPicture.getPath());
        }
        itemConfirmInfoEntity.setName(item.getName());
        itemConfirmInfoEntity.setIntroduction(item.getIntroduction());
        itemConfirmInfoEntity.setPoint(item.getPoint());
        itemConfirmInfoEntity.setLeft(user.getPoint() - item.getPoint());
        return itemConfirmInfoEntity;
    }

    /**
     * 检查积分商品是否有标题图片
     *
     * @param item 要检查的积分商品
     * @return 有的话返回true，否则返回false
     */
    public RespBody<?> checkItemHasCover(Item item) {
        return RespBody.isOk().data(!(itemPictureDao.countByItemAndPosition(item, ItemPicture.PositionEnum.COVER.getValue()) == 0));
    }

    /**
     * 用户购买一个积分商品
     *
     * @param user 购买的用户
     * @param item 要购买的积分商品
     * @return 购买结果
     */
    @Transactional(rollbackOn = Exception.class)
    public RespBody<?> orderItem(User user, Item item) {
        logger.info("start to sell item {} to user = {}", item, user);
        if (user.getPoint() < item.getPoint()) {
            logger.info("user point {} is less than item point {}", user.getPoint(), item.getPoint());
            return RespBody.isFail().msg(ErrorEnum.POINT_NOT_ENOUGH);
        }
        String lockName = RedisKey.USER_BUY_LOCK_KEY + item.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 10, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to sell");
        int stock = item.getStock();
        int sold = item.getSold();
        int left = stock - sold;
        logger.info("item stock is = {}, sold = {}, left = {}", stock, sold, left);
        if (left < 1) {
            logger.info("item is out of stock");
            return RespBody.isFail().msg(ErrorEnum.OUT_OF_STOCK);
        }
        // 商品的sold加1
        item.setSold(sold + 1);
        itemDao.save(item);
        logger.info("item sold changed from {} to {}", sold, item.getSold());
        logService.recordApiLog(Log.ActionEnum.USER_BUY_ITEM,
                item + "卖出" + sold, item + "卖出" + item.getSold());
        // 扣除用户积分
        pointOperationService.buyItemUsingPoint(user, item);
        // 创建订单
        logger.info("start to create order");
        ItemOrder itemOrder = new ItemOrder();
        itemOrder.setItem(item);
        itemOrder.setPoint(item.getPoint());
        itemOrder.setUser(user);
        itemOrder.setStatus(ItemOrder.StatusEnum.PAYED.getValue());
        itemOrderDao.save(itemOrder);
        logger.info("order {} created", itemOrder);
        logService.recordApiLog(Log.ActionEnum.CREATE_ORDER, "", itemOrder);
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 根据订单id获取订单
     *
     * @param id 要获取的订单id
     * @return 改id对应的订单, 没找到返回<code>null</code>
     */
    public ItemOrder getItemOrderById(int id) {
        return itemOrderDao.findById(id).orElse(null);
    }

    /**
     * 修改订单状态
     *
     * @param order      要修改的订单
     * @param statusEnum 要修改的状态
     * @return 修改结果
     */
    public RespBody<?> setItemOrderStatus(ItemOrder order, ItemOrder.StatusEnum statusEnum) {
        logger.info("start to edit status of item order {} to {}", order, statusEnum);
        int oldStatus = order.getStatus();
        String lockName = RedisKey.ADMIN_EDIT_ITEM_ORDER_LOCK_KEY + order.getId();
        logger.info("check lock in redis");
        // 加锁
        Boolean lockedSuccess = redisTemplate.opsForValue().setIfAbsent(lockName, "1", 5, TimeUnit.SECONDS);
        if (lockedSuccess == null || !lockedSuccess) {
            logger.info("obtain lock {} failed", lockName);
            return RespBody.isFail().msg("重复提交");
        }
        logger.info("no lock, start to save");
        order.setStatus(statusEnum.getValue());
        itemOrderDao.save(order);
        logger.info("item order {} is saved", order);
        logService.recordAdminLog(Log.ActionEnum.EDIT_ITEM_ORDER_STATUS, ItemOrder.StatusEnum.findStatusByValue(oldStatus).getName(),
                statusEnum.getName());
        logger.info("delete lock in redis");
        Boolean result = redisTemplate.delete(lockName);
        logger.info("lock delete is {}", result);
        return RespBody.isOk();
    }

    /**
     * 获取某个用户的订单列表
     *
     * @param user     要查询的用户
     * @param pageable 分页信息
     * @return 改页的用户订单列表
     */
    public MyOrder listOrderByUser(User user, Pageable pageable) {
        Page<ItemOrder> itemOrders = itemOrderDao.findAllByUser(user, pageable);
        List<MyOrder.Info> list = itemOrders.stream().map(itemOrder -> {
            MyOrder.Info info = new MyOrder.Info();
            info.setDate(DateUtil.getDateStr(itemOrder.getCreateTime()));
            info.setName(itemOrder.getItem().getName());
            info.setPoint(itemOrder.getPoint());
            info.setResult(ItemOrder.StatusEnum.findStatusByValue(itemOrder.getStatus()).getName());
            return info;
        }).collect(Collectors.toList());
        MyOrder myOrder = new MyOrder();
        myOrder.setLast(itemOrders.isLast());
        myOrder.setList(list);
        return myOrder;
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param redisTemplate  操作redis的工具类
     * @param cosService     腾讯COS相关的服务类
     * @param itemDao        积分商品的数据操作类
     * @param itemPictureDao 积分商品图片的数据操作类
     * @param itemOrderDao   订单的数据操作类
     * @param pointRecordDao 积分变化记录的数据操作类
     * @param userDao        用户信息的数据操作类
     * @param logService     操作日志的数据服务类
     */
    public PointService(RedisTemplate<String, String> redisTemplate, CosService cosService,
                        ItemDao itemDao, ItemPictureDao itemPictureDao, ItemOrderDao itemOrderDao,
                        PointRecordDao pointRecordDao, UserDao userDao, LogService logService,
                        PointOperationService pointOperationService) {
        this.redisTemplate = redisTemplate;
        this.cosService = cosService;
        this.itemDao = itemDao;
        this.itemPictureDao = itemPictureDao;
        this.itemOrderDao = itemOrderDao;
        this.pointRecordDao = pointRecordDao;
        this.userDao = userDao;
        this.logService = logService;
        this.pointOperationService = pointOperationService;
    }

    private final RedisTemplate<String, String> redisTemplate;
    private final CosService cosService;
    private final ItemDao itemDao;
    private final ItemPictureDao itemPictureDao;
    private final ItemOrderDao itemOrderDao;
    private final PointRecordDao pointRecordDao;
    private final UserDao userDao;
    private final LogService logService;
    private final PointOperationService pointOperationService;
    private final Logger logger = LogManager.getLogger("serviceLogger");
}

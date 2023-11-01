package cc.bearvalley.badminton.entity.admin;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 操作日志的实体类
 */
@Entity
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;            // 自增主键id
    private String account;    // 操作的账号

    private String operation;  // 操作

    private String oldContent; // 操作前内容

    private String newContent; // 操作后内容

    private int type;          // 日志种类

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @CreationTimestamp
    private Date createTime;   // 创建时间

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updateTime;   // 更新时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOldContent() {
        return oldContent;
    }

    public void setOldContent(String oldContent) {
        this.oldContent = oldContent;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", operation='" + operation + '\'' +
                ", oldContent='" + oldContent + '\'' +
                ", newContent='" + newContent + '\'' +
                ", type=" + type +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    /**
     * 操作日志类型的枚举类
     */
    public enum TypeEnum {
        ADMIN(0, "后台操作"),
        API(1, "前台操作");
        private final int value;
        private final String name;

        TypeEnum(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return this.name;
        }
    }

    /**
     * 操作行为的枚举类
     */
    public enum ActionEnum {
        ADD_EVENT("添加活动"),
        EDIT_EVENT("修改活动"),
        DELETE_EVENT("删除活动"),
        EDIT_EVENT_STATUS("修改活动状态"),
        ADD_ADMIN("添加后台账号"),
        EDIT_ADMIN("修改后台账号"),
        ADD_ROLE("添加角色"),
        EDIT_ROLE("修改角色名"),
        BIND_ROLE("绑定角色"),
        UNBIND_ROLE("解绑角色"),
        BIND_PRIVILEGE("绑定权限"),
        UNBIND_PRIVILEGE("解绑权限"),
        EDIT_PASSWORD("修改密码"),
        RESET_PASSWORD("重置密码"),
        ADD_ITEM("添加商品"),
        EDIT_ITEM("修改商品"),
        EDIT_ITEM_STATUS("修改商品状态"),
        EDIT_ITEM_ORDER_STATUS("修改商品订单状态"),
        ADD_PICTURE("添加商品图片"),
        DELETE_PICTURE("删除商品图片"),
        SET_COVER("设置商品封面"),
        ADD_BATTLE("添加对战"),
        JOIN_BATTLE("加入对战"),
        LEAVE_BATTLE("退出对战"),
        DELETE_BATTLE("删除对战"),
        VOTE_BATTLE("投票对战"),
        CANCEL_VOTE_BATTLE("取消投票"),
        CONFIRM_BATTLE_RESULT("修改对战结果"),
        CONFIRM_USER_BATTLE_RESULT("确认用户对战结果"),
        SET_USER_INTRODUCER("确认用户介绍人"),
        UNSET_USER_INTRODUCER("撤销确认用户介绍人"),
        ADD_POINT("增加积分"),
        CANCEL_POINT("取消积分"),
        SET_USER_ATTEND("确认用户参加活动"),
        INTRODUCER_SET_ATTEND("介绍的用户参加活动"),
        UNSET_USER_ATTEND("取消用户参加活动确认"),
        INTRODUCER_UNSET_ATTEND("介绍的用户参加活动确认被取消"),
        API_USER_ENROLL_EVENT("用户参加活动"),
        ADJUST_POINT("手动积分调整"),
        USER_BUY_ITEM("用户兑换商品"),
        CREATE_ORDER("创建订单");
        private final String name;

        ActionEnum(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

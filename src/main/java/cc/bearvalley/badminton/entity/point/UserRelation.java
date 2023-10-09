package cc.bearvalley.badminton.entity.point;

import cc.bearvalley.badminton.entity.User;

import javax.persistence.*;


/**
 * 用户推荐关系的实体类
 */
@Entity
public class UserRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;          // 自增主键
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private User user;       // 被推荐的用户

    @ManyToOne
    @JoinColumn(name = "introducer_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private User introducer; // 推荐他的用户

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getIntroducer() {
        return introducer;
    }

    public void setIntroducer(User introducer) {
        this.introducer = introducer;
    }

    @Override
    public String toString() {
        return "UserRelation{" +
                "id=" + id +
                ", user=" + user +
                ", introducer=" + introducer +
                '}';
    }
}

package cc.bearvalley.badminton.entity.admin;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 角色和权限绑定的实体类
 */
@Entity
public class RolePrivilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;           // 自增主键

    @ManyToOne
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Role role;        // 子分类ID

    private String privilege; // 权限路径

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @CreationTimestamp
    private Date createTime;  // 创建时间

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updateTime;  // 更新时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
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
        return "RolePrivilege{" +
                "id=" + id +
                ", role=" + role +
                ", privilege='" + privilege + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}

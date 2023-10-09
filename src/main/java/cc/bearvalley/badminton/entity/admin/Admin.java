package cc.bearvalley.badminton.entity.admin;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 管理后台登录账号的实体类
 */
@Entity
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;          // 主键
    private String username; // 登录名
    private String realname; // 真实姓名
    private String mobile;   // 手机号
    private String password; // 密码
    private int status;      // 状态

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "AdminRole",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> roles = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @CreationTimestamp
    private Date createTime; // 创建时间

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updateTime; // 更新时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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
        return "Admin{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", realname='" + realname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                ", status=" + status +
                ", roles=" + roles +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    /**
     * 管理后台登录账号的状态枚举类
     */
    public enum StatusEnum {
        OFF(1, "停用"),
        ON(0, "启用");

        private final int value;
        private final String name;

        StatusEnum(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public static StatusEnum findStatusByValue(int value) {
            return Arrays.stream(StatusEnum.values()).filter(s -> s.getValue() == value).findFirst().orElse(StatusEnum.OFF);
        }
    }
}

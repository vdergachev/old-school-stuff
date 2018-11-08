package ru.glavkniga.gklients.integration.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Vladimir on 16.06.2017.
 */

/*
DROP TABLE IF EXISTS `gk_sys_site_user`;
CREATE TABLE `gk_sys_site_user` (
  `id` varchar(36) NOT NULL,
  `email` varchar(50) NOT NULL,
  `pass_hash` varchar(32) NOT NULL,
  `access_type` tinyint(4) unsigned NOT NULL,
  `phone_hash` varchar(32) NOT NULL,
  `blocked` tinyint(1) DEFAULT '0',
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
*/
@Entity
@Table(name = "gk_sys_site_user")
public class User {
    @Id
    @Column(name="id", length = 36)
    private String id;

    @NotNull
    @Column(name="email", length = 50)
    private String email;

    @NotNull
    @Column(name="pass_hash", length = 32)
    private String passwordHash;

    @NotNull
    @Column(name="access_type")
    private byte accessType;

    @NotNull
    @Column(name="phone_hash", length = 32)
    private String phoneHash;

    @NotNull
    @Column(nullable = false, name="blocked")
    private boolean blocked;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public byte getAccessType() {
        return accessType;
    }

    public void setAccessType(byte accessType) {
        this.accessType = accessType;
    }

    public String getPhoneHash() {
        return phoneHash;
    }

    public void setPhoneHash(String phoneHash) {
        this.phoneHash = phoneHash;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (accessType != user.accessType) return false;
        if (blocked != user.blocked) return false;
        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (passwordHash != null ? !passwordHash.equals(user.passwordHash) : user.passwordHash != null) return false;
        return phoneHash != null ? phoneHash.equals(user.phoneHash) : user.phoneHash == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (passwordHash != null ? passwordHash.hashCode() : 0);
        result = 31 * result + (int) accessType;
        result = 31 * result + (phoneHash != null ? phoneHash.hashCode() : 0);
        result = 31 * result + (blocked ? 1 : 0);
        return result;
    }
}

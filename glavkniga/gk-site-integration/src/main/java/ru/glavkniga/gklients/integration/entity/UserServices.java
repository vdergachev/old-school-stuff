package ru.glavkniga.gklients.integration.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Vladimir on 16.06.2017.
 */

/*
DROP TABLE IF EXISTS `gk_sys_site_user_services`;
CREATE TABLE `gk_sys_site_user_services` (
  `user_id` varchar(36) NOT NULL,
  `service` tinyint(3) unsigned NOT NULL,
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 */
@Entity
@Table(name = "gk_sys_site_user_services")
public class UserServices {
    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    @NotNull
    @Column(name = "service")
    private byte service;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public byte getService() {
        return service;
    }

    public void setService(byte service) {
        this.service = service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserServices that = (UserServices) o;

        if (service != that.service) return false;
        return userId != null ? userId.equals(that.userId) : that.userId == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (int) service;
        return result;
    }
}

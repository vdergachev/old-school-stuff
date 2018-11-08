package ru.glavkniga.gklients.integration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Vladimir on 17.06.2017.
 */
/*
DROP TABLE IF EXISTS `gk_sys_site_user_service_3`;
CREATE TABLE `gk_sys_site_user_service_3` (
  `user_id` varchar(36) NOT NULL,
  `date_begin` datetime NOT NULL,
  `date_end` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 */

@Entity
@Table(name = "gk_sys_site_user_service_3")
public class UserServiceThree {
    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    @NotNull
    @Column(name = "date_begin")
    private Date dateBegin;

    @NotNull
    @Column(name = "date_end")
    private Date dateEnd;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDateBegin() {
        return dateBegin;
    }

    public void setDateBegin(Date dateBegin) {
        this.dateBegin = dateBegin;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserServiceThree that = (UserServiceThree) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (dateBegin != null ? !dateBegin.equals(that.dateBegin) : that.dateBegin != null) return false;
        return dateEnd != null ? dateEnd.equals(that.dateEnd) : that.dateEnd == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (dateBegin != null ? dateBegin.hashCode() : 0);
        result = 31 * result + (dateEnd != null ? dateEnd.hashCode() : 0);
        return result;
    }
}

package ru.glavkniga.gklients.integration.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Vladimir on 16.06.2017.
 */
/*
DROP TABLE IF EXISTS `gk_sys_site_user_service_1`;
CREATE TABLE `gk_sys_site_user_service_1` (
  `user_id` varchar(36) NOT NULL,
  `number` varchar(4) NOT NULL,
  `year` int(10) unsigned NOT NULL,
  `date_begin` datetime NOT NULL,
  `date_end` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
*/

@Entity
@Table(name = "gk_sys_site_user_service_1")
public class UserServiceOne {
    @Id
    @Column(name="user_id", length = 36)
    private String userId;

    @NotNull
    @Column(name="number", length = 4)
    private String number;

    @NotNull
    @Column(name="year")
    private Integer year;

    @NotNull
    @Column(name="date_begin")
    private Date dateBegin;

    @NotNull
    @Column(name="date_end")
    private Date dateEnd;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
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

        UserServiceOne that = (UserServiceOne) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (number != null ? !number.equals(that.number) : that.number != null) return false;
        if (year != null ? !year.equals(that.year) : that.year != null) return false;
        if (dateBegin != null ? !dateBegin.equals(that.dateBegin) : that.dateBegin != null) return false;
        return dateEnd != null ? dateEnd.equals(that.dateEnd) : that.dateEnd == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (year != null ? year.hashCode() : 0);
        result = 31 * result + (dateBegin != null ? dateBegin.hashCode() : 0);
        result = 31 * result + (dateEnd != null ? dateEnd.hashCode() : 0);
        return result;
    }
}

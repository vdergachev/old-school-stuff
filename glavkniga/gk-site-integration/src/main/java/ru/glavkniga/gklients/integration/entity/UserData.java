package ru.glavkniga.gklients.integration.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Vladimir on 16.06.2017.
 */
/*
DROP TABLE IF EXISTS `gk_sys_site_user_data`;
CREATE TABLE `gk_sys_site_user_data` (
  `id` varchar(36) NOT NULL,
  `name` text NOT NULL,
  `phone` varchar(11) NOT NULL,
  `date` datetime NOT NULL,
  `pass` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
*/
@Entity
@Table(name = "gk_sys_site_user_data")
public class UserData {

    @Id
    @Column(name="id", length = 36)
    private String id;

    @NotNull
    @Column(name="name")
    @Type(type="text")
    private String name;

    @NotNull
    @Column(name="phone", length = 11)
    private String phone;

    @NotNull
    @Column(name="date")
    private Date date;

    @NotNull
    @Column(name="pass")
    @Type(type="text")
    private String pass;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserData userData = (UserData) o;

        if (id != null ? !id.equals(userData.id) : userData.id != null) return false;
        if (name != null ? !name.equals(userData.name) : userData.name != null) return false;
        if (phone != null ? !phone.equals(userData.phone) : userData.phone != null) return false;
        if (date != null ? !date.equals(userData.date) : userData.date != null) return false;
        return pass != null ? pass.equals(userData.pass) : userData.pass == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (pass != null ? pass.hashCode() : 0);
        return result;
    }
}

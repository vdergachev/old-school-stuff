/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.List;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * @author IgorLysov
 */
@NamePattern("%s|name")
@Table(name = "GKLIENTS_TOKEN")
@Entity(name = "gklients$Token")
public class Token extends StandardEntity {
    private static final long serialVersionUID = -883638547220423045L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "TOKEN")
    protected String token;

    @Column(name = "ENTITY_NAME")
    protected String entityName;

    @Column(name = "ENTITY_FIELD")
    protected String entityField;

    @Column(name = "IS_PERSONAL")
    protected Boolean isPersonal;




    public void setIsPersonal(Boolean isPersonal) {
        this.isPersonal = isPersonal;
    }

    public Boolean getIsPersonal() {
        return isPersonal;
    }











    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityField(String entityField) {
        this.entityField = entityField;
    }

    public String getEntityField() {
        return entityField;
    }


}
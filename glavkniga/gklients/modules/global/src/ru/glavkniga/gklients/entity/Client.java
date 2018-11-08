/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.global.UuidProvider;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;

/**
 * @author LysovIA
 */
@Listeners("gklients_ClientUpdateListener")
@NamePattern("%s|email,itn")
@Table(name = "GKLIENTS_CLIENT")
@Entity(name = "gklients$Client")
public class Client extends StandardEntity {
    private static final long serialVersionUID = -1148774379205891607L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "EMAIL", unique = true)
    protected String email;

    @Column(name = "PASSWORD", length = 50)
    protected String password;

    @Column(name = "ITN", length = 20)
    protected String itn;

    @Column(name = "PHONE")
    protected String phone;

    @Column(name = "EMAIL_HASH")
    protected String emailHash;

    @Column(name = "PASSWORD_HASH", length = 32)
    protected String passwordHash;

    @Column(name = "BAD_EMAIL")
    protected Boolean badEmail;

    @Column(name = "IS_BLOCKED")
    protected Boolean isBlocked;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORIGIN_ID")
    protected Origin origin;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "client")
    @OnDelete(DeletePolicy.CASCADE)
    protected ClientDistributionSettings clientDistributionSettings;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "client")
    protected List<ElverSubscription> elverSubscription;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "client")
    protected List<DistributionSubscription> distributionSubscription;

    @JoinTable(name = "GKLIENTS_ONETIME_MAILING_CLIENT_LINK",
        joinColumns = @JoinColumn(name = "CLIENT_ID"),
        inverseJoinColumns = @JoinColumn(name = "ONETIME_MAILING_ID"))
    @ManyToMany
    protected List<OnetimeMailing> onetimeMailings;

    public void setBadEmail(Boolean badEmail) {
        this.badEmail = badEmail;
    }

    public Boolean getBadEmail() {
        return badEmail;
    }


    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    public Origin getOrigin() {
        return origin;
    }


    public void setOnetimeMailings(List<OnetimeMailing> onetimeMailings) {
        this.onetimeMailings = onetimeMailings;
    }

    public List<OnetimeMailing> getOnetimeMailings() {
        return onetimeMailings;
    }


    public ClientDistributionSettings getClientDistributionSettings() {
        return clientDistributionSettings;
    }

    public void setClientDistributionSettings(ClientDistributionSettings clientDistributionSettings) {
        this.clientDistributionSettings = clientDistributionSettings;
    }


    public void setElverSubscription(List<ElverSubscription> elverSubscription) {
        this.elverSubscription = elverSubscription;
    }

    public List<ElverSubscription> getElverSubscription() {
        return elverSubscription;
    }

    public void setDistributionSubscription(List<DistributionSubscription> distributionSubscription) {
        this.distributionSubscription = distributionSubscription;
    }

    public List<DistributionSubscription> getDistributionSubscription() {
        return distributionSubscription;
    }




    public void setIsBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public Boolean getIsBlocked() {
        return isBlocked;
    }


    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }

    public String getEmailHash() {
        return emailHash;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setItn(String itn) {
        this.itn = itn;
    }

    public String getItn() {
        return itn;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id){
        this.id = UuidProvider.fromString(id);
    }


}
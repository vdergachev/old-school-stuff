/*
 * Copyright (c) 2015 ru.glavkniga.gklients.entity.
 */
package ru.glavkniga.gklients.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Set;
import javax.persistence.OneToMany;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import com.haulmont.chile.core.annotations.Composition;
import ru.glavkniga.gklients.enumeration.Region;

/**
 * @author LysovIA
 */
@NamePattern("%s (%s)|number,name")
@Table(name = "gklients_RIZ")
@Entity(name = "gklients$Riz")
public class Riz extends StandardEntity {
    private static final long serialVersionUID = -7033567221406139375L;

    @Column(name = "NUMBER_")
    protected Integer number;

    @Column(name = "NAME", length = 300)
    protected String name;

    @Column(name = "CITY", length = 50)
    protected String city;

    @Column(name = "REGION")
    protected String region;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PODHOST_ID")
    protected Riz podhost;

    @Column(name = "PHONE", length = 300)
    protected String phone;

    @Column(name = "ELVER_EMAIL", length = 300)
    protected String elverEmail;
    @Column(name = "DELIVERY_ADDRESS", length = 300)
    protected String deliveryAddress;

    @Column(name = "DELIVERY_EMAIL", length = 300)
    protected String deliveryEmail;

    @Column(name = "DELIVERY_COMPANY", length = 50)
    protected String deliveryCompany;

    @Column(name = "ORDER_PERSON", length = 300)
    protected String orderPerson;

    @Column(name = "ORDER_PHONE", length = 300)
    protected String orderPhone;

    @Column(name = "ORDER_EMAIL", length = 300)
    protected String orderEmail;

    @Column(name = "ORDER_ACCOUNTIST", length = 300)
    protected String orderAccountist;

    @Column(name = "PR_PERSON", length = 300)
    protected String prPerson;

    @Column(name = "PR_PHONE", length = 300)
    protected String prPhone;

    @Column(name = "PR_EMAIL", length = 300)
    protected String prEmail;

    @Column(name = "DISTR_PERSON", length = 300)
    protected String distrPerson;

    @Column(name = "DISTR_EMAIL", length = 300)
    protected String distrEmail;

    @Column(name = "WS_NAME")
    protected String wsName;

    @Column(name = "WS_PERSON")
    protected String wsPerson;

    @Column(name = "WS_ADDRESS", length = 300)
    protected String wsAddress;

    @Column(name = "WS_PHONE", length = 300)
    protected String wsPhone;

    @Column(name = "WS_EMAIL", length = 300)
    protected String wsEmail;

    @Column(name = "WS_WEBSITE")
    protected String wsWebsite;

    @Column(name = "WS_TERRITORY", length = 5000)
    protected String wsTerritory;

    @JoinTable(name = "GKLIENTS_RIZ_MUN_OBRAZ_LINK",
        joinColumns = @JoinColumn(name = "RIZ_ID"),
        inverseJoinColumns = @JoinColumn(name = "MUN_OBRAZ_ID"))
    @ManyToMany
    protected Set<MunObraz> munObraz;

    @Column(name = "DISCOUNT")
    protected Double discount;

    @Column(name = "MAIL_NAME")
    protected String mailName;

    @Column(name = "MAIL_PERSON")
    protected String mailPerson;

    @Column(name = "MAIL_EMAIL")
    protected String mailEmail;

    @Column(name = "MAIL_PHONE", length = 50)
    protected String mailPhone;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "riz")
    protected Set<RizContract> contract;

    public void setContract(Set<RizContract> contract) {
        this.contract = contract;
    }

    public Set<RizContract> getContract() {
        return contract;
    }


    public void setMailName(String mailName) {
        this.mailName = mailName;
    }

    public String getMailName() {
        return mailName;
    }

    public void setMailPerson(String mailPerson) {
        this.mailPerson = mailPerson;
    }

    public String getMailPerson() {
        return mailPerson;
    }

    public void setMailEmail(String mailEmail) {
        this.mailEmail = mailEmail;
    }

    public String getMailEmail() {
        return mailEmail;
    }

    public void setMailPhone(String mailPhone) {
        this.mailPhone = mailPhone;
    }

    public String getMailPhone() {
        return mailPhone;
    }


    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getDiscount() {
        return discount;
    }


    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }


    public Set<MunObraz> getMunObraz() {
        return munObraz;
    }

    public void setMunObraz(Set<MunObraz> munObraz) {
        this.munObraz = munObraz;
    }



    public void setPodhost(Riz podhost) {
        this.podhost = podhost;
    }

    public Riz getPodhost() {
        return podhost;
    }


    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryEmail(String deliveryEmail) {
        this.deliveryEmail = deliveryEmail;
    }

    public String getDeliveryEmail() {
        return deliveryEmail;
    }

    public void setDeliveryCompany(String deliveryCompany) {
        this.deliveryCompany = deliveryCompany;
    }

    public String getDeliveryCompany() {
        return deliveryCompany;
    }

    public void setOrderPerson(String orderPerson) {
        this.orderPerson = orderPerson;
    }

    public String getOrderPerson() {
        return orderPerson;
    }

    public void setOrderPhone(String orderPhone) {
        this.orderPhone = orderPhone;
    }

    public String getOrderPhone() {
        return orderPhone;
    }

    public void setOrderEmail(String orderEmail) {
        this.orderEmail = orderEmail;
    }

    public String getOrderEmail() {
        return orderEmail;
    }

    public void setOrderAccountist(String orderAccountist) {
        this.orderAccountist = orderAccountist;
    }

    public String getOrderAccountist() {
        return orderAccountist;
    }

    public void setPrPerson(String prPerson) {
        this.prPerson = prPerson;
    }

    public String getPrPerson() {
        return prPerson;
    }

    public void setPrPhone(String prPhone) {
        this.prPhone = prPhone;
    }

    public String getPrPhone() {
        return prPhone;
    }

    public void setPrEmail(String prEmail) {
        this.prEmail = prEmail;
    }

    public String getPrEmail() {
        return prEmail;
    }

    public void setDistrPerson(String distrPerson) {
        this.distrPerson = distrPerson;
    }

    public String getDistrPerson() {
        return distrPerson;
    }

    public void setDistrEmail(String distrEmail) {
        this.distrEmail = distrEmail;
    }

    public String getDistrEmail() {
        return distrEmail;
    }


    public void setElverEmail(String elverEmail) {
        this.elverEmail = elverEmail;
    }

    public String getElverEmail() {
        return elverEmail;
    }

    public void setWsName(String wsName) {
        this.wsName = wsName;
    }

    public String getWsName() {
        return wsName;
    }

    public void setWsPerson(String wsPerson) {
        this.wsPerson = wsPerson;
    }

    public String getWsPerson() {
        return wsPerson;
    }

    public void setWsAddress(String wsAddress) {
        this.wsAddress = wsAddress;
    }

    public String getWsAddress() {
        return wsAddress;
    }

    public void setWsPhone(String wsPhone) {
        this.wsPhone = wsPhone;
    }

    public String getWsPhone() {
        return wsPhone;
    }

    public void setWsEmail(String wsEmail) {
        this.wsEmail = wsEmail;
    }

    public String getWsEmail() {
        return wsEmail;
    }

    public void setWsWebsite(String wsWebsite) {
        this.wsWebsite = wsWebsite;
    }

    public String getWsWebsite() {
        return wsWebsite;
    }

    public void setWsTerritory(String wsTerritory) {
        this.wsTerritory = wsTerritory;
    }

    public String getWsTerritory() {
        return wsTerritory;
    }


    public Region getRegion() {
        return region == null ? null : Region.fromId(region);
    }

    public void setRegion(Region region) {
        this.region = region == null ? null : region.getId();
    }








    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }





    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }








}
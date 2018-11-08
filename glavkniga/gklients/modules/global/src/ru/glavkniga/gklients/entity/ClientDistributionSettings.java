/*
 * Copyright (c) 2015 ru.glavkniga.gklients.entity.
 */
package ru.glavkniga.gklients.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;

/**
 * @author Alex
 */
@NamePattern("%s|client")
@Table(name = "GKLIENTS_CLIENT_DISTRIBUTION_SETTINGS")
@Entity(name = "gklients$ClientDistributionSettings")
public class ClientDistributionSettings extends StandardEntity {
    private static final long serialVersionUID = 300863294861796762L;


    @OneToOne(fetch = FetchType.LAZY)
    @Lookup(type = LookupType.DROPDOWN)
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @OnDelete(DeletePolicy.UNLINK)
    @JoinColumn(name = "CLIENT_ID")
    protected Client client;


    @Column(name = "IS_COMPANY")
    protected Boolean isCompany;

    @Column(name = "IS_IP")
    protected Boolean isIp;

    @Column(name = "IS_CIVIL")
    protected Boolean isCivil;

    @Column(name = "IS_WITH_WORKERS")
    protected Boolean isWithWorkers;

    @Column(name = "IS_WITHOUT_WORKERS")
    protected Boolean isWithoutWorkers;

    @Column(name = "IS_OSNO")
    protected Boolean isOsno;

    @Column(name = "IS_USN")
    protected Boolean isUsn;

    @Column(name = "IS_ENVD")
    protected Boolean isEnvd;


    @Column(name = "IS_ESHN")
    protected Boolean isEshn;

    @Column(name = "IS_PSN")
    protected Boolean isPsn;

    @Column(name = "MARKER")
    protected String marker;

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getMarker() {
        return marker;
    }


    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setIsEshn(Boolean isEshn) {
        this.isEshn = isEshn;
    }


    public Boolean getIsEshn() {
        return isEshn;
    }


    public void setIsCompany(Boolean isCompany) {
        this.isCompany = isCompany;
    }

    public Boolean getIsCompany() {
        return isCompany;
    }

    public void setIsIp(Boolean isIp) {
        this.isIp = isIp;
    }

    public Boolean getIsIp() {
        return isIp;
    }

    public void setIsCivil(Boolean isCivil) {
        this.isCivil = isCivil;
    }

    public Boolean getIsCivil() {
        return isCivil;
    }

    public void setIsWithWorkers(Boolean isWithWorkers) {
        this.isWithWorkers = isWithWorkers;
    }

    public Boolean getIsWithWorkers() {
        return isWithWorkers;
    }

    public void setIsWithoutWorkers(Boolean isWithoutWorkers) {
        this.isWithoutWorkers = isWithoutWorkers;
    }

    public Boolean getIsWithoutWorkers() {
        return isWithoutWorkers;
    }

    public void setIsOsno(Boolean isOsno) {
        this.isOsno = isOsno;
    }

    public Boolean getIsOsno() {
        return isOsno;
    }

    public void setIsUsn(Boolean isUsn) {
        this.isUsn = isUsn;
    }

    public Boolean getIsUsn() {
        return isUsn;
    }

    public void setIsEnvd(Boolean isEnvd) {
        this.isEnvd = isEnvd;
    }

    public Boolean getIsEnvd() {
        return isEnvd;
    }


    public void setIsPsn(Boolean isPsn) {
        this.isPsn = isPsn;
    }

    public Boolean getIsPsn() {
        return isPsn;
    }

    public byte getTaxesAsByte() {
        byte value = 0;
        byte[] flags = new byte[5];
        flags[0] = (byte) ((getIsOsno() != null && getIsOsno()) ? 1 : 0);
        flags[1] = (byte) ((getIsUsn() != null && getIsUsn()) ? 1 : 0);
        flags[2] = (byte) ((getIsEnvd() != null && getIsEnvd()) ? 1 : 0);
        flags[3] = (byte) ((getIsEshn() != null && getIsEshn()) ? 1 : 0);
        flags[4] = (byte) ((getIsPsn() != null && getIsPsn()) ? 1 : 0);


        for (byte i = 0; i < 5; i++) {
            value = (byte) ((value << 1) + flags[i]);
        }

        return value;
    }

    public byte getOrgsAsByte() {
        byte value = 0;
        byte[] flags = new byte[3];
        flags[0] = (byte) ((getIsCompany() != null && getIsCompany()) ? 1 : 0);
        flags[1] = (byte) ((getIsIp() != null && getIsIp()) ? 1 : 0);


        for (byte i = 0; i < 2; i++) {
            value = (byte) ((value << 1) + flags[i]);
        }

        return value;
    }

    public byte getWorkersAsByte(){
        byte value = 0;
        byte[] flags = new byte[2];
        flags[0] = (byte) ((getIsWithWorkers() != null && getIsWithWorkers()) ? 1 : 0);
        flags[1] = (byte) ((getIsWithoutWorkers() != null && getIsWithoutWorkers()) ? 1 : 0);


        for (byte i = 0; i < 2; i++) {
            value = (byte) ((value << 1) + flags[i]);
        }

        return value;
    }

}
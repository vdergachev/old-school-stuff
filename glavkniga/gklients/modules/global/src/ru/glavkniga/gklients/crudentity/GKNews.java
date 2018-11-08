/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.crudentity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Creatable;
import com.haulmont.cuba.core.entity.SoftDelete;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author IgorLysov
 */
@Table(name = "GKLIENTS_GK_NEWS")
@Entity(name = "gklients$GKNews")
public class GKNews extends BaseUuidEntity implements Creatable, SoftDelete {
    private static final long serialVersionUID = 5721878353139448869L;

    @Column(name = "SITE_ID")
    protected Integer siteId;

    @Column(name = "TITLE")
    protected String title;

    @Lob
    @Column(name = "URL")
    protected String url;

    @Lob
    @Column(name = "LEAD_TEXT")
    protected String leadText;

    @Lob
    @Column(name = "FULL_TEXT")
    protected String fullText;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "NEWS_DATE")
    protected Date newsDate;

    @Lob
    @Column(name = "PARAMS")
    protected String params;

    @Column(name = "IMAGE")
    protected String image;

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

    @Column(name = "IS_OTHER")
    protected Boolean isOther;

    @Column(name = "IS_SENT")
    protected Boolean isSent;

    @OnDelete(DeletePolicy.CASCADE)
    @Composition
    @OneToMany(mappedBy = "gkNews")
    protected List<GKNewsImage> images;

    @Column(name = "CREATE_TS")
    protected Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;

    @Column(name = "DELETE_TS")
    protected Date deleteTs;

    @Column(name = "DELETED_BY", length = 50)
    protected String deletedBy;


    public void setIsSent(Boolean isSent) {
        this.isSent = isSent;
    }

    public Boolean getIsSent() {
        return isSent;
    }


    public void setIsOther(Boolean isOther) {
        this.isOther = isOther;
    }

    public Boolean getIsOther() {
        return isOther;
    }


    @Override
    public Boolean isDeleted() {
        return deleteTs != null;
    }

    @Override
    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    @Override
    public String getDeletedBy() {
        return deletedBy;
    }

    @Override
    public void setDeleteTs(Date deleteTs) {
        this.deleteTs = deleteTs;
    }

    @Override
    public Date getDeleteTs() {
        return deleteTs;
    }

    public void setImages(List<GKNewsImage> images) {
        this.images = images;
    }

    public List<GKNewsImage> getImages() {
        return images;
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


    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setLeadText(String leadText) {
        this.leadText = leadText;
    }

    public String getLeadText() {
        return leadText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getFullText() {
        return fullText;
    }

    public void setNewsDate(Date newsDate) {
        this.newsDate = newsDate;
    }

    public Date getNewsDate() {
        return newsDate;
    }

    public void setParams(String params) {
        this.params = params;

        if (params.contains("ORG"))
            this.setIsCompany(true);

        if (params.contains("PBOUL")) {
            int pboulIndex = params.indexOf("PBOUL");
            int withWorkersIndex = params.indexOf("PBOUL_R1");
            int withoutWorkersIndex = params.indexOf("PBOUL_R2");
            if ((pboulIndex != withoutWorkersIndex) && (pboulIndex != withWorkersIndex)) {
                this.setIsIp(true);
            }
        }

        if (params.contains("PBOUL_R1"))
            this.setIsWithWorkers(true);

        if (params.contains("PBOUL_R2"))
            this.setIsWithoutWorkers(true);

        if (params.contains("PPL"))
            this.setIsCivil(true);

        if (params.contains("OSNO"))
            this.setIsOsno(true);

        if (params.contains("USNO"))
            this.setIsUsn(true);

        if (params.contains("ENVD"))
            this.setIsEnvd(true);

        if (params.contains("ESNX"))
            this.setIsEshn(true);

        if (params.contains("PSN"))
            this.setIsPsn(true);

    }

    public String getParams() {
        return params;
    }


    public String getImage() {
        return image;
    }


    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    @Override
    public Date getCreateTs() {
        return createTs;
    }

    public int getTaxesAsByte() {
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
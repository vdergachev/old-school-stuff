package com.gksm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gksm.networking.EnoviaHost;
import com.gksm.networking.EventData;
import com.gksm.networking.EventType;
import com.gksm.networking.helpers.EnoviaHostConverter;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by VSDergachev on 11/11/2015.
 */
@Entity
@Table(indexes = {@Index(name="by_host", columnList = "hostname", unique = false)})
public class Event {

    @JsonIgnore
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "hostname", length = 64, nullable = false,
            columnDefinition = "enum('enovia-d.gksm.local','enovia-mcs-test.gksm.local','enovia-deploy.gksm.local','sm-enovia-ref.gksm.local', " +
                    "'plm-enovia-tst.gksm.local','sm-enovia-dev.gksm.local','enovia-mcs1.gksm.local','enovia-mcs2.gksm.local')")
    @Convert(converter = EnoviaHostConverter.class)
    private EnoviaHost hostname;

    @Column(length = 32, nullable = false)
    private String username;

    @Column(name = "event_dt", nullable = false)
    private Date date;

    @Column(nullable = false, columnDefinition = "enum('LOGIN','LOGOUT', 'CLICK')")
    @Enumerated(EnumType.STRING)
    private EventType type;

    public Event() {
    }

    public EnoviaHost getHostname() {
        return hostname;
    }

    public String getUsername() {
        return username;
    }

    public EventType getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHostname(EnoviaHost hostname) {
        this.hostname = hostname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Event(EventData e) {
        setDate(e.getDate());
        setUsername(e.getUsername());
        setType(e.getType());
    }
}

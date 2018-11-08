package com.gksm.networking;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gksm.models.Event;
import com.gksm.networking.helpers.CustomerDateAndTimeDeserializer;
import com.gksm.networking.helpers.CustomerDateAndTimeSerializer;
import com.gksm.networking.helpers.EventTypeDeserializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by VSDergachev on 11/12/2015.
 */
public class EventData {
    private String username;
    private Date date;
    private EventType type;

    public EventData() {
    }

    public EventData(String username, Date date, EventType type) {
        this.username = username;
        this.date = date;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }
    public EventType getType() {
        return type;
    }
    public void setUsername(String username) {
        this.username = username;
    }


    @JsonDeserialize(using=CustomerDateAndTimeDeserializer.class)
    public void setDate(Date date) {
        this.date = date;
    }

    @JsonSerialize(using=CustomerDateAndTimeSerializer.class)
    public Date getDate() {
        return date;
    }

    @JsonDeserialize(using=EventTypeDeserializer.class)
    public void setType(EventType type) {
        this.type = type;
    }

    public static List<Event> getEvents(EventData[] events, EnoviaHost hostname) {
        List pEvents = null;
        if(events != null){
            pEvents = new ArrayList<>(events.length);
            for(EventData eventData : events){
                Event e = new Event(eventData);
                e.setHostname(hostname);
                pEvents.add(e);
            }
        }
        return pEvents;
    }
}

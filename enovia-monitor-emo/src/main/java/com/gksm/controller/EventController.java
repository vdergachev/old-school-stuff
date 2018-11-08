package com.gksm.controller;

import com.gksm.models.Event;
import com.gksm.networking.EnoviaHost;
import com.gksm.networking.EventData;
import com.gksm.dao.EventRepository;
import com.gksm.networking.helpers.EnoviaHostConverter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by VSDergachev on 11/11/2015.
 */
@RestController
@RequestMapping("/{hostname}/events")
public class EventController {
    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    @Autowired
    EventRepository eventRepository;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public void add(@PathVariable EnoviaHost hostname, @RequestBody EventData[] eventDatas)
    {
        log.info(String.format("event from hostname %s", hostname));
        List<Event> events = EventData.getEvents(eventDatas, hostname);
        eventRepository.save(events);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public List<Event> curDay(@PathVariable EnoviaHost hostname){
        List<Event> events = eventRepository.findByHostnameAndDate(hostname);
        if(events != null)
            return events;
        return new ArrayList<>();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public String handleException1(HttpMessageNotReadableException ex)
    {
        return ex.getMessage();
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(EnoviaHost.class, new EnoviaHostConverter());
    }

}

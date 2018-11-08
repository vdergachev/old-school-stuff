package com.gator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Владимир on 16.09.2016.
 */
@Component
public class ScheduledTasks {

    @Autowired
    UpdateChannelsTask updateChannelsTask;

    @Scheduled(fixedDelay = 5000, initialDelay = 3000)
    public void updateChannels(){
        updateChannelsTask.updateChannels();
    }
}

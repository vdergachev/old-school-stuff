package com.gator.web;

import com.gator.db.Channel;
import com.gator.db.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Владимир on 16.09.2016.
 */

@RestController
@RequestMapping("/channel")
public class ChannelController {

    @Autowired
    ChannelRepository channelRepository;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<Channel> getChannels(){
        return channelRepository.findAll();
    }
}

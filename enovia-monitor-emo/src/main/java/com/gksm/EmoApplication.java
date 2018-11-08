package com.gksm;

import com.gksm.dao.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmoApplication {

    @Autowired
    EventRepository eventRepository;

    public static void main(String[] args) {
        SpringApplication.run(EmoApplication.class, args);
    }
}

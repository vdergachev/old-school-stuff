package com.gksm.dao;

import com.gksm.models.Event;
import com.gksm.networking.EnoviaHost;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Created by VSDergachev on 11/11/2015.
 */
@Transactional
public interface EventRepository extends CrudRepository<Event, Long> {

//    List<Event> findByHostname(String hostname);

    @Query("SELECT e FROM Event e WHERE e.hostname = ?1 AND e.date >= CURRENT_DATE ")
    List<Event> findByHostnameAndDate(EnoviaHost hostname);
}

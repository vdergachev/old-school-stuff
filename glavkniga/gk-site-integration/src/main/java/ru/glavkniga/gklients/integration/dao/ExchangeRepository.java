package ru.glavkniga.gklients.integration.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.glavkniga.gklients.integration.entity.Exchange;

import java.util.List;

/**
 * Created by Vladimir on 17.06.2017.
 */
@Transactional
public interface ExchangeRepository extends CrudRepository<Exchange, Integer> {
    @Query(value = "SELECT * FROM gk_sys_site_exchange WHERE event = :event", nativeQuery = true)
    List<Exchange> findWithEventType(@Param("event") String event);
}

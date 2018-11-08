package ru.glavkniga.gklients.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.haulmont.cuba.core.global.AppBeans;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import ru.glavkniga.gklients.BaseIT;
import ru.glavkniga.gklients.crudentity.SiteExchange;
import ru.glavkniga.gklients.crudentity.SiteExchangeEvent;
import ru.glavkniga.gklients.rule.SiteRule;
import ru.glavkniga.gklients.service.GKExchangeProcessorService;

import java.util.*;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.*;
import static ru.glavkniga.gklients.crudentity.SiteExchangeEvent.*;

public class GKExchangeProcessorServiceBeanIT extends BaseIT {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final static String[] TABLES = {
            "gk_sys_site_user",
            "gk_sys_site_user_data",
            "gk_sys_site_user_services",
            "gk_sys_site_user_service_1",
            "gk_sys_site_user_service_2",
            "gk_sys_site_user_service_3"
    };

    private final static Map<String, Map<String, String>> DATA = ImmutableMap.<String, Map<String, String>>builder()
            .put("gk_sys_site_user", new HashMap<>(ImmutableMap.<String, String>builder()
                    .put("id", "")
                    .put("email", "email@email.com")
                    .put("pass_hash", "123#")
                    .put("access_type", "1")
                    .put("phone_hash", "456#")
                    .put("blocked", "false")
                    .build())
            )
            .put("gk_sys_site_user_data", new HashMap<>(ImmutableMap.<String, String>builder()
                    .put("id", "")
                    .put("name", "username")
                    .put("phone", "+79511239865")
                    .put("date", "01.01.1970")
                    .put("pass", "12345")
                    .build())
            )
            .put("gk_sys_site_user_services", new HashMap<>(ImmutableMap.<String, String>builder()
                    .put("user_id", "")
                    .put("service", "1")
                    .build())
            )
            .put("gk_sys_site_user_service_1", new HashMap<>(ImmutableMap.<String, String>builder()
                    .put("user_id", "")
                    .put("number", "1")
                    .put("year", "2017")
                    .put("date_begin", "01.01.1970")
                    .put("date_end", "02.01.1970")
                    .build())
            )
            .put("gk_sys_site_user_service_2", new HashMap<>(ImmutableMap.<String, String>builder()
                    .put("user_id", "")
                    .put("number", "1")
                    .put("year", "2017")
                    .put("date_begin", "01.01.1970")
                    .put("date_end", "02.01.1970")
                    .build())
            )
            .put("gk_sys_site_user_service_3", new HashMap<>(ImmutableMap.<String, String>builder()
                    .put("user_id", "")
                    .put("date_begin", "01.01.1970")
                    .put("date_end", "02.01.1970")
                    .build())
            )
            .build();


    private final static SiteExchangeEvent[] EVENTS = {
            CREATED,
            UPDATED,
            DELETED
    };

    @ClassRule
    public static SiteRule server = new SiteRule("localhost", 38888);

    @Before
    public void init() {
        cleanUpDB();
    }

    @Test
    public void processed() throws Exception {
        // given
        final GKExchangeProcessorServiceBean bean = getTargetObject(AppBeans.get(GKExchangeProcessorService.class));
        final GKExchangeProcessorServiceBean spyBean = spy(bean);

        when(spyBean.getActualExchangesFromSite()).thenReturn(createFakeList());
        doNothing().when(spyBean).setSiteExchangesStatusInProgress(anyListOf(String.class));
        doNothing().when(spyBean).setSiteExchangesStatusProcessed(anyListOf(String.class));

        // when
        spyBean.process();

        // then
    }

    private List<Object> createFakeList() {
        final List<Object> result = new ArrayList<>();
        int i = 0;
        for (String table : TABLES) {
            for (int j = 0; j < 3; j++) {
                final UUID uuid = UUID.randomUUID();
                for (SiteExchangeEvent event : EVENTS) {

                    if (event == CREATED ||
                            event == UPDATED && i % 2 == 0 ||
                            event == DELETED && (i % 3 == 0)) {

                        result.add(createSiteExchange(
                                table, event, uuid, new Date(System.currentTimeMillis() - i * 1000), i
                        ));


                        if (event != UPDATED) {
                            continue;
                        }

                        result.add(createSiteExchange(
                                table, event, uuid, new Date(System.currentTimeMillis() - i * 2 * 1000), i
                        ));
                        i++;

                    }
                    i++;
                }
            }
        }
        return result;
    }

    private SiteExchange createSiteExchange(String table, SiteExchangeEvent event, UUID uuid, Date date, int i) {
        final SiteExchange exchange = new SiteExchange();
        exchange.setSiteId(i);
        exchange.setUuid(UUID.randomUUID());
        exchange.setTableName(table);
        exchange.setEvent(event);
        exchange.setData(createData(table, uuid));
        exchange.setLastUpdate(date);
        return exchange;
    }

    private String createData(String table, UUID uuid) {
        final Map<String, String> data = DATA.get(table);
        if (data.containsKey("id")) {
            data.put("id", uuid.toString());
        } else if (data.containsKey("user_id")) {
            data.put("user_id", uuid.toString());
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}

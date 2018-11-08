package com.gksm;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gksm.networking.EventData;
import com.gksm.networking.EventType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EmoApplication.class)
@WebAppConfiguration
public class EmoApplicationTests {

    public MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @org.junit.Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void contextLoads() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        EventData[] datas = {
                new EventData("Test Everything", new Date(), EventType.LOGIN),
                new EventData("Test Everything", new Date(), EventType.CLICK),
                new EventData("Test Everything", new Date(), EventType.CLICK),
                new EventData("Test Everything", new Date(), EventType.CLICK),
                new EventData("Test Everything", new Date(), EventType.CLICK),
                new EventData("Test Everything", new Date(), EventType.LOGOUT)
        };

        final String jsonStr = mapper.writeValueAsString(datas);

        mockMvc.perform(post("/enovia-d.gksm.local/events")
                .header("Host", "localhost:8090")
                .contentType(MediaType.APPLICATION_JSON).content(jsonStr))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/enovia-d.gksm.local/events")
                .header("Host", "localhost:8090"))
                .andDo(print())
                .andReturn();

    }

}

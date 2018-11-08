package com.fasten.inc.net;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.server.ServerEndpoint;

import javax.websocket.*;

import com.fasten.inc.net.model.Type;
import com.fasten.inc.service.LoginService;
import com.fasten.inc.net.model.Message;

import java.io.IOException;

/**
 * Created by Владимир on 19.08.2016.
 */

@ServerEndpoint(value = "/ws", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class FastenEndpoint {

    @Inject
    private LoginService loginService;

    @OnOpen
    private void handleOpen(){
        System.out.println("New connection...");
    }

    @OnMessage
    public void handleMessage(Message message, Session client) {
        if (Type.LOGIN_CUSTOMER == message.getType()) {
            loginService.processRequest(message, client);
        } else {
            //Send error message
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error_description", "Service error")
                    .add("error_code", "customer.Service error")
                    .build();
            Message responseData = new Message(Type.CUSTOMER_ERROR, message.getSequenceId(), errorJson);
            try {
                client.getBasicRemote().sendText(responseData.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

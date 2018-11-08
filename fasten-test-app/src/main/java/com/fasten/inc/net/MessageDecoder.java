package com.fasten.inc.net;

import com.fasten.inc.net.model.Message;
//import com.google.gson.JsonObject;


import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.StringReader;

/**
 * Created by Владимир on 19.08.2016.
 */
public class MessageDecoder implements Decoder.Text<Message> {
    @Override
    public Message decode(String message) throws DecodeException {
        JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
        return new Message(jsonObject);
    }

    @Override
    public boolean willDecode(String message) {
        if(message != null) {
            try {
                Json.createReader(new StringReader(message)).readObject();
                return true;
            } catch (JsonException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }
}

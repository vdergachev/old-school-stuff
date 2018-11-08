package com.fasten.inc.net.model;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.Serializable;

/**
 * Created by Владимир on 19.08.2016.
 */
public class Message implements Serializable{

    private final Type type;
    private final String sequenceId;
    private final JsonObject data;

    //For send
    public Message(Type type, String sequenceId, JsonObject data) {
        this.type = type;
        this.sequenceId = sequenceId;
        this.data = data;
    }

    //For receive
    public Message(JsonObject jsonObject) {
        String typeValue = jsonObject.getString("type");

        type = Type.parse(typeValue);
        sequenceId = jsonObject.getString("sequence_id");
        data = jsonObject.getJsonObject("data");
    }

    public Type getType() {
        return type;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public JsonObject getData() {
        return data;
    }

    @Override
    public String toString() {
        return Json.createObjectBuilder()
                .add("type", type.getType())
                .add("sequence_id", sequenceId)
                .add("data", data)
                .build()
                .toString();
    }
}

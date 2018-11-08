package com.gksm.networking.helpers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.gksm.networking.EventType;

import java.io.IOException;

/**
 * Created by VSDergachev on 11/12/2015.
 */

public class EventTypeDeserializer extends JsonDeserializer<EventType> {

    @Override
    public EventType deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException
    {
        final String jsonValue = parser.getText().trim();
        for (final EventType enumValue : EventType.values())
        {
            if (enumValue.name().equalsIgnoreCase(jsonValue))
            {
                return enumValue;
            }
        }
        return null;
    }
}

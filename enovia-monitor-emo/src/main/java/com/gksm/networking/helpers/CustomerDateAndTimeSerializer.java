package com.gksm.networking.helpers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by VSDergachev on 11/12/2015.
 */
public class CustomerDateAndTimeSerializer extends JsonSerializer<Date> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat(CustomerDateAndTimeDeserializer.DATE_FORMAT);

    @Override
    public void serialize(Date date, JsonGenerator generator, SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {
        generator.writeString(dateFormat.format(date));
    }
}

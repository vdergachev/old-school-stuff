package com.gksm.networking.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by VSDergachev on 11/12/2015.
 */
public class CustomerDateAndTimeDeserializer extends JsonDeserializer<Date> {

    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    @Override
    public Date deserialize(JsonParser jsonParser,
                            DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        String str = jsonParser.getText().trim();
        try {
            return dateFormat.parse(str);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return deserializationContext.parseDate(str);
    }
}
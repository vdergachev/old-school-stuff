package com.gksm.networking.helpers;

import com.gksm.networking.EnoviaHost;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.beans.PropertyEditorSupport;

/**
 * Created by VSDergachev on 11/18/2015.
 */
@Converter
public class EnoviaHostConverter extends PropertyEditorSupport implements AttributeConverter<EnoviaHost, String> {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        for (final EnoviaHost enumValue : EnoviaHost.values()) {
            if (enumValue.getHostname().equalsIgnoreCase(text)) {
                setValue(enumValue);
            }
        }
    }

    @Override
    public EnoviaHost convertToEntityAttribute(String text) {
        for (final EnoviaHost enumValue : EnoviaHost.values()) {
            if (enumValue.getHostname().equalsIgnoreCase(text)) {
                return enumValue;
            }
        }

        return null;
    }

    @Override
    public String convertToDatabaseColumn(EnoviaHost enoviaHost) {
        return enoviaHost.getHostname();
    }
}

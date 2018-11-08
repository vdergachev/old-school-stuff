package com.gksm.networking.helpers;

import com.gksm.networking.EnoviaHost;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.stereotype.Component;

/**
 * Created by VSDergachev on 11/18/2015.
 */

@Component("enoviaHostRegistrar")
public class EnoviaHostConverterRegistrar implements PropertyEditorRegistrar {

    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        registry.registerCustomEditor(EnoviaHost.class, new EnoviaHostConverter());
    }
}

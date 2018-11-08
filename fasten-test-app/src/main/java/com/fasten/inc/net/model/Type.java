package com.fasten.inc.net.model;

/**
 * Created by Владимир on 19.08.2016.
 */
public enum Type {

    LOGIN_CUSTOMER("LOGIN_CUSTOMER"),
    CUSTOMER_API_TOKEN("CUSTOMER_API_TOKEN"),
    CUSTOMER_ERROR("CUSTOMER_ERROR"),
    UNKNOWN("");

    private final String type;

    Type(String type) {
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public static Type parse(String value){
        for(Type aType: Type.values())
            if(aType.getType().equals(value))
                return aType;
        return UNKNOWN;
    }
}

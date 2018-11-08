/*
 * Copyright (c) 2015 ru.glavkniga.gklients.service
 */
package ru.glavkniga.gklients.service;

/**
 * @author LysovIA
 */
public interface GeneratorService {
    String NAME = "gklients_GeneratorService";

    public String generateKey(String email);

    public String generatePass(String email);

}
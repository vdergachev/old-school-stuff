/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.service;

import com.haulmont.cuba.core.entity.FileDescriptor;


/**
 * @author LysovIA
 */
public interface CsvParserService {
    String NAME = "gklients_CsvParserService";

    public void parseCsvToMunObraz(FileDescriptor file);
    public void parseCsvToRiz(FileDescriptor file);
}
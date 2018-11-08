/*
 * Copyright (c) 2015 gklients
 */

package ru.glavkniga.gklients.gui.tarif;

import java.io.File;

/**
 * Created by LysovIA on 02.12.2015.
 */
public class FileParam {
    private String fileFieldName;
    private File file;
    private String fileName;
    private String contentType;

    public FileParam(String fileFieldName, String fileName, File file, String contentType) {
        this.fileFieldName = fileFieldName;
        this.file = file;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public String getFileFieldName() {
        return fileFieldName;
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }
}

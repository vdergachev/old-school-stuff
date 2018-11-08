package ru.glavkniga.gklients.networking.api.model;

import java.io.Serializable;

/**
 * Created by vdergachev on 03.07.17.
 */
public class UploadAttachmentResponse implements Serializable{

    private static final long serialVersionUID = -7146705980331196612L;

    private String path;

    public UploadAttachmentResponse() {
    }

    public UploadAttachmentResponse(final String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

/*
 * Copyright (c) 2015 gklients
 */

package ru.glavkniga.gklients.gui.tarif;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by LysovIA on 02.12.2015.
 */
public class HTTPMultipartRequest {


    private static final String BOUNDARY = "----------Vhgskgpwjxkjdfnldsnfjldsnjlbsndfbgdslfngfnldfg";

    private String url;
    private List<Param> params;
    private List<FileParam> fileParams;


    public HTTPMultipartRequest(String url, List<Param> params, List<FileParam> fileParams) {
        this.url = url;
        this.params = params;
        this.fileParams = fileParams;
    }

    public byte[] send() {
        HttpURLConnection hc = null;
        InputStream is = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] res = null;
        try {
            URL _url = new URL(url);
            hc = (HttpURLConnection) _url.openConnection();
            hc.setDoOutput(true);
            hc.setDoInput(true);
            hc.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            hc.setRequestMethod("POST");
            OutputStream dout = hc.getOutputStream();


            for (Param p : params) {
                dout.write(
                        new StringBuffer().append("--").append(BOUNDARY)
                                .append("\r\n")
                                .append("Content-Disposition: form-data; name=\"").append(p.getName()).append("\"")
                                .append("\r\n\r\n")
                                .append(p.getValue())
                                .append("\r\n").toString().getBytes()
                );
            }


            for (FileParam fp : fileParams) {
                dout.write(
                        new StringBuffer().append("--").append(BOUNDARY)
                                .append("\r\n")
                                .append("Content-Disposition: form-data; name=\"").append(fp.getFileFieldName()).append("\"; filename=\"").append(fp.getFileName()).append("\"")
                                .append("\r\n")
                                .append("Content-Type: ").append(fp.getContentType())
                                .append("\r\n\r\n").toString().getBytes()
                );
                byte[] fileBytes = new byte[(int) fp.getFile().length()];
                new FileInputStream(fp.getFile()).read(fileBytes);
                dout.write(fileBytes);
                dout.write("\r\n".getBytes());
            }
            dout.write(("\r\n--" + BOUNDARY + "--\r\n").getBytes());
            dout.flush();
            dout.close();
            int ch;
            is = hc.getInputStream();
            while ((ch = is.read()) != -1) {
                bos.write(ch);
            }
            res = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();

                if (is != null)
                    is.close();

                if (hc != null)
                    hc.disconnect();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return res;
    }
}



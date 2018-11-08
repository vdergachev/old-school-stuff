/*
 * Copyright (c) 2015 gklients
 */

package ru.glavkniga.gklients.gconnection;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by LysovIA on 09.12.2015.
 */
public class SendGet {

    public static Response get(String requestURL) {
        URL url;
        Response response = new Response();
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            response.code = conn.getResponseCode();
            if (response.code == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = "";
                while ((line = br.readLine()) != null) {
                    jsonResponse += line;
                }
                response.json = jsonResponse;
            }
        } catch (Exception e) {
            response.code = 0;
            e.printStackTrace();
        }
        return response;
    }
}
